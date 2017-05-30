/*
 * Copyright 2017 Atypon Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atypon.wayf.integration.publisher;

import com.atypon.wayf.request.ResponseWriter;
import com.atypon.wayf.verticle.routing.BaseHttpTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class PublisherDeviceIntegrationTest extends BaseHttpTest {

    private static final String HTTP_LOGGING_FILE = "publisher_integration_test";

    private static final String BASE_FILE_PATH = "json_files/publisher_device_integration/";

    private static final String CREATE_PUBLISHER_A_REQUEST_JSON = getFileAsString(BASE_FILE_PATH + "publisher/create_publisher_a_request.json");
    private static final String CREATE_PUBLISHER_A_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "publisher/create_publisher_a_response.json");

    private static final String CREATE_PUBLISHER_B_REQUEST_JSON = getFileAsString(BASE_FILE_PATH + "publisher/create_publisher_b_request.json");
    private static final String CREATE_PUBLISHER_B_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "publisher/create_publisher_b_response.json");

    private static final String CREATE_SAML_IDP_REQUEST_JSON = getFileAsString(BASE_FILE_PATH + "identity_provider/create_saml_entity_request.json");
    private static final String CREATE_SAML_IDP_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "identity_provider/create_saml_entity_response.json");

    private static final String CREATE_OPEN_ATHENS_IDP_REQUEST_JSON = getFileAsString(BASE_FILE_PATH + "identity_provider/create_open_athens_entity_request.json");
    private static final String CREATE_OPEN_ATHENS_IDP_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "identity_provider/create_open_athens_entity_response.json");

    private static final String CREATE_OAUTH_IDP_REQUEST_JSON = getFileAsString(BASE_FILE_PATH + "identity_provider/create_oauth_entity_request.json");
    private static final String CREATE_OAUTH_IDP_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "identity_provider/create_oauth_entity_response.json");

    private static final String RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/device/relate_new_device_publisher_a_response.json");

    private static final String NEW_DEVICE_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/empty_history_response.json");
    private static final String INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/initial_add_idp_response.json");
    private static final String AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/after_delete_idp_response.json");
    private static final String RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/re-add_saml_idp_response.json");
    private static final String ONE_OATH_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/one_oath_response.json");

    private String publisherAToken;
    private String publisherBToken;

    public PublisherDeviceIntegrationTest() {
        super(HTTP_LOGGING_FILE);
    }


    @Before
    public void createPublishers() {
        if (publisherAToken == null) {
            // Create 2 Publishers
            publisherAToken = publisherTestUtil.testCreatePublisher(CREATE_PUBLISHER_A_REQUEST_JSON, CREATE_PUBLISHER_A_RESPONSE_JSON);
            publisherBToken = publisherTestUtil.testCreatePublisher(CREATE_PUBLISHER_B_REQUEST_JSON, CREATE_PUBLISHER_B_RESPONSE_JSON);
        }
    }

    @Test
    public void multiplePublisherFullFlow() throws Exception {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        // Create device
        String globalIdPublisherA = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherAToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Assert an empty history
        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherAToken, NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Get the minimum last active date
        Date earliestLastActiveDate = DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));

        // Add the IDPs to the device multiple times and validate the IDP's ID is the same each time
        Long samlId = identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(5, publisherALocalId, publisherAToken, CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(4, publisherALocalId, publisherAToken, CREATE_OPEN_ATHENS_IDP_REQUEST_JSON, CREATE_OPEN_ATHENS_IDP_RESPONSE_JSON);
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(3, publisherALocalId, publisherAToken, CREATE_OAUTH_IDP_REQUEST_JSON, CREATE_OAUTH_IDP_RESPONSE_JSON);

        // Get the maximum last active active date
        Date latestLastActiveDate = ResponseWriter.DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));

        // Test the device history after adding the IDPs
        String deviceHistoryFromPublisherA = deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherAToken, INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON);
        deviceAccessTestUtil.testLastActiveDateBetween(earliestLastActiveDate, latestLastActiveDate, 3, deviceHistoryFromPublisherA);

        // Relate the device to publisher B
        String publisherBLocalId = "local-id-publisher-b-" + UUID.randomUUID().toString();
        String globalIdPublisherB = deviceTestUtil.relateDeviceToPublisher(publisherBLocalId, publisherBToken, globalIdPublisherA, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        assertEquals(globalIdPublisherA, globalIdPublisherB);

        // Get the usage history for publisher B
        String deviceHistoryFromPublisherB = deviceAccessTestUtil.testDeviceHistory(publisherBLocalId, publisherBToken, INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON);

        // Compare the usage history from publisher A to that of publisher B
        deviceAccessTestUtil.compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);

        // Remove the SAML entity from the device from publisher A
        identityProviderTestUtil.removeIdpForDevice(publisherALocalId, publisherAToken, samlId);

        // Get the usage history as publisher A and then publisher B
        deviceHistoryFromPublisherA = deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherAToken, AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON);
        deviceHistoryFromPublisherB = deviceAccessTestUtil.testDeviceHistory(publisherBLocalId, publisherBToken, AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON);

        // Ensure the usage history is the same for both publishers
        deviceAccessTestUtil.compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);

        // Add back the SAML identity to the device
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(5, publisherALocalId, publisherAToken, CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);

        // Get the usage history as publisher A and then publisher B
        deviceHistoryFromPublisherA = deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherAToken, RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON);
        deviceHistoryFromPublisherB = deviceAccessTestUtil.testDeviceHistory(publisherBLocalId, publisherBToken, RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON);

        // Ensure the usage history is the same for both publishers
        deviceAccessTestUtil.compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);
    }

    @Test
    public void testExistingDeviceDeletePublisherLocalId() {
        String publisherAFirstLocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        // Create device
        String firstGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherAFirstLocalId, publisherAToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        String publisherASecondLocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        assertFalse(publisherAFirstLocalId.equals(publisherASecondLocalId));

        String secondGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherASecondLocalId, publisherAToken, firstGlobalId, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        assertEquals(firstGlobalId, secondGlobalId);
    }

    @Test
    public void testExistingDeviceDeleteGlobalId() {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        // Create device for local ID
        String firstGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherAToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Create device again for same local ID but do not pass in global ID
        String secondGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherAToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Ensure the system resolved to the same global ID each time
        assertEquals(firstGlobalId, secondGlobalId);

        // Try passing in the same local ID but this time with a different publisher
        String thirdGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherBToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Test that the system did not resolve to the same device because this was for a different publisher
        assertNotNull(firstGlobalId, thirdGlobalId);
    }

    @Test
    public void testNonUniqueLocalId() {
        // Generate Device X for Publisher A
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();
        String deviceXGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherAToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Link Device X to Publisher B
        String publisherBDeviceXLocalId = "local-id-publisher-b-" + UUID.randomUUID().toString();
        String deviceXGlobalIdPublisherB = deviceTestUtil.relateDeviceToPublisher(publisherBDeviceXLocalId, publisherBToken, deviceXGlobalId, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Ensure same global ID
        assertEquals(deviceXGlobalId, deviceXGlobalIdPublisherB);

        // Assert an empty history
        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherAToken, NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Add activity to Device X
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(1, publisherALocalId, publisherAToken, CREATE_OAUTH_IDP_REQUEST_JSON, CREATE_OAUTH_IDP_RESPONSE_JSON);

        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherAToken, ONE_OATH_HISTORY_RESPONSE_JSON);

        // Generate Device Y under same local ID as Device X
        String publisherBSecondLocalId = "local-id-publisher-b-" + UUID.randomUUID().toString();
        String deviceYGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherBSecondLocalId, publisherBToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Assert an empty history
        deviceAccessTestUtil.testDeviceHistory(publisherBSecondLocalId, publisherBToken, NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Overwrite Device X with Device Y for publisher A's localId
        String deviceYGlobalIdSecond = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherAToken, deviceYGlobalId, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Test to ensure the localId now resolves to Device Y
        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherAToken, NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Test to make sure Device X still has data for publisher B
        deviceAccessTestUtil.testDeviceHistory(publisherBDeviceXLocalId, publisherBToken, ONE_OATH_HISTORY_RESPONSE_JSON);

    }
}

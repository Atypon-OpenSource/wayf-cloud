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

import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.integration.HttpTestUtil;
import com.atypon.wayf.request.ResponseWriter;
import com.atypon.wayf.verticle.routing.BaseHttpTest;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class PublisherIntegrationTest extends BaseHttpTest {

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

    private static final String ERROR_401_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/authentication/401.json");
    private static final String ERROR_404_BAD_LOCAL_ID_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/authentication/404_bad_local_id.json");
    private static final String ERROR_404_BAD_GLOBAL_ID_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/authentication/404_bad_global_id.json");
    private static final String ERROR_400_BAD_IDENTITY_PROVIDER_ID_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/authentication/400_invalid_identity_provider_id.json");
    private static final String ERROR_404_LOCAL_ID_NOT_FOUND_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/device/404_local_id_not_found.json");

    private static final String NEW_DEVICE_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/empty_history_response.json");
    private static final String INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/initial_add_idp_response.json");
    private static final String AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/after_delete_idp_response.json");
    private static final String RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/re-add_saml_idp_response.json");
    private static final String ONE_OATH_HISTORY_RESPONSE_JSON = getFileAsString(BASE_FILE_PATH + "/history/one_oath_response.json");

    private Publisher publisherA;
    private Publisher publisherB;

    public PublisherIntegrationTest() {
        super(HTTP_LOGGING_FILE);
    }

    @Before
    public void createPublishers() {
        if (publisherA  == null) {
            // Create 2 Publishers
            String publisherAJson = CREATE_PUBLISHER_A_REQUEST_JSON;
            publisherAJson = HttpTestUtil.setField(publisherAJson, "code", "code-" + UUID.randomUUID().toString());

            String publisherBJson = CREATE_PUBLISHER_B_REQUEST_JSON;
            publisherBJson = HttpTestUtil.setField(publisherBJson, "code", "code-" + UUID.randomUUID().toString());

            publisherA = publisherTestUtil.testCreatePublisher(publisherAJson, CREATE_PUBLISHER_A_RESPONSE_JSON);
            publisherB = publisherTestUtil.testCreatePublisher(publisherBJson, CREATE_PUBLISHER_B_RESPONSE_JSON);
        }
    }

    @Test
    public void multiplePublisherFullFlow() throws Exception {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        deviceTestUtil.registerLocalId(publisherALocalId, publisherA.getToken());

        // Create device
        String globalIdPublisherA = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Assert an empty history
        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherA.getToken(), NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Get the minimum last active date
        Date earliestLastActiveDate = DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));

        // Add the IDPs to the device multiple times and validate the IDP's ID is the same each time
        Long samlId = identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(5, publisherALocalId, publisherA.getToken(), CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(4, publisherALocalId, publisherA.getToken(), CREATE_OPEN_ATHENS_IDP_REQUEST_JSON, CREATE_OPEN_ATHENS_IDP_RESPONSE_JSON);
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(3, publisherALocalId, publisherA.getToken(), CREATE_OAUTH_IDP_REQUEST_JSON, CREATE_OAUTH_IDP_RESPONSE_JSON);

        // Get the maximum last active active date
        Date latestLastActiveDate = ResponseWriter.DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));

        // Test the device history after adding the IDPs
        String deviceHistoryFromPublisherA = deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherA.getToken(), INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON);
        deviceAccessTestUtil.testLastActiveDateBetween(earliestLastActiveDate, latestLastActiveDate, 3, deviceHistoryFromPublisherA);

        // Relate the device to publisher B
        String publisherBLocalId = "local-id-publisher-b-" + UUID.randomUUID().toString();
        deviceTestUtil.registerLocalId(publisherBLocalId, publisherB.getToken());
        String globalIdPublisherB = deviceTestUtil.relateDeviceToPublisher(publisherBLocalId, publisherB.getCode(), globalIdPublisherA, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        assertEquals(globalIdPublisherA, globalIdPublisherB);

        // Get the usage history for publisher B
        String deviceHistoryFromPublisherB = deviceAccessTestUtil.testDeviceHistory(publisherBLocalId, publisherB.getToken(), INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON);

        // Compare the usage history from publisher A to that of publisher B
        deviceAccessTestUtil.compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);

        // Remove the SAML entity from the device from publisher A
        identityProviderTestUtil.removeIdpForDevice(publisherALocalId, publisherA.getToken(), samlId);

        // Get the usage history as publisher A and then publisher B
        deviceHistoryFromPublisherA = deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherA.getToken(), AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON);
        deviceHistoryFromPublisherB = deviceAccessTestUtil.testDeviceHistory(publisherBLocalId, publisherB.getToken(), AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON);

        // Ensure the usage history is the same for both publishers
        deviceAccessTestUtil.compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);

        // Add back the SAML identity to the device
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(5, publisherALocalId, publisherA.getToken(), CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);

        // Get the usage history as publisher A and then publisher B
        deviceHistoryFromPublisherA = deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherA.getToken(), RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON);
        deviceHistoryFromPublisherB = deviceAccessTestUtil.testDeviceHistory(publisherBLocalId, publisherB.getToken(), RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON);

        // Ensure the usage history is the same for both publishers
        deviceAccessTestUtil.compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);
    }

    @Test
    public void relateDeviceBeforeRegisteringLocalId() {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        deviceTestUtil.relateDeviceToPublisherError(HttpStatus.SC_NOT_FOUND, publisherALocalId, publisherA.getCode(), null, ERROR_404_LOCAL_ID_NOT_FOUND_RESPONSE_JSON);

    }

    @Test
    public void existingDeviceDeletePublisherLocalId() {
        String publisherAFirstLocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();
        deviceTestUtil.registerLocalId(publisherAFirstLocalId, publisherA.getToken());

        // Create device
        String firstGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherAFirstLocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        String publisherASecondLocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        assertFalse(publisherAFirstLocalId.equals(publisherASecondLocalId));

        deviceTestUtil.registerLocalId(publisherASecondLocalId, publisherA.getToken());
        String secondGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherASecondLocalId, publisherA.getCode(), firstGlobalId, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        assertEquals(firstGlobalId, secondGlobalId);
    }

    @Test
    public void existingDeviceDeleteGlobalId() {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();
        deviceTestUtil.registerLocalId(publisherALocalId, publisherA.getToken());

        // Create device for local ID
        String firstGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Create device again for same local ID but do not pass in global ID
        String secondGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Ensure the system resolved to the same global ID each time
        assertEquals(firstGlobalId, secondGlobalId);

        // Try passing in the same local ID but this time with a different publisher without registering it
        deviceTestUtil.relateDeviceToPublisherError(HttpStatus.SC_NOT_FOUND, publisherALocalId, publisherB.getCode(), null, ERROR_404_LOCAL_ID_NOT_FOUND_RESPONSE_JSON);

        deviceTestUtil.registerLocalId(publisherALocalId, publisherB.getToken());

        String thirdGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherB.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Test that the system did not resolve to the same device because this was for a different publisher
        assertNotEquals(firstGlobalId, thirdGlobalId);
    }

    @Test
    public void nonUniqueLocalId() {
        // Generate Device X for Publisher A
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();
        deviceTestUtil.registerLocalId(publisherALocalId, publisherA.getToken());
        String deviceXGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Link Device X to Publisher B
        String publisherBDeviceXLocalId = "local-id-publisher-b-" + UUID.randomUUID().toString();
        deviceTestUtil.registerLocalId(publisherBDeviceXLocalId, publisherB.getToken());
        String deviceXGlobalIdPublisherB = deviceTestUtil.relateDeviceToPublisher(publisherBDeviceXLocalId, publisherB.getCode(), deviceXGlobalId, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Ensure same global ID
        assertEquals(deviceXGlobalId, deviceXGlobalIdPublisherB);

        // Assert an empty history
        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherA.getToken(), NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Add activity to Device X
        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(1, publisherALocalId, publisherA.getToken(), CREATE_OAUTH_IDP_REQUEST_JSON, CREATE_OAUTH_IDP_RESPONSE_JSON);

        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherA.getToken(), ONE_OATH_HISTORY_RESPONSE_JSON);

        // Generate Device Y under same local ID as Device X
        String publisherBSecondLocalId = "local-id-publisher-b-" + UUID.randomUUID().toString();
        deviceTestUtil.registerLocalId(publisherBSecondLocalId, publisherB.getToken());
        String deviceYGlobalId = deviceTestUtil.relateDeviceToPublisher(publisherBSecondLocalId, publisherB.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Assert an empty history
        deviceAccessTestUtil.testDeviceHistory(publisherBSecondLocalId, publisherB.getToken(), NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Overwrite Device X with Device Y for publisher A's localId
        String deviceYGlobalIdSecond = deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), deviceYGlobalId, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Test to ensure the localId now resolves to Device Y
        deviceAccessTestUtil.testDeviceHistory(publisherALocalId, publisherA.getToken(), NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Test to make sure Device X still has data for publisher B
        deviceAccessTestUtil.testDeviceHistory(publisherBDeviceXLocalId, publisherB.getToken(), ONE_OATH_HISTORY_RESPONSE_JSON);
    }

    @Test
    public void badPublisherToken() {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        // Try to relate device to publisher with a bad publisher token
        String badToken = "obviously-bad-token";
        deviceTestUtil.deviceQueryBadPublisherToken(publisherALocalId, badToken, ERROR_401_RESPONSE_JSON);

        deviceTestUtil.registerLocalId(publisherALocalId, publisherA.getToken());
        // Create device
        deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Read History with bad token
        deviceAccessTestUtil.testDeviceHistoryError(HttpStatus.SC_UNAUTHORIZED, publisherALocalId, badToken, ERROR_401_RESPONSE_JSON);

        // Test adding an IDP with a bad token
        identityProviderTestUtil.addIdpToDeviceError(HttpStatus.SC_UNAUTHORIZED, publisherALocalId, badToken, CREATE_OAUTH_IDP_REQUEST_JSON, ERROR_401_RESPONSE_JSON);

        Long samlId = identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(1, publisherALocalId, publisherA.getToken(), CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);

        // Test removing IDP with bad token
        identityProviderTestUtil.removeIdpForDeviceError(HttpStatus.SC_UNAUTHORIZED, publisherALocalId, badToken, samlId, ERROR_401_RESPONSE_JSON);
    }

    @Test
    public void badLocalId() {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        deviceTestUtil.registerLocalId(publisherALocalId, publisherA.getToken());
        deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        String badLocalId = "obviously-bad-local-id";
        deviceAccessTestUtil.testDeviceHistoryError(HttpStatus.SC_NOT_FOUND, badLocalId, publisherA.getToken(), ERROR_404_BAD_LOCAL_ID_RESPONSE_JSON);

        identityProviderTestUtil.addIdpToDeviceError(HttpStatus.SC_NOT_FOUND, badLocalId, publisherA.getToken(), CREATE_OAUTH_IDP_REQUEST_JSON, ERROR_404_BAD_LOCAL_ID_RESPONSE_JSON);

        Long samlId = identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(1, publisherALocalId, publisherA.getToken(), CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);

        identityProviderTestUtil.removeIdpForDeviceError(HttpStatus.SC_NOT_FOUND, badLocalId, publisherA.getToken(), samlId, ERROR_404_BAD_LOCAL_ID_RESPONSE_JSON);
    }

    @Test
    public void badIdentityProviderId() {
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        deviceTestUtil.registerLocalId(publisherALocalId, publisherA.getToken());
        deviceTestUtil.relateDeviceToPublisher(publisherALocalId, publisherA.getCode(), null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        identityProviderTestUtil.testAddIdpToDeviceAndIdpResolution(1, publisherALocalId, publisherA.getToken(), CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);
        identityProviderTestUtil.removeIdpForDeviceError(HttpStatus.SC_BAD_REQUEST, publisherALocalId, publisherA.getToken(), 0L, ERROR_400_BAD_IDENTITY_PROVIDER_ID_RESPONSE_JSON);
    }

    @Test
    public void badGlobalId() {
        String badGlobalId = "obviously-bad-global-id";
        String publisherALocalId = "local-id-publisher-a-" + UUID.randomUUID().toString();

        deviceTestUtil.registerLocalId(publisherALocalId, publisherA.getToken());
        deviceTestUtil.relateDeviceToPublisherError(HttpStatus.SC_NOT_FOUND, publisherALocalId, publisherA.getCode(), badGlobalId, ERROR_404_BAD_GLOBAL_ID_RESPONSE_JSON);
    }
}

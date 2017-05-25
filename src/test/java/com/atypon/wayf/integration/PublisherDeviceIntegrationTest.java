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

package com.atypon.wayf.integration;

import com.atypon.wayf.verticle.routing.BaseHttpTest;
import com.atypon.wayf.verticle.routing.LoggingHttpRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PublisherDeviceIntegrationTest extends BaseHttpTest {

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

    private LoggingHttpRequest request = new LoggingHttpRequest("publisher_integration_test");

    @Test
    public void runIntegration() throws Exception {
        // Create 2 Publishers
        String publisherAToken = testCreatePublisher(CREATE_PUBLISHER_A_REQUEST_JSON, CREATE_PUBLISHER_A_RESPONSE_JSON);
        String publisherBToken = testCreatePublisher(CREATE_PUBLISHER_B_REQUEST_JSON, CREATE_PUBLISHER_B_RESPONSE_JSON);

        String publisherALocalId = "local-id-publisher-a";

        // Create device
        String globalIdPublisherA = relateDeviceToPublisher(publisherALocalId, publisherAToken, null, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Assert an empty history
        testDeviceHistory(publisherALocalId, publisherAToken, NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Get the minimum last active date
        Date earliestLastActiveDate = DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));

        // Add the IDPs to the device multiple times and validate the IDP's ID is the same each time
        Long samlId = testAddIdpToDeviceAndIdpResolution(5, publisherALocalId, publisherAToken, CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);
        testAddIdpToDeviceAndIdpResolution(4, publisherALocalId, publisherAToken, CREATE_OPEN_ATHENS_IDP_REQUEST_JSON, CREATE_OPEN_ATHENS_IDP_RESPONSE_JSON);
        testAddIdpToDeviceAndIdpResolution(3, publisherALocalId, publisherAToken, CREATE_OAUTH_IDP_REQUEST_JSON, CREATE_OAUTH_IDP_RESPONSE_JSON);

        // Get the maximum last active active date
        Date latestLastActiveDate = DATE_FORMAT.parse(DATE_FORMAT.format(new Date()));

        // Test the device history after adding the IDPs
        String deviceHistoryFromPublisherA = testDeviceHistory(publisherALocalId, publisherAToken, INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON);
        testLastActiveDateBetween(earliestLastActiveDate, latestLastActiveDate, 3, deviceHistoryFromPublisherA);

        // Relate the device to publisher B
        String publisherBLocalId = "local-id-publisher-b";
        String globalIdPublisherB = relateDeviceToPublisher(publisherBLocalId, publisherBToken, globalIdPublisherA, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        assertEquals(globalIdPublisherA, globalIdPublisherB);

        // Get the usage history for publisher B
        String deviceHistoryFromPublisherB = testDeviceHistory(publisherBLocalId, publisherBToken, INITIAL_ADD_IDP_DEVICE_HISTORY_RESPONSE_JSON);

        // Compare the usage history from publisher A to that of publisher B
        compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);

        // Remove the SAML entity from the device from publisher A
        removeIdpForDevice(publisherALocalId, publisherAToken, samlId);

        // Get the usage history as publisher A and then publisher B
        deviceHistoryFromPublisherA = testDeviceHistory(publisherALocalId, publisherAToken, AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON);
        deviceHistoryFromPublisherB = testDeviceHistory(publisherBLocalId, publisherBToken, AFTER_DELETE_IDP_DEVICE_HISTORY_RESPONSE_JSON);

        // Ensure the usage history is the same for both publishers
        compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);

        // Add back the SAML identity to the device
        testAddIdpToDeviceAndIdpResolution(5, publisherALocalId, publisherAToken, CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);

        // Get the usage history as publisher A and then publisher B
        deviceHistoryFromPublisherA = testDeviceHistory(publisherALocalId, publisherAToken, RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON);
        deviceHistoryFromPublisherB = testDeviceHistory(publisherBLocalId, publisherBToken, RE_ADD_SAML_IDP_HISTORY_RESPONSE_JSON);

        // Ensure the usage history is the same for both publishers
        compareDeviceHistory(deviceHistoryFromPublisherA, deviceHistoryFromPublisherB);
    }

    private String testCreatePublisher(String requestBody, String response) {
        String createResponse =
                request
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .method(Method.POST)
                        .url("/1/publisher")
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.token",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);

        String token = readField(createResponse, "$.token");

        assertJsonEquals(response, createResponse, createResponseGeneratedFields);

        return token;
    }

    private String relateDeviceToPublisher(String localId, String publisherToken, String globalId, String expectedResponseJson) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", publisherToken);
        headers.put("User-Agent", "Test-Agent");
        if (globalId != null) {
            headers.put("X-Device-Id", globalId);
        }

        ExtractableResponse relateResponse = request
                .headers(headers)
                .url("/1/device/" + localId)
                .method(Method.PATCH)
                .execute()
                .statusCode(200)
                .extract();

        String deviceIdHeader = relateResponse.header("X-Device-Id");
        assertNotNull(deviceIdHeader);

        String deviceBody = relateResponse.response().body().asString();

        String[] relateResponseGeneratedFields = {
                "$.id",
                "$.createdDate"
        };

        assertJsonEquals(expectedResponseJson, deviceBody, relateResponseGeneratedFields);

        return deviceIdHeader;
    }

    private String testDeviceHistory(String localId, String publisherToken, String expectedHistoryJson) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", publisherToken);

        String historyResponse =
                request
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .url("/1/device/" + localId + "/history")
                        .method(Method.GET)
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        compareDeviceHistory(expectedHistoryJson, historyResponse);

        return historyResponse;
    }

    private void compareDeviceHistory(String history1, String history2) {
        String[] addDeviceHistoryGeneratedFields = {
                "$[*].lastActiveDate",
                "$[*].idp.id",
                "$[*].idp.createdDate"
        };

        assertJsonEquals(history1, history2, addDeviceHistoryGeneratedFields);
    }

    private Long addIdpToDevice(String localId, String publisherToken, String idpBodyJson, String expectedResponseJson) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", publisherToken);

        String addIdpResponse =
                request
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .body(idpBodyJson)
                        .method(Method.POST)
                        .url("/1/device/" + localId + "/history/idp")
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        Long idpId = Long.valueOf(readField(addIdpResponse, "$.id"));

        String[] addIdpResponseGeneratedFields = {
                "$.id",
                "$.createdDate"
        };

        assertJsonEquals(expectedResponseJson, addIdpResponse, addIdpResponseGeneratedFields);

        return idpId;
    }

    private Long testAddIdpToDeviceAndIdpResolution(int count, String localId, String publisherToken, String requestJson, String expectedResponseJson) {
        Long[] ids = new Long[count];

        for (int i = 0; i < count; i++) {
            ids[i] = addIdpToDevice(localId, publisherToken, requestJson, expectedResponseJson);
        }

        for (int i = 0; i < count - 1; i++) {
            assertEquals(ids[i], ids[i+1]);
        }

        return ids[0];
    }

    private void testLastActiveDateBetween(Date startDate, Date endDate, int idpCount, String deviceHistory) {
        for (int i = 0; i < idpCount; i++) {
            try {
                String lastActiveDateStr = readField(deviceHistory, "$[" + i + "].lastActiveDate");
                Date lastActiveDate = DATE_FORMAT.parse(lastActiveDateStr);

                assertTrue(startDate.compareTo(lastActiveDate) <= 0);
                assertTrue(endDate.compareTo(lastActiveDate) >= 0);
            } catch (Exception e) {
                throw new RuntimeException("Could not parse date in " + deviceHistory, e);
            }
        }
    }

    private void removeIdpForDevice(String localId, String publisherToken, Long idpId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", publisherToken);

        String addIdpResponse =
                request
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .method(Method.DELETE)
                        .url("/1/device/" + localId + "/history/idp/" + idpId)
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        assertTrue(addIdpResponse.isEmpty());
    }
}

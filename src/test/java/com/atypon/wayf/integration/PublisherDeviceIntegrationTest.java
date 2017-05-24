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
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Test
    public void runIntegration() {
        // Create 2 Publishers
        String publisherAToken = testCreatePublisher(CREATE_PUBLISHER_A_REQUEST_JSON, CREATE_PUBLISHER_A_RESPONSE_JSON);
        String publisherBToken = testCreatePublisher(CREATE_PUBLISHER_B_REQUEST_JSON, CREATE_PUBLISHER_B_RESPONSE_JSON);

        String publisherALocalId = "local-id-publisher-a";

        // Create device
        String globalId = relateDeviceToPublisher(publisherALocalId, publisherAToken, RELATE_NEW_DEVICE_PUBLISHER_A_RESPONSE_JSON);

        // Assert an empty history
        testDeviceHistory(publisherALocalId, publisherAToken, NEW_DEVICE_HISTORY_RESPONSE_JSON);

        // Add the IDPs to the device multiple times and validate the IDP's ID is the same each time
        testAddIdpToDeviceAndIdpResolution(5, publisherALocalId, publisherAToken, CREATE_SAML_IDP_REQUEST_JSON, CREATE_SAML_IDP_RESPONSE_JSON);
        testAddIdpToDeviceAndIdpResolution(4, publisherALocalId, publisherAToken, CREATE_OPEN_ATHENS_IDP_REQUEST_JSON, CREATE_OPEN_ATHENS_IDP_RESPONSE_JSON);
        testAddIdpToDeviceAndIdpResolution(3, publisherALocalId, publisherAToken, CREATE_OAUTH_IDP_REQUEST_JSON, CREATE_OAUTH_IDP_RESPONSE_JSON);

        testDeviceHistory(publisherALocalId, publisherAToken, NEW_DEVICE_HISTORY_RESPONSE_JSON);


        // POST an IDP assert correct history
        // POST more IDPS
        // Asset correct staits via GET
        // Create new local ID for other publisher
        // assert existing correct stats
        //  Delete IDP for new publihser
        // Assert history is correct on both publishers
    }

    private String testCreatePublisher(String request, String response) {
        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .post("/1/publisher")
                        .then()
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

    private Long testCreateIdp(String request, String expectedResponseJson) {
        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .post("/1/identityProvider")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);

        Long id = Long.valueOf(readField(createResponse, "$.id"));

        assertJsonEquals(expectedResponseJson, createResponse, createResponseGeneratedFields);

        return id;
    }

    private String relateDeviceToPublisher(String localId, String publisherToken, String expectedResponseJson) {
        ExtractableResponse relateResponse =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", publisherToken)
                        .header("User-Agent", "Test-Agent")
                        .patch("/1/device/" + localId)
                        .then()
                        .statusCode(200)
                        .extract();

        String deviceIdHeader = relateResponse.header("deviceId");
        assertNotNull(deviceIdHeader);

        String deviceBody = relateResponse.response().body().asString();

        String[] relateResponseGeneratedFields = {
                "$.id",
                "$.createdDate"
        };

        assertJsonEquals(expectedResponseJson, deviceBody, relateResponseGeneratedFields);

        return deviceIdHeader;
    }

    private void testDeviceHistory(String localId, String publisherToken, String expectedHistoryJson) {
        String historyResponse =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", publisherToken)
                        .get("/1/device/" + localId + "/history")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        assertJsonEquals(expectedHistoryJson, historyResponse, null);
    }

    private Long addIdpToDevice(String localId, String publisherToken, String idpBodyJson, String expectedResponseJson) {
        String addIdpResponse =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", publisherToken)
                        .body(idpBodyJson)
                        .post("/1/device/" + localId + "/history/idp")
                        .then()
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

    private void testAddIdpToDeviceAndIdpResolution(int count, String localId, String publisherToken, String requestJson, String expectedResponseJson) {
        Long[] ids = new Long[count];

        for (int i = 0; i < count; i++) {
            ids[i] = addIdpToDevice(localId, publisherToken, requestJson, expectedResponseJson);
        }

        for (int i = 0; i < count - 1; i++) {
            assertEquals(ids[i], ids[i+1]);
        }
    }
}

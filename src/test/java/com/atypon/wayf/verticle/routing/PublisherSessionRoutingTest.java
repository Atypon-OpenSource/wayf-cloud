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

package com.atypon.wayf.verticle.routing;

import io.restassured.http.ContentType;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PublisherSessionRoutingTest extends BaseHttpTest {
    private static final String ID_FIELD = "$.id";
    private static final String LOCAL_ID_FIELD = "$.localId";

    @Test
    public void testCreateSessionForNewDevice() throws Exception {
        String requestJsonString = getFileAsString("json_files/publisher_session/create_request.json");
        String uniqueLocalId  = "local-id-" + UUID.randomUUID().toString();

        requestJsonString = setField(requestJsonString, LOCAL_ID_FIELD, uniqueLocalId);

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .post("/1/publisherSession")
                .then()
                    .statusCode(200)
                    .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.device.id",
                "$.device"
        };

        // Validate that the server generated fields
        assertNotNullPaths(createResponse, createResponseGeneratedFields);


        // Compare the JSON to the payload on record
        assertJsonEquals(requestJsonString, createResponse, createResponseGeneratedFields);
    }

    @Test
    public void testCreateSessionForExistingDevice() throws Exception {
        String deviceRequest = getFileAsString("json_files/publisher_session/device.json");

        String deviceResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(deviceRequest)
                        .post("/1/device")
                .then()
                        .statusCode(200)
                        .extract().response().asString();

        String deviceId = readField(deviceResponse, ID_FIELD);

        assertNotNull(deviceId);

        String publisherSessionRequest = getFileAsString("json_files/publisher_session/create_request.json");
        String uniqueLocalId  = "local-id-" + UUID.randomUUID().toString();

        publisherSessionRequest = setField(publisherSessionRequest, LOCAL_ID_FIELD, uniqueLocalId);

        String createResponse =
                given()
                        .header("deviceId", deviceId)
                        .contentType(ContentType.JSON)
                        .body(publisherSessionRequest)
                        .post("/1/publisherSession")
                .then()
                        .statusCode(200)
                        .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.device.id",
                "$.device"
        };

        // Validate that the server generated fields
        assertNotNullPaths(createResponse, createResponseGeneratedFields);


        // Validate that the device ID on the session was the one passed in via the header
        assertEquals(deviceId, readField(createResponse, "$.device.id"));

        // Compare the JSON to the payload on record
        assertJsonEquals(publisherSessionRequest, createResponse, createResponseGeneratedFields);
    }

    @Test
    public void testReadByLocalId() throws Exception {
        String requestJsonString = getFileAsString("json_files/publisher_session/create_request.json");
        String uniqueLocalId  = "local-id-" + UUID.randomUUID().toString();

        requestJsonString = setField(requestJsonString, LOCAL_ID_FIELD, uniqueLocalId);

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .post("/1/publisherSession")
                .then()
                        .statusCode(200)
                        .extract().response().asString();

        String id = readField(createResponse, ID_FIELD);

        // Assert that we were assigned an ID for our local id
        assertNotNull(id);
        assertEquals(uniqueLocalId, readField(createResponse, LOCAL_ID_FIELD));

        String readByLocalIdResponse =
                given()
                        .urlEncodingEnabled(false)
                        .get("/1/publisherSession/localId=" + uniqueLocalId)
                .then()
                        .statusCode(200)
                        .extract().response().asString();

        // Ensure the IDs are correct
        assertEquals(id, readField(readByLocalIdResponse, ID_FIELD));

        assertEquals(uniqueLocalId, readField(readByLocalIdResponse,LOCAL_ID_FIELD));

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.device.id",
                "$.device"
        };
        // Ensure server generated fields come back
        assertNotNullPaths(readByLocalIdResponse, createResponseGeneratedFields);

        // Compare the JSON to the payload on record
        assertJsonEquals(requestJsonString, readByLocalIdResponse, createResponseGeneratedFields);
    }

    @Test
    public void testReadById() throws Exception {
        String requestJsonString = getFileAsString("json_files/publisher_session/create_request.json");
        String uniqueLocalId  = "local-id-" + UUID.randomUUID().toString();

        requestJsonString = setField(requestJsonString, LOCAL_ID_FIELD, uniqueLocalId);

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .post("/1/publisherSession")
                .then()
                        .statusCode(200)
                        .extract().response().asString();


        String id = readField(createResponse, ID_FIELD);

        assertNotNull(id);

        String readByIdResponse =
                given()
                        .urlEncodingEnabled(false)
                        .get("/1/publisherSession/" + id)
                 .then()
                        .statusCode(200)
                        .extract().response().asString();

        // Ensure the server gave us the ID we requested
        assertEquals(id, readField(readByIdResponse, ID_FIELD));


        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.device.id",
                "$.device"
        };

        String[] compareJsonBlacklist = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.device.id",
                "$.device"
        };

        // Ensure server generated fields come back
        assertNotNullPaths(readByIdResponse, createResponseGeneratedFields);

        // Compare the JSON to the payload on record
        assertJsonEquals(requestJsonString, readByIdResponse, createResponseGeneratedFields);
        assertJsonEquals(requestJsonString, readByIdResponse, compareJsonBlacklist);
    }

    @Test
    public void testReadByIdWithFields() throws Exception {
        String publisherRequest = getFileAsString("json_files/publisher_session/publisher.json");

        String publisherResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(publisherRequest)
                        .post("/1/publisher")
                .then()
                        .statusCode(200)
                        .extract().response().asString();

        String publisherId = readField(publisherResponse, ID_FIELD);
        assertNotNull(publisherId);

        String identityProviderRequest = getFileAsString("json_files/publisher_session/identity_provider.json");
        String randomEntityId = "test-entity-" + UUID.randomUUID().toString();

        identityProviderRequest = setField(identityProviderRequest, "$.entityId", randomEntityId);

        String createIdentityProviderResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(identityProviderRequest)
                        .post("/1/identityProvider")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String identityProviderId = readField(createIdentityProviderResponse, ID_FIELD);

        String requestJsonString = getFileAsString("json_files/publisher_session/create_with_fields.json");
        String uniqueLocalId  = "local-id-" + UUID.randomUUID().toString();

        requestJsonString = setField(requestJsonString, LOCAL_ID_FIELD, uniqueLocalId);

        requestJsonString = setField(requestJsonString, "$.authenticatedBy.id", identityProviderId);

        requestJsonString = setField(requestJsonString, "$.publisher.id", publisherId);

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .post("/1/publisherSession")
                .then()
                        .statusCode(200)
                        .extract().response().asString();


        String id = readField(createResponse, ID_FIELD);

        assertNotNull(id);

        String readByIdWithFieldsResponse =
                given()
                        .urlEncodingEnabled(false)
                        .queryParam("fields", "device,publisher,authenticatedBy")
                        .get("/1/publisherSession/" + id)
                .then()
                        .statusCode(200)
                        .extract().response().asString();

        // Ensure the server gave us the ID we requested
        assertEquals(id, readField(readByIdWithFieldsResponse, ID_FIELD));

        // Get the publisher from the response and verify that it matches the one that was created
        String publisherOnSession = readField(readByIdWithFieldsResponse, "$.publisher");
        assertJsonEquals(publisherResponse, publisherOnSession, null);

        String deviceId = readField(readByIdWithFieldsResponse, "$.device.id");
        String deviceOnResponse = readField(readByIdWithFieldsResponse, "$.device");

        String deviceResponse =
                given()
                    .get("/1/device/" + deviceId)
                .then()
                    .statusCode(200)
                    .extract().response().asString();

        // Make sure the device is the same as if it was read via it's own service
        assertJsonEquals(deviceResponse, deviceOnResponse);


        String identityProvider = readField(readByIdWithFieldsResponse, "$.authenticatedBy");
        assertJsonEquals(createIdentityProviderResponse, identityProvider);


        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.device.id",
                "$.device"
        };

        String[] compareJsonBlacklist = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.publisher",
                "$.device",
                "$.authenticatedBy"
        };
        // Ensure server generated fields come back
        assertNotNullPaths(readByIdWithFieldsResponse, createResponseGeneratedFields);

        // Compare the JSON to the payload on record
        assertJsonEquals(requestJsonString, readByIdWithFieldsResponse, compareJsonBlacklist);
    }

    @Test
    public void testAddIdp() {
        String requestJsonString = getFileAsString("json_files/publisher_session/create_request.json");
        // Generate a random localId
        String uniqueLocalId = "local-id-" + UUID.randomUUID().toString();

        // Update the local ID to our randomly generated one
        requestJsonString= setField(requestJsonString, LOCAL_ID_FIELD, uniqueLocalId);

        String createSessionResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .post("/1/publisherSession")
                 .then()
                        .statusCode(200)
                        .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate",
                "$.lastActiveDate",
                "$.device.id",
                "$.device"
        };

        // Validate that the server generated fields
        assertNotNullPaths(createSessionResponse, createResponseGeneratedFields);

        String localId = readField(createSessionResponse, LOCAL_ID_FIELD);
        assertEquals(uniqueLocalId, localId);

        String identityProviderRequest = getFileAsString("json_files/publisher_session/identity_provider.json");
        String randomEntityId = "test-entity-" + UUID.randomUUID().toString();

        String identityProviderRequestRandomEntityId = setField(identityProviderRequest, "$.entityId", randomEntityId);

        String createIdentityProviderResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(identityProviderRequestRandomEntityId)
                .post("/1/identityProvider")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String idpId = readField(createIdentityProviderResponse, ID_FIELD);
        assertNotNull(idpId);

        String entityId = readField(createIdentityProviderResponse, "$.entityId");
        assertEquals(randomEntityId, entityId);

        String addIdentityProviderRequest = getFileAsString("json_files/publisher_session/add_identity_provider.json");

        addIdentityProviderRequest = setField(addIdentityProviderRequest, LOCAL_ID_FIELD, uniqueLocalId);

        addIdentityProviderRequest= setField(addIdentityProviderRequest, "$.entityId", randomEntityId);

        given()
                .contentType(ContentType.JSON)
                .urlEncodingEnabled(false)
                .body(addIdentityProviderRequest)
                .put("/1/publisherSession/localId=" + localId + "/authenticatedBy")
         .then()
                .statusCode(200);

        String readByLocalIdResponse =
                given()
                        .urlEncodingEnabled(false)
                        .queryParam("fields", "identityProvider")
                        .get("/1/publisherSession/localId=" + uniqueLocalId)
                .then()
                        .statusCode(200)
                        .extract().response().asString();

        String actualAuthenticatedById = readField(readByLocalIdResponse, "$.authenticatedBy.id");


        assertEquals(idpId, actualAuthenticatedById);
    }

    @Test
    public void testFilter() throws Exception {
        String deviceRequest = getFileAsString("json_files/publisher_session/device.json");

        String deviceResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(deviceRequest)
                        .post("/1/device")
                 .then()
                        .statusCode(200)
                        .extract().response().asString();

        String deviceId = readField(deviceResponse, ID_FIELD);

        assertNotNull(deviceId);

        String publisherSessionRequest1 = getFileAsString("json_files/publisher_session/create_request_1.json");
        String uniqueLocalId = "local-id-" + UUID.randomUUID().toString();

        publisherSessionRequest1 = setField(publisherSessionRequest1, LOCAL_ID_FIELD, uniqueLocalId);

        String createResponse1 =
                given()
                        .header("deviceId", deviceId)
                        .contentType(ContentType.JSON)
                        .body(publisherSessionRequest1)
                        .post("/1/publisherSession")
                .then()
                        .statusCode(200)
                        .extract().response().asString();


        // Validate that the device ID on the session was the one passed in via the header
        assertEquals(deviceId, readField(createResponse1, "$.device.id"));

        String publisherSessionRequest2 = getFileAsString("json_files/publisher_session/create_request_2.json");
        String uniqueLocalId2 = "local-id-" + UUID.randomUUID().toString();

        publisherSessionRequest2 = setField(publisherSessionRequest2, LOCAL_ID_FIELD, uniqueLocalId2);

        String createResponse2 =
                given()
                        .header("deviceId", deviceId)
                        .contentType(ContentType.JSON)
                        .body(publisherSessionRequest2)
                        .post("/1/publisherSession")
                .then()
                        .statusCode(200)
                        .extract().response().asString();


        // Validate that the device ID on the session was the one passed in via the header
        assertEquals(deviceId, readField(createResponse2, "$.device.id"));

        String filterResponse = getFileAsString("json_files/publisher_session/filter_response.json");

        String actualFilterResponse =
                given()
                        .urlEncodingEnabled(false)
                        .queryParam("device.id", deviceId)
                        .get("/1/publisherSessions")
                .then()
                        .statusCode(200)
                        .extract().response().asString();


        String[] compareJsonBlacklist = {
                "$[*].id",
                "$[*].createdDate",
                "$[*].modifiedDate",
                "$[*].lastActiveDate",
                "$[*].localId",
                "$[*].device.id",
                "$[*].device"
        };

        // Compare the JSON to the payload on record
        assertJsonEquals(filterResponse, actualFilterResponse, compareJsonBlacklist);
    }

}

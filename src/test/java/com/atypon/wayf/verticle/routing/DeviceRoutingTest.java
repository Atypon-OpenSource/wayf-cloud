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
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.given;

@Ignore
public class DeviceRoutingTest extends BaseHttpTest {

    private static final String ID_FIELD = "$.id";

    private static String[] SERVER_GENERATED_FIELDS = {
            ID_FIELD,
            "$.createdDate",
    };

    private static String[] SERVER_GENERATED_FIELDS_LIST = {
            "$[*].id",
            "$[*].createdDate",
    };

    @Test
    public void testCreate() {
        String requestJsonString = getFileAsString("json_files/device/create.json");

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .header("User-Agent", "Test")
                        .post("/1/device")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        // Validate that the server generated fields
        assertNotNullPaths(createResponse, SERVER_GENERATED_FIELDS);

        String responseJsonString = getFileAsString("json_files/device/create_response.json");

        // Compare the JSON to the payload on record
        assertJsonEquals(responseJsonString, createResponse, ArrayUtils.addAll(SERVER_GENERATED_FIELDS));
    }

    @Test
    public void testRead() {
        String requestJsonString = getFileAsString("json_files/device/create.json");

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .header("User-Agent", "Test")
                        .post("/1/device")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        // Validate that the server generated fields
        assertNotNullPaths(createResponse, SERVER_GENERATED_FIELDS);

        String id = readField(createResponse, ID_FIELD);

        String readResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .get("/1/device/" + id)
                        .then()
                        .statusCode(200)
                        .extract().response().asString();


        String responseJsonString = getFileAsString("json_files/device/create_response.json");

        // Compare the JSON to the payload on record
        assertJsonEquals(readResponse, createResponse, ArrayUtils.addAll(SERVER_GENERATED_FIELDS));
    }

    @Test
    public void testFilter() {
        String requestJsonString = getFileAsString("json_files/device/create.json");

        String createResponse1 =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .header("User-Agent", "Test")
                        .post("/1/device")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        // Validate that the server generated fields
        assertNotNullPaths(createResponse1, SERVER_GENERATED_FIELDS);

        String createResponse2 =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestJsonString)
                        .header("User-Agent", "Test")
                        .post("/1/device")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        // Validate that the server generated fields
        assertNotNullPaths(createResponse1, SERVER_GENERATED_FIELDS);

        String id1 = readField(createResponse1, ID_FIELD);
        String id2 = readField(createResponse2, ID_FIELD);

        String filterResponse =
                given()
                        .contentType(ContentType.JSON)
                        .urlEncodingEnabled(false)
                        .queryParam("ids", id1+","+id2)
                        .get("/1/devices")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        assertJsonEquals(getFileAsString("json_files/device/filter_response.json"), filterResponse, SERVER_GENERATED_FIELDS_LIST);
    }
}

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
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class PublisherRoutingTest extends BaseHttpTest {

    private static final String ID = "$.id";

    @Test
    public void testCreateAndRead() {
        String createRequestJson = getFileAsString("json_files/publisher/create_request.json");

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(createRequestJson)
                        .post("/1/publisher")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.status",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);

        String expectedCreateResponse = getFileAsString("json_files/publisher/create_response.json");

        assertJsonEquals(expectedCreateResponse, createResponse, createResponseGeneratedFields);

        String generatedId = readField(createResponse, ID);
        String expectedReadResponse = expectedCreateResponse;

        String readResponse =
                given()
                        .contentType(ContentType.JSON)
                        .get("/1/publisher/" + generatedId)
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        assertNotNullPaths(readResponse, createResponseGeneratedFields);

        assertJsonEquals(expectedReadResponse, readResponse, createResponseGeneratedFields);
    }


    @Test
    public void testFilter() {
        String createRequestJson = getFileAsString("json_files/publisher/create_request.json");

        String createResponse1 =
                given()
                        .contentType(ContentType.JSON)
                        .body(createRequestJson)
                        .post("/1/publisher")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String generatedId1 = readField(createResponse1, ID);

        String createResponse2 =
                given()
                        .contentType(ContentType.JSON)
                        .body(createRequestJson)
                        .post("/1/publisher")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String generatedId2 = readField(createResponse2, ID);

        String filterResponse =
                given()
                        .contentType(ContentType.JSON)
                        .urlEncodingEnabled(false)
                        .queryParam("ids", generatedId1 + "," + generatedId2)
                        .get("/1/publishers")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String[] filterResponseGeneratedFields = {
                "$[*].id",
                "$[*].status",
                "$[*].createdDate"
        };

        String expectedFilterResponse = getFileAsString("json_files/publisher/filter_response.json");

        assertNotNullPaths(filterResponse, filterResponseGeneratedFields);

        assertJsonEquals(expectedFilterResponse, filterResponse, filterResponseGeneratedFields);
    }
}

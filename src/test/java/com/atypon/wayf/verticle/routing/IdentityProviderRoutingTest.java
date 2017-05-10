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

public class IdentityProviderRoutingTest extends BaseHttpTest {

    private static final String ID = "$.id";
    @Test
    public void testCreateAndRead() {
        String createRequestJson = getFileAsString("json_files/identity_provider/create_request.json");

        String createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(createRequestJson)
                        .post("/1/identityProvider")
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);

        String expectedCreateResponse = createRequestJson;
        assertJsonEquals(expectedCreateResponse, createResponse, createResponseGeneratedFields);

        String generatedId = readField(createResponse, ID);

        String readResponse =
                given()
                        .contentType(ContentType.JSON)
                        .get("/1/identityProvider/" + generatedId)
                        .then()
                        .statusCode(200)
                        .extract().response().asString();

        String expectedReadResponse = expectedCreateResponse;

        assertNotNullPaths(readResponse, createResponseGeneratedFields);
        assertJsonEquals(expectedReadResponse, readResponse, createResponseGeneratedFields);
    }
}

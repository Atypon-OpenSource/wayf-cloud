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

import com.atypon.wayf.verticle.routing.LoggingHttpRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import static com.atypon.wayf.integration.HttpTestUtil.assertJsonEquals;
import static com.atypon.wayf.integration.HttpTestUtil.assertNotNullPaths;
import static com.atypon.wayf.integration.HttpTestUtil.setField;
import static org.junit.Assert.assertNotNull;

public class UserTestUtil {
    private LoggingHttpRequest request;

    public UserTestUtil(LoggingHttpRequest request) {
        this.request = request;
    }

    public void testCreateUser(String credentialsEmail, String requestJson, String expectedResponseJson) {
        requestJson = setField(requestJson, "$.passwordCredentials.emailAddress", credentialsEmail);

        String createResponse =
                request
                        .contentType(ContentType.JSON)
                        .method(Method.POST)
                        .body(requestJson)
                        .url("/1/user")
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);
        assertJsonEquals(expectedResponseJson, createResponse, createResponseGeneratedFields);
    }

    public void testLogin(String credentialsEmail, String requestJson) {
        requestJson = setField(requestJson, "$.emailAddress", credentialsEmail);

        String adminToken =
                request
                        .contentType(ContentType.JSON)
                        .method(Method.PATCH)
                        .body(requestJson)
                        .url("/1/user/credentials")
                        .execute()
                        .statusCode(200)
                        .extract().cookie("adminToken");



        assertNotNull(adminToken);
    }
}

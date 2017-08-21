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

import java.util.HashMap;
import java.util.Map;

import static com.atypon.wayf.integration.HttpTestUtil.*;
import static org.junit.Assert.assertNotNull;

public class UserTestUtil {
    private LoggingHttpRequest request;

    public UserTestUtil(LoggingHttpRequest request) {
        this.request = request;
    }

    public void testCreateUserNoToken(String credentialsEmail, String requestJson, String expectedResponseJson) {
        requestJson = setField(requestJson, "$.passwordCredentials.emailAddress", credentialsEmail);

        String errorResponse =
                request
                        .contentType(ContentType.JSON)
                        .method(Method.POST)
                        .body(requestJson)
                        .url("/1/user")
                        .execute()
                        .statusCode(401)
                        .extract().response().asString();

        String[] errorResponseGeneratedFields = {
                "$.stacktrace"
        };

        assertJsonEquals(expectedResponseJson, errorResponse, errorResponseGeneratedFields);
    }

    public Long testCreateUser(String credentialsEmail, String requestJson, String expectedResponseJson) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", AuthorizationTokenTestUtil.generateDefaultApiTokenHeaderValue());

        requestJson = setField(requestJson, "$.passwordCredentials.emailAddress", credentialsEmail);

        String createResponse =
                request
                        .contentType(ContentType.JSON)
                        .method(Method.POST)
                        .headers(headers)
                        .body(requestJson)
                        .url("/1/user")
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        Long userId = Long.valueOf(readField(createResponse, "$.id"));

        String[] createResponseGeneratedFields = {
                "$.id",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);
        assertJsonEquals(expectedResponseJson, createResponse, createResponseGeneratedFields);

        return userId;
    }

    public String testLogin(String credentialsEmail, String requestJson) {
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

        return adminToken;
    }

    public void deleteUser(Long userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", AuthorizationTokenTestUtil.generateDefaultApiTokenHeaderValue());

        request
                .contentType(ContentType.JSON)
                .method(Method.DELETE)
                .headers(headers)
                .url("/1/user/" + userId)
                .execute()
                .statusCode(200).extract().response();

    }

    public void readDeletedUser(Long userId, String expectedResponse) {
        String response =
                request
                        .contentType(ContentType.JSON)
                        .method(Method.GET)
                        .url("/1/user/" + userId)
                        .execute()
                        .statusCode(404)
                        .extract().response().asString();

        String[] responseGeneratedFields = {
                "$.message",
                "$.stacktrace"
        };

        assertJsonEquals(expectedResponse, response, responseGeneratedFields);

    }
}

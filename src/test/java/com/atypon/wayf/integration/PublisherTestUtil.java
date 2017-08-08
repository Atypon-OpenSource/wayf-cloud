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

import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.data.AuthorizationTokenType;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.verticle.routing.LoggingHttpRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import java.util.List;

import static com.atypon.wayf.integration.HttpTestUtil.*;

public class PublisherTestUtil {

    private LoggingHttpRequest request;

    public PublisherTestUtil(LoggingHttpRequest request) {
        this.request = request;
    }

    public void testReadPublisher(Long publisherId, String expectedResponseJson) {
        String readResponse =
                request
                        .contentType(ContentType.JSON)
                        .method(Method.GET)
                        .url("/1/publisher/" + publisherId)
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        String[] readResponseGeneratedFields = {
                "$.id",
                "$.code",
                "$.contact.id",
                "$.createdDate"
        };

        assertNotNullPaths(readResponse, readResponseGeneratedFields);
        assertJsonEquals(expectedResponseJson, readResponse, readResponseGeneratedFields);
    }

    public void testReadPublishers(List<Long> publisherIds, String expectedResponseJson) {
        StringBuilder idsBuilder = new StringBuilder();

        for (Long publisherId : publisherIds) {
            idsBuilder.append(publisherId);
            idsBuilder.append(",");
        }

        idsBuilder.setLength(idsBuilder.length() - 1);

        String readResponse =
                request
                        .contentType(ContentType.JSON)
                        .method(Method.GET)
                        .url("/1/publishers?ids=" + idsBuilder.toString())
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        String[] readResponseGeneratedFields = {
                "$[*].id",
                "$[*].code",
                "$[*].contact.id",
                "$[*].createdDate"
        };

        assertNotNullPaths(readResponse, readResponseGeneratedFields);
        assertJsonEquals(expectedResponseJson, readResponse, readResponseGeneratedFields);
    }

    public Publisher testCreatePublisher(String requestBody, String response) {
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
                "$.code",
                "$.authorizationToken.value",
                "$.contact.id",
                "$.contact.createdDate",
                "$.widgetLocation",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);

        Long id = Long.valueOf(readField(createResponse, "$.id"));
        String authorizationTokenType = readField(createResponse, "$.authorizationToken.type");
        String authorizationTokenValue = readField(createResponse, "$.authorizationToken.value");
        String code = readField(createResponse, "$.code");

        assertJsonEquals(response, createResponse, createResponseGeneratedFields);

        Publisher publisher = new Publisher();
        publisher.setId(id);
        publisher.setCode(code);

        AuthorizationToken token = new AuthorizationToken();
        token.setType(AuthorizationTokenType.valueOf(authorizationTokenType));
        token.setValue(authorizationTokenValue);
        publisher.setAuthorizationToken(token);

        return publisher;
    }
}

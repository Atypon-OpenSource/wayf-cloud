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

import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.verticle.routing.LoggingHttpRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import static com.atypon.wayf.integration.HttpTestUtil.*;

public class PublisherTestUtil {

    private LoggingHttpRequest request;

    public PublisherTestUtil(LoggingHttpRequest request) {
        this.request = request;
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
                "$.token",
                "$.createdDate"
        };

        assertNotNullPaths(createResponse, createResponseGeneratedFields);

        String token = readField(createResponse, "$.token");
        String code = readField(createResponse, "$.code");

        assertJsonEquals(response, createResponse, createResponseGeneratedFields);

        Publisher publisher = new Publisher();
        publisher.setCode(code);
        publisher.setToken(token);

        return publisher;
    }
}

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
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;

import java.util.HashMap;
import java.util.Map;

import static com.atypon.wayf.integration.HttpTestUtil.assertJsonEquals;
import static org.junit.Assert.assertNotNull;

public class DeviceTestUtil {
    private LoggingHttpRequest request;

    public DeviceTestUtil(LoggingHttpRequest request) {
        this.request = request;
    }

    public String relateDeviceToPublisher(String localId, String publisherToken, String globalId, String expectedResponseJson) {
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

}

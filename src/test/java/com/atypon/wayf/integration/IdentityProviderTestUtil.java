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

import static com.atypon.wayf.integration.HttpTestUtil.assertJsonEquals;
import static com.atypon.wayf.integration.HttpTestUtil.readField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IdentityProviderTestUtil {

    private LoggingHttpRequest request;

    public IdentityProviderTestUtil(LoggingHttpRequest request) {
        this.request = request;
    }

    public Long addIdpToDevice(String localId, String publisherToken, String idpBodyJson, String expectedResponseJson) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", publisherToken);

        String addIdpResponse =
                request
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .body(idpBodyJson)
                        .method(Method.POST)
                        .url("/1/device/" + localId + "/history/idp")
                        .execute()
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

    public Long testAddIdpToDeviceAndIdpResolution(int count, String localId, String publisherToken, String requestJson, String expectedResponseJson) {
        Long[] ids = new Long[count];

        for (int i = 0; i < count; i++) {
            ids[i] = addIdpToDevice(localId, publisherToken, requestJson, expectedResponseJson);
        }

        for (int i = 0; i < count - 1; i++) {
            assertEquals(ids[i], ids[i+1]);
        }

        return ids[0];
    }

    public void removeIdpForDevice(String localId, String publisherToken, Long idpId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", publisherToken);

        String addIdpResponse =
                request
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .method(Method.DELETE)
                        .url("/1/device/" + localId + "/history/idp/" + idpId)
                        .execute()
                        .statusCode(200)
                        .extract().response().asString();

        assertTrue(addIdpResponse.isEmpty());
    }
}

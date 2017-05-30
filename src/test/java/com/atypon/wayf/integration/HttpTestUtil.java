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

import com.atypon.wayf.verticle.routing.BaseHttpTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.matchers.JsonPathMatchers;
import org.hamcrest.core.IsNull;

import java.io.InputStreamReader;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HttpTestUtil {

    public static Predicate ALWAYS_TRUE_PREDICATE = (arg) -> true;


    protected static String getFileAsString(String path) {
        try {
            return CharStreams.toString(new InputStreamReader(BaseHttpTest.class.getClassLoader().getResourceAsStream(path), Charsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String readField(String json, String field) {
        Object value = JsonPath.read(json, field, ALWAYS_TRUE_PREDICATE);

        if (value == null) {
            return null;
        }

        if (Map.class.isAssignableFrom(value.getClass())) {
            return JsonPath.parse(value).jsonString();
        } else {
            return String.valueOf(value);
        }
    }

    public static String setField(String json, String field, String value) {
        return JsonPath.parse(json).set(field, value, ALWAYS_TRUE_PREDICATE).jsonString();
    }

    public static void assertNotNullPaths(String json, String... fields) {
        for (String field : fields) {
            assertThat(json, JsonPathMatchers.hasJsonPath(field, IsNull.notNullValue()));
        }
    }

    public static void removeFields(DocumentContext document, String... fields) {
        for (String field : fields) {
            try {
                document.delete(field, ALWAYS_TRUE_PREDICATE);
            } catch (PathNotFoundException e) {
                // ignore
            }
        }
    }

    public static void assertJsonEquals(String expected, String actual, String... blacklistedFields) {
        DocumentContext expectedDocument = JsonPath.parse(expected);
        DocumentContext actualDocument = JsonPath.parse(actual);

        if (blacklistedFields != null) {
            removeFields(expectedDocument, blacklistedFields);
            removeFields(actualDocument, blacklistedFields);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode expectedNode = mapper.readTree(expectedDocument.jsonString());
            JsonNode actualNode = mapper.readTree(actualDocument.jsonString());

            assertEquals(expectedNode, actualNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

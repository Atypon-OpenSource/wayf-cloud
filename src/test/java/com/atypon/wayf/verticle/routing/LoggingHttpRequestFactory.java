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
import io.restassured.http.Cookie;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LoggingHttpRequestFactory {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String OUTPUT_FOLDER = "target/test_output/";
    private static final String FILE_EXTENSION = ".log";

    private String fileName;

    public LoggingHttpRequestFactory(String testName) {
        fileName = OUTPUT_FOLDER + testName + FILE_EXTENSION;
        createTestFile();
    }

    public LoggingHttpRequest request() {
        return new LoggingHttpRequest(fileName);
    }

    public class LoggingHttpRequest {
        private Method method;
        private String url;
        private Map<String, String> headers;
        private Cookie cookie;
        private String body;
        private ContentType contentType;
        private String fileName;

        LoggingHttpRequest(String fileName) {
            this.fileName = fileName;
        }

        public LoggingHttpRequest contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public LoggingHttpRequest method(Method method) {
            this.method = method;
            return this;
        }

        public LoggingHttpRequest url(String url) {
            this.url = url;
            return this;
        }

        public LoggingHttpRequest headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public LoggingHttpRequest cookie(Cookie cookie) {
            this.cookie = cookie;
            return this;
        }

        public LoggingHttpRequest body(String body) {
            this.body = body;
            return this;
        }

        public ValidatableResponse execute() {
            RequestSpecification request = given();
            if (contentType != null) {
                request.contentType(contentType);
            }

            if (headers != null) {
                request.headers(headers);
            }

            if (cookie != null) {
                request.cookie(cookie);
            }

            if (body != null) {
                request.body(body);
            }

            String requestStr = createRequestLog();

            ValidatableResponse response = request
                    .urlEncodingEnabled(false)
                    .request(method, url)
                    .then();

            String responseStr = createResponseLog(response);

            appendToFile(requestStr, responseStr);

            return response;
        }

        private void appendToFile(String request, String response) {
            try (Writer writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.append(request);
                writer.append(NEW_LINE);
                writer.append(response);
                writer.append(NEW_LINE);
                writer.append(NEW_LINE);
                writer.append(NEW_LINE);

                writer.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String createRequestLog() {
            StringBuilder builder = new StringBuilder();
            builder.append(method);
            builder.append(" - ");
            builder.append(url);
            builder.append(" - Headers: ");
            builder.append(headers == null? "{}" : headers);
            builder.append(NEW_LINE);
            builder.append(body);

            return builder.toString();
        }

        private String createResponseLog(ValidatableResponse response) {
            ExtractableResponse extractableResponse = response.extract();

            StringBuilder builder = new StringBuilder();
            builder.append("RESPONSE: ");
            builder.append(extractableResponse.statusCode());
            builder.append(" - Headers: ");

            Headers headers = extractableResponse.headers();
            Map<String, String> headerMap = new HashMap<>();

            if (headers != null) {
                headers.forEach((header) -> headerMap.put(header.getName(), header.getValue()));
            }

            builder.append(headerMap);
            builder.append(NEW_LINE);
            builder.append(extractableResponse.body().asString());
            return builder.toString();
        }

    }

    private void createTestFile() {
        File directory = new File(OUTPUT_FOLDER);
        File file = new File(fileName);
        try {
            directory.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
            writer.write("");
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

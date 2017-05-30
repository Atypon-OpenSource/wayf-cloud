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

import com.atypon.wayf.integration.DeviceAccessTestUtil;
import com.atypon.wayf.integration.DeviceTestUtil;
import com.atypon.wayf.integration.IdentityProviderTestUtil;
import com.atypon.wayf.integration.PublisherTestUtil;
import com.atypon.wayf.request.ResponseWriter;
import com.atypon.wayf.verticle.WayfVerticle;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.matchers.JsonPathMatchers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.hamcrest.core.IsNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(VertxUnitRunner.class)
public abstract class BaseHttpTest {
    private static final Logger LOG = LoggerFactory.getLogger(BaseHttpTest.class);

    private static Vertx vertx;
    private static Integer port;

    public static final DateFormat DATE_FORMAT = ResponseWriter.DATE_FORMAT;

    protected PublisherTestUtil publisherTestUtil;
    protected DeviceTestUtil deviceTestUtil;
    protected DeviceAccessTestUtil deviceAccessTestUtil;
    protected IdentityProviderTestUtil identityProviderTestUtil;

    public BaseHttpTest(String httpLoggingFilename) {
        LoggingHttpRequest request = new LoggingHttpRequest(httpLoggingFilename);

        publisherTestUtil = new PublisherTestUtil(request);
        deviceTestUtil = new DeviceTestUtil(request);
        deviceAccessTestUtil = new DeviceAccessTestUtil(request);
        identityProviderTestUtil = new IdentityProviderTestUtil(request);
    }

    @BeforeClass
    public static void setUpClass(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

        vertx.deployVerticle(WayfVerticle.class.getName(), options, context.asyncAssertSuccess());

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @AfterClass
    public static void afterClass() {
        RestAssured.reset();
        vertx.close();
    }


    protected static String getFileAsString(String path) {
        LOG.debug("Loading file: {}", path);
        try {
            return CharStreams.toString(new InputStreamReader(BaseHttpTest.class.getClassLoader().getResourceAsStream(path), Charsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

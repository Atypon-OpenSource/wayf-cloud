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

import com.atypon.wayf.data.Institution;
import com.atypon.wayf.verticle.WayfVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class InstitutionRoutingTest {

    private Vertx vertx;
    private Integer port;

    private final String institutionName = "Test Institution";
    private final String institutionDescription = "A thorough description of the test Institution";

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );

        vertx.deployVerticle(WayfVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testInstitutionCreate(TestContext context) {
        final Async async = context.async();

        Institution institution = new Institution();
        institution.setName(institutionName);
        institution.setDescription(institutionDescription);

        final String institutionJson = Json.encodePrettily(institution);
        final String length = Integer.toString(institutionJson.length());

        vertx.createHttpClient().post(port, "localhost", "/1/institution?forceSync=true")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        Institution responseInstitution = Json.decodeValue(body.toString(), Institution.class);
                        context.assertEquals(responseInstitution.getName(), institutionName);
                        context.assertEquals(responseInstitution.getDescription(), institutionDescription);
                        context.assertNotNull(responseInstitution.getId());
                        async.complete();
                    });
                })
                .write(institutionJson)
                .end();
    }

    @Test
    public void testInstitutionRead(TestContext context) {
        final Async async = context.async();

        Institution institution = new Institution();
        institution.setName(institutionName);
        institution.setDescription(institutionDescription);

        final String institutionJson = Json.encodePrettily(institution);
        final String length = Integer.toString(institutionJson.length());

        vertx.createHttpClient().post(port, "localhost", "/1/institution?forceSync=true")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(postResponse -> {
                    context.assertEquals(postResponse.statusCode(), 200);
                    context.assertTrue(postResponse.headers().get("content-type").contains("application/json"));
                    postResponse.bodyHandler(postBody -> {
                        Institution postInstitution = Json.decodeValue(postBody.toString(), Institution.class);
                        context.assertEquals(postInstitution.getName(), institutionName);
                        context.assertEquals(postInstitution.getDescription(), institutionDescription);
                        context.assertNotNull(postInstitution.getId());

                        vertx.createHttpClient().getNow(port, "localhost", "/1/institution/" + postInstitution.getId(), getResponse -> {
                            getResponse.handler(getBody -> {
                                Institution getInstitution = Json.decodeValue(postBody.toString(), Institution.class);

                                context.assertEquals(getInstitution.getName(), institutionName);
                                context.assertEquals(getInstitution.getDescription(), institutionDescription);
                                context.assertNotNull(getInstitution.getId());
                                async.complete();
                            });
                        });
                    });
                })
                .write(institutionJson)
                .end();
    }

    @Test
    public void testInstitutionUpdate(TestContext context) {
        final Async async = context.async();

        Institution institution = new Institution();
        institution.setName(institutionName);
        institution.setDescription(institutionDescription);

        final String institutionJson = Json.encodePrettily(institution);
        final String length = Integer.toString(institutionJson.length());

        vertx.createHttpClient().post(port, "localhost", "/1/institution?forceSync=true")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(postResponse -> {
                    context.assertEquals(postResponse.statusCode(), 200);
                    context.assertTrue(postResponse.headers().get("content-type").contains("application/json"));
                    postResponse.bodyHandler(postBody -> {
                        Institution postInstitution = Json.decodeValue(postBody.toString(), Institution.class);
                        context.assertEquals(postInstitution.getName(), institutionName);
                        context.assertEquals(postInstitution.getDescription(), institutionDescription);
                        context.assertNotNull(postInstitution.getId());

                        final String updatedInstitutionDescription = institutionDescription + " UPDATED";
                        postInstitution.setDescription(updatedInstitutionDescription);

                        final String updatedJson = Json.encodePrettily(postInstitution);
                        final String updatedLength = Integer.toString(updatedJson.length());


                        vertx.createHttpClient().put(port, "localhost", "/1/institution/" + postInstitution.getId())
                                .putHeader("content-type", "application/json")
                                .putHeader("content-length", updatedLength)
                                .handler(putResponse -> {
                                    context.assertEquals(putResponse.statusCode(), 200);
                                    context.assertTrue(putResponse.headers().get("content-type").contains("application/json"));
                                    putResponse.bodyHandler(putBody -> {
                                        Institution putInstitution = Json.decodeValue(putBody.toString(), Institution.class);
                                        context.assertEquals(putInstitution.getName(), institutionName);
                                        context.assertEquals(putInstitution.getDescription(), updatedInstitutionDescription);
                                        context.assertNotNull(putInstitution.getId());
                                        async.complete();
                            });
                        })
                        .write(updatedJson)
                        .end();
                    });
                })
                .write(institutionJson)
                .end();
    }
}
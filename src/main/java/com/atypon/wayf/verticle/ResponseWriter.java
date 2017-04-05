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

package com.atypon.wayf.verticle;

import com.atypon.wayf.data.ErrorResponse;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * A utility class to write responses to VertX
 */
public class ResponseWriter {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseWriter.class);

    public static <B> void buildSuccess(RoutingContext routingContext, B body) {
        LOG.debug("Building success message");

        Completable.fromAction(
                () ->
                        routingContext.response()
                                .setStatusCode(200)
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(body != null ? Json.encodePrettily(body) : ""))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {}, // Do nothing on success
                        (ex) -> routingContext.fail(ex)
                );
    }

    public static void buildFailure(RoutingContext routingContext) {
        Throwable failure = routingContext.failure();

        LOG.error("Error processing request", failure);

        // Write the stack trace to a stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        failure.printStackTrace(printStream);

        // Build an error response message
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(failure.getMessage());

        try {
            errorResponse.setStackTrace(outputStream.toString("utf-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.error("Could not build stack trace", e);
        }

        Completable.fromAction(
                () -> routingContext.response()
                        .setStatusCode(500)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(errorResponse)))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {},
                        (ex) -> LOG.error("Could not write response to client", ex)
                );
    }

}

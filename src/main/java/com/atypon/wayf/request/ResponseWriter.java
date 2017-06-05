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

package com.atypon.wayf.request;

import com.atypon.wayf.data.ErrorResponse;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.facade.ErrorLoggerFacade;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A utility class to write responses to VertX
 */
@Singleton
public class ResponseWriter {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseWriter.class);

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    private static final String CONTENT_TYPE_KEY = "content-type";
    private static final String CONTENT_TYPE_VALUE = "application/json; charset=utf-8";

    @Inject
    protected ErrorLoggerFacade errorLoggerFacade;

    public ResponseWriter() {
        Json.prettyMapper.setDateFormat(DATE_FORMAT);
        Json.prettyMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void setDeviceIdHeader(RoutingContext routingContext, String globalId) {
        routingContext.response().putHeader(RequestReader.DEVICE_ID_HEADER, globalId);
    }

    public <B> void buildSuccess(RoutingContext routingContext, B body) {
        LOG.debug("Building success message");

        Completable.fromAction(
                () ->
                        routingContext.response()
                                .setStatusCode(200)
                                .putHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
                                .putHeader("Link", buildLinkHeaderValue())
                                .end(body != null ? Json.encodePrettily(body) : StringUtils.EMPTY))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {}, // Do nothing on success
                        (ex) -> routingContext.fail(ex)
                );
    }

    public void buildFailure(RoutingContext routingContext) {
        Throwable failure = routingContext.failure();

        LOG.error("Error processing request", failure);

        // If this is a composite exception from RxJava, get the root failure
        if (CompositeException.class.isAssignableFrom(failure.getClass())) {
            failure = ((CompositeException) failure).getExceptions().get(0);
        }

        // Write the stack trace to a stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        failure.printStackTrace(printStream);

        // Build an error response message
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(failure.getMessage());

        try {
            errorResponse.setStacktrace(outputStream.toString("utf-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.error("Could not build stack trace", e);
        }

        int statusCode = 500;

        if (ServiceException.class.isAssignableFrom(failure.getClass())) {
            statusCode = ((ServiceException) failure).getStatusCode();
        }

        final int statusCodeToUse = statusCode;

        try {
            errorLoggerFacade.buildAndLogError(statusCode, failure)
                    .subscribe(
                            () -> {}, 
                            (e) -> LOG.error("Could not log error", e)
                    );
        } catch(Exception e) {
            LOG.error("Could not log exception", e);
        }

        Completable.fromAction(
                () -> routingContext.response()
                        .setStatusCode(statusCodeToUse)
                        .putHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
                        .end(Json.encodePrettily(errorResponse)))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {},
                        (ex) -> LOG.error("Could not write response to client", ex)
                );
    }

    protected String buildLinkHeaderValue() {
        Boolean hasAnotherPage = RequestContextAccessor.get().getHasAnotherDbPage();

        if (hasAnotherPage) {
            int currentLimit = RequestContextAccessor.get().getLimit();
            int currentOffset = RequestContextAccessor.get().getOffset();
            int newOffset = currentOffset + currentLimit;

            String urlStr = null;

            try {
                URL url = new URL(RequestContextAccessor.get().getRequestUrl());

                urlStr = new URIBuilder(url.toURI())
                        .setParameter(RequestReader.LIMIT_QUERY_PARAM, String.valueOf(currentLimit))
                        .setParameter(RequestReader.OFFSET_QUERY_PARAM, String.valueOf(newOffset))
                        .build()
                        .toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return new StringBuilder()
                    .append("<")
                    .append(urlStr)
                    .append(">; rel=\"next\"")
                    .toString();
        }

        return StringUtils.EMPTY;
    }
}

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

import com.atypon.wayf.data.ServiceException;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to read information from VertX requests
 */
public class RequestReader {
    private static final Logger LOG = LoggerFactory.getLogger(RequestReader.class);

    public static final String LIMIT_QUERY_PARAM = "limit";
    public static final String OFFSET_QUERY_PARAM = "offset";

    public static final String DEVICE_ID = "deviceId";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String USER_AGENT_HEADER = "User-Agent";

    private static <B> B _readRequestBody(RoutingContext routingContext, Class<B> bodyClass) {
        LOG.debug("Reading request body of type [{}] from request", bodyClass);

        return Json.decodeValue(routingContext.getBodyAsString(), bodyClass);
    }

    public static <B> Single<B> readRequestBody(RoutingContext routingContext, Class<B> bodyClass) {
        return Single.just(routingContext).observeOn(Schedulers.computation()).map((rc) -> _readRequestBody(rc, bodyClass));
    }

    public static String readPathArgument(RoutingContext routingContext, String argumentName) {
        LOG.debug("Reading param [{}] from request", argumentName);

        return routingContext.request().getParam(argumentName);
    }

    public static String getQueryValue(RoutingContext routingContext, String queryKey) {
        LOG.debug("Reading query param [{}] from request", queryKey);
        return routingContext.request().getParam(queryKey);
    }

    public static String getHeaderValue(RoutingContext routingContext, String headerName) {
        LOG.debug("Reading header value [{}] from request", headerName);
        return routingContext.request().getHeader(headerName);
    }

    public static String getCookieValue(RoutingContext routingContext, String cookieName) {
        LOG.debug("Reading cookie value [{}] from request", cookieName);

        Cookie cookie = routingContext.getCookie(cookieName);

        return cookie == null? null : cookie.getValue();
    }


    public static String readRequiredPathParameter(RoutingContext routingContext, String argumentName, String argDescription) {
        String parameter = readPathArgument(routingContext, argumentName);

        if (parameter == null || parameter.isEmpty()) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, argDescription + " is a required URL parameter");
        }

        return parameter;
    }
}

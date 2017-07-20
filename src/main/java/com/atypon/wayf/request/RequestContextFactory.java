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

import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.facade.AuthenticationFacade;
import com.google.inject.Inject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RequestContextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RequestContextFactory.class);

    @Inject
    private AuthenticationFacade authenticationFacade;

    public RequestContext fromRoutingContext(RoutingContext routingContext) {
        RequestContext requestContext = new RequestContext();

        requestContext.setUserAgent(RequestReader.getHeaderValue(routingContext, RequestReader.USER_AGENT_HEADER));
        requestContext.setRequestUri(routingContext.request().uri());
        requestContext.setRequestUrl(routingContext.request().absoluteURI());
        requestContext.setRequestBody(routingContext.getBodyAsString());
        requestContext.setHttpMethod(routingContext.request().method().toString());

        Map<String, List<String>> headers = new HashMap<>();

        // Don't want to propagate the Vert.x Multimap into the application code
        routingContext.request().headers().forEach((entry) -> {
            List<String> values = headers.get(entry.getKey());
            if (values == null) {
                values = new LinkedList<>();
                headers.put(entry.getKey(), values);
            }
            values.add(entry.getValue());
        });

        requestContext.setHeaders(headers);

        String limit = RequestReader.getQueryValue(routingContext, RequestReader.LIMIT_QUERY_PARAM);
        if (limit != null && !limit.isEmpty()) {
            requestContext.setLimit(Integer.parseInt(limit));
        }

        String offset = RequestReader.getQueryValue(routingContext, RequestReader.OFFSET_QUERY_PARAM);
        if (offset != null && !offset.isEmpty()) {
            requestContext.setOffset(Integer.parseInt(offset));
        }

        Cookie deviceIdCookie = routingContext.getCookie(RequestReader.DEVICE_ID);
        if (deviceIdCookie != null) {
            String deviceId = deviceIdCookie.getValue();
            requestContext.setDeviceId(deviceId);
        }

        String authorizationHeaderValue = RequestReader.getHeaderValue(routingContext, RequestReader.AUTHORIZATION_HEADER);
        if (authorizationHeaderValue != null && !authorizationHeaderValue.isEmpty()) {
            try {
                AuthorizationToken token = authenticationFacade.parseAuthenticationValue(authorizationHeaderValue);
                requestContext.setAuthorizationToken(token);
            } catch (Exception e) {
                LOG.error("Could not parse authorization header", e);
            }
        }

        return requestContext;
    }
}

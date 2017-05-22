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

import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.facade.AuthenticationFacade;
import com.google.inject.Inject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestContextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RequestContextFactory.class);

    @Inject
    private AuthenticationFacade authenticationFacade;

    public RequestContext fromRoutingContext(RoutingContext routingContext) {
        RequestContext requestContext = new RequestContext();

        requestContext.setUserAgent(RequestReader.getHeaderValue(routingContext, "User-Agent"));
        requestContext.setRequestUri(routingContext.request().uri());
        requestContext.setRequestUrl(routingContext.request().absoluteURI());

        String limit = RequestReader.getQueryValue(routingContext, "limit");
        if (limit != null && !limit.isEmpty()) {
            requestContext.setLimit(Integer.parseInt(limit));
        }

        String offset = RequestReader.getQueryValue(routingContext, "offset");
        if (offset != null && !offset.isEmpty()) {
            requestContext.setOffset(Integer.parseInt(offset));
        }

        String deviceId = RequestReader.getHeaderValue(routingContext, "deviceId");
        if (deviceId != null && !deviceId.isEmpty()) {
            requestContext.setDeviceId(deviceId);
        }

        String apiKey = RequestReader.getHeaderValue(routingContext, "Authorization");
        if (apiKey != null && !apiKey.isEmpty()) {
            Authenticatable authenticatable = authenticationFacade.authenticate(apiKey).blockingGet();
            requestContext.setAuthenticatable(authenticatable);
        }

        return requestContext;
    }
}

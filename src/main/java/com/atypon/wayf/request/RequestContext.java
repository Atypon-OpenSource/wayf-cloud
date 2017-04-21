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

import io.vertx.ext.web.RoutingContext;

public class RequestContext {
    private String requestUrl;
    private boolean forceSync;

    RequestContext() {
    }

    public static RequestContext fromRoutingContext(RoutingContext routingContext) {
        RequestContext requestContext = new RequestContext();

        requestContext.setRequestUrl(routingContext.request().uri());

        String forceSyncQueryValue = RequestReader.getQueryValue(routingContext, "forceSync");
        requestContext.setForceSync(Boolean.parseBoolean(forceSyncQueryValue));

        return requestContext;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public RequestContext setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public boolean isForceSync() {
        return forceSync;
    }

    public RequestContext setForceSync(boolean forceSync) {
        this.forceSync = forceSync;
        return this;
    }

}

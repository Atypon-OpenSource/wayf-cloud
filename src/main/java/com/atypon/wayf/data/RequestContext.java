package com.atypon.wayf.data;

import io.vertx.ext.web.RoutingContext;

public class RequestContext {
    private String requestUrl;

    public RequestContext() {
    }

    public static RequestContext fromRoutingContext(RoutingContext routingContext) {
        RequestContext requestContext = new RequestContext();
        requestContext.setRequestUrl(routingContext.request().uri());

        return requestContext;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public RequestContext setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }
}

package com.atypon.wayf.data;

public class RequestContext {
    private String requestUrl;

    public RequestContext() {
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public RequestContext setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }
}

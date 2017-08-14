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

import com.atypon.wayf.data.AuthenticatedEntity;
import com.atypon.wayf.data.AuthorizationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class RequestContext {
    private static final Logger LOG = LoggerFactory.getLogger(RequestContext.class);

    private static final Integer DEFAULT_LIMIT = 30;
    private static final Integer DEFAULT_OFFSET = 0;

    private Integer limit = DEFAULT_LIMIT;
    private Integer offset = DEFAULT_OFFSET;

    private AuthorizationToken authorizationToken;
    private String httpMethod;
    private String requestBody;
    private String userAgent;
    private String deviceId;
    private String requestUrl;
    private String requestUri;
    private Map<String, List<String>> headers;

    private AuthenticatedEntity authenticated;

    private Boolean hasAnotherDbPage = Boolean.FALSE;

    public RequestContext() {
    }

    public AuthorizationToken getAuthorizationToken() {
        return authorizationToken;
    }

    public RequestContext setAuthorizationToken(AuthorizationToken authorizationToken) {
        this.authorizationToken = authorizationToken;
        return this;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public RequestContext setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public RequestContext setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public RequestContext setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public RequestContext setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public RequestContext setRequestUri(String requestUri) {
        this.requestUri = requestUri;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public RequestContext setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public RequestContext setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Boolean getHasAnotherDbPage() {
        return hasAnotherDbPage;
    }

    public RequestContext setHasAnotherDbPage(Boolean hasAnotherDbPage) {
        this.hasAnotherDbPage = hasAnotherDbPage;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public RequestContext setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public RequestContext setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public AuthenticatedEntity getAuthenticated() {
        return authenticated;
    }

    public RequestContext setAuthenticated(AuthenticatedEntity authenticated) {
        this.authenticated = authenticated;
        return this;
    }
}

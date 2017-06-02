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

package com.atypon.wayf.data;

import java.util.Date;

public class ErrorLogEntry {
    private Long id;
    private String authenticatedParty;
    private String deviceGlobalId;
    private String httpMethod;
    private String requestUrl;
    private String headers;
    private String callerIp;
    private String serverIp;
    private int responseCode;
    private String exceptionType;
    private String exceptionMessage;
    private String exceptionStacktrace;
    private Date errorDate;
    private Date createdDate;
    private Date modifiedDate;

    public ErrorLogEntry() {
    }

    public Long getId() {
        return id;
    }

    public ErrorLogEntry setId(Long id) {
        this.id = id;
        return this;
    }
    public String getAuthenticatedParty() {
        return authenticatedParty;
    }

    public ErrorLogEntry setAuthenticatedParty(String authenticatedParty) {
        this.authenticatedParty = authenticatedParty;
        return this;
    }

    public String getDeviceGlobalId() {
        return deviceGlobalId;
    }

    public ErrorLogEntry setDeviceGlobalId(String deviceGlobalId) {
        this.deviceGlobalId = deviceGlobalId;
        return this;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public ErrorLogEntry setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public ErrorLogEntry setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public String getHeaders() {
        return headers;
    }

    public ErrorLogEntry setHeaders(String headers) {
        this.headers = headers;
        return this;
    }

    public String getCallerIp() {
        return callerIp;
    }

    public ErrorLogEntry setCallerIp(String callerIp) {
        this.callerIp = callerIp;
        return this;
    }

    public String getServerIp() {
        return serverIp;
    }

    public ErrorLogEntry setServerIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public ErrorLogEntry setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public ErrorLogEntry setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
        return this;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public ErrorLogEntry setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    public String getExceptionStacktrace() {
        return exceptionStacktrace;
    }

    public ErrorLogEntry setExceptionStacktrace(String exceptionStacktrace) {
        this.exceptionStacktrace = exceptionStacktrace;
        return this;
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public ErrorLogEntry setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public ErrorLogEntry setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public ErrorLogEntry setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
        return this;
    }
}

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

package com.atypon.wayf.facade.impl;

import com.atypon.wayf.dao.ErrorLoggerDao;
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.ErrorLogEntry;
import com.atypon.wayf.facade.ErrorLoggerFacade;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;

@Singleton
public class ErrorLoggerFacadeImpl implements ErrorLoggerFacade {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorLoggerFacadeImpl.class);

    @Inject
    private ErrorLoggerDao errorLoggerDao;

    private String ipAddress;

    public ErrorLoggerFacadeImpl() {
        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            ipAddress = ipAddr.getHostAddress();
        } catch (Exception e) {
            LOG.error("Could not get IP address of server", e);
        }
    }

    public Completable buildAndLogError(int statusCode, Throwable t) {
        ErrorLogEntry logEntry = new ErrorLogEntry();
        logEntry.setResponseCode(statusCode);

        logEntry.setExceptionType(t.getClass().getCanonicalName());
        logEntry.setExceptionMessage(t.getMessage());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        t.printStackTrace(printStream);

        try {
            logEntry.setExceptionStacktrace(outputStream.toString("utf-8"));
        } catch (Exception e) {
            LOG.error("Could not get stacktrace", e);
        }

        logEntry.setErrorDate(new Date());

        RequestContext requestContext = RequestContextAccessor.get();

        logEntry.setDeviceGlobalId(requestContext.getDeviceId());

        Authenticatable authenticated = requestContext.getAuthenticated();
        if(authenticated != null) {
            logEntry.setAuthenticatedParty(authenticated.getType() + "-" + authenticated.getId());
        }

        logEntry.setHeaders(requestContext.getHeaders().toString());
        List<String> forwardedFor = requestContext.getHeaders().get("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            logEntry.setCallerIp(forwardedFor.get(0).toString());
        }
        logEntry.setServerIp(ipAddress);
        logEntry.setHttpMethod(requestContext.getHttpMethod());
        logEntry.setRequestUrl(requestContext.getRequestUrl());

        return errorLoggerDao.logError(logEntry);
    }
}

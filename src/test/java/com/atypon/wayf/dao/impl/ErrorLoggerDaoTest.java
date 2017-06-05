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

package com.atypon.wayf.dao.impl;

import com.atypon.wayf.dao.ErrorLoggerDao;
import com.atypon.wayf.data.ErrorLogEntry;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static junit.framework.Assert.fail;

public class ErrorLoggerDaoTest {

    @Inject
    private ErrorLoggerDao errorLoggerDao;

    @Before
    public void setup() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testDbWrite() {
        ErrorLogEntry logEntry = new ErrorLogEntry();
        logEntry.setHttpMethod(HttpMethod.POST.toString());
        logEntry.setRequestUrl("test-request-url");
        logEntry.setServerIp("127.0.0.1");
        logEntry.setCallerIp("1227.0.01");
        logEntry.setResponseCode(500);
        logEntry.setAuthenticatedParty("PUBLISHER-123");
        logEntry.setDeviceGlobalId(UUID.randomUUID().toString());
        logEntry.setErrorDate(new Date());
        logEntry.setExceptionMessage("test-exception-mesage");
        logEntry.setExceptionType(ServiceException.class.getCanonicalName());
        logEntry.setExceptionStacktrace("test-stack-trace");
        logEntry.setHeaders("{\"a\", {\"b\"}}");

        errorLoggerDao.logError(logEntry).subscribe(() -> {}, (e) -> fail("DB called return error"));
    }
}

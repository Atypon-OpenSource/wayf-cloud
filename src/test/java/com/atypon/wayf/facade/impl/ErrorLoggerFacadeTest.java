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

import com.atypon.wayf.dao.impl.ErrorLoggerDaoMockImpl;
import com.atypon.wayf.data.*;
import com.atypon.wayf.data.authentication.AuthenticatedEntity;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.data.authentication.AuthorizationTokenType;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.facade.ErrorLoggerFacade;
import com.atypon.wayf.integration.AuthorizationTokenTestUtil;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.common.collect.Lists;
import io.vertx.core.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ErrorLoggerFacadeTest {

    private ErrorLoggerFacade errorLoggerFacade;
    private ErrorLoggerDaoMockImpl mockDao;

    @Before
    public void setup() {
        ErrorLoggerFacadeImpl facadeImpl = new ErrorLoggerFacadeImpl();
        mockDao = new ErrorLoggerDaoMockImpl();
        facadeImpl.setErrorLoggerDao(mockDao);
        errorLoggerFacade = facadeImpl;
    }

    @Test
    public void testErrorLogger() throws Exception {
        String serverIp = InetAddress.getLocalHost().getHostAddress();

        Publisher authenticatable = new Publisher();
        authenticatable.setId(123L);

        int statusCode = 500;
        String errorMessage = "Test error message";
        String url = "localhost:8080/error";
        String tokenValue = "testToken";
        String forwardedFor = "127.0.0.1";
        String globalId = "test-global-id";
        String httpMethod = HttpMethod.POST.toString();
        String requestBody = "{ \"body\" : \"end\" }";

        AuthorizationToken token = new AuthorizationToken();
        token.setType(AuthorizationTokenType.API_TOKEN);
        token.setValue(tokenValue);

        AuthenticatedEntity authenticatedEntity = new AuthenticatedEntity();
        authenticatedEntity.setAuthenticatable(authenticatable);
        authenticatedEntity.setCredentials(token);

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authentication", Lists.newArrayList(AuthorizationTokenTestUtil.generateApiTokenHeaderValue(token)));
        headers.put("X-Forwarded-For", Lists.newArrayList(forwardedFor));
        headers.put("X-Device-Id", Lists.newArrayList(globalId));

        ServiceException exception = new ServiceException(statusCode, errorMessage);

        RequestContext requestContext = new RequestContext()
                .setRequestUrl(url)
                .setHeaders(headers)
                .setDeviceId(globalId)
                .setAuthorizationToken(token)
                .setAuthenticated(authenticatedEntity)
                .setHttpMethod(httpMethod)
                .setRequestBody(requestBody);

        RequestContextAccessor.set(requestContext);

        errorLoggerFacade.buildAndLogError(statusCode, exception);

        ErrorLogEntry loggedError = mockDao.getLastLoggedError();

        assertEquals(authenticatable.getClass().getSimpleName() + "-" + authenticatable.getId(), loggedError.getAuthenticatedParty());
        assertEquals(errorMessage, loggedError.getExceptionMessage());
        assertEquals(statusCode, loggedError.getResponseCode());
        assertEquals(url, loggedError.getRequestUrl());
        assertEquals(headers.toString(), loggedError.getHeaders());
        assertEquals(forwardedFor, loggedError.getCallerIp());
        assertEquals(globalId, loggedError.getDeviceGlobalId());
        assertEquals(httpMethod, loggedError.getHttpMethod());
        assertEquals(ServiceException.class.getCanonicalName(), loggedError.getExceptionType());
        assertNotNull(loggedError.getExceptionStacktrace());
        assertEquals(serverIp, loggedError.getServerIp());
        assertNotNull(loggedError.getErrorDate());
    }
}

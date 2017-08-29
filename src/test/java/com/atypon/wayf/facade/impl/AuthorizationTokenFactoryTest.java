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

import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.data.authentication.AuthorizationTokenType;
import com.atypon.wayf.facade.AuthorizationTokenFactory;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthorizationTokenFactoryTest {
    @Inject
    private AuthorizationTokenFactory authorizationTokenFactory;

    @Before
    public void setUp() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testParseJwtToken() {
        String jwtTokenValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

        String jwt = "Bearer " + jwtTokenValue;

        AuthorizationToken token = authorizationTokenFactory.fromAuthorizationHeader(jwt);

        assertNotNull(token);
        assertEquals(AuthorizationTokenType.JWT, token.getType());
        assertEquals(jwtTokenValue, token.getValue());
    }

    @Test
    public void testParseApiToken() {
        String apiTokenValue = UUID.randomUUID().toString();
        String apiToken = "Token " + apiTokenValue;

        AuthorizationToken token = authorizationTokenFactory.fromAuthorizationHeader(apiToken);

        assertNotNull(token);
        assertEquals(AuthorizationTokenType.API_TOKEN, token.getType());
        assertEquals(apiTokenValue, token.getValue());
    }

    @Test(expected = ServiceException.class)
    public void testBadToken() {
        String badToken = "gobble-gook";

        authorizationTokenFactory.fromAuthorizationHeader(badToken);
    }
}

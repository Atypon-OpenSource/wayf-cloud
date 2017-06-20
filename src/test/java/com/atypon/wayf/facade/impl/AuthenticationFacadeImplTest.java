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

import com.atypon.wayf.dao.AuthenticationDao;
import com.atypon.wayf.dao.impl.AuthenticationDaoRedisImpl;
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.data.AuthorizationTokenType;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.publisher.Publisher;
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

public class AuthenticationFacadeImplTest {

    @Inject
    private AuthenticationFacadeTestImpl facade;

    @Before
    public void setUp() {
        facade = new AuthenticationFacadeTestImpl();
        Guice.createInjector(new WayfGuiceModule()).injectMembers(facade);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testAllCacheLayers() {
        Publisher testPublisher = new Publisher();
        testPublisher.setId(1122L);

        // Test Create
        String tokenValue = facade.createToken(testPublisher).blockingGet();
        assertNotNull(tokenValue);

        AuthorizationToken token = new AuthorizationToken().setType(AuthorizationTokenType.API_TOKEN).setValue(tokenValue);

        // Test Read
        Authenticatable authenticated = facade.authenticate(token);
        assertNotNull(authenticated);
        assertEquals(Publisher.class, authenticated.getClass());
        assertEquals(testPublisher.getId(), authenticated.getId());

        // Remove the L2 Cache and see if we can read from L1
        AuthenticationDao l2Dao = facade.getRedisDao();
        facade.setRedisDao(null);

        Authenticatable authenticatedFromL1 = facade.authenticate(token);
        assertNotNull(authenticatedFromL1);
        assertEquals(Publisher.class, authenticatedFromL1.getClass());
        assertEquals(testPublisher.getId(), authenticatedFromL1.getId());

        // Reset the L2 Cache
        facade.setRedisDao(l2Dao);

        // Clear L1 and Remove L3 to verify we can read from l2
        facade.getL1Cache().invalidateAll();
        ((AuthenticationDaoRedisImpl)facade.getRedisDao()).setDbDao(null);

        Authenticatable authenticatedFromL2 = facade.authenticate(token);
        assertNotNull(authenticatedFromL2);
        assertEquals(Publisher.class, authenticatedFromL2.getClass());
        assertEquals(testPublisher.getId(), authenticatedFromL2.getId());
    }

    @Test
    public void testParseJwtToken() {
        String jwtTokenValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

        String jwt = "Bearer " + jwtTokenValue;

        AuthorizationToken token = facade.parseAuthenticationValue(jwt);

        assertNotNull(token);
        assertEquals(AuthorizationTokenType.JWT, token.getType());
        assertEquals(jwtTokenValue, token.getValue());
    }

    @Test
    public void testParseApiToken() {
        String apiTokenValue = UUID.randomUUID().toString();
        String apiToken = "Token " + apiTokenValue;

        AuthorizationToken token = facade.parseAuthenticationValue(apiToken);

        assertNotNull(token);
        assertEquals(AuthorizationTokenType.API_TOKEN, token.getType());
        assertEquals(apiTokenValue, token.getValue());
    }

    @Test(expected = ServiceException.class)
    public void testBadToken() {
        String badToken = "gobble-gook";

        facade.parseAuthenticationValue(badToken);
    }
}

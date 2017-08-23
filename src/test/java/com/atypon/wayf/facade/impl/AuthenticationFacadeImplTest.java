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

import com.atypon.wayf.cache.CacheLoader;
import com.atypon.wayf.cache.LoadingCache;
import com.atypon.wayf.data.authentication.AuthenticatedEntity;
import com.atypon.wayf.data.authentication.AuthenticationCredentials;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.data.authentication.AuthorizationTokenType;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Maybe;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthenticationFacadeImplTest {

    @Inject
    @Named("authenticatableCache")
    protected LoadingCache<AuthenticationCredentials, AuthenticatedEntity> persistence;

    @Before
    public void setUp() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Ignore
    @Test
    public void testAllCacheLayers() {
        /*Publisher testPublisher = new Publisher();
        testPublisher.setId(1122L);

        String tokenValue = UUID.randomUUID().toString();

        AuthorizationToken authorizationToken = new AuthorizationToken();
        authorizationToken.setType(AuthorizationTokenType.API_TOKEN);
        authorizationToken.setValue(tokenValue);

        authorizationToken.setAuthenticatable(testPublisher);
        persistence.


        // Test Read
        AuthenticatedEntity authenticated = facade.authenticate(token);
        assertNotNull(authenticated);
        assertEquals(Publisher.class, authenticated.getAuthenticatable().getClass());
        assertEquals(testPublisher.getId(), authenticated.getAuthenticatable().getId());

        CacheLoader l1CacheLoader =  ((LoadingCache) facade.getL1Cache()).getCacheLoader();
        ((LoadingCache) facade.getL1Cache()).setCacheLoader((key) -> Maybe.empty());
        // Remove the L2 Cache and see if we can read from L1

        AuthenticatedEntity authenticatedFromL1 = facade.authenticate(token);
        assertNotNull(authenticatedFromL1);
        assertEquals(Publisher.class, authenticatedFromL1.getAuthenticatable().getClass());
        assertEquals(testPublisher.getId(), authenticatedFromL1.getAuthenticatable().getId());

        // Reset the L2 Cache
        ((LoadingCache) facade.getL1Cache()).setCacheLoader(l1CacheLoader);

        // Clear L1 and Remove L3 to verify we can read from l2
        facade.getL1Cache().invalidateAll();
        facade.getRedisCache().setCacheLoader((key) -> Maybe.empty());

        AuthenticatedEntity authenticatedFromL2 = facade.authenticate(token);
        assertNotNull(authenticatedFromL2);
        assertEquals(Publisher.class, authenticatedFromL2.getAuthenticatable().getClass());
        assertEquals(testPublisher.getId(), authenticatedFromL2.getAuthenticatable().getId());*/
    }
}

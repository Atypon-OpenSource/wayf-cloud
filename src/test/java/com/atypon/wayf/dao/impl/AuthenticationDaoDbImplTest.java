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

import com.atypon.wayf.data.authentication.AuthenticatedEntity;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.data.authentication.AuthorizationTokenType;
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

public class AuthenticationDaoDbImplTest {
    @Inject
    private AuthorizationTokenDaoDbImpl dao;

    @Before
    public void setUp() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testCreateAndAuthenticate() {
        AuthorizationToken token = new AuthorizationToken();
        token.setType(AuthorizationTokenType.API_TOKEN);
        token.setValue(UUID.randomUUID().toString());

        Publisher publisher = new Publisher();
        publisher.setId(123L);
        publisher.setToken(token);

        token.setAuthenticatable(publisher);
        dao.create(token).blockingGet();

        AuthenticatedEntity authenticated = dao.authenticate(token).blockingGet();
        assertNotNull(authenticated);

        assertEquals(AuthenticatedEntity.class, authenticated.getClass());
        assertEquals(publisher.getId(), authenticated.getAuthenticatable().getId());
        assertEquals(token.getValue(), ((AuthorizationToken) authenticated.getCredentials()).getValue());
    }
}

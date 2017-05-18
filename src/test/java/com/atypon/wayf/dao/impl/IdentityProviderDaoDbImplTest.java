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

import com.atypon.wayf.dao.IdentityProviderDao;
import com.atypon.wayf.data.identity.OauthEntity;
import com.atypon.wayf.data.identity.OauthProvider;
import com.atypon.wayf.data.identity.OpenAthensEntity;
import com.atypon.wayf.data.identity.SamlEntity;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IdentityProviderDaoDbImplTest {

    @Inject
    @Named("openAthensEntity")
    private IdentityProviderDao openAthensDao;

    @Inject
    @Named("samlEntity")
    private IdentityProviderDao samlDao;

    @Inject
    @Named("oauthEntity")
    private IdentityProviderDao oauthDao;

    @Before
    public void setUp() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);
        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testOpenAthens() {
        OpenAthensEntity openAthensEntity = new OpenAthensEntity();
        openAthensEntity.setEntityId("open-athens-entity");
        openAthensEntity.setOrganizationId("organization-id");
        openAthensEntity.setScope("scope");

        OpenAthensEntity createdOpenAthensEntity = (OpenAthensEntity) openAthensDao.create(openAthensEntity).blockingGet();

        // Test create
        assertNotNull(createdOpenAthensEntity.getId());
        assertNotNull(createdOpenAthensEntity.getCreatedDate());
        assertEquals(openAthensEntity.getEntityId(), createdOpenAthensEntity.getEntityId());
        assertEquals(openAthensEntity.getOrganizationId(), createdOpenAthensEntity.getOrganizationId());
        assertEquals(openAthensEntity.getScope(), createdOpenAthensEntity.getScope());

        OpenAthensEntity readOpenAthensEntity = (OpenAthensEntity) openAthensDao.read(createdOpenAthensEntity.getId()).blockingGet();

        // Test read
        assertEquals(createdOpenAthensEntity.getId(), readOpenAthensEntity.getId());
        assertEquals(createdOpenAthensEntity.getCreatedDate(), readOpenAthensEntity.getCreatedDate());
        assertEquals(createdOpenAthensEntity.getEntityId(), readOpenAthensEntity.getEntityId());
        assertEquals(createdOpenAthensEntity.getOrganizationId(), readOpenAthensEntity.getOrganizationId());
        assertEquals(createdOpenAthensEntity.getScope(), readOpenAthensEntity.getScope());
    }

    @Test
    public void testSamlEntity() {
        SamlEntity samlEntity = new SamlEntity();
        samlEntity.setEntityId("saml-entity-id");
        samlEntity.setFederationId("saml-federation-id");

        SamlEntity createdSamlEntity = (SamlEntity) samlDao.create(samlEntity).blockingGet();

        // Test create
        assertNotNull(createdSamlEntity.getId());
        assertNotNull(createdSamlEntity.getCreatedDate());
        assertEquals(samlEntity.getEntityId(), createdSamlEntity.getEntityId());
        assertEquals(samlEntity.getFederationId(), createdSamlEntity.getFederationId());

        SamlEntity readSamlEntity = (SamlEntity) samlDao.read(createdSamlEntity.getId()).blockingGet();

        // Test read
        assertEquals(createdSamlEntity.getId(), readSamlEntity.getId());
        assertEquals(createdSamlEntity.getCreatedDate(), readSamlEntity.getCreatedDate());
        assertEquals(createdSamlEntity.getEntityId(), readSamlEntity.getEntityId());
        assertEquals(createdSamlEntity.getFederationId(), readSamlEntity.getFederationId());
    }

    @Test
    public void testOauthEntity() {
        OauthEntity oauthEntity = new OauthEntity();
        oauthEntity.setProvider(OauthProvider.FACEBOOK);

        OauthEntity createdOauthEntity = (OauthEntity) oauthDao.create(oauthEntity).blockingGet();

        assertNotNull(createdOauthEntity.getId());
        assertNotNull(createdOauthEntity.getCreatedDate());
        assertEquals(oauthEntity.getProvider(), createdOauthEntity.getProvider());

        OauthEntity readOauthEntity = (OauthEntity) oauthDao.read(createdOauthEntity.getId()).blockingGet();

        // Test read
        assertEquals(createdOauthEntity.getId(), readOauthEntity.getId());
        assertEquals(createdOauthEntity.getCreatedDate(), readOauthEntity.getCreatedDate());
        assertEquals(createdOauthEntity.getProvider(), readOauthEntity.getProvider());
    }
}

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

import com.atypon.wayf.dao.DeviceIdentityProviderBlacklistDao;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.identity.SamlEntity;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeviceIdentityProviderBlacklistDaoDbImplTest {

    @Inject
    private DeviceIdentityProviderBlacklistDao dao;

    @Before
    public void setUp() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testAdd() {
        Device device = new Device();
        device.setGlobalId(UUID.randomUUID().toString());

        IdentityProvider idp = new SamlEntity();
        idp.setId(123L);

        dao.add(device, idp).subscribe();

        List<IdentityProvider> idps = dao.getBlacklistedIdentityProviders(device).toList().blockingGet();

        assertEquals(1, idps.size());
        assertEquals(idp.getId(), idps.get(0).getId());
    }

    @Test
    public void testRemove() {
        Device device = new Device();
        device.setGlobalId(UUID.randomUUID().toString());

        IdentityProvider idp = new SamlEntity();
        idp.setId(123L);

        dao.add(device, idp).subscribe();

        List<IdentityProvider> idps = dao.getBlacklistedIdentityProviders(device).toList().blockingGet();

        assertEquals(1, idps.size());
        assertEquals(idp.getId(), idps.get(0).getId());

        dao.remove(device, idp).subscribe();
        idps = dao.getBlacklistedIdentityProviders(device).toList().blockingGet();

        assertEquals(0, idps.size());
    }

    @Test
    public void testFilter() {
        Device device = new Device();
        device.setGlobalId(UUID.randomUUID().toString());

        IdentityProvider idp1 = new SamlEntity();
        idp1.setId(123L);

        IdentityProvider idp2 = new SamlEntity();
        idp2.setId(456L);

        Set<Long> idpIds = Sets.newHashSet(idp1.getId(), idp2.getId());

        dao.add(device, idp1).subscribe();
        dao.add(device, idp2).subscribe();

        List<IdentityProvider> idps = dao.getBlacklistedIdentityProviders(device).toList().blockingGet();

        assertEquals(2, idps.size());

        for (IdentityProvider idp : idps) {
            assertTrue(idpIds.contains(idp.getId()));
        }
    }
}

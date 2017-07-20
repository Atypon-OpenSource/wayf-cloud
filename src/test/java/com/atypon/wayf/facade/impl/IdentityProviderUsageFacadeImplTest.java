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

import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.identity.IdentityProviderUsage;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class IdentityProviderUsageFacadeImplTest {

    private IdentityProviderUsageFacadeImpl usageFacade;
    private DeviceIdentityProviderBlacklistFacadeMockImpl blacklistFacade;
    private DeviceAccessFacadeMockImpl deviceAccessFacade;

    @Before
    public void setUp() {
        usageFacade = new IdentityProviderUsageFacadeImpl();

        deviceAccessFacade = new DeviceAccessFacadeMockImpl();
        usageFacade.setDeviceAccessFacade(deviceAccessFacade);

        blacklistFacade = new DeviceIdentityProviderBlacklistFacadeMockImpl();
        usageFacade.setIdpBlacklistFacade(blacklistFacade);

        usageFacade.setIdentityProviderFacade(new IdentityProviderFacadeMockImpl());

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testUsages() {
        IdentityProvider idp123 = new IdentityProvider();
        idp123.setId(123L);

        IdentityProvider idp456 = new IdentityProvider();
        idp456.setId(456L);

        IdentityProvider idp789 = new IdentityProvider();
        idp789.setId(789L);

        Date startDate = new Date(0l);
        Date endDate = new Date();

        List<DeviceAccess> deviceAccessList = new ArrayList<>(6);

        DeviceAccess deviceAccess123 = new DeviceAccess();
        deviceAccess123.setIdentityProvider(idp123);
        deviceAccess123.setCreatedDate(startDate);
        deviceAccessList.add(deviceAccess123);

        DeviceAccess deviceAccess456_1 = new DeviceAccess();
        deviceAccess456_1.setIdentityProvider(idp456);
        deviceAccess456_1.setCreatedDate(startDate);
        deviceAccessList.add(deviceAccess456_1);

        DeviceAccess deviceAccess456_2 = new DeviceAccess();
        deviceAccess456_2.setIdentityProvider(idp456);
        deviceAccess456_2.setCreatedDate(endDate);
        deviceAccessList.add(deviceAccess456_2);

        DeviceAccess deviceAccess456_3 = new DeviceAccess();
        deviceAccess456_3.setIdentityProvider(idp456);
        deviceAccess456_3.setCreatedDate(startDate);
        deviceAccessList.add(deviceAccess456_3);

        DeviceAccess deviceAccess789_1 = new DeviceAccess();
        deviceAccess789_1.setIdentityProvider(idp789);
        deviceAccess789_1.setCreatedDate(endDate);
        deviceAccessList.add(deviceAccess789_1);

        DeviceAccess deviceAccess789_2 = new DeviceAccess();
        deviceAccess789_2.setIdentityProvider(idp789);
        deviceAccess789_2.setCreatedDate(startDate);
        deviceAccessList.add(deviceAccess789_2);

        deviceAccessFacade.setDeviceAccessList(deviceAccessList);
        blacklistFacade.setBlacklist(new LinkedList<>());

        Device testDevice = new Device();
        testDevice.setId(12L);

        List<IdentityProviderUsage> usages = usageFacade.buildRecentHistory(testDevice);
        assertNotNull(usages);
        assertEquals(3, usages.size());

        IdentityProviderUsage usage456 = usages.get(0);
        assertEquals(new Long(456L), usage456.getIdp().getId());
        assertEquals(endDate, usage456.getLastActiveDate());
        assertEquals(Double.valueOf("50.00"), usage456.getFrequency());

        IdentityProviderUsage usage789 = usages.get(1);
        assertEquals(new Long(789L), usage789.getIdp().getId());
        assertEquals(endDate, usage789.getLastActiveDate());
        assertEquals(Double.valueOf("33.33"), usage789.getFrequency());

        IdentityProviderUsage usage123 = usages.get(2);
        assertEquals(new Long(123L), usage123.getIdp().getId());
        assertEquals(startDate, usage123.getLastActiveDate());
        assertEquals(Double.valueOf("16.67"), usage123.getFrequency());
    }
}

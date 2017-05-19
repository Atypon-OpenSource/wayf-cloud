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

import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.data.device.DeviceStatus;
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


public class DeviceDaoDbImplTest {

    @Inject
    private DeviceDaoDbImpl dao;

    @Before
    public void setUp() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testCreate() {
        Device device = new Device();
        device.setGlobalId(UUID.randomUUID().toString());
        device.setStatus(DeviceStatus.ACTIVE);

        Device createdDevice = dao.create(device).blockingGet();

        assertNotNull(createdDevice.getGlobalId());
        assertNotNull(createdDevice.getCreatedDate());

        assertEquals(device.getGlobalId(), createdDevice.getGlobalId());
        assertEquals(device.getStatus(), createdDevice.getStatus());
    }



    @Test
    public void testRead() {
        Device device = new Device();
        device.setGlobalId(UUID.randomUUID().toString());
        device.setStatus(DeviceStatus.ACTIVE);

        Device createdDevice = dao.create(device).blockingGet();
        Device readDevice = dao.read(new DeviceQuery().setGlobalId(createdDevice.getGlobalId())).blockingGet();

        assertNotNull(readDevice.getGlobalId());
        assertNotNull(readDevice.getCreatedDate());

        assertEquals(createdDevice.getGlobalId(), readDevice.getGlobalId());
        assertEquals(createdDevice.getStatus(), readDevice.getStatus());
    }
}

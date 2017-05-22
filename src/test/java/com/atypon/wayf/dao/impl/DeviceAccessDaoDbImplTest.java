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

import com.atypon.wayf.dao.DeviceAccessDao;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.data.device.access.DeviceAccessQuery;
import com.atypon.wayf.data.device.access.DeviceAccessType;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.identity.SamlEntity;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeviceAccessDaoDbImplTest {
    @Inject
    private DeviceAccessDao deviceAccessDao;

    @Before
    public void setup() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);
        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext());
    }

    @Test
    public void testCreate() {
        DeviceAccess deviceAccess = new DeviceAccess();
        deviceAccess.setLocalId("test-local-id");
        deviceAccess.setType(DeviceAccessType.READ_IDP_HISTORY);


        Device device = new Device();
        device.setId(Long.valueOf(new Random().nextInt(3200)));
        deviceAccess.setDevice(device);

        Publisher publisher = new Publisher();
        publisher.setId(123L);
        deviceAccess.setPublisher(publisher);

        IdentityProvider identityProvider = new SamlEntity();
        identityProvider.setId(123L);
        deviceAccess.setIdentityProvider(identityProvider);

        DeviceAccess createdDeviceAccess = deviceAccessDao.create(deviceAccess).blockingGet();

        assertNotNull(createdDeviceAccess.getId());
        assertNotNull(createdDeviceAccess.getCreatedDate());

        assertEquals(deviceAccess.getLocalId(), createdDeviceAccess.getLocalId());
        assertEquals(deviceAccess.getDevice().getId(), createdDeviceAccess.getDevice().getId());
        assertEquals(deviceAccess.getIdentityProvider().getId(), createdDeviceAccess.getIdentityProvider().getId());
        assertEquals(deviceAccess.getPublisher().getId(), createdDeviceAccess.getPublisher().getId());
        assertEquals(deviceAccess.getType(), createdDeviceAccess.getType());
    }

    @Test
    public void testFilterByDevice() {
        DeviceAccess deviceAccess = new DeviceAccess();
        deviceAccess.setLocalId("test-local-id");
        deviceAccess.setType(DeviceAccessType.READ_IDP_HISTORY);


        Device device = new Device();
        device.setId(Long.valueOf(new Random().nextInt(32000)));
        deviceAccess.setDevice(device);

        Publisher publisher = new Publisher();
        publisher.setId(123L);
        deviceAccess.setPublisher(publisher);

        IdentityProvider identityProvider = new SamlEntity();
        identityProvider.setId(123L);
        deviceAccess.setIdentityProvider(identityProvider);

        DeviceAccess createdDeviceAccess = deviceAccessDao.create(deviceAccess).blockingGet();

        List<DeviceAccess> filterDeviceAccess = deviceAccessDao.filter(new DeviceAccessQuery().setDeviceIds(Lists.newArrayList(device.getId()))).toList().blockingGet();

        assertEquals(1, filterDeviceAccess.size());

        DeviceAccess filteredDeviceAccess = filterDeviceAccess.get(0);

        assertEquals(createdDeviceAccess.getId(), filteredDeviceAccess.getId());
        assertEquals(createdDeviceAccess.getCreatedDate(), filteredDeviceAccess.getCreatedDate());
        assertEquals(createdDeviceAccess.getModifiedDate(), filteredDeviceAccess.getModifiedDate());
        assertEquals(createdDeviceAccess.getLocalId(), filteredDeviceAccess.getLocalId());
        assertEquals(createdDeviceAccess.getDevice().getId(), filteredDeviceAccess.getDevice().getId());
        assertEquals(createdDeviceAccess.getIdentityProvider().getId(), filteredDeviceAccess.getIdentityProvider().getId());
        assertEquals(createdDeviceAccess.getPublisher().getId(), filteredDeviceAccess.getPublisher().getId());
        assertEquals(createdDeviceAccess.getType(), filteredDeviceAccess.getType());
    }

    @Test
    public void testFilterExcludeIdp() {
        DeviceAccess deviceAccess = new DeviceAccess();
        deviceAccess.setLocalId("test-local-id");
        deviceAccess.setType(DeviceAccessType.READ_IDP_HISTORY);


        Device device = new Device();
        device.setId(Long.valueOf(new Random().nextInt(32000)));
        deviceAccess.setDevice(device);

        Publisher publisher = new Publisher();
        publisher.setId(123L);
        deviceAccess.setPublisher(publisher);

        IdentityProvider identityProvider = new SamlEntity();
        identityProvider.setId(123L);
        deviceAccess.setIdentityProvider(identityProvider);

        DeviceAccess createdDeviceAccess = deviceAccessDao.create(deviceAccess).blockingGet();

        List<DeviceAccess> filterDeviceAccess = deviceAccessDao.filter(
                new DeviceAccessQuery()
                        .setDeviceIds(Lists.newArrayList(device.getId()))
                        .setNotIdps(Lists.newArrayList(identityProvider.getId()))).toList().blockingGet();

        assertEquals(0, filterDeviceAccess.size());
    }

    @Test
    public void testFilterType() {
        DeviceAccess deviceAccess = new DeviceAccess();
        deviceAccess.setLocalId("test-local-id");
        deviceAccess.setType(DeviceAccessType.READ_IDP_HISTORY);


        Device device = new Device();
        device.setId(Long.valueOf(new Random().nextInt(32000)));
        deviceAccess.setDevice(device);

        Publisher publisher = new Publisher();
        publisher.setId(123L);
        deviceAccess.setPublisher(publisher);

        IdentityProvider identityProvider = new SamlEntity();
        identityProvider.setId(123L);
        deviceAccess.setIdentityProvider(identityProvider);

        DeviceAccess createdDeviceAccess = deviceAccessDao.create(deviceAccess).blockingGet();

        List<DeviceAccess> filterDeviceAccess = deviceAccessDao.filter(
                new DeviceAccessQuery()
                        .setDeviceIds(Lists.newArrayList(device.getId()))
                        .setType(DeviceAccessType.ADD_IDP)).toList().blockingGet();

        assertEquals(0, filterDeviceAccess.size());

        filterDeviceAccess = deviceAccessDao.filter(
                new DeviceAccessQuery()
                        .setDeviceIds(Lists.newArrayList(device.getId()))
                        .setType(DeviceAccessType.READ_IDP_HISTORY)).toList().blockingGet();

        DeviceAccess filteredDeviceAccess = filterDeviceAccess.get(0);

        assertEquals(createdDeviceAccess.getId(), filteredDeviceAccess.getId());
        assertEquals(createdDeviceAccess.getCreatedDate(), filteredDeviceAccess.getCreatedDate());
        assertEquals(createdDeviceAccess.getModifiedDate(), filteredDeviceAccess.getModifiedDate());
        assertEquals(createdDeviceAccess.getLocalId(), filteredDeviceAccess.getLocalId());
        assertEquals(createdDeviceAccess.getDevice().getId(), filteredDeviceAccess.getDevice().getId());
        assertEquals(createdDeviceAccess.getIdentityProvider().getId(), filteredDeviceAccess.getIdentityProvider().getId());
        assertEquals(createdDeviceAccess.getPublisher().getId(), filteredDeviceAccess.getPublisher().getId());
        assertEquals(createdDeviceAccess.getType(), filteredDeviceAccess.getType());
    }
}

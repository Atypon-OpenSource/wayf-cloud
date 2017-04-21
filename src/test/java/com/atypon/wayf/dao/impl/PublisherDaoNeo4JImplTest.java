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
import com.atypon.wayf.data.device.DeviceStatus;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.data.publisher.PublisherStatus;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class PublisherDaoNeo4JImplTest {

    @Inject
    private PublisherDaoNeo4JImpl dao;

    @Before
    public void setUp() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);
    }

    @Test
    public void testCreate() {
        Publisher publisher = new Publisher();
        publisher.setName("Test Publisher");
        publisher.setStatus(PublisherStatus.ACTIVE);

        Publisher createdPublisher = dao.create(publisher);

        Assert.assertEquals(PublisherStatus.ACTIVE, createdPublisher.getStatus());
        Assert.assertNotNull(createdPublisher.getId());
        Assert.assertEquals("Test Publisher", createdPublisher.getName());
        Assert.assertNotNull(createdPublisher.getCreatedDate());
        Assert.assertNotNull(createdPublisher.getModifiedDate());
    }
}

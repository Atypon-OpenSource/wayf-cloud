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

package com.atypon.wayf.database;

import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.database.QueryMapper;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.publisher.Publisher;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class QueryMapperTest {

    @Test
    public void testQueryMapper() throws Exception {
        DeviceAccess deviceAccess = new DeviceAccess();
        deviceAccess.setId(456L);
        deviceAccess.setCreatedDate(new Date());
        deviceAccess.setModifiedDate(new Date());

        Device device = new Device();
        device.setGlobalId(UUID.randomUUID().toString());
        deviceAccess.setDevice(device);

        Publisher publisher = new Publisher();
        publisher.setId(123L);
        deviceAccess.setPublisher(publisher);

        Map<String, Object> arguments = QueryMapper.buildQueryArguments("SELECT id AS 'id', " +
                "        device_id AS 'device.globalId', " +
                "        publisher_id AS 'publisher.id' " +
                "        authenticated_by_id AS 'authenticatedBy.id', " +
                "        created_date AS 'createdDate',  " +
                "        modified_date AS 'modifiedDate' " +
                "    FROM wayf.publisher_session " +
                "        WHERE id = :id AND device_id = :device.globalId;", deviceAccess);

        Assert.assertEquals(deviceAccess.getId(), arguments.get("id"));
        Assert.assertEquals(deviceAccess.getDevice().getGlobalId(), arguments.get("device.globalId"));


    }
}

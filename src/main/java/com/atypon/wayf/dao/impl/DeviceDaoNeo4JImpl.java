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

import com.atypon.wayf.dao.DeviceDao;
import com.atypon.wayf.dao.QueryMapper;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.Institution;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class DeviceDaoNeo4JImpl implements DeviceDao {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceDaoNeo4JImpl.class);

    @Inject
    @Named("device.dao.neo4j.create")
    private String createCypher;

    @Inject@Named("device.dao.neo4j.read")
    private String readCypher;

    @Inject
    @Named("device.dao.neo4j.update")
    private String updateCypher;

    @Inject
    @Named("device.dao.neo4j.delete")
    private String deleteCypher;

    @Inject
    private Neo4JExecutor dbExecutor;

    public DeviceDaoNeo4JImpl() {
    }

    @Override
    public Device create(Device device) {
        LOG.debug("Creating device [{}] in Neo4J", device);

        device.setId(UUID.randomUUID().toString());
        device.setCreatedDate(new Date());
        device.setModifiedDate(new Date());

        Map<String, Object> arguments = QueryMapper.buildQueryArguments(createCypher, device);

        return dbExecutor.executeQuerySelectFirst(createCypher, arguments, Device.class);
    }

    @Override
    public Device read(String id) {
        Device device = new Device();
        device.setId(id);
        Map<String, Object> arguments = QueryMapper.buildQueryArguments(readCypher, device);

        return dbExecutor.executeQuerySelectFirst(readCypher, arguments, Device.class);
    }

    @Override
    public Device update(Device device) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}

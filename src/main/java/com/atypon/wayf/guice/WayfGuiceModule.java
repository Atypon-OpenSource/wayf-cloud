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

package com.atypon.wayf.guice;

import com.atypon.wayf.dao.*;
import com.atypon.wayf.dao.impl.*;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.facade.InstitutionFacade;
import com.atypon.wayf.facade.PublisherSessionFacade;
import com.atypon.wayf.facade.impl.DeviceFacadeImpl;
import com.atypon.wayf.facade.impl.IdentityProviderFacadeImpl;
import com.atypon.wayf.facade.impl.InstitutionFacadeImpl;
import com.atypon.wayf.facade.impl.PublisherSessionFacadeImpl;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class WayfGuiceModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(WayfGuiceModule.class);

    @Override
    protected void configure() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            Properties properties = new Properties();
            properties.load(classLoader.getResourceAsStream("dao/institution-dao-neo4j.properties"));
            properties.load(classLoader.getResourceAsStream("dao/publisher-session-dao-neo4j.properties"));
            properties.load(classLoader.getResourceAsStream("dao/publisher-dao-neo4j.properties"));
            properties.load(classLoader.getResourceAsStream("dao/device-dao-neo4j.properties"));
            properties.load(classLoader.getResourceAsStream("dao/identity-provider-dao-neo4j.properties"));

            Names.bindProperties(binder(), properties);

            bind(InstitutionFacade.class).to(InstitutionFacadeImpl.class);
            bind(InstitutionDao.class).to(InstitutionDaoNeo4JImpl.class);

            bind(PublisherSessionFacade.class).to(PublisherSessionFacadeImpl.class);
            bind(PublisherSessionDao.class).to(PublisherSessionDaoNeo4JImpl.class);

            bind(DeviceFacade.class).to(DeviceFacadeImpl.class);
            bind(DeviceDao.class).to(DeviceDaoNeo4JImpl.class);

            bind(PublisherDao.class).to(PublisherDaoNeo4JImpl.class);


            bind(PublisherSessionIdDao.class).to(PublisherSessionIdDaoRedisImpl.class);

            bind(IdentityProviderFacade.class).to(IdentityProviderFacadeImpl.class);
            bind(IdentityProviderDao.class).to(IdentityProviderDaoNeo4JImpl.class);
        } catch (Exception e) {
            LOG.error("Error initializing Guice", e);
            throw new RuntimeException(e);
        }
    }
}
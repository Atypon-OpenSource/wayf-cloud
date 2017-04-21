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
import com.atypon.wayf.dao.redis.RedisDao;
import com.atypon.wayf.dao.redis.impl.RedisDaoDefaultImpl;
import com.atypon.wayf.data.cache.CascadingCache;
import com.atypon.wayf.facade.*;
import com.atypon.wayf.facade.impl.*;
import com.google.inject.*;
import com.google.inject.name.Named;
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
            properties.load(classLoader.getResourceAsStream("dao/publisher-session-id-dao-neo4j.properties"));

            Names.bindProperties(binder(), properties);

            bind(InstitutionFacade.class).to(InstitutionFacadeImpl.class);
            bind(InstitutionDao.class).to(InstitutionDaoNeo4JImpl.class);

            bind(PublisherSessionFacade.class).to(PublisherSessionFacadeImpl.class);
            bind(PublisherSessionDao.class).to(PublisherSessionDaoNeo4JImpl.class);

            bind(DeviceFacade.class).to(DeviceFacadeImpl.class);
            bind(DeviceDao.class).to(DeviceDaoNeo4JImpl.class);

            bind(PublisherFacade.class).to(PublisherFacadeImpl.class);
            bind(PublisherDao.class).to(PublisherDaoNeo4JImpl.class);

            bind(IdentityProviderFacade.class).to(IdentityProviderFacadeImpl.class);
            bind(IdentityProviderDao.class).to(IdentityProviderDaoNeo4JImpl.class);

            bind(RedisDao.class)
                    .annotatedWith(Names.named("publisherIdRedisDao"))
                    .toProvider(() -> new RedisDaoDefaultImpl("PUBLISHER_ID"));

            bind(RedisDao.class)
                    .annotatedWith(Names.named("identityProviderRedisDao"))
                    .toProvider(() -> new RedisDaoDefaultImpl("IDENTITY_PROVIDER"));

            bind(new TypeLiteral<CascadingCache<String, String>>(){})
                    .annotatedWith(Names.named("publisherIdCache"))
                    .toProvider(new Provider<CascadingCache<String, String>>() {
                        @Inject
                        @Named("publisherIdRedisDao")
                        private RedisDao l1;

                        @Inject
                        private PublisherSessionIdDaoNeo4JImpl l2;

                        @Override
                        public CascadingCache<String, String> get() {
                            Guice.createInjector(new WayfGuiceModule()).injectMembers(this);
                            return new CascadingCache(l1, l2);
                        }
                    });

            bind(new TypeLiteral<CascadingCache<String, String>>(){})
                    .annotatedWith(Names.named("identityProviderCache"))
                    .toProvider(new Provider<CascadingCache<String, String>>() {
                        @Inject
                        @Named("identityProviderRedisDao")
                        private RedisDao l1;

                        @Inject
                        private IdentityProviderDaoNeo4JImpl l2;

                        @Override
                        public CascadingCache<String, String> get() {
                            Guice.createInjector(new WayfGuiceModule()).injectMembers(this);
                            return new CascadingCache(l1, l2);
                        }
                    });
        } catch (Exception e) {
            LOG.error("Error initializing Guice", e);
            throw new RuntimeException(e);
        }
    }
}
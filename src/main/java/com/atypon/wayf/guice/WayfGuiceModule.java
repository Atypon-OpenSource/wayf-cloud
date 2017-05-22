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
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.InflationPolicyParser;
import com.atypon.wayf.data.InflationPolicyParserQueryParamImpl;
import com.atypon.wayf.data.cache.CascadingCache;
import com.atypon.wayf.data.identity.IdentityProviderType;
import com.atypon.wayf.data.identity.OauthEntity;
import com.atypon.wayf.data.identity.OpenAthensEntity;
import com.atypon.wayf.data.identity.SamlEntity;
import com.atypon.wayf.database.AuthenticatableBeanFactory;
import com.atypon.wayf.database.BeanFactory;
import com.atypon.wayf.database.DbExecutor;
import com.atypon.wayf.facade.*;
import com.atypon.wayf.facade.impl.*;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WayfGuiceModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(WayfGuiceModule.class);

    @Override
    protected void configure() {
        try {
            Module module = this;
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            Properties properties = new Properties();
            properties.load(classLoader.getResourceAsStream("dao/device-access-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/publisher-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/device-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/open-athens-entity-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/oauth-entity-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/device-identity-provider-blacklist-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/authentication-dao-db.properties"));

            Names.bindProperties(binder(), properties);

            bind(AuthenticationDao.class).annotatedWith(Names.named("authenticationDaoRedisImpl")).to(AuthenticationDaoRedisImpl.class);
            bind(AuthenticationDao.class).annotatedWith(Names.named("authenticationDaoDbImpl")).to(AuthenticationDaoDbImpl.class);
            bind(AuthenticationFacade.class).to(AuthenticatableFacadeImpl.class);

            bind(DeviceAccessFacade.class).to(DeviceAccessFacadeImpl.class);
            bind(DeviceAccessDao.class).to(DeviceAccessDaoDbImpl.class);

            bind(DeviceFacade.class).to(DeviceFacadeImpl.class);
            bind(DeviceDao.class).to(DeviceDaoDbImpl.class);

            bind(PublisherFacade.class).to(PublisherFacadeImpl.class);
            bind(PublisherDao.class).to(PublisherDaoDbImpl.class);

            bind(IdentityProviderFacade.class).to(IdentityProviderFacadeImpl.class);

            bind(new TypeLiteral<InflationPolicyParser<String>>(){}).to(InflationPolicyParserQueryParamImpl.class);

            bind(RedisDao.class)
                    .annotatedWith(Names.named("publisherIdRedisDao"))
                    .toProvider(() -> new RedisDaoDefaultImpl("PUBLISHER_ID"));

            bind(RedisDao.class)
                    .annotatedWith(Names.named("identityProviderRedisDao"))
                    .toProvider(() -> new RedisDaoDefaultImpl("IDENTITY_PROVIDER"));

            bind(DeviceIdentityProviderBlacklistDao.class).to(DeviceIdentityProviderBlacklistDaoDbImpl.class);


            BasicDataSource dataSource = new BasicDataSource();

            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUsername("root");
            dataSource.setPassword("test");
            dataSource.setUrl("jdbc:mysql://localhost:3306/wayf");
            dataSource.setMaxActive(10);
            dataSource.setMaxIdle(5);
            dataSource.setInitialSize(5);
            dataSource.setValidationQuery("SELECT 1");

            bind(NamedParameterJdbcTemplate.class).toProvider(() -> new NamedParameterJdbcTemplate(dataSource));
        } catch (Exception e) {
            LOG.error("Error initializing Guice", e);
            throw new RuntimeException(e);
        }
    }

    @Provides @Named("samlEntity")
    public IdentityProviderDao provideSamlEntityDao(DbExecutor dbExecutor) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Properties properties = new Properties();
        properties.load(classLoader.getResourceAsStream("dao/saml-entity-dao-db.properties"));

        return new IdentityProviderDaoDbImpl(properties.getProperty("saml-entity.dao.db.create"),
                properties.getProperty("saml-entity.dao.db.read"),
                properties.getProperty("saml-entity.dao.db.filter"),
                dbExecutor,
                SamlEntity.class);
    }

    @Provides @Named("openAthensEntity")
    public IdentityProviderDao provideOpenAthensEntityDao(DbExecutor dbExecutor) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Properties properties = new Properties();

        properties.load(classLoader.getResourceAsStream("dao/open-athens-entity-dao-db.properties"));

        return new IdentityProviderDaoDbImpl(properties.getProperty("open-athens-entity.dao.db.create"),
                properties.getProperty("open-athens-entity.dao.db.read"),
                properties.getProperty("open-athens-entity.dao.db.filter"),
                dbExecutor,
                OpenAthensEntity.class);
    }

    @Provides @Named("oauthEntity")
    public IdentityProviderDao provideOauthEntityDao(DbExecutor dbExecutor) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Properties properties = new Properties();

        properties.load(classLoader.getResourceAsStream("dao/oauth-entity-dao-db.properties"));

        return new IdentityProviderDaoDbImpl(properties.getProperty("oauth-entity.dao.db.create"),
                properties.getProperty("oauth-entity.dao.db.read"),
                properties.getProperty("oauth-entity.dao.db.filter"),
                dbExecutor,
                OauthEntity.class);
    }

    @Provides @Named("identityProviderDaoMap")
    public Map<IdentityProviderType, IdentityProviderDao> provideIdentityProviderDaoMap(
            @Named("samlEntity") IdentityProviderDao samlDao,
            @Named("openAthensEntity") IdentityProviderDao openAthensDao,
            @Named("oauthEntity") IdentityProviderDao oauthDao) {
        Map<IdentityProviderType, IdentityProviderDao> daoMap = new HashMap<>();
        daoMap.put(IdentityProviderType.SAML, samlDao);
        daoMap.put(IdentityProviderType.OPEN_ATHENS, openAthensDao);
        daoMap.put(IdentityProviderType.OAUTH, oauthDao);
        return daoMap;
    }

    @Provides @Named("identityProviderCache")
    public CascadingCache<String, Long> provideIdentityProviderCache(@Named("identityProviderRedisDao") RedisDao l1, IdentityProviderFacade l2) {
        return new CascadingCache(l1, l2);
    }

    @Provides @Named("beanFactoryMap")
    public Map<Class<?>, BeanFactory<?>> provideBeanFactoryMap(AuthenticatableBeanFactory authenticatableBeanFactory) {
        Map<Class<?>, BeanFactory<?>> beanFactoryMap = new HashMap<>();

        beanFactoryMap.put(Authenticatable.class, authenticatableBeanFactory);

        return beanFactoryMap;
    }

    @Provides
    public JedisPool getJedisPool() {
        return new JedisPool(new JedisPoolConfig(), "localhost");
    }
}
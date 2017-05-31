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
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.InflationPolicyParser;
import com.atypon.wayf.data.InflationPolicyParserQueryParamImpl;
import com.atypon.wayf.data.identity.IdentityProviderType;
import com.atypon.wayf.data.identity.OauthEntity;
import com.atypon.wayf.data.identity.OpenAthensEntity;
import com.atypon.wayf.data.identity.SamlEntity;
import com.atypon.wayf.database.AuthenticatableBeanFactory;
import com.atypon.wayf.database.BeanFactory;
import com.atypon.wayf.database.DbExecutor;
import com.atypon.wayf.facade.*;
import com.atypon.wayf.facade.impl.*;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WayfGuiceModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(WayfGuiceModule.class);

    private static final String WAYF_CONFIG_FILE = "wayf.properties";

    @Override
    protected void configure() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            Properties properties = new Properties();

            String configDirectory = System.getProperty("wayf.conf.dir");
            String configFile = configDirectory == null? WAYF_CONFIG_FILE : configDirectory + "/" + WAYF_CONFIG_FILE;

            LOG.info("Loading wayf config file from location [{}]", configFile);

            FileReader reader = new FileReader(configFile);
            properties.load(reader);

            properties.load(classLoader.getResourceAsStream("dao/device-access-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/publisher-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/device-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/open-athens-entity-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/oauth-entity-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/device-identity-provider-blacklist-dao-db.properties"));
            properties.load(classLoader.getResourceAsStream("dao/authentication-dao-db.properties"));

            Names.bindProperties(binder(), properties);

            bind(DeviceIdentityProviderBlacklistFacade.class).to(DeviceIdentityProviderBlacklistFacadeImpl.class);
            bind(IdentityProviderUsageFacade.class).to(IdentityProviderUsageFacadeImpl.class);

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

            bind(DeviceIdentityProviderBlacklistDao.class).to(DeviceIdentityProviderBlacklistDaoDbImpl.class);
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

    @Provides @Named("beanFactoryMap")
    public Map<Class<?>, BeanFactory<?>> provideBeanFactoryMap(AuthenticatableBeanFactory authenticatableBeanFactory) {
        Map<Class<?>, BeanFactory<?>> beanFactoryMap = new HashMap<>();

        beanFactoryMap.put(Authenticatable.class, authenticatableBeanFactory);

        return beanFactoryMap;
    }

    @Provides
    public JedisPool getJedisPool(@Named("redis.host") String redisHost) {
        return new JedisPool(new JedisPoolConfig(), redisHost);
    }

    @Provides
    public NamedParameterJdbcTemplate getJdbcTemplate(
            @Named("jdbc.driver") String driver,
            @Named("jdbc.username") String username,
            @Named("jdbc.password") String password,
            @Named("jdbc.url") String url,
            @Named("jdbc.maxActive") Integer maxActive,
            @Named("jdbc.maxIdle") Integer maxIdle,
            @Named("jdbc.initialSize") Integer initialSize,
            @Named("jdbc.validationQuery") String validationQuery) {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(driver);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setInitialSize(initialSize);
        dataSource.setValidationQuery(validationQuery);

        return new NamedParameterJdbcTemplate(dataSource);
    }
}
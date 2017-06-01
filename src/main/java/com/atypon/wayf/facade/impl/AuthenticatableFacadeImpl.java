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

import com.atypon.wayf.dao.AuthenticationDao;
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.facade.AuthenticationFacade;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Single;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AuthenticatableFacadeImpl implements AuthenticationFacade {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatableFacadeImpl.class);

    protected LoadingCache<String, Authenticatable> l1Cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build(
                    new CacheLoader<String, Authenticatable>() {
                        @Override
                        public Authenticatable load(String token) throws Exception {
                            LOG.debug("Reading from Redis Cache");

                            return redisCache.authenticate(token).blockingGet();
                        }
                    }
            );

    @Inject
    @Named("authenticationDaoRedisImpl")
    protected AuthenticationDao redisCache;

    @Inject
    @Named("authenticationDaoDbImpl")
    protected AuthenticationDao dbDao;

    @Override
    public Single<String> createToken(Authenticatable authenticatable) {
        String token = UUID.randomUUID().toString();

        return dbDao.create(token, authenticatable).toSingleDefault(token);
    }

    @Override
    public Authenticatable authenticate(String token) {
        LOG.debug("Authenticating token");

        try {
            return l1Cache.get(token);
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Could not authenticate token", e);
        }
    }
}

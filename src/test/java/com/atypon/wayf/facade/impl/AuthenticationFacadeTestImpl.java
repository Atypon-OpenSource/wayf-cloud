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

import com.atypon.wayf.cache.LoadingCache;
import com.atypon.wayf.dao.AuthenticationDao;
import com.atypon.wayf.dao.impl.AuthorizationTokenDaoDbImpl;
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.AuthenticationCredentials;
import com.atypon.wayf.data.AuthorizationToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AuthenticationFacadeTestImpl extends AuthenticationFacadeImpl {
    private LoadingCache<AuthenticationCredentials, Authenticatable> redisCache;

    public void setL1Cache(LoadingCache<AuthenticationCredentials, Authenticatable> l1Cache) {
        super.cache = l1Cache;
    }

    public LoadingCache<AuthenticationCredentials, Authenticatable> getL1Cache() {
        return cache;
    }

    public AuthenticationDao getDbDao() {
        return authorizationTokenDao;
    }

    @Inject
    public void setDbDao(AuthorizationTokenDaoDbImpl dao) {
        super.authorizationTokenDao = dao;
    }

    @Inject
    public void setRedisDao(@Named("authenticatableRedisCache") LoadingCache<AuthenticationCredentials, Authenticatable> redisCache) {
        this.redisCache = redisCache;
    }

    public LoadingCache<AuthenticationCredentials, Authenticatable> getRedisCache() {
        return redisCache;
    }
}

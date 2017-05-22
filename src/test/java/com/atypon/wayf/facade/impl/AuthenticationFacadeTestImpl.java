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
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AuthenticationFacadeTestImpl extends AuthenticatableFacadeImpl {

    public void setL1Cache(LoadingCache<String, Authenticatable> l1Cache) {
        super.l1Cache = l1Cache;
    }

    public LoadingCache<String, Authenticatable> getL1Cache() {
        return l1Cache;
    }

    public AuthenticationDao getDbDao() {
        return dbDao;
    }

    @Inject
    public void setDbDao(@Named("authenticationDaoDbImpl") AuthenticationDao dao) {
        super.dbDao = dao;
    }

    @Inject
    public void setRedisDao(@Named("authenticationDaoRedisImpl") AuthenticationDao dao) {
        super.redisCache = dao;
    }

    public AuthenticationDao getRedisDao() {
        return redisCache;
    }
}

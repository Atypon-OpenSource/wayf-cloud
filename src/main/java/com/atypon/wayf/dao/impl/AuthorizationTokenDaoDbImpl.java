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

import com.atypon.wayf.dao.AuthenticationDao;
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.database.DbExecutor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthorizationTokenDaoDbImpl implements AuthenticationDao<AuthorizationToken> {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationTokenDaoDbImpl.class);

    @Inject
    @Named("authorization-token.dao.db.create")
    private String createSql;

    @Inject
    @Named("authorization-token.dao.db.authenticate")
    private String authenticateSql;

    @Inject
    private DbExecutor dbExecutor;

    @Override
    public Completable create(Authenticatable<AuthorizationToken> authenticatable) {
        LOG.debug("Creating authentication policy for [{}]", authenticatable);

        return dbExecutor.executeUpdate(createSql, authenticatable).toCompletable();
    }

    @Override
    public Maybe<Authenticatable> authenticate(AuthorizationToken token) {
        LOG.debug("Authenticating");

        return dbExecutor.executeSelectFirst(authenticateSql, token, Authenticatable.class);
    }
}

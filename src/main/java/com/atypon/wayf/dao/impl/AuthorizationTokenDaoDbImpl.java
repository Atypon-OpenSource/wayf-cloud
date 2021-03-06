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

import com.atypon.wayf.dao.AuthenticationCredentialsDao;
import com.atypon.wayf.data.authentication.Authenticatable;
import com.atypon.wayf.data.authentication.AuthenticatedEntity;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.database.DbExecutor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthorizationTokenDaoDbImpl implements AuthenticationCredentialsDao<AuthorizationToken> {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationTokenDaoDbImpl.class);

    @Inject
    @Named("authorization-token.dao.db.create")
    private String createSql;

    @Inject
    @Named("authorization-token.dao.db.authenticate")
    private String authenticateSql;

    @Inject
    @Named("authorization-token.dao.db.tokens-for-authenticatable")
    private String tokensForAuthenticatableSql;

    @Inject
    @Named("authorization-token.dao.db.delete")
    private String deleteSql;

    @Inject
    private DbExecutor dbExecutor;

    @Override
    public Completable create(AuthorizationToken token) {
        LOG.debug("Creating authentication policy  [{}]", token);

        return dbExecutor.executeUpdate(createSql, token).toCompletable();
    }

    @Override
    public Maybe<AuthenticatedEntity> authenticate(AuthorizationToken token) {
        LOG.debug("Authenticating");

        return dbExecutor.executeSelectFirst(authenticateSql, token, AuthenticatedEntity.class);
    }

    @Override
    public Observable<AuthorizationToken> getCredentialsForAuthenticatable(Authenticatable authenticatable) {
        LOG.debug("Selecting tokens for [{}-{}]", authenticatable.getAuthenticatableType(), authenticatable.getId());

        return dbExecutor.executeSelect(tokensForAuthenticatableSql, authenticatable, AuthorizationToken.class);
    }

    @Override
    public Completable delete(AuthorizationToken credentials) {
        LOG.debug("Deleting credentials for [{}-{}]", credentials.getType(), credentials.getValue());

        return dbExecutor.executeUpdate(deleteSql, credentials).toCompletable();
    }
}

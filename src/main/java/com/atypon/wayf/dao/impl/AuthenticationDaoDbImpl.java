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
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.database.DbExecutor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationDaoDbImpl implements AuthenticationDao {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationDaoDbImpl.class);

    private static final String TOKEN = "token";
    public static final String AUTHENTICATABLE_ID = "id";
    public static final String AUTHENTICATABLE_TYPE = "type";

    @Inject
    @Named("authentication.dao.db.create")
    private String createSql;

    @Inject
    @Named("authentication.dao.db.authenticate")
    private String authenticateSql;

    @Inject
    private DbExecutor dbExecutor;

    @Override
    public Completable create(String token, Authenticatable authenticatable) {
        LOG.debug("Creating authentication policy for [{}]", authenticatable);

        Map<String, Object> args = new HashMap<>();
        args.put(TOKEN, token);
        args.put(AUTHENTICATABLE_ID, authenticatable.getId());
        args.put(AUTHENTICATABLE_TYPE, authenticatable.getType().toString());

        return dbExecutor.executeUpdate(createSql, args).toCompletable();
    }

    @Override
    public Maybe<Authenticatable> authenticate(String token) {
        LOG.debug("Authenticating");

        Map<String, Object> args = new HashMap<>();
        args.put(TOKEN, token);

        return dbExecutor.executeSelectFirst(authenticateSql, args, Authenticatable.class)
                .doOnError((e) -> {throw new ServiceException(HttpStatus.SC_UNAUTHORIZED);});
    }
}

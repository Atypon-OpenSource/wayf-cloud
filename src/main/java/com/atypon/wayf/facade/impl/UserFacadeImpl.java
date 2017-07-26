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

import com.atypon.wayf.dao.UserDao;
import com.atypon.wayf.data.user.User;
import com.atypon.wayf.data.user.UserQuery;
import com.atypon.wayf.facade.UserFacade;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.google.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFacadeImpl implements UserFacade {
    private static final Logger LOG = LoggerFactory.getLogger(UserFacadeImpl.class);

    @Inject
    private UserDao dao;

    @Override
    public Single<User> create(User user) {
        LOG.debug("Creating user [{}]", user);

        return dao.create(user)
                .compose((single) -> FacadePolicies.applySingle(single));
    }

    @Override
    public Single<User> read(Long id) {
        LOG.debug("Reading user with ID [{}]", id);

        return FacadePolicies.singleOrException(
                dao.read(id).
                        compose((maybe) -> FacadePolicies.applyMaybe(maybe)),
                HttpStatus.SC_NOT_FOUND,
                "Could not read User with id {}", id);
    }

    @Override
    public Observable<User> filter(UserQuery query) {
        LOG.debug("Filtering users for [{}]", query);

        return dao.filter(query)
                .compose((observable) -> FacadePolicies.applyObservable(observable));
    }
}

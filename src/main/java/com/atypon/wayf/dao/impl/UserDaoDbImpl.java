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

import com.atypon.wayf.dao.UserDao;
import com.atypon.wayf.data.publisher.registration.PublisherRegistration;
import com.atypon.wayf.data.publisher.registration.PublisherRegistrationQuery;
import com.atypon.wayf.data.user.User;
import com.atypon.wayf.data.user.UserQuery;
import com.atypon.wayf.database.DbExecutor;
import com.atypon.wayf.reactivex.DaoPolicies;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UserDaoDbImpl implements UserDao {
    private static final Logger LOG = LoggerFactory.getLogger(UserDaoDbImpl.class);

    @Inject
    @Named("user.dao.db.create")
    private String createSql;

    @Inject
    @Named("user.dao.db.read")
    private String readSql;

    @Inject
    @Named("user.dao.db.filter")
    private String filterSql;

    @Inject
    @Named("user.dao.db.delete")
    private String deleteSql;

    @Inject
    private DbExecutor dbExecutor;

    public UserDaoDbImpl() {
    }

    @Override
    public Single<User> create(User user) {
        LOG.debug("Creating user [{}] in the DB", user);

        return Single.just(user)
                .compose((single) -> DaoPolicies.applySingle(single))
                .flatMap((_user) -> dbExecutor.executeUpdate(createSql, _user))
                .flatMapMaybe((genId) -> read(genId))
                .toSingle();
    }

    @Override
    public Maybe<User> read(Long id) {
        LOG.debug("Reading user with id [{}] in db", id);

        User user = new User();
        user.setId(id);

        return Single.just(user)
                .compose((single) -> DaoPolicies.applySingle(single))
                .flatMapMaybe((_user) -> dbExecutor.executeSelectFirst(readSql, _user, User.class));
    }

    @Override
    public Observable<User> filter(UserQuery filter) {
        LOG.debug("Filtering users against [{}]", filter);

        return Observable.just(filter)
                .compose((observable) -> DaoPolicies.applyObservable(observable))
                .flatMap((_filter) -> dbExecutor.executeSelect(filterSql, _filter, User.class));
    }

    @Override
    public Completable delete(Long id) {
        LOG.debug("Deleting user with id [{}]", id);

        User user = new User();
        user.setId(id);

        return dbExecutor.executeUpdate(deleteSql, user).toCompletable();
    }
}

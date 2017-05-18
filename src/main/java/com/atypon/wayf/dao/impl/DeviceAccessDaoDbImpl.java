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

import com.atypon.wayf.dao.DeviceAccessDao;
import com.atypon.wayf.data.cache.KeyValueCache;
import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.data.device.access.DeviceAccessQuery;
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

import java.util.HashMap;
import java.util.Map;

@Singleton
public class DeviceAccessDaoDbImpl implements DeviceAccessDao, KeyValueCache<String, Long> {
    private static Logger LOG = LoggerFactory.getLogger(com.atypon.wayf.dao.impl.DeviceAccessDaoDbImpl.class);

    @Inject
    @Named("publisher-session.dao.db.create")
    private String createSql;

    @Inject
    @Named("publisher-session.dao.db.read")
    private String readSql;

    @Inject
    @Named("publisher-session.dao.db.update")
    private String updateSql;

    @Inject
    @Named("publisher-session.dao.db.delete")
    private String deleteSql;

    @Inject
    @Named("publisher-session.dao.db.filter")
    private String filterSql;

    @Inject
    private DbExecutor dbExecutor;

    public DeviceAccessDaoDbImpl() {
    }

    @Override
    public Single<DeviceAccess> create(DeviceAccess deviceAccess) {
        return Single.just(deviceAccess)
                .compose((single) -> DaoPolicies.applySingle(single))
                .flatMap((_deviceAccess) -> dbExecutor.executeUpdate(createSql, deviceAccess))
                .flatMapMaybe((genId) -> read(deviceAccess.getId()))
                .toSingle();
    }

    @Override
    public Maybe<DeviceAccess> read(Long id) {
        DeviceAccess session = new DeviceAccess();
        session.setId(id);

        return Maybe.just(session)
                .compose((maybe) -> DaoPolicies.applyMaybe(maybe))
                .flatMap((_session) -> dbExecutor.executeSelectFirst(readSql, _session, DeviceAccess.class));
    }

    @Override
    public Single<DeviceAccess> update(DeviceAccess deviceAccess) {
        return Single.just(deviceAccess)
                .compose((single) -> DaoPolicies.applySingle(single))
                .flatMap((_deviceAccess) -> dbExecutor.executeUpdate(updateSql, deviceAccess))
                .flatMapMaybe((genId) -> read(deviceAccess.getId()))
                .toSingle();
    }

    @Override
    public Completable delete(Long id) {
        Map<String, Object> args = new HashMap<>();
        args.put("id", id);

        return Completable.fromSingle(dbExecutor.executeUpdate(deleteSql, args))
                .compose((completable) -> DaoPolicies.applyCompletable(completable));
    }

    @Override
    public Observable<DeviceAccess> filter(DeviceAccessQuery query) {
        return Observable.just(query)
                .compose((observable) -> DaoPolicies.applyObservable(observable))
                .flatMap((_query) -> dbExecutor.executeSelect(filterSql, _query, DeviceAccess.class));
    }

    @Override
    public Completable put(String publisherId, Long wayfId) {
        return null;
    }

    @Override
    public Maybe<Long> get(String publisherId) {
        DeviceAccessQuery query = new DeviceAccessQuery().setLocalId(publisherId);

        return Maybe.just(query)
                .compose((maybe) -> DaoPolicies.applyMaybe(maybe))
                .flatMap((_query) -> dbExecutor.executeSelectFirst(filterSql, _query, DeviceAccess.class))
                .map((_deviceAccess) -> _deviceAccess.getId());
    }
}

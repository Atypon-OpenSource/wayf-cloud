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

import com.atypon.wayf.dao.IdentityProviderDao;
import com.atypon.wayf.dao.QueryMapper;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.IdentityProvider;
import com.atypon.wayf.data.cache.KeyValueCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class IdentityProviderDaoNeo4JImpl implements IdentityProviderDao, KeyValueCache<String, String> {

    @Inject
    @Named("identity-provider.dao.neo4j.create")
    private String createCypher;

    @Inject
    @Named("identity-provider.dao.neo4j.read")
    private String readCypher;

    @Inject
    @Named("identity-provider.dao.neo4j.get-by-entity-id")
    private String getByEntityIdCypher;

    @Inject
    private Neo4JExecutor dbExecutor;

    public IdentityProviderDaoNeo4JImpl() {
    }

    @Override
    public Single<IdentityProvider> create(IdentityProvider identityProvider) {
        return Single.just(identityProvider)
                .observeOn(Schedulers.io())
                .map((identityProviderToWrite) -> {
                    Map<String, Object> args = QueryMapper.buildQueryArguments(createCypher, identityProvider);

                    return dbExecutor.executeQuerySelectFirst(createCypher, args, IdentityProvider.class);
                });
    }

    @Override
    public Single<IdentityProvider> read(String id) {
        return Single.just(id)
                .observeOn(Schedulers.io())
                .map((_id) -> {
                    IdentityProvider provider = new IdentityProvider();
                    provider.setId(_id);

                    Map<String, Object> args = QueryMapper.buildQueryArguments(readCypher, provider);

                    return dbExecutor.executeQuerySelectFirst(readCypher, args, IdentityProvider.class);
                });
    }

    @Override
    public Maybe<String> get(String key) {
        return Maybe.just(key)
                .observeOn(Schedulers.io())
                .flatMap((keyToRead) -> {
                    Map<String, Object> args = new HashMap<>();
                    args.put("entityId", key);

                    IdentityProvider result = dbExecutor.executeQuerySelectFirst(getByEntityIdCypher, args, IdentityProvider.class);

                    return result == null? Maybe.empty() : Maybe.just(result.getId());
                });
    }

    @Override
    public Completable put(String key, String value) {
        return null;
    }
}

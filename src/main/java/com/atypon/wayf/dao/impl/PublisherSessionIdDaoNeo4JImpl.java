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

import com.atypon.wayf.dao.QueryMapper;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.cache.KeyValueCache;
import com.atypon.wayf.data.publisher.PublisherSession;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

import java.util.Map;

@Singleton
public class PublisherSessionIdDaoNeo4JImpl implements KeyValueCache<String, String> {

    @Inject
    @Named("publisher-session-id.dao.neo4j.findInternal")
    private String readInternalIdCypher;

    @Inject
    private Neo4JExecutor dbExecutor;

    public PublisherSessionIdDaoNeo4JImpl() {
    }

    @Override
    public Completable put(String publisherId, String wayfId) {
        return null;
    }

    @Override
    public Maybe<String> get(String publisherId) {
        return Maybe.just(publisherId)
                .observeOn(Schedulers.io())
                .map((o_publisherId) -> {
                        PublisherSession session = new PublisherSession();
                        session.setLocalId(publisherId);

                        Map<String, Object> args = QueryMapper.buildQueryArguments(readInternalIdCypher, session);

                        session = dbExecutor.executeQuerySelectFirst(readInternalIdCypher, args, PublisherSession.class);
                        return session.getId();
                });
    }
}

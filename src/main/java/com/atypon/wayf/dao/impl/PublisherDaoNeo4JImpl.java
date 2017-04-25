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

import com.atypon.wayf.dao.PublisherDao;
import com.atypon.wayf.dao.QueryMapper;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.data.publisher.PublisherFilter;
import com.atypon.wayf.reactivex.DaoPolicies;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@Singleton
public class PublisherDaoNeo4JImpl implements PublisherDao {
    private static final Logger LOG = LoggerFactory.getLogger(PublisherDaoNeo4JImpl.class);

    @Inject
    @Named("publisher.dao.neo4j.create")
    private String createCypher;

    @Inject
    @Named("publisher.dao.neo4j.read")
    private String readCypher;

    @Inject
    @Named("publisher.dao.neo4j.update")
    private String updateCypher;

    @Inject
    @Named("publisher.dao.neo4j.delete")
    private String deleteCypher;

    @Inject
    @Named("publisher.dao.neo4j.filter")
    private String filterCypher;

    @Inject
    private Neo4JExecutor dbExecutor;

    public PublisherDaoNeo4JImpl() {
    }

    @Override
    public Single<Publisher> create(Publisher publisher) {
        LOG.debug("Creating publisher [{}] in Neo4J", publisher);

        publisher.setId(UUID.randomUUID().toString());
        publisher.setCreatedDate(new Date());
        publisher.setModifiedDate(new Date());

        return Single.just(publisher)
                .compose((single) -> DaoPolicies.applySingle(single))
                .map((_publisher) -> QueryMapper.buildQueryArguments(createCypher, publisher))
                .map((arguments) -> dbExecutor.executeQuerySelectFirst(createCypher, arguments, Publisher.class));
    }

    @Override
    public Single<Publisher> read(String id) {
        LOG.debug("Reading publisher with id [{}] in Neo4J", id);

        Publisher publisher = new Publisher();
        publisher.setId(id);

        return Single.just(publisher)
                .compose((single) -> DaoPolicies.applySingle(single))
                .map((_publisher) -> QueryMapper.buildQueryArguments(readCypher, publisher))
                .map((arguments) -> dbExecutor.executeQuerySelectFirst(readCypher, arguments, Publisher.class));
    }

    @Override
    public Single<Publisher> update(Publisher publisher) {
        return null;
    }

    @Override
    public Completable delete(String id) {
        return Completable.complete();
    }

    @Override
    public Observable<Publisher> filter(PublisherFilter filter) {
        return Observable.just(filter)
                .compose((observable) -> DaoPolicies.applyObservable(observable))
                .map((_filter) ->  QueryMapper.buildQueryArguments(filterCypher, filter))
                .flatMap((arguments) -> dbExecutor.executeQuery(filterCypher, arguments, Publisher.class));
    }
}

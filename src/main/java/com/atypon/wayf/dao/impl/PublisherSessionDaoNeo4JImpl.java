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

import com.atypon.wayf.dao.PublisherSessionDao;
import com.atypon.wayf.dao.QueryMapper;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.publisher.PublisherSession;
import com.atypon.wayf.data.publisher.PublisherSessionFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Singleton
public class PublisherSessionDaoNeo4JImpl implements PublisherSessionDao {
    private static Logger LOG = LoggerFactory.getLogger(PublisherSessionDaoNeo4JImpl.class);

    @Inject
    @Named("publisher-session.dao.neo4j.create")
    private String createCypher;

    @Inject
    @Named("publisher-session.dao.neo4j.read")
    private String readCypher;

    @Inject
    @Named("publisher-session.dao.neo4j.update")
    private String updateCypher;

    @Inject
    @Named("publisher-session.dao.neo4j.delete")
    private String deleteCypher;

    @Inject
    @Named("publisher-session.dao.neo4j.add-idp-relationship")
    private String addIdpRelationshipCypher;

    @Inject
    @Named("publisher-session.dao.neo4j.filter")
    private String filterCypher;

    @Inject
    private Neo4JExecutor dbExecutor;

    public PublisherSessionDaoNeo4JImpl() {
    }

    @Override
    public PublisherSession create(PublisherSession publisherSession) {
        LOG.debug("Creating publisher session [{}] in Neo4J", publisherSession);

        publisherSession.setCreatedDate(new Date());
        publisherSession.setModifiedDate(new Date());

        Map<String, Object> arguments = QueryMapper.buildQueryArguments(createCypher, publisherSession);

        return dbExecutor.executeQuerySelectFirst(createCypher, arguments, PublisherSession.class);
    }

    @Override
    public PublisherSession read(String id) {
        PublisherSession session = new PublisherSession();
        session.setId(id);

        Map<String, Object> arguments = QueryMapper.buildQueryArguments(createCypher, session);

        return dbExecutor.executeQuerySelectFirst(readCypher, arguments, PublisherSession.class);
    }

    @Override
    public PublisherSession update(PublisherSession publisherSession) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public Completable addIdpRelationship(PublisherSession publisherSession) {
        LOG.debug("Adding IDP relationship");

        return Single.just(publisherSession)
                .observeOn(Schedulers.io())
                .map((o_publisherSession) -> {
                    Map<String, Object> arguments = QueryMapper.buildQueryArguments(addIdpRelationshipCypher, publisherSession);

                    return dbExecutor.executeQuerySelectFirst(addIdpRelationshipCypher, arguments, PublisherSession.class);
                }).toCompletable();

    }

    @Override
    public PublisherSession[] filter(PublisherSessionFilter filterCriteria) {
        LOG.debug("Filtering in Neo4J for criteria [{}]", filterCriteria);

        Map<String, Object> arguments = QueryMapper.buildQueryArguments(filterCypher, filterCriteria);

        List<PublisherSession> publisherSessions = dbExecutor.executeQuery(filterCypher, arguments, PublisherSession.class);

        return publisherSessions.toArray(new PublisherSession[0]);
    }
}

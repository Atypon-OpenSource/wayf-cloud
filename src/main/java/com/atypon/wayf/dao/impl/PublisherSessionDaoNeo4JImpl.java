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
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.publisher.PublisherSession;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class PublisherSessionDaoNeo4JImpl implements PublisherSessionDao {
    private static Logger LOG = LoggerFactory.getLogger(PublisherSessionDaoNeo4JImpl.class);

    private String createCypher;
    private String readCypher;
    private String updateCypher;
    private String deleteCypher;
    private String addIdpRelationshipCypher;

    @Inject
    public PublisherSessionDaoNeo4JImpl(
            @Named("publisher-session.dao.neo4j.create") String createCypher,
            @Named("publisher-session.dao.neo4j.read") String readCypher,
            @Named("publisher-session.dao.neo4j.update")  String updateCypher,
            @Named("publisher-session.dao.neo4j.delete") String deleteCypher,
            @Named("publisher-session.dao.neo4j.add-idp-relationship") String addIdpRelationshipCypher) {
        this.createCypher = createCypher;
        this.readCypher = readCypher;
        this.updateCypher = updateCypher;
        this.deleteCypher = deleteCypher;
        this.addIdpRelationshipCypher = addIdpRelationshipCypher;
    }

    @Override
    public PublisherSession create(PublisherSession publisherSession) {
        LOG.debug("Creating publisher session [{}] in Neo4J", publisherSession);

        publisherSession.setCreatedDate(new Date());
        publisherSession.setModifiedDate(new Date());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", publisherSession.getId());
        arguments.put("publisherId", publisherSession.getPublisherId());
        arguments.put("status", publisherSession.getStatus().toString());
        arguments.put("lastActiveDate", publisherSession.getLastActiveDate().getTime());
        arguments.put("device_id", publisherSession.getDevice().getId());
        arguments.put("publisher_id", publisherSession.getPublisher().getId());
        arguments.put("createdDate", publisherSession.getCreatedDate().getTime());
        arguments.put("modifiedDate", publisherSession.getModifiedDate().getTime());

        return Neo4JExecutor.executeQuery(createCypher, arguments, PublisherSession.class).get(0);
    }

    @Override
    public PublisherSession read(String id) {
        return null;
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
                    Map<String, Object> arguments = new HashMap<>();
                    arguments.put("publisherSessionId", "666e9a41-01ae-4d9b-9294-ce05597ddd69");
                    arguments.put("idpId", o_publisherSession.getIdp().getId());

                    return Neo4JExecutor.executeQuery(addIdpRelationshipCypher, arguments, PublisherSession.class);
                }).toCompletable();

    }
}

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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PublisherDaoNeo4JImpl implements PublisherDao {
    private static final Logger LOG = LoggerFactory.getLogger(PublisherDaoNeo4JImpl.class);

    private String createCypher;
    private String readCypher;
    private String updateCypher;
    private String deleteCypher;

    @Inject
    public PublisherDaoNeo4JImpl(
            @Named("publisher.dao.neo4j.create") String createCypher,
            @Named("publisher.dao.neo4j.read") String readCypher,
            @Named("publisher.dao.neo4j.update")  String updateCypher,
            @Named("publisher.dao.neo4j.delete") String deleteCypher) {
        this.createCypher = createCypher;
        this.readCypher = readCypher;
        this.updateCypher = updateCypher;
        this.deleteCypher = deleteCypher;
    }

    @Override
    public Publisher create(Publisher publisher) {
        LOG.debug("Creating publisher [{}] in Neo4J", publisher);

        publisher.setId(UUID.randomUUID().toString());
        publisher.setCreatedDate(new Date());
        publisher.setModifiedDate(new Date());

        Map<String, Object> arguments = QueryMapper.buildQueryArguments(createCypher, publisher);

        return Neo4JExecutor.executeQuery(createCypher, arguments, Publisher.class).get(0);
    }

    @Override
    public Publisher read(String id) {
        Publisher publisher = new Publisher();
        publisher.setId(id);


        Map<String, Object> arguments = QueryMapper.buildQueryArguments(readCypher, publisher);

        return Neo4JExecutor.executeQuery(readCypher, arguments, Publisher.class).get(0);
    }

    @Override
    public Publisher update(Publisher publisher) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}

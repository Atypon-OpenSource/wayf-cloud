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

import com.atypon.wayf.dao.InstitutionDao;
import com.atypon.wayf.dao.neo4j.Neo4JBatchWriter;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.Institution;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Singleton
public class InstitutionDaoNeo4JImpl implements InstitutionDao {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionDaoNeo4JImpl.class);

    @Inject
    @Named("institution.dao.neo4j.create")
    private String createCypher;

    @Inject
    @Named("institution.dao.neo4j.read")
    private String readCypher;

    @Inject
    @Named("institution.dao.neo4j.update")
    private String updateCypher;

    @Inject
    @Named("institution.dao.neo4j.delete")
    private String deleteCypher;

    @Inject
    private Neo4JExecutor dbExecutor;

    public InstitutionDaoNeo4JImpl() {
    }

    @Override
    public Institution create(Institution institution) {
        LOG.debug("Creating institution [{}] in Neo4J", institution);

        institution.setId(UUID.randomUUID().toString());

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", institution.getId());
        arguments.put("name", institution.getName());
        arguments.put("description", institution.getDescription());

        if (RequestContextAccessor.get().isForceSync()) {
            LOG.debug("Running create in sync mode");
            return dbExecutor.executeQuerySelectFirst(createCypher, arguments, Institution.class);
        } else {
            Neo4JBatchWriter.INSTANCE.queue(createCypher, arguments);
        }

        return institution;
    }

    @Override
    public Institution read(String id) {
        LOG.debug("Reading institution with [{}] from Neo4J", id);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", id);

        return dbExecutor.executeQuerySelectFirst(readCypher, arguments, Institution.class);
    }

    @Override
    public Institution update(Institution institution) {
        LOG.debug("Updating institution [{}] in Neo4J", institution);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", institution.getId());
        arguments.put("name", institution.getName());
        arguments.put("description", institution.getDescription());

        return dbExecutor.executeQuerySelectFirst(updateCypher, arguments, Institution.class);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Deleting institution with id [{}] in Neo4J", id);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", id);

        dbExecutor.executeQuerySelectFirst(deleteCypher, arguments, null);
    }
}

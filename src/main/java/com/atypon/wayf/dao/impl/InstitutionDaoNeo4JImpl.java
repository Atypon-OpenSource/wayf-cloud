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
import com.atypon.wayf.data.Institution;
import com.atypon.wayf.request.RequestContextAccessor;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InstitutionDaoNeo4JImpl implements InstitutionDao {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionDaoNeo4JImpl.class);

    public static Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "test", "test" ) );

    // Eventually we'll want to source these from a properties file
    private static final String CREATE_CYPHER = "CREATE (i:Institution {id:{id}, name:{name}, description:{description}});";// RETURN i.id AS id, i.name AS name, i.description AS description;";
    private static final String READ_CYPHER = "MATCH (i:Institution) WHERE i.id = {id} RETURN i.id AS id, i.name AS name, i.description AS description;";
    private static final String UPDATE_CYPHER = "MATCH (i:Institution) WHERE i.id = {id} SET i.name = {name}, i.description = {description} RETURN i.id AS id, i.name AS name, i.description AS description;";
    private static final String DELETE_CYPHER = "MATCH (i:Institution) WHERE i.id = {id} DETACH DELETE i";

    public InstitutionDaoNeo4JImpl() {}

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
            executeQuery(CREATE_CYPHER, arguments);
        } else {
            Neo4JBatchWriter.INSTANCE.queue(CREATE_CYPHER, arguments);
        }

        return institution;
    }

    @Override
    public Institution read(String id) {
        LOG.debug("Reading institution with [{}] from Neo4J", id);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", id);

        return executeQuery(READ_CYPHER, arguments).get(0);
    }

    @Override
    public Institution update(Institution institution) {
        LOG.debug("Updating institution [{}] in Neo4J", institution);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", institution.getId());
        arguments.put("name", institution.getName());
        arguments.put("description", institution.getDescription());

        return executeQuery(UPDATE_CYPHER, arguments).get(0);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Deleting institution with id [{}] in Neo4J", id);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("id", id);

        executeQuery(DELETE_CYPHER, arguments);
    }

    private List<Institution> executeQuery(String query, Map<String, Object> arguments) {
        Session session = driver.session();

        LOG.debug("Got session");
        StatementResult result = session.run( query, Values.value(arguments ) );

        List<Institution> institutions = new LinkedList<>();

        while (result.hasNext()) {
            Record record = result.next();

            Institution institution = new Institution();
            institution.setId(record.get("id").asString());
            institution.setName(record.get("name").asString());
            institution.setDescription(record.get("description").asString());

            institutions.add(institution);
        }

        session.close();

        return institutions;
    }

}

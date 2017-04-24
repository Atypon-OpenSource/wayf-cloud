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

package com.atypon.wayf.dao.neo4j;

import com.atypon.wayf.dao.ResultSetProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Singleton
public class Neo4JExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4JExecutor.class);

    @Inject
    private Driver driver;

    private ResultSetProcessor processor;

    public Neo4JExecutor() {
        processor = new ResultSetProcessor();
    }

    public <T> T executeQuerySelectFirst(String query, Map<String, Object> arguments, Class<T> returnType) {
        LOG.debug("Running statement[{}] with values[{}]", query, arguments);

        try (Session session = driver.session()){
            StatementResult result = session.run(query, arguments);

            return (returnType != null && result.hasNext())? processor.processRow(result.next().asMap(), returnType) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> executeQuery(String query, Map<String, Object> arguments, Class<T> returnType) {
        LOG.debug("Running statement[{}] with values[{}]", query, arguments);


        try (Session session = driver.session()){
            StatementResult result = session.run(query, arguments);

            List<T> returnValues = new LinkedList<>();

            while (result.hasNext()) {
                Record record = result.next();
                T returnValue = processor.processRow(record.asMap(), returnType);

                returnValues.add(returnValue);
            }

            return returnValues;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
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
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Neo4JExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4JExecutor.class);

    public static Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("test", "test"));

    private static final ResultSetProcessor processor = new ResultSetProcessor();

    public static <T> List<T> executeQuery(String query, Map<String, Object> arguments, Class<T> returnType) {
        LOG.debug("Running statement[{}] with values[{}]", query, arguments);

        Session session = driver.session();

        StatementResult result = session.run(query, arguments);

        List<T> returnValues = new LinkedList<>();

        if (returnType == null) {
            return null;
        }

        while (result.hasNext()) {
            Record record = result.next();
            T returnValue = null;

            try {
                returnValue = processor.processRow(record.asMap(), returnType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            returnValues.add(returnValue);
        }

        session.close();

        return returnValues;
    }
}
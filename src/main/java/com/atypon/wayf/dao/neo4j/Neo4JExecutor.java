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
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Singleton
public class Neo4JExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4JExecutor.class);

    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";

    @Inject
    private Driver driver;

    private ResultSetProcessor processor;

    public Neo4JExecutor() {
        processor = new ResultSetProcessor();
    }

    public Driver getDriver() {
        return driver;
    }

    public <T> T executeQuerySelectFirst(String query, Map<String, Object> arguments, Class<T> returnType) {
        LOG.debug("Running statement[{}] with values[{}]", query, arguments);

        List<Record> records =  execute(query, arguments);

        if (records != null && records.size() > 0) {
            try {
                processor.processRow(records.get(0).asMap(), returnType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public <T> Observable<T> executeQuery(String query, Map<String, Object> arguments, Class<T> returnType) {
        LOG.debug("Running statement[{}] with values[{}]", query, arguments);

        List<Record> records = execute(query, arguments);

        return records == null?
                Observable.empty() :
                Observable.fromIterable(records)
                    .map((record) -> processor.processRow(record.asMap(), returnType));
    }

    private List<Record> execute(String query, Map<String, Object> arguments) {
        LOG.debug("Running statement[{}] with values[{}]", query, arguments);

        // Add in limit and offset arguments by default. The limit is increased by 1 so that we can see if there is
        // more data for the client to paginate
        arguments.put(LIMIT, RequestContextAccessor.get().getLimit() + 1);
        arguments.put(OFFSET, RequestContextAccessor.get().getOffset());

        try (Session session = driver.session()) {
            StatementResult result = session.run(query, arguments);

            if (result != null && result.hasNext()) {
                List<Record> records = result.list();

                LOG.debug("Record size {} limit {}", records.size(), RequestContextAccessor.get().getLimit());
                // Check to see if there is more data to paginate over
                RequestContextAccessor.get().setHasAnotherDbPage(records.size() > RequestContextAccessor.get().getLimit());

                // Remove the extra record
                records.remove(records.size() - 1);

                return records;
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
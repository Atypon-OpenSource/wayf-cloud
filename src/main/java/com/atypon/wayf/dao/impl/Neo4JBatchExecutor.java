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

import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class Neo4JBatchExecutor implements org.quartz.Job {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Neo4JBatchExecutor.class);

    public Neo4JBatchExecutor() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.trace("Running Neo4J batch import");

        List<Neo4JRequest> requests = new LinkedList<>();

        Neo4JBatchWriter.INSTANCE.getRequestQueue().drainTo(requests);

        LOG.trace("Found [{}] requests to write", requests.size());

        if (!requests.isEmpty()) {
            Session session = Neo4JExecutor.driver.session();

            for (Neo4JRequest request : requests) {
                session.run(request.getCypher(), Values.value(request.getArgs()));
            }

            LOG.trace("Finished execution requests, closing session");

            session.close();
        }
    }
}

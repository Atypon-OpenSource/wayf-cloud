package com.atypon.wayf.dao.impl;

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
            Session session = InstitutionDaoNeo4JImpl.driver.session();

            for (Neo4JRequest request : requests) {
                session.run(request.getCypher(), Values.value(request.getArgs()));
            }

            LOG.trace("Finished execution requests, closing session");

            session.close();
        }
    }
}

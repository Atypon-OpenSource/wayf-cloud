package com.atypon.wayf.dao.impl;

import org.neo4j.driver.v1.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.*;

import java.util.LinkedList;
import java.util.List;

public class Neo4JBatchExecutor implements org.quartz.Job {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Neo4JBatchExecutor.class);

    public Neo4JBatchExecutor() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.debug("Running neo4j request");
        List<Neo4JBatchWriter.Neo4JRequest> requests = new LinkedList<>();

        Neo4JBatchWriter.INSTANCE.getRequestQueue().drainTo(requests);

        if (!requests.isEmpty()) {
            Session session = InstitutionDaoNeo4JImpl.driver.session();

            for (Neo4JBatchWriter.Neo4JRequest request : requests) {
                LOG.debug("Writing request");
                session.run(request.getCypher(), Values.value(request.getArgs()));
            }

            session.close();
        }
    }
}

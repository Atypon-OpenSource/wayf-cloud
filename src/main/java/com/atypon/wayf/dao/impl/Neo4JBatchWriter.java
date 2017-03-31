package com.atypon.wayf.dao.impl;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Neo4JBatchWriter {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4JBatchWriter.class);

    public static class Neo4JRequest {
        private String cypher;
        private Map<String, Object> args;

        public Neo4JRequest(String cypher, Map<String, Object> args) {
            this.cypher = cypher;
            this.args = args;
        }

        public String getCypher() {
            return cypher;
        }

        public Map<String, Object> getArgs() {
            return args;
        }
    }

    private BlockingQueue<Neo4JRequest> requestQueue = new LinkedBlockingQueue<>();

    public static final Neo4JBatchWriter INSTANCE = new Neo4JBatchWriter();

    private Scheduler scheduler;

    private Neo4JBatchWriter() {
        initScheduledTasks();
    }

    public BlockingQueue<Neo4JRequest> getRequestQueue() {
        return requestQueue;
    }

    public void queue(String cypher, Map<String, Object> args) {
        LOG.debug("Queueing neo4j request");
        requestQueue.add(new Neo4JRequest(cypher, args));
    }

    private void initScheduledTasks() {
        // Grab the Scheduler instance from the Factory
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            // define the job and tie it to our MyJob class
            JobDetail job = newJob(Neo4JBatchExecutor.class)
                    .withIdentity("neo4jBulkWrite", "neo4j")
                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(1)
                            .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}

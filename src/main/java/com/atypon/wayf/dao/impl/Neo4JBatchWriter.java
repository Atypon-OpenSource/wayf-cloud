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
    public static final Neo4JBatchWriter INSTANCE = new Neo4JBatchWriter();

    private BlockingQueue<Neo4JRequest> requestQueue;
    private Scheduler scheduler;

    private Neo4JBatchWriter() {
        requestQueue = new LinkedBlockingQueue<>();

        initScheduledTasks();
    }

    public BlockingQueue<Neo4JRequest> getRequestQueue() {
        return requestQueue;
    }

    public void queue(String cypher, Map<String, Object> args) {
        LOG.debug("Queueing Neo4J request");

        requestQueue.add(new Neo4JRequest(cypher, args));
    }

    private void initScheduledTasks() {
        // Grab the Scheduler instance from the Factory
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            JobDetail job = newJob(Neo4JBatchExecutor.class)
                    .withIdentity("neo4jBulkWrite", "neo4j")
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("neo4jBulkWriteTrigger", "neo4j")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(1)
                            .repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}

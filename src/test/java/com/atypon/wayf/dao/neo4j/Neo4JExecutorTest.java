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

import com.atypon.wayf.reactivex.WayfReactivexConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;

import java.util.HashMap;
import java.util.Map;

public class Neo4JExecutorTest {

    @Inject
    private Neo4JExecutor dbExecutor;

    @Before
    public void setUp() {
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Driver.class).to(MockDriver.class);
            }
        }).injectMembers(this);

        WayfReactivexConfig.initializePlugins();
        RequestContextAccessor.set(new RequestContext().setLimit(5).setOffset(0));
    }

    @After
    public void tearDown() {
        RequestContextAccessor.set(null);
    }

    @Test
    public void testPaginationOnSingle() {
        String cypher = "A test cypher";
        Map<String, Object> args = new HashMap<>();

        Assert.assertNull(args.get(Neo4JExecutor.LIMIT));
        Assert.assertNull(args.get(Neo4JExecutor.OFFSET));

        dbExecutor.executeQuerySelectFirst(cypher, args, Object.class);

        Assert.assertEquals(RequestContextAccessor.get().getLimit() + 1, args.get(Neo4JExecutor.LIMIT));
        Assert.assertEquals(RequestContextAccessor.get().getOffset(), args.get(Neo4JExecutor.OFFSET));
    }

    @Test
    public void testPaginationOnMultiple() {
        String cypher = "A test cypher";
        Map<String, Object> args = new HashMap<>();

        Assert.assertNull(args.get(Neo4JExecutor.LIMIT));
        Assert.assertNull(args.get(Neo4JExecutor.OFFSET));

        dbExecutor.executeQuery(cypher, args, Object.class);

        Assert.assertEquals(RequestContextAccessor.get().getLimit() + 1, args.get(Neo4JExecutor.LIMIT));
        Assert.assertEquals(RequestContextAccessor.get().getOffset(), args.get(Neo4JExecutor.OFFSET));
    }

    @Test
    public void testPaginationHasMore() {
        String cypher = "A test cypher";
        Map<String, Object> args = new HashMap<>();

        ((MockSession) ((MockDriver)dbExecutor.getDriver()).session()).setNumRowsToReturn(6);

        dbExecutor.executeQuery(cypher, args, Object.class);

        Assert.assertEquals(Boolean.TRUE, RequestContextAccessor.get().getHasAnotherDbPage());
    }


    @Test
    public void testPaginationHasNoMore() {
        String cypher = "A test cypher";
        Map<String, Object> args = new HashMap<>();

        ((MockSession) ((MockDriver)dbExecutor.getDriver()).session()).setNumRowsToReturn(4);

        dbExecutor.executeQuery(cypher, args, Object.class);

        Assert.assertEquals(Boolean.FALSE, RequestContextAccessor.get().getHasAnotherDbPage());
    }

}

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

import com.atypon.wayf.dao.QueryMapper;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.publisher.Publisher;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PublisherDaoTest {
    String cypher = "    MATCH (p:Publisher) " +
            "    WHERE p.id IN {ids} " +
            "    RETURN p.id AS id, " +
            "            p.status AS status, " +
            "            p.name AS name, " +
            "            p.createdDate AS createdDate, " +
            "            p.modifiedDate AS modifiedDate;";

    @Test
    public void runTest() {
        Map<String, Object> args = new HashMap<>();
        args.put("ids", Lists.newArrayList("0c6b3091-d108-465d-a295-cca7918e82cd","eaa5a972-3865-4eae-ac96-4d977934b214"));
        Neo4JExecutor.executeQuery(cypher, args, Publisher.class);
    }
}

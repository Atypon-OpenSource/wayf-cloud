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


import org.junit.Test;
import org.neo4j.driver.v1.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Neo4JCollectionTest {

    private String cypherWithParams =
            "MATCH (p:Publisher) " +
            "    WHERE p.id IN {ids} " +
            "    RETURN p.id AS id, " +
            "            p.status AS status, " +
            "            p.name AS name, " +
            "            p.createdDate AS createdDate, " +
            "            p.modifiedDate AS modifiedDate;";


    private String cypherWithoutParams =
            "MATCH (p:Publisher) " +
            "    WHERE p.id IN ['0c6b3091-d108-465d-a295-cca7918e82cd','eaa5a972-3865-4eae-ac96-4d977934b214'] " +
            "    RETURN p.id AS id, " +
            "            p.status AS status, " +
            "            p.name AS name, " +
            "            p.createdDate AS createdDate, " +
            "            p.modifiedDate AS modifiedDate;";

    @Test
    public void test() {

        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("test", "test"));
        Session session = driver.session();

        List<String> ids = new LinkedList<>();
        ids.add("0c6b3091-d108-465d-a295-cca7918e82cd");
        ids.add("eaa5a972-3865-4eae-ac96-4d977934b214");

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("ids", ids);

        StatementResult resultWithParams = session.run(cypherWithParams, arguments);
        System.out.println("Count using params: " + resultWithParams.list().size());

        StatementResult resultWithoutParams = session.run(cypherWithoutParams);
        System.out.println("Count without params: " + resultWithoutParams.list().size());

        session.close();
        driver.close();
    }
}

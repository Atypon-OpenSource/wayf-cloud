package com.atypon.wayf.dao.impl;

import java.util.Map;

class Neo4JRequest {
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
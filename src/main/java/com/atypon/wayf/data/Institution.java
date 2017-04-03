package com.atypon.wayf.data;

public class Institution {

    private String id;
    private String name;
    private String description;

    public Institution() {
    }

    public String getName() {
        return name;
    }

    public Institution setName(String name) {
        this.name = name;
        return this;
    }
    public String getDescription() {
        return description;
    }

    public Institution setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getId() {
        return id;
    }

    public Institution setId(String id) {
        this.id = id;
        return this;
    }
}
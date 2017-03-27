package com.atypon.wayf.data;

import io.vertx.core.json.JsonObject;

public class Institution {

  private String id;

  private String name;

  private String description;

  public Institution(String name, String description) {
    this.name = name;
    this.description = description;
    this.id = null;
  }

  public Institution(JsonObject json) {
    this.name = json.getString("name");
    this.description = json.getString("description");
    this.id = json.getString("_id");
  }

  public Institution() {
    this.id = null;
  }

  public Institution(String id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject()
        .put("name", name)
        .put("description", description);
    if (id != null) {
      json.put("_id", id);
    }
    return json;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public Institution setName(String name) {
    this.name = name;
    return this;
  }

  public Institution setDescription(String description) {
    this.description = description;
    return this;
  }

  public Institution setId(String id) {
    this.id = id;
    return this;
  }
}
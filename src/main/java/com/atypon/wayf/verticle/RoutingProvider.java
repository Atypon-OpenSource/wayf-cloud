package com.atypon.wayf.verticle;

import io.vertx.ext.web.Router;

public interface RoutingProvider {
    public void addRoutings(Router router);
}

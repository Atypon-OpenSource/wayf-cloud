package com.atypon.wayf.verticle.routing;

import io.vertx.ext.web.Router;

public interface RoutingProvider {
    public void addRoutings(Router router);
}

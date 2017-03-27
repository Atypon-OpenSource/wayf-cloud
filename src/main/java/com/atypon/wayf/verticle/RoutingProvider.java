package com.atypon.wayf.verticle;

import io.vertx.ext.web.Router;

/**
 * Created by mmason on 3/22/17.
 */
public interface RoutingProvider {
    public void addRoutings(Router router);
}

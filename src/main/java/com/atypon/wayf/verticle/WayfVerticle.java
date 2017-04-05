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

package com.atypon.wayf.verticle;

import com.atypon.wayf.data.ErrorResponse;
import com.atypon.wayf.data.RequestContextAccessor;
import com.atypon.wayf.reactive.WayfReactiveConfig;
import com.atypon.wayf.reactive.WayfRunnable;
import com.atypon.wayf.verticle.routing.InstitutionRouting;
import com.atypon.wayf.verticle.routing.RoutingProvider;
import com.google.common.collect.Lists;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class WayfVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(WayfVerticle.class);

    private List<RoutingProvider> routingProviders = Lists.newArrayList(new InstitutionRouting());

    public WayfVerticle() {
    }

    @Override
    public void start(Future<Void> fut) {
        LOG.info("Starting wayf-cloud server");
        startWebApp((http) -> completeStartup(http, fut));
    }

    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        // Create a router object.
        Router router = Router.router(vertx);

        LOG.debug("Adding routes");
        routingProviders.forEach((routingProvider) -> routingProvider.addRoutings(router));


        LOG.debug("Adding default error handler to routes");
        for (Route route : router.getRoutes()) {
            route.failureHandler((rc) -> ResponseWriter.buildFailure(rc));
        }

        LOG.debug("Starting HTTP server");
        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        next::handle
                );
    }

    private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if (http.succeeded()) {
            initConfigs();

            LOG.info("SUCCESS: wayf-cloud successfully initialized");
            fut.complete();
        } else {
            LOG.debug("FAILURE: Could not start wayf-cloud due to exception", http.cause());
            fut.fail(http.cause());
        }
    }

    private void initConfigs() {
        LOG.info("Initializing server configs");

        WayfReactiveConfig.initializePlugins();
    }
}

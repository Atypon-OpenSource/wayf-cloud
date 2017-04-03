package com.atypon.wayf.verticle;

import com.atypon.wayf.data.ErrorResponse;
import com.atypon.wayf.verticle.routing.InstitutionRouting;
import com.atypon.wayf.verticle.routing.RoutingProvider;
import com.google.common.collect.Lists;
import io.reactivex.Completable;
import io.reactivex.Single;
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
        startWebApp((http) -> completeStartup(http, fut));
    }

    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        // Create a router object.
        Router router = Router.router(vertx);

        // Let the services of requests define the endpoints that they service
        for (RoutingProvider routingProvider : routingProviders) {
            routingProvider.addRoutings(router);
        }

        // Enforce that every route (endpoint) handles failures the same way
        for (Route route : router.getRoutes()) {
            route.failureHandler((rc) -> ResponseWriter.buildFailure(rc));
        }

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
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }
}

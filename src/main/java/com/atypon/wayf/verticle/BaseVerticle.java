package com.atypon.wayf.verticle;

import com.google.common.collect.Lists;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by mmason on 3/21/17.
 */
public class BaseVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(BaseVerticle.class);

    private List<RoutingProvider> routingProviders = Lists.newArrayList(new InstitutionVerticle());

    public BaseVerticle() {
    }


    protected static <B> void buildSuccess(RoutingContext routingContext, B body) {
        LOG.debug("Building success message");

        Completable.fromAction(
                () -> {
                    routingContext.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(body != null ? Json.encodePrettily(body) : "");
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    protected static void buildFailure(RoutingContext routingContext, Throwable t) {
        Completable.fromAction(
                () -> {
                    routingContext.response()
                            .setStatusCode(500)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(t));
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
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


    public static <B> B readRequestBody(RoutingContext routingContext, Class<B> bodyClass) {
        LOG.debug("Reading request body of type [{}] from request", bodyClass);

        return Json.decodeValue(routingContext.getBodyAsString(), bodyClass);
    }

    public static <B> Observable<B> readRequestBodyObservable(RoutingContext routingContext, Class<B> bodyClass) {
        return Observable.just(routingContext).observeOn(Schedulers.computation()).map((rc) -> readRequestBody(rc, bodyClass));
    }

    public static Observable<String> readPathArugment(RoutingContext routingContext, String argumentName) {
        return Observable.just(routingContext).observeOn(Schedulers.computation()).map((rc) -> rc.request().getParam(argumentName));
    }
}

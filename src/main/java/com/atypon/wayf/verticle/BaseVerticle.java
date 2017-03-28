package com.atypon.wayf.verticle;

import com.atypon.wayf.data.ErrorResponse;
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

public class BaseVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(BaseVerticle.class);

    private List<RoutingProvider> routingProviders = Lists.newArrayList(new InstitutionVerticle());

    public BaseVerticle() {
    }

    protected static <B> void buildSuccess(RoutingContext routingContext, B body) {
        LOG.debug("Building success message");

       Completable.fromAction(
                () ->
                    routingContext.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(body != null ? Json.encodePrettily(body) : "")
                )
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {}, // Do nothing on success
                        (ex) -> routingContext.fail(ex)
                );
    }

    protected static void buildFailure(RoutingContext routingContext) {
        Throwable t = routingContext.failure();
        
        LOG.error("Error processing request", t);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        t.printStackTrace(printStream);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(t.getMessage());
        try {
            errorResponse.setStackTrace(outputStream.toString("utf-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.error("Could not build stack trace", e);
        }

        Completable.fromAction(
                () -> routingContext.response()
                            .setStatusCode(500)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(errorResponse))
                )
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {},
                        (ex) -> LOG.error("Could not write response to client", ex)
                );
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
            route.failureHandler((rc) -> buildFailure(rc));
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


    private static <B> B _readRequestBody(RoutingContext routingContext, Class<B> bodyClass) {
        LOG.debug("Reading request body of type [{}] from request", bodyClass);

        return Json.decodeValue(routingContext.getBodyAsString(), bodyClass);
    }

    public static <B> Single<B> readRequestBody(RoutingContext routingContext, Class<B> bodyClass) {
        return Single.just(routingContext).observeOn(Schedulers.computation()).map((rc) -> _readRequestBody(rc, bodyClass));
    }

    public static Single<String> readPathArugment(RoutingContext routingContext, String argumentName) {
        return Single.just(routingContext).observeOn(Schedulers.computation()).map((rc) -> rc.request().getParam(argumentName));
    }
}

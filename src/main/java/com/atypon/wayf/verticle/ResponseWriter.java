package com.atypon.wayf.verticle;

import com.atypon.wayf.data.ErrorResponse;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ResponseWriter {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseWriter.class);

    public static <B> void buildSuccess(RoutingContext routingContext, B body) {
        LOG.debug("Building success message");

        Completable.fromAction(
                () ->
                        routingContext.response()
                                .setStatusCode(200)
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(body != null ? Json.encodePrettily(body) : ""))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {}, // Do nothing on success
                        (ex) -> routingContext.fail(ex)
                );
    }

    public static void buildFailure(RoutingContext routingContext) {
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
                        .end(Json.encodePrettily(errorResponse)))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {},
                        (ex) -> LOG.error("Could not write response to client", ex)
                );
    }

}

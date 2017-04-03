package com.atypon.wayf.verticle;

import com.atypon.wayf.data.RequestContextAccessor;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestReader {
    private static final Logger LOG = LoggerFactory.getLogger(RequestReader.class);

    private static <B> B _readRequestBody(RoutingContext routingContext, Class<B> bodyClass) {
        LOG.debug("Reading request body of type [{}] from request", bodyClass);

        return Json.decodeValue(routingContext.getBodyAsString(), bodyClass);
    }

    public static <B> Single<B> readRequestBody(RoutingContext routingContext, Class<B> bodyClass) {
        return Single.just(routingContext).observeOn(Schedulers.computation()).map((rc) -> _readRequestBody(rc, bodyClass));
    }

    public static Single<String> readPathArgument(RoutingContext routingContext, String argumentName) {
        LOG.info("Request URI [{}]", RequestContextAccessor.get().getRequestUrl());
        return Single.just(routingContext).observeOn(Schedulers.computation()).map((rc) -> rc.request().getParam(argumentName));
    }
}

package com.atypon.wayf.verticle;

import com.atypon.wayf.data.RequestContext;
import com.atypon.wayf.data.RequestContextAccessor;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of Handler that ensures all inbound requests are handled uniformly. This stores relevant
 * information about inbound requests, switches the processing to an appropriate threadpool, and guarantees consistent
 * response marshalling for both successes and failures.
 */
public abstract class WayfRequestHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(WayfRequestHandler.class);

    private WayfRequestHandler() {
    }

    public static WayfRequestHandler observable(Function<RoutingContext, Observable<?>> delegate) {
        return new WayfRequestHandlerObservableImpl(delegate);
    }

    public static WayfRequestHandler single(Function<RoutingContext, Single<?>> delegate) {
        return new WayfRequestHandlerSingleImpl(delegate);
    }

    public static WayfRequestHandler completable(Function<RoutingContext, Completable> delegate) {
        return new WayfRequestHandlerCompletableImpl(delegate);
    }

    private static class WayfRequestHandlerSingleImpl extends WayfRequestHandler {
        private Function<RoutingContext, Single<?>> singleDelegate;

        public WayfRequestHandlerSingleImpl(Function<RoutingContext, Single<?>> delegate) {
            super();
            this.singleDelegate = delegate;
        }

        public void handle(RoutingContext event) {
            RequestContextAccessor.set(RequestContext.fromRoutingContext(event));

            Single.just(event)
                    .observeOn(Schedulers.io())
                    .flatMap((s_event) -> singleDelegate.apply(s_event))
                    .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                    .subscribe(
                            (result) -> ResponseWriter.buildSuccess(event, result),
                            (e) -> event.fail(e)
                    );

            RequestContextAccessor.remove();
        }
    }

    private static class WayfRequestHandlerObservableImpl extends WayfRequestHandler {
        private Function<RoutingContext, Observable<?>> observableDelegate;

        public WayfRequestHandlerObservableImpl(Function<RoutingContext, Observable<?>> delegate) {
            super();
            this.observableDelegate = delegate;
        }

        public void handle(RoutingContext event) {
            RequestContextAccessor.set(RequestContext.fromRoutingContext(event));

            Single.just(event)
                    .observeOn(Schedulers.io())
                    .flatMapObservable((s_event) -> observableDelegate.apply(s_event))
                    .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                    .subscribe(
                            (result) -> ResponseWriter.buildSuccess(event, result),
                            (e) -> event.fail(e)
                    );

            RequestContextAccessor.remove();
        }
    }

    private static class WayfRequestHandlerCompletableImpl extends WayfRequestHandler {
        private Function<RoutingContext, Completable> completableDelgate;

        public WayfRequestHandlerCompletableImpl(Function<RoutingContext, Completable> delegate) {
            super();
            this.completableDelgate = delegate;
        }

        public void handle(RoutingContext event) {
            RequestContextAccessor.set(RequestContext.fromRoutingContext(event));

            Single.just(event)
                    .observeOn(Schedulers.io())
                    .flatMapCompletable((s_event) -> completableDelgate.apply(s_event))
                    .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                    .subscribe(
                            () -> ResponseWriter.buildSuccess(event, null),
                            (e) -> event.fail(e)
                    );

            RequestContextAccessor.remove();
        }
    }
}

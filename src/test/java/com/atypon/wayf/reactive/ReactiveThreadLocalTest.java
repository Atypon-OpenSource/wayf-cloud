package com.atypon.wayf.reactive;

import com.atypon.wayf.data.RequestContext;
import com.atypon.wayf.data.RequestContextAccessor;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReactiveThreadLocalTest {

    public static final String REQUEST_URI = "/1/institution";

    @Before
    public void setUp() {
        WayfReactiveConfig.initializePlugins();

        RequestContextAccessor.set(new RequestContext().setRequestUrl(REQUEST_URI));
    }

    @After
    public void cleanUp() {
        RequestContextAccessor.remove();
    }

    @Test
    public void testRequestContext() {
        Observable.just("Test observable")
                .observeOn(Schedulers.io())
                .flatMap((i) -> {
                        Assert.assertNotNull("RequestContext should not be null in observe", RequestContextAccessor.get());
                        Assert.assertEquals("Request URL should have carried over in request context in observe", REQUEST_URI, RequestContextAccessor.get().getRequestUrl());
                        return Observable.just(i);
                    }
                )
                .subscribeOn(Schedulers.computation())
                .subscribe((i) -> {
                        Assert.assertNotNull("RequestContext should not be null in subscribe", RequestContextAccessor.get());
                        Assert.assertEquals("Request URL should have carried over in request context in subscribe", REQUEST_URI, RequestContextAccessor.get().getRequestUrl());
                    }
                );
    }
}

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

package com.atypon.wayf.request;

import com.atypon.wayf.reactive.WayfReactiveConfig;
import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
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

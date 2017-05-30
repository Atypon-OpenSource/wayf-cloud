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

package com.atypon.wayf.reactive;

import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.reactivex.WayfReactivexConfig;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.plugins.RxJavaPlugins;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class ExceptionHandlerTest {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerTest.class);

    @Before
    public void setUp() {
       //WayfReactivexConfig.initializePlugins();
    }


    @Test(expected = ServiceException.class)
    public void testNestedException() {
        String foo = "bar";

        Observable.just(foo)
                .flatMap(ffoo ->
                    Observable.just(foo)
                            .flatMap((_foo) ->
                                    Observable.just(foo).map((__foo) -> {
                                            throw new ServiceException(HttpStatus.SC_PRECONDITION_FAILED);
                                        })
                            ))
                            .subscribe((o) -> fail(), (e) -> {

            if (ServiceException.class.isAssignableFrom(e.getClass())) {
                throw (Exception)e;
            }

            if (CompositeException.class.isAssignableFrom(e.getClass())) {
                throw (Exception) ((CompositeException)e).getExceptions().get(0);
            }
            throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, e);
        });
    }
}
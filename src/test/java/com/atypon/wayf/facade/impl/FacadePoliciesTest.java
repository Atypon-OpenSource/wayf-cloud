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

package com.atypon.wayf.facade.impl;

import com.atypon.wayf.data.ServiceException;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.atypon.wayf.reactivex.FacadePolicies.singleOrException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FacadePoliciesTest {

    @Test
    public void testMaybeToSingleEmpty() {
        final List<Object> results = new LinkedList<>();

        singleOrException(Maybe.empty(), HttpStatus.SC_BAD_REQUEST, "Too few elements")
                .subscribe((ignore) -> fail(), (e) -> results.add(e));

        assertEquals(1, results.size());

        Object result = results.get(0);

        if (result.getClass() != ServiceException.class) {
            fail();
        }

        ServiceException serviceException = (ServiceException) result;

        assertEquals(HttpStatus.SC_BAD_REQUEST, serviceException.getStatusCode());
        assertEquals("Too few elements", serviceException.getMessage());
    }

    @Test
    public void testMaybeToSingleOneElement() {
        final List<Object> results = new LinkedList<>();

        singleOrException(Maybe.just("ABC"), HttpStatus.SC_BAD_REQUEST, "Too few elements")
                .subscribe((result) -> results.add(result), (e) -> fail(e.getMessage()));

        assertEquals(1, results.size());

        Object result = results.get(0);

        assertEquals("ABC", result);
    }

    @Test
    public void testObservableToSingleEmpty() {
        final List<Object> results = new LinkedList<>();

        singleOrException(Observable.empty(), HttpStatus.SC_BAD_REQUEST, "Too few elements")
                .subscribe((ignore) -> fail(), (e) -> results.add(e));

        assertEquals(1, results.size());

        Object result = results.get(0);

        if (result.getClass() != ServiceException.class) {
            fail();
        }

        ServiceException serviceException = (ServiceException) result;

        assertEquals(HttpStatus.SC_BAD_REQUEST, serviceException.getStatusCode());
        assertEquals("Too few elements", serviceException.getMessage());
    }

    @Test
    public void testObservableToSingleTooMany() {
        final List<Object> results = new LinkedList<>();

        singleOrException(Observable.just("a", "b", "c"), HttpStatus.SC_BAD_REQUEST, "Too many elements")
                .subscribe((ignore) -> fail(), (e) -> results.add(e));

        assertEquals(1, results.size());

        Object result = results.get(0);

        if (result.getClass() != ServiceException.class) {
            fail();
        }

        ServiceException serviceException = (ServiceException) result;

        assertEquals(HttpStatus.SC_BAD_REQUEST, serviceException.getStatusCode());
        assertEquals("Too many elements", serviceException.getMessage());
    }

    @Test
    public void testObservableToSingleOneElement() {
        final List<Object> results = new LinkedList<>();

        singleOrException(Observable.just("ABC"), HttpStatus.SC_BAD_REQUEST, "Too few elements")
                .subscribe((result) -> results.add(result), (e) -> fail(e.getMessage()));

        assertEquals(1, results.size());

        Object result = results.get(0);

        assertEquals("ABC", result);
    }
}

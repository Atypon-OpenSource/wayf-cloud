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

package com.atypon.wayf.reactivex;

import com.atypon.wayf.data.ServiceException;
import io.reactivex.*;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class FacadePolicies {
    private static final Logger LOG = LoggerFactory.getLogger(FacadePolicies.class);

    private static Scheduler subscribeOnScheduler = Schedulers.io();
    private static final long TIMEOUT = 10l;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    private static Consumer<? super Throwable> doOnError = (e) -> {
        if (ServiceException.class.isAssignableFrom(e.getClass())) {
            throw (ServiceException) e;
        }

        throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error processing request in facade", e);
    };


    public static <T> Single<T> applySingle(Single<T> single) {
        return single.subscribeOn(subscribeOnScheduler)
                .doOnError(doOnError)
                .timeout(TIMEOUT, TIMEOUT_UNIT);
    }

    public static <T> Maybe<T> applyMaybe(Maybe<T> maybe) {
        return maybe.subscribeOn(subscribeOnScheduler)
                .doOnError(doOnError)
                .timeout(TIMEOUT, TIMEOUT_UNIT);
    }

    public static <T> Observable<T> applyObservable(Observable<T> observable) {
        return observable.subscribeOn(subscribeOnScheduler)
                .doOnError(doOnError)
                .timeout(TIMEOUT, TIMEOUT_UNIT);
    }

    public static Completable applyCompletable(Completable completable) {
        return completable.subscribeOn(subscribeOnScheduler)
                .doOnError(doOnError)
                .timeout(TIMEOUT,TIMEOUT_UNIT);
    }

    public static final <T> Maybe<T> onError401(Maybe<T> maybe, String message, Object... args) {
        return maybe.doOnError((e) -> {
            if (NoSuchElementException.class.isAssignableFrom(e.getClass())) {
                FormattingTuple formattedMessage = MessageFormatter.arrayFormat(message, args);

                ServiceException serviceException = new ServiceException(HttpStatus.SC_UNAUTHORIZED, formattedMessage.getMessage());

                LOG.error("Encountered NoSuchElementException, rethrowing as 401 ServiceException", serviceException);

                throw serviceException;
            }

            LOG.error("Encountered unknown exception, rethrowing", e);

            throw (Exception) e;
        });
    }

    public static final <T> Single<T> onError401(Single<T> single, String message, Object... args) {
        return single.doOnError((e) -> {
            if (NoSuchElementException.class.isAssignableFrom(e.getClass())) {
                FormattingTuple formattedMessage = MessageFormatter.arrayFormat(message, args);

                ServiceException serviceException = new ServiceException(HttpStatus.SC_UNAUTHORIZED, formattedMessage.getMessage());

                LOG.error("Encountered NoSuchElementException, rethrowing as 401 ServiceException", serviceException);

                throw serviceException;
            }

            LOG.error("Encountered unknown exception, rethrowing", e);

            throw (Exception) e;
        });
    }

    public static final <T> Maybe<T> onError404(Maybe<T> maybe, String message, Object... args) {
        return maybe.doOnError((e) -> {
            if (NoSuchElementException.class.isAssignableFrom(e.getClass())) {
                FormattingTuple formattedMessage = MessageFormatter.arrayFormat(message, args);

                ServiceException serviceException = new ServiceException(HttpStatus.SC_NOT_FOUND, formattedMessage.getMessage());

                LOG.error("Encountered NoSuchElementException, rethrowing as 404 ServiceException", serviceException);

                throw serviceException;
            }

            LOG.error("Encountered unknown exception, rethrowing", e);

            throw (Exception) e;
        });
    }
}

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

import com.atypon.wayf.facade.ErrorLoggerFacade;
import io.reactivex.Completable;

public class ErrorLoggerFacadeMockImpl implements ErrorLoggerFacade {
    private int statusCode;
    private Throwable exception;

    public int getStatusCode() {
        return statusCode;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public Completable buildAndLogError(int statusCode, Throwable t) {
        this.statusCode = statusCode;
        this.exception = t;

        return Completable.complete();
    }
}

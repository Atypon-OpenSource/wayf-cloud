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


import com.atypon.wayf.request.RequestContext;
import com.atypon.wayf.request.RequestContextAccessor;
import org.slf4j.MDC;

import java.util.Map;

/**
 * This class helps bootstrap the state of threads for use with ReactiveX. The RxJava2 library does a lot of useful
 * threadpool switching but does not copy any thread state over. Any information relevant to the thread that needs to be
 * copied should happen in this class.
 */
public class WayfRunnable implements Runnable {

    private Runnable runnable;
    private RequestContext requestContext;
    private Map<String, String> mdcContents;

    public WayfRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.requestContext = RequestContextAccessor.get();
        this.mdcContents = MDC.getCopyOfContextMap();
    }

    @Override
    public void run() {
        RequestContextAccessor.set(requestContext);

        if (mdcContents != null) {
            MDC.setContextMap(mdcContents);
        }

        runnable.run();

        MDC.clear();
        RequestContextAccessor.remove();
    }
}

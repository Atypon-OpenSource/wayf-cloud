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

import io.reactivex.plugins.RxJavaPlugins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to manage any configurations required for the ReactiveX library.
 */
public class WayfReactivexConfig {
    private static final Logger LOG = LoggerFactory.getLogger(WayfReactivexConfig.class);

    public static void initializePlugins() {
        LOG.debug("Initializing ReactiveX plugins");

        RxJavaPlugins.setScheduleHandler((runnable) -> new WayfRunnable(runnable));
    }
}

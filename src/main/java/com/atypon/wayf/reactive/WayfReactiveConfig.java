package com.atypon.wayf.reactive;

import io.reactivex.plugins.RxJavaPlugins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to manage any configurations required for the ReactiveX library/.
 */
public class WayfReactiveConfig {
    private static final Logger LOG = LoggerFactory.getLogger(WayfReactiveConfig.class);

    public static void initializePlugins() {
        LOG.debug("Initializing reactive plugins");

        RxJavaPlugins.setScheduleHandler((runnable) -> new WayfRunnable(runnable));
    }
}

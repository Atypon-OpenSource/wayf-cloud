package com.atypon.wayf.reactive;


import com.atypon.wayf.data.RequestContext;
import com.atypon.wayf.data.RequestContextAccessor;
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

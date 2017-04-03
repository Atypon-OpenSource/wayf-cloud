package com.atypon.wayf.reactive;


import com.atypon.wayf.data.RequestContext;
import com.atypon.wayf.data.RequestContextAccessor;

public class WayfRunnable implements Runnable {

    private Runnable runnable;
    private RequestContext requestContext;

    public WayfRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.requestContext = RequestContextAccessor.get();
    }

    @Override
    public void run() {
        RequestContextAccessor.set(requestContext);

        runnable.run();

        RequestContextAccessor.remove();
    }
}

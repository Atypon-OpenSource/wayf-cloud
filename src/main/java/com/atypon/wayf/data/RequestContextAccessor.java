package com.atypon.wayf.data;

public class RequestContextAccessor {
    private static final ThreadLocal<RequestContext> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(RequestContext requestContext) {
        THREAD_LOCAL.set(requestContext);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static RequestContext get() {
        return THREAD_LOCAL.get();
    }
}

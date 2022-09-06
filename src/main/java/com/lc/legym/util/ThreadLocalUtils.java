package com.lc.legym.util;

/**
 * @author Aaron Yeung
 * @date 9/6/2022 11:01 AM
 */
public class ThreadLocalUtils {
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String str) {
        THREAD_LOCAL.set(str);
    }

    public static Object get() {
        Object o = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        return o;
    }

}

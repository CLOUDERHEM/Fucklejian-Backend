package com.lc.legym.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aaron Yeung
 * @date 9/6/2022 11:01 AM
 */
public class ThreadLocalUtils {
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    static {
        THREAD_LOCAL.set(new HashMap<>());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        THREAD_LOCAL.remove();
    }

    public static void set(String key, String value) {
        Map<String, Object> stringStringMap = THREAD_LOCAL.get();
        if (stringStringMap != null) {
            stringStringMap.put(key, value);
        }
    }

    public static Object get(String key) {
        Map<String, Object> stringStringMap = THREAD_LOCAL.get();
        if (stringStringMap != null) {
            return stringStringMap.get(key);
        }
        return null;
    }

}

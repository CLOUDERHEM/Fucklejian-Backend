package com.lc.legym.util;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lc.legym.enums.Constant.PERMITS_PER_SECOND;

/**
 * @author Aaron Yeung
 * @date 9/8/2022 9:59 AM
 */
@SuppressWarnings("all")
public class RateLimitUtils {

    private static final Map<String, RateLimiter> STRING_LIMITER_MAP = new ConcurrentHashMap<>();

    public static boolean tryAcquire(String remoteAdd) {
        RateLimiter limiter;
        if (STRING_LIMITER_MAP.get(remoteAdd) == null) {
            synchronized (STRING_LIMITER_MAP) {
                if (STRING_LIMITER_MAP.get(remoteAdd) == null) {
                    limiter = RateLimiter.create(PERMITS_PER_SECOND);
                    STRING_LIMITER_MAP.put(remoteAdd, limiter);
                    return true;
                }
            }
        }

        limiter = STRING_LIMITER_MAP.get(remoteAdd);
        return limiter.tryAcquire();
    }

}

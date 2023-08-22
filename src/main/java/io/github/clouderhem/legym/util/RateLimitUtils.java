package io.github.clouderhem.legym.util;

import com.google.common.util.concurrent.RateLimiter;

import static io.github.clouderhem.legym.enums.Constant.PERMITS_PER_SECOND;

/**
 * @author Aaron Yeung
 * @date 9/8/2022 9:59 AM
 */
@SuppressWarnings("all")
public class RateLimitUtils {

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(PERMITS_PER_SECOND);

    public static boolean tryAcquire(String remoteAdd) {
        // todo remoteAdd
        return RATE_LIMITER.tryAcquire();
    }

}

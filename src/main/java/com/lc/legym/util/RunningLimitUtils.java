package com.lc.legym.util;

import cn.hutool.core.collection.ConcurrentHashSet;
import kotlin.Pair;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.lc.legym.enums.Constant.PER_RUNNING_INTERVAL_SEC;

/**
 * @author Aaron Yeung
 * @date 9/9/2022 5:28 PM
 */
public class RunningLimitUtils {
    private static final Map<String, Long> MAP = new ConcurrentHashMap<>();

    public static Pair<String, Long> tryRun(String username) {
        Long last = MAP.get(username);
        if (last == null) {
            MAP.put(username, System.currentTimeMillis());
            return null;
        }

        long interval = (System.currentTimeMillis() - last) / 1000;
        long left = PER_RUNNING_INTERVAL_SEC - interval;
        if (left >= 0) {
            long minute = left / 60;
            if (minute == 0) {
                return new Pair<>(String.format("频繁提交, 请%2ds后再试", left % 60),
                        last);
            } else {
                return new Pair<>(String.format("频繁提交, 请%d:%02ds后再试", minute, left % 60),
                        last);
            }
        }

        MAP.put(username, System.currentTimeMillis());
        return null;
    }

    public static Set<Map.Entry<String, Long>> gc() {
        Set<Map.Entry<String, Long>> res = new ConcurrentHashSet<>();
        Set<Map.Entry<String, Long>> entries = MAP.entrySet();
        Iterator<Map.Entry<String, Long>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> next = iterator.next();
            long interval = (System.currentTimeMillis() - next.getValue()) / 1000;
            if (interval > PER_RUNNING_INTERVAL_SEC + 1) {
                iterator.remove();
                res.add(next);
            }
        }
        return res;
    }
}

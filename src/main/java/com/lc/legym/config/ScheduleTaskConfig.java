package com.lc.legym.config;

import com.lc.legym.util.RunningLimitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author Aaron Yeung
 * @date 9/9/2022 5:50 PM
 */
@Slf4j
@EnableScheduling
@Component
public class ScheduleTaskConfig {

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanLastInMap() {
        Set<Map.Entry<String, Long>> gc = RunningLimitUtils.gc();
        log.info("cleaned the last use = {}", gc);
    }
}

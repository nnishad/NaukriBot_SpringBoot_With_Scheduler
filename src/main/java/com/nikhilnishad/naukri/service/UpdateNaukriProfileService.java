package com.nikhilnishad.naukri.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class UpdateNaukriProfileService implements TaskSchedulerCustomizer {

    Logger log = LoggerFactory.getLogger(UpdateNaukriProfileService.class);

    public int count=0;

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        log.info("scheduled task ::"+count++);
    }

    @Override
    public void customize(ThreadPoolTaskScheduler taskScheduler) {
        taskScheduler.setErrorHandler(t -> {
            log.error("Scheduled task threw an exception: {}", t.getMessage(), t);

        });
    }
}

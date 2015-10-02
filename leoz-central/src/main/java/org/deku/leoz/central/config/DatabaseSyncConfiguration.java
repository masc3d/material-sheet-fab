package org.deku.leoz.central.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.central.data.sync.DatabaseSync;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Leoz-central database sync configuration
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
public class DatabaseSyncConfiguration {
    Log mLog = LogFactory.getLog(DatabaseSyncConfiguration.class);

    @Inject
    DatabaseSync mDatabaseSync;

    /** Scheduler */
    ScheduledExecutorService mExecutorService;

    @PostConstruct
    public void onInitialize() {
        mLog.info("Starting database sync scheduler");

        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        mExecutorService.scheduleWithFixedDelay(
                () -> {
                    try {
                        mDatabaseSync.sync();
                    } catch(Exception e) {
                        mLog.error(e.getMessage(), e);
                    }
                },
                // Initial delay
                0,
                // Interval
                10, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void onDestroy() {
        mLog.info("Shutting down database sync scheduler");
        mExecutorService.shutdown();
        try {
            mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            mLog.error(e.getMessage(), e);
        }
    }
}

package sx.concurrent.task

import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/**
 * Delegating scheduled executor service, tracking scheduled tasks with support for falling back
 * to a complementary executor service for regular submits (as scheduled executor services usually
 * support fixed pools only (eg. {@link ScheduledThreadPoolExecutor})
 * See base class {@link TaskExecutorService} for details.
 * Created by masc on 16/03/16.
 * @param scheduledExecutorService A scheduled executor service to use (for scheduling only, except executorService is omitted)
 * @param executorService Regular executor service for regular submit
 */
class ScheduledTaskExecutorService(
        scheduledExecutorService: ScheduledExecutorService,
        executorService: ExecutorService = scheduledExecutorService)
:
        TaskExecutorService(executorService),
        ScheduledExecutorService by scheduledExecutorService {
}
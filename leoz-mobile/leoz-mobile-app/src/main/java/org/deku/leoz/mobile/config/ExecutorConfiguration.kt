package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.singleton
import sx.concurrent.task.CompositeExecutorService
import java.util.concurrent.*

/**
 * Executor configuration
 * Created by n3 on 10/02/2017.
 */
class ExecutorConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<ScheduledExecutorService>() with singleton {
                val scheduledExecutorService = ScheduledThreadPoolExecutor(2);

                val cachedExecutorService = ThreadPoolExecutor(
                        0,
                        Integer.MAX_VALUE,
                        60L,
                        TimeUnit.SECONDS,
                        SynchronousQueue<Runnable>())

                CompositeExecutorService(scheduledExecutorService, cachedExecutorService)
            }
        }
    }
}
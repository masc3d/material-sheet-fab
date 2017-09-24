package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.rx.Schedulers
import sx.concurrent.task.CompositeExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/**
 * Executor configuration
 * Created by masc on 10/02/2017.
 */
class ExecutorConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<ScheduledExecutorService>() with singleton {
                CompositeExecutorService.create(
                        scheduledCorePoolSize = 2)
            }

            bind<ExecutorService>() with singleton {
                instance<ScheduledExecutorService>()
            }

            bind<Schedulers>() with singleton {
                Schedulers()
            }
        }
    }
}
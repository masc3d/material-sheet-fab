package org.deku.leoz.mobile.config

import android.app.Application
import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.android.ApplicationStateMonitor
import sx.android.IdleTimer
import sx.android.ui.Timer
import sx.time.TimeSpan
import java.util.concurrent.ScheduledExecutorService

/**
 * Application configuration
 * Created by masc on 12/12/2016.
 */
class ApplicationConfiguration {
    companion object {
        private val log = LoggerFactory.getLogger(ApplicationConfiguration::class.java)

        val module = Kodein.Module {
            bind<ApplicationStateMonitor>() with eagerSingleton {
                ApplicationStateMonitor(instance<Application>())
            }

            /** Application wide main thread timer, suitable for chonologically updating clocks */
            bind<Timer>() with eagerSingleton {
                Timer(interval = Duration.ofSeconds(1))
            }

            bind<IdleTimer>() with eagerSingleton {
                IdleTimer(
                        context = instance<Context>(),
                        executor = instance<ScheduledExecutorService>()
                ).also {
                    it.notifyIdleDuration = Duration.ofMinutes(1)
                }
            }
        }
    }
}
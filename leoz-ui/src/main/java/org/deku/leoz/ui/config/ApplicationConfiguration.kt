package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.ui.Settings
import sx.JarManifest
import sx.concurrent.task.CompositeExecutorService
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/**
 * Application configuration
 * Created by masc on 20/11/2016.
 */
class ApplicationConfiguration {
    companion object {
        val module = Kodein.Module {
            /** Application side settings */
            bind<Settings>() with singleton { Settings() }

            bind<JarManifest>() with singleton {
                JarManifest(this.javaClass)
            }

            /** Localization & internationalization */
            bind<org.deku.leoz.ui.Localization>() with singleton {
                org.deku.leoz.ui.Localization(
                        baseName = "i18n.leoz",
                        locale = Locale.GERMAN,
                        defaultLocale = Locale.ENGLISH)
            }

            /** Scheduled executor service */
            bind<ScheduledExecutorService>() with singleton {
                CompositeExecutorService.create(
                        scheduledCorePoolSize =  2,
                        cachedCorePoolSize = 2)
            }

            /** Regular executor service */
            bind<ExecutorService>() with singleton {
                instance<ScheduledExecutorService>()
            }
        }
    }
}
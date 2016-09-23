package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.ui.Localization
import org.deku.leoz.ui.Settings
import org.deku.leoz.ui.bridge.LeoBridge
import sx.concurrent.task.CompositeExecutorService
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Application/injection configuration
 * Created by masc on 18/08/16.
 */
object Configurations {
    /**
     * Kodein application module
     */
    val application = Kodein.Module {
        /** Application side settings */
        bind<Settings>() with singleton { Settings() }

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

        /** Discovery service */
        bind<DiscoveryService>() with singleton {
            DiscoveryService(
                    executorService = instance(),
                    bundleType = BundleType.LEOZ_UI)
        }

        /** Leo bridge */
        bind<LeoBridge>() with singleton {
            LeoBridge()
        }

    }

    /**
     * Kodein messenging module
     */
    val messenging = Kodein.Module {
    }
}

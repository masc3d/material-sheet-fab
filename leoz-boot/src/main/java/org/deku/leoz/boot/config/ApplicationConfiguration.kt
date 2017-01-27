package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.boot.Settings
import sx.JarManifest
import sx.concurrent.task.CompositeExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/**
 * Application configuration
 * Created by masc on 08/11/2016.
 */
class ApplicationConfiguration {
    companion object {
        val module = Kodein.Module {
            /** Application wide settings */
            bind<Settings>() with singleton { Settings() }
            bind<ScheduledExecutorService>() with singleton { CompositeExecutorService.create(scheduledCorePoolSize = 2) }
            bind<ExecutorService>() with singleton { instance<ScheduledExecutorService>() }
            bind<JarManifest>() with singleton {
                JarManifest(this.javaClass)
            }
        }
    }
}
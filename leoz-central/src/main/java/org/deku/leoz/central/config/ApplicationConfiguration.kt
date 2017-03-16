package org.deku.leoz.central.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.central.Application
import org.deku.leoz.node.Storage
import org.deku.leoz.node.config.LogConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Created by masc on 28/11/2016.
 */
@Configuration
@Profile(Application.PROFILE_CENTRAL)
open class ApplicationConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Application>() with eagerSingleton {
                Application()
            }

            bind<org.deku.leoz.node.Application>() with eagerSingleton {
                val application: Application = instance()
                application
            }
        }
    }

    @get:Bean
    open val storage: Storage = Kodein.global.instance()

    @get:Bean
    open val logConfiguration: LogConfiguration = Kodein.global.instance()

    @get:Bean
    open val app: Application = Kodein.global.instance()
}
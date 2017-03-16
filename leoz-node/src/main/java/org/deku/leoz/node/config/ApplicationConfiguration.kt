package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Created by masc on 28/11/2016.
 */
@Configuration
@Profile(Application.PROFILE_CLIENT_NODE)
open class ApplicationConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Application>() with eagerSingleton {
                Application()
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
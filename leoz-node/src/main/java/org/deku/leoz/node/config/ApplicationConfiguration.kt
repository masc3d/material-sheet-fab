package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.SystemInformation
import org.deku.leoz.identity.Identity
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile

/**
 * Created by masc on 28/11/2016.
 */
@Configuration
@Profile(Application.PROFILE_CLIENT_NODE)
class ApplicationConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<Application>() with eagerSingleton {
                Application()
            }

            bind<SystemInformation>() with singleton {
                SystemInformation.create()
            }

            bind<Storage>() with eagerSingleton {
                val application: Application = instance()
                Storage(application.name)
            }
        }
    }

    @get:Bean
    val storage: Storage by lazy { Kodein.global.instance<Storage>() }

    @get:Bean
    val logConfiguration: LogConfiguration by lazy { Kodein.global.instance<LogConfiguration>() }

    @get:Lazy
    @get:Bean
    val app: Application by lazy { Kodein.global.instance<Application>() }

    @get:Lazy
    @get:Bean
    val identity: Identity by lazy { this.app.identity }

    @get:Bean
    val systemInformation: SystemInformation by lazy { Kodein.global.instance<SystemInformation>() }
}
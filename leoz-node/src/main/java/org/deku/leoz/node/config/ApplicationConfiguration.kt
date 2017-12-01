package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.SystemInformation
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import javax.annotation.PostConstruct

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
    open val storage: Storage by lazy { Kodein.global.instance<Storage>() }

    @get:Bean
    open val logConfiguration: LogConfiguration by lazy { Kodein.global.instance<LogConfiguration>() }

    @get:Bean
    open val app: Application by lazy { Kodein.global.instance<Application>() }

    @get:Bean
    open val systemInformation: SystemInformation by lazy { Kodein.global.instance<SystemInformation>() }
}
package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
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

    @Bean
    open fun storage(): Storage {
        return Kodein.global.instance()
    }

    @Bean
    open fun logConfiguration(): LogConfiguration {
        return Kodein.global.instance()
    }

    @Bean
    open fun app(): Application {
        return Kodein.global.instance()
    }
}
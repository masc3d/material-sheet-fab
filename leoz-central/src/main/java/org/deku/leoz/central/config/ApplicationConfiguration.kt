package org.deku.leoz.central.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.central.Application
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

    @Bean
    open fun storageConfiguration(): StorageConfiguration {
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
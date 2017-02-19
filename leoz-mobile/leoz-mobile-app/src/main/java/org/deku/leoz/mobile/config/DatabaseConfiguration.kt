package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.mobile.Database
import org.slf4j.LoggerFactory
import sx.ConfigurationMap
import sx.ConfigurationMapPath

/**
 * Database configuration
 * Created by masc on 12/12/2016.
 */
class DatabaseConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @ConfigurationMapPath("database")
    class Settings(map: ConfigurationMap) {
        val cleanStartup: Boolean by map.value(false)
    }

    companion object {
        val module = Kodein.Module {
            bind<Database>() with singleton {
                val settings = Settings(instance())

                Database(
                        context = instance(),
                        cleanStartup = settings.cleanStartup)
            }
        }
    }
}
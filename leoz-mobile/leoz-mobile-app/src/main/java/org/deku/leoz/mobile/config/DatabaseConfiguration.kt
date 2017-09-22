package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
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
            /**
             * Application database class
             */
            bind<Database>() with singleton {
                val settings = Settings(instance())

                val context = instance<Context>()
                val name = "${context.getString(R.string.app_project_name)}.db"

                Database(
                        context = instance(),
                        name = name,
                        clean = settings.cleanStartup)
            }
        }
    }
}
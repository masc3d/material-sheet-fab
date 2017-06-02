package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.*
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.reactivex.ReactiveEntityStore
import io.requery.reactivex.ReactiveSupport
import io.requery.sql.EntityDataStore
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import org.deku.leoz.mobile.data.requery.Models
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
            /**
             * Application database class
             */
            bind<Database>() with singleton {
                val settings = Settings(instance())

                Database(
                        context = instance(),
                        cleanStartup = settings.cleanStartup)
            }

            /**
             * Requery data source
             */
            bind<DatabaseSource>() with singleton {
                DatabaseSource(instance<Context>(), Models.REQUERY, 1)
            }

            /**
             * Requery reactive entity store
             */
            bind<KotlinReactiveEntityStore<Persistable>>() with singleton {
                val configuration = instance<DatabaseSource>().configuration

                KotlinReactiveEntityStore(
                        store = KotlinEntityDataStore<Persistable>(
                                configuration = configuration))
            }
        }
    }
}
package org.deku.leoz.mobile.config

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.erased.*
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import org.deku.leoz.mobile.data.requery.Models
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

                // Requery data store
                val ds = object : DatabaseSource(
                        instance<Context>(),
                        Models.REQUERY,
                        name,
                        1) {

                    override fun onCreate(db: SQLiteDatabase?) {
                        // Leave creation/migration to flyway
                    }

                    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                        // Leave creation/migration to flyway
                    }
                }

                val store = KotlinReactiveEntityStore(
                        store = KotlinEntityDataStore<Persistable>(
                                configuration = ds.configuration))

                Database(
                        context = instance(),
                        name = name,
                        store = store,
                        clean = settings.cleanStartup)
            }

            bind<Database.Migration>() with singleton {
                instance<Database>().Migration()
            }
        }
    }
}
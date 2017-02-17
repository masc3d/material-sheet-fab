package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.*
import org.deku.leoz.mobile.*
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.android.ContextHolder
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.subscribeWith
import rx.schedulers.Schedulers

/**
 * Database configuration
 * Created by masc on 12/12/2016.
 */
class DatabaseConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val module = Kodein.Module {
            bind<Database>() with singleton {
                val rootSettings: Settings = instance()

                Database(
                        context = instance(),
                        settings = Database.Settings(
                                map = rootSettings.resolve("database")))
            }
        }
    }
}
package org.deku.leoz.mobile.config

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.mobile.R
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.android.ContextHolder
import org.slf4j.LoggerFactory
import sx.android.BuildConfig

/**
 * Created by n3 on 12/12/2016.
 */
class DatabaseConfiguration(val context: Context) {
    companion object {
        val module = Kodein.Module {
            bind<DatabaseConfiguration>() with eagerSingleton {
                DatabaseConfiguration(context = instance())
            }
        }
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    val dbFile by lazy {
        context.getDatabasePath("${context.getString(R.string.app_project_name)}.db")
    }

    val dbPath by lazy {
        this.dbFile.parentFile
    }

    init {
        // Remove database file in debug builds
        if (BuildConfig.DEBUG && this.dbFile.exists()) {
            this.dbFile.delete()
        }

        this.dbPath.mkdirs()

        // Initialize context holder for flyway
        ContextHolder.setContext(this.context)

        // Initialize/migrate database schema
        val jdbcUrl = String.format("jdbc:sqldroid:%s", this.dbFile)
        val flyway = Flyway()
        flyway.setDataSource(jdbcUrl, "", "")
        flyway.clean()
        flyway.migrate()
    }
}
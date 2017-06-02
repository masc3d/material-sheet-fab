package org.deku.leoz.mobile

import android.content.Context
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.android.ContextHolder
import org.slf4j.LoggerFactory
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.Configuration
import io.requery.sql.KotlinEntityDataStore
import sx.rx.toHotReplay

/**
 * Database
 * Created by n3 on 17/02/2017.
 */
class Database(
        val context: Context,
        val cleanStartup: Boolean = false) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    val name by lazy {
        "${context.getString(R.string.app_project_name)}.db"
    }

    /**
     * Database file
     */
    val file by lazy {
        context.getDatabasePath(this.name)
    }

    /**
     * Database path
     */
    val path by lazy {
        this.file.parentFile
    }

    init {
        if (this.cleanStartup) {

            // Remove database file in debug builds
            if (this.file.exists()) {
                log.warn("Deleting database file")
                this.file.delete()
            }
        }

        this.path.mkdirs()

        // Initialize context holder for flyway
        ContextHolder.setContext(this.context)
    }

    /**
     * Most recent asynchronous migration result. Null is ok.
     */
    var migrationResult: Throwable? = null
        private set

    /**
     * Migrate database (asynchronously)
     * @return Hot observable emitting the number of successfully applied migrations
     */
    fun migrate(): Observable<Int> {
        return Observable.fromCallable {
            // Initialize/migrate database schema
            val jdbcUrl = String.format("jdbc:sqldroid:%s", this.file)
            val flyway = Flyway()
            flyway.setDataSource(jdbcUrl, "", "")
            flyway.migrate()
        }
                .subscribeOn(Schedulers.computation())
                .doOnComplete {
                    migrationResult = null
                    log.info("Completed")
                }
                .doOnError {
                    migrationResult = it
                    log.error(it.message)
                }
                .toHotReplay()
    }

}
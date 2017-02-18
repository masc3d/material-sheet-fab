package org.deku.leoz.mobile

import android.content.Context
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.android.ContextHolder
import org.slf4j.LoggerFactory
import rx.Observable
import rx.schedulers.Schedulers
import sx.maps.mapValue
import sx.rx.Awaitable
import sx.rx.subscribeAwaitableWith

/**
 * Created by n3 on 17/02/2017.
 */
class Database(
        val context: Context,
        val settings: Settings = Settings()) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    class Settings(map: Map<String, Any> = mapOf()) {
        val cleanStartup: Boolean by mapValue(map, false)
    }

    /**
     * Database file
     */
    val file by lazy {
        context.getDatabasePath("${context.getString(R.string.app_project_name)}.db")
    }

    /**
     * Database path
     */
    val path by lazy {
        this.file.parentFile
    }

    init {
        if (this.settings.cleanStartup) {

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
     * @return Awaitable
     */
    fun migrate(): Awaitable {
        return Observable.fromCallable {
            // Initialize/migrate database schema
            val jdbcUrl = String.format("jdbc:sqldroid:%s", this.file)
            val flyway = Flyway()
            flyway.setDataSource(jdbcUrl, "", "")
            flyway.migrate()
        }
                .subscribeOn(Schedulers.computation())
                .subscribeAwaitableWith {
                    onCompleted {
                        migrationResult = null
                        log.info("Completed")
                    }
                    onError {
                        migrationResult = it
                        log.error(it.message)
                    }
                }
    }

}
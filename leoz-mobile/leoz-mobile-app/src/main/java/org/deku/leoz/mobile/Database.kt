package org.deku.leoz.mobile

import android.content.Context
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.android.ContextHolder
import org.slf4j.LoggerFactory
import rx.Observable
import rx.schedulers.Schedulers
import sx.rx.Awaitable
import sx.rx.subscribeAwaitableWith

/**
 * Database
 * Created by n3 on 17/02/2017.
 */
class Database(
        val context: Context,
        val cleanStartup: Boolean = false) {

    private val log = LoggerFactory.getLogger(this.javaClass)

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
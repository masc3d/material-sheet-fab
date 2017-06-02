package org.deku.leoz.mobile

import android.content.Context
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.android.ContextHolder
import org.slf4j.LoggerFactory
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import sx.Stopwatch
import sx.rx.toHotReplay

/**
 * Database
 * Created by n3 on 17/02/2017.
 */
class Database(
        val context: Context,
        val name: String,
        val store: KotlinReactiveEntityStore<Persistable>,
        val clean: Boolean = false) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    inner class Migration {
        /**
         * Most recent asynchronous migration result. Null is ok.
         */
        var result: Throwable? = null
            private set

        /**
         * Migrate database (asynchronously)
         * @return Hot observable emitting the number of successfully applied migrations
         */
        fun run(): Observable<Int> {
            return Observable.fromCallable {
                val sw = Stopwatch.createStarted()

                // Initialize/run database schema
                val jdbcUrl = String.format("jdbc:sqldroid:%s", this@Database.file)
                val flyway = Flyway()
                flyway.setDataSource(jdbcUrl, "", "")

                val result = flyway.migrate()
                // This may appear to take up to a second on older devices
                // which is misleading as migration is usually done threaded, simultaneously
                // with other initialization tasks. Even on older devices flyway migration
                // won't affect startup time significantly
                log.info("Migration completed in ${sw}")
                result
            }
                    .subscribeOn(Schedulers.computation())
                    .doOnComplete {
                        result = null
                    }
                    .doOnError {
                        result = it
                        log.error(it.message)
                    }
                    .toHotReplay()
        }
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
        if (this.clean) {

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
}
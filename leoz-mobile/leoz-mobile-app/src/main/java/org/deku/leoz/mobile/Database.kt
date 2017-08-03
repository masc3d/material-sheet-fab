package org.deku.leoz.mobile

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github.salomonbrys.kodein.erased.instance
import org.slf4j.LoggerFactory
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.requery.EntityCache
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.android.sqlitex.SqlitexDatabaseSource
import io.requery.cache.EntityCacheBuilder
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import org.deku.leoz.mobile.model.entity.Models
import sx.Stopwatch
import sx.rx.toHotReplay

/**
 * Database
 * Created by n3 on 17/02/2017.
 */
class Database(
        val context: Context,
        val name: String,
        val clean: Boolean = false) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        /**
         * Schema version. Must be increased on entity model changes.
         */
        val SCHEMA_VERSION = 1
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

    /**
     * Requery data source
     */
    val dataSource: SqlitexDatabaseSource by lazy {
        // Using requery's more current sqlite implementation
        // SqlitexDatabaseSource -> https://github.com/requery/sqlite-android
        val ds = object : SqlitexDatabaseSource(
                this.context,
                Models.DEFAULT,
                this.name,
                SCHEMA_VERSION
        ) {

            override fun onDowngrade(db: io.requery.android.database.sqlite.SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                throw IllegalStateException("Downgrade not allowed")
            }

            override fun onCreate(db: io.requery.android.database.sqlite.SQLiteDatabase?) {
                super.onCreate(db)
            }

            override fun onUpgrade(db: io.requery.android.database.sqlite.SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                super.onUpgrade(db, oldVersion, newVersion)
            }

            override fun onConfigure(builder: ConfigurationBuilder) {
                builder.setEntityCache(
                        EntityCacheBuilder(Models.DEFAULT)
                                .useReferenceCache(true)
                                .build()
                )
                super.onConfigure(builder)
            }
        }

        ds.setLoggingEnabled(false)
        ds.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS)

        ds
    }

    /**
     * Requery entity store
     */
    val store by lazy {
        KotlinReactiveEntityStore(
                store = KotlinEntityDataStore<Persistable>(
                        configuration = this.dataSource.configuration))
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
    }
}
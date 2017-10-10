package org.deku.leoz.mobile

import android.content.Context
import io.reactivex.Scheduler
import io.requery.Persistable
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.sqlitex.SqlitexDatabaseSource
import io.requery.cache.EntityCacheBuilder
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import org.deku.leoz.mobile.model.entity.Models
import org.slf4j.LoggerFactory
import sx.android.requery.sqliteVersion

/**
 * Database
 * Created by n3 on 17/02/2017.
 * @param context
 * @param name Database name
 * @param clean Remove database prior to initialization
 * @param scheduler The scheduler to use for database operations
 */
class Database(
        val context: Context,
        val name: String,
        val clean: Boolean = false,
        val scheduler: Scheduler
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        /**
         * Schema version. Must be increased on entity model changes.
         * Minor increases indicate soft/compatible migrations (only fields with default value or indexes added)
         * Major increases indicate breaking changes and will reset the database on migration
         */
        val SCHEMA_VERSION = SchemaVersion(major = 6, minor = 2)
    }

    /**
     * Database schema version
     */
    data class SchemaVersion(
            val major: Int,
            val minor: Int
    ) {
        val sqliteVersion by lazy { major * MAJOR_BASE + minor }

        companion object {
            /** Number base for major version updates */
            val MAJOR_BASE = 1000

            fun parse(sqliteVersion: Int): SchemaVersion {
                return SchemaVersion(
                        major = sqliteVersion.div(MAJOR_BASE),
                        minor = sqliteVersion.rem(MAJOR_BASE)
                )
            }
        }

        override fun toString(): String = "${major}.${minor}"
    }

    /**
     * Database file
     */
    val file by lazy {
        context.getDatabasePath(this.name)
    }

    /**t
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

        if (this.file.exists()) {
            val db = SQLiteDatabase.openOrCreateDatabase(this.file.toString(), null)
            val schemaVersion = SchemaVersion.parse(db.version)
            val sqliteVersion = db.sqliteVersion
            db.close()

            log.info("SQLite [${sqliteVersion}] database schema version [${schemaVersion}]")

            if (schemaVersion.major != SCHEMA_VERSION.major) {
                log.warn("Major schema update ${schemaVersion} -> ${SCHEMA_VERSION}, removing database file")
                this.file.delete()
            }
        }

        val ds = object : SqlitexDatabaseSource(
                this.context,
                Models.DEFAULT,
                this.name,
                SCHEMA_VERSION.sqliteVersion
        ) {

            override fun onDowngrade(db: io.requery.android.database.sqlite.SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                throw IllegalStateException("Downgrade not allowed")
            }

            override fun onCreate(db: io.requery.android.database.sqlite.SQLiteDatabase?) {
                log.info("Creating database")
                super.onCreate(db)
            }

            override fun onUpgrade(db: io.requery.android.database.sqlite.SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                val oldSchemaVersion = SchemaVersion.parse(oldVersion)
                val newSchemaVersion = SchemaVersion.parse(newVersion)

                log.info("Migrating database schema ${oldSchemaVersion} -> ${newSchemaVersion}")

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

            override fun onConfigure(db: SQLiteDatabase) {
                super.onConfigure(db)
                // TODO: workaround for requery bug #698 and #644 (disabling foreign key constraints)
                db.setForeignKeyConstraintsEnabled(false)
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
                log.warn("Clean initialization, removing database file")
                this.file.delete()
            }
        }

        this.path.mkdirs()
    }
}
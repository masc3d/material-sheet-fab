package org.deku.leoz.mobile

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.requery.Persistable
import io.requery.TransactionIsolation
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.sqlitex.SqlitexDatabaseSource
import io.requery.cache.EntityCacheBuilder
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import org.deku.leoz.mobile.model.entity.Models
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import sx.android.requery.backupTo
import sx.android.requery.sqliteVersion
import sx.io.serialization.Serializable
import java.io.File
import java.io.InputStream

/**
 * Database
 * Created by n3 on 17/02/2017.
 * @param context
 * @param name Database (file) name
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
        /** Asset file containing database metainfo, eg. schema version */
        val ASSET_DATABASE = "database-schema.yml"
    }

    /**
     * Database schema version
     */
    @Serializable
    data class SchemaVersion(
            var major: Int = 0,
            var minor: Int = 0
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

            fun fromYaml(inputStream: InputStream): SchemaVersion
                    = Yaml().loadAs(inputStream, SchemaVersion::class.java)
        }

        /** Determine if database schema versions are compatible */
        fun isCompatibleWith(other: SchemaVersion): Boolean =
                other.major == this.major

        override fun toString(): String = "${major}.${minor}"
    }

    /**
     * Schema version, loaded lazily from assets
     */
    val schemaVersion: SchemaVersion by lazy {
        this.context.assets.open(ASSET_DATABASE).use {
            SchemaVersion.fromYaml(it)
        }
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
     * Backup the database
     * @param destinationFile Destination file
     */
    fun backup(destinationFile: File) {
        this.dataSource.writableDatabase.backupTo(destinationFile)
    }

    /**
     * Requery data source
     */
    val dataSource: SqlitexDatabaseSource by lazy {
        // Using requery's more current sqlite implementation
        // SqlitexDatabaseSource -> https://github.com/requery/sqlite-android

        if (this.file.exists()) {
            val db = SQLiteDatabase.openOrCreateDatabase(this.file.toString(), null)
            val dbSchemaVersion = SchemaVersion.parse(db.version)
            val dbLibraryVersion = db.sqliteVersion
            db.close()

            log.info("SQLite [${dbLibraryVersion}] schema version [${dbSchemaVersion}] -> [${this.schemaVersion}] ")

            if (this.schemaVersion.isCompatibleWith(dbSchemaVersion) == false) {
                log.warn("Incompatible schema update ${dbSchemaVersion} -> ${this.schemaVersion}, removing database file")
                this.file.delete()
            }
        }

        val ds = object : SqlitexDatabaseSource(
                this.context,
                Models.DEFAULT,
                this.name,
                this.schemaVersion.sqliteVersion
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
                                // Disabling reference cache as it trigger #644 when cascade is disabled.
                                // and it has to be disabled as it's buggy, removing references still in use eg.

                                // IMPORTANT. Disabling reference cache breaks consistency of in-memory graphs!
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
package sx.android.mqtt

import android.database.sqlite.SQLiteDatabase
import org.eclipse.paho.client.mqttv3.MqttClientPersistence
import org.eclipse.paho.client.mqttv3.MqttPersistable
import org.eclipse.paho.client.mqttv3.MqttPersistenceException
import org.eclipse.paho.client.mqttv3.internal.MqttPersistentData
import org.jetbrains.anko.db.*
import org.slf4j.LoggerFactory
import sx.LazyInstance
import java.io.File
import java.util.*

/**
 * MQTT client persistence for SQLite on android
 * @param databaseFile Database file to use
 * Created by masc on 16.05.17.
 */
class MqttClientPersistenceSQLite constructor(
        private val databaseFile: File
)
    : MqttClientPersistence {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val TABLE_NAME = "mqtt"
    private val COL_KEY = "key"
    private val COL_DATA = "data"

    private val dbInstance = LazyInstance<SQLiteDatabase>({
        SQLiteDatabase.openOrCreateDatabase(this.databaseFile, null)
    })

    val db by lazy {
        this.dbInstance.get()
    }

    init {
        log.trace("Datbase version [${this.db.version}]")

        this.db.createTable(
                tableName = TABLE_NAME,
                ifNotExists = true,
                columns = *arrayOf(
                        Pair(COL_KEY, TEXT.plus(UNIQUE)),
                        Pair(COL_DATA, BLOB)))
    }

    override fun clear() {
        this.db.delete(TABLE_NAME)
    }

    override fun put(key: String, persistable: MqttPersistable) {
        val data = ByteArray(persistable.headerLength + persistable.payloadLength)

        // Copy header to buffer
        System.arraycopy(
                persistable.headerBytes,
                persistable.headerOffset,
                data,
                0,
                persistable.headerLength)

        // Copy payload to buffer
        System.arraycopy(
                persistable.payloadBytes,
                persistable.payloadOffset,
                data,
                persistable.headerLength,
                persistable.payloadLength)

        db.insertOrThrow(
                tableName = TABLE_NAME,
                values = *arrayOf(Pair(COL_KEY, key), Pair(COL_DATA, data)))
    }

    override fun open(clientId: String, serverURI: String) {
        // Creates/open connection to SQLite by retrieving lazy instance
        this.dbInstance.get()
    }

    override fun remove(key: String) {
        this.db.delete(
                tableName = TABLE_NAME,
                whereClause = "${COL_KEY} = {key}",
                args = COL_KEY to key)
    }

    override fun get(key: String): MqttPersistable {
        val data = this.db.select(
                tableName = TABLE_NAME,
                columns = COL_DATA
        )
                .whereSimple(
                        select = "${COL_KEY} = ?",
                        args = key
                )
                .parseOpt(BlobParser) ?: throw MqttPersistenceException()

        return MqttPersistentData(key, data, 0, data.size, null, 0, 0)
    }

    override fun containsKey(key: String): Boolean {
        return this.db.select(
                tableName = TABLE_NAME,
                columns = COL_KEY
        )
                .whereSimple(select = "${COL_KEY} = ?", args = key)
                .parseOpt(StringParser) != null

    }

    override fun close() {
        this.dbInstance.ifSet {
            it.close()
            this.dbInstance.reset()
        }
    }

    override fun keys(): Enumeration<*> {
        return Vector(
                this.db.select(
                        tableName = TABLE_NAME,
                        columns = COL_KEY)
                        .parseList(StringParser)
        )
                .elements()
    }
}
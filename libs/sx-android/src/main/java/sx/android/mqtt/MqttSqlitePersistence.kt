package sx.android.mqtt

import android.database.sqlite.SQLiteDatabase
import io.reactivex.Observable
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.jetbrains.anko.db.*
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.anko.sqlite.getBoolean
import sx.android.anko.sqlite.getByteArray
import sx.android.anko.sqlite.getInt
import sx.android.anko.sqlite.getString
import java.io.File
import sx.mq.mqtt.IMqttPersistence
import sx.mq.mqtt.MqttPersistentMessage
import sx.mq.mqtt.toPersistentMessage

/**
 * MQTT dispatcher persistence implementation for SQLite on android
 * @param databaseFile Database file to use
 * Created by masc on 16.05.17.
 * TODO: (not yet) thread-safe. currently not suited for sharing among multiple dispatchers (or a dispatcher implementing multi-threaded dequee)
 */
class MqttSqlitePersistence constructor(
        private val databaseFile: File
)
    : IMqttPersistence {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private val TABLE_NAME = "mqtt"
        private val COL_ID = "id"
        private val COL_TOPIC = "topic"
        private val COL_QOS = "qos"
        private val COL_RETAINED = "retained"
        private val COL_MESSAGEID = "message_id"
        private val COL_PAYLOAD = "payload"
    }

    private val db by lazy {
        // Make sure that database path exists
        this.databaseFile.parentFile.mkdirs()

        SQLiteDatabase.openOrCreateDatabase(this.databaseFile, null)
    }

    init {
        log.trace("Database version [${this.db.version}]")

        this.db.createTable(
                tableName = TABLE_NAME,
                ifNotExists = true,
                columns = *arrayOf(
                        Pair(COL_ID, INTEGER + PRIMARY_KEY),
                        Pair(COL_TOPIC, TEXT),
                        Pair(COL_QOS, INTEGER),
                        Pair(COL_RETAINED, INTEGER),
                        Pair(COL_MESSAGEID, INTEGER),
                        Pair(COL_PAYLOAD, BLOB)))

        this.db.execSQL("CREATE INDEX IF NOT EXISTS ix_topic ON ${TABLE_NAME} ( ${COL_TOPIC} )")
    }

    override fun add(topicName: String, message: MqttMessage) {
        // For this persistence implementation, the persistent id is always SQLite's rowid/id primary key
        // The actual persistence id in this instance will be set when retrieving the record
        val pm = message.toPersistentMessage(topicName, 0)

        val rowId = db.insertOrThrow(
                tableName = TABLE_NAME,
                values = *arrayOf(
                        Pair(COL_TOPIC, pm.topicName),
                        Pair(COL_QOS, pm.qos),
                        Pair(COL_RETAINED, pm.retained),
                        Pair(COL_MESSAGEID, pm.messageId),
                        Pair(COL_PAYLOAD, pm.payload))
        )
    }

    /**
     * MqttPersistentMessage row parser
     */
    private class MessageRowParser : MapRowParser<MqttPersistentMessage> {
        override fun parseRow(columns: Map<String, Any?>): MqttPersistentMessage {
            return MqttPersistentMessage(
                    persistentId = columns.getInt(COL_ID),
                    topicName = columns.getString(COL_TOPIC),
                    qos = columns.getInt(COL_QOS),
                    retained = columns.getBoolean(COL_RETAINED),
                    messageId = columns.getInt(COL_MESSAGEID),
                    payload = columns.getByteArray(COL_PAYLOAD)
            )
        }
    }

    override fun get(topicName: String?): Observable<MqttPersistentMessage> {
        return Observable.create<MqttPersistentMessage> { onSubscribe ->
            this.db.select(
                    tableName = TABLE_NAME
            ).exec {
                val parser = MessageRowParser()

                try {
                    asMapSequence().forEach {
                        onSubscribe.onNext(parser.parseRow(it))
                    }
                } catch(e: Throwable) {
                    onSubscribe.onError(e)
                    return@exec
                }
                onSubscribe.onComplete()
            }
        }
    }

    override fun remove(message: MqttPersistentMessage) {
        this.db.delete(
                tableName = TABLE_NAME,
                whereClause = "${COL_ID} = {${COL_ID}}",
                args = COL_ID to message.persistentId
        )
    }
}
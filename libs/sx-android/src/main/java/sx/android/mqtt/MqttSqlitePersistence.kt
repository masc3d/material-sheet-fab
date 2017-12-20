package sx.android.mqtt

import android.database.sqlite.SQLiteDatabase
import io.reactivex.Observable
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.jetbrains.anko.db.*
import org.slf4j.LoggerFactory
import sx.android.anko.sqlite.getBoolean
import sx.android.anko.sqlite.getByteArray
import sx.android.anko.sqlite.getInt
import sx.android.anko.sqlite.getString
import sx.android.database.sqlite.backupTo
import sx.mq.mqtt.IMqttPersistence
import sx.mq.mqtt.MqttPersistentMessage
import sx.mq.mqtt.toPersistentMessage
import java.io.File

/**
 * MQTT dispatcher persistence implementation for SQLite on android
 * @param databaseFile Database file to use
 * Created by masc on 16.05.17.
 * TODO: (not yet) thread-safe. currently not suited for sharing among multiple dispatchers (or a dispatcher implementing multi-threaded dequee)
 */
class MqttSqlitePersistence constructor(
        private val databaseFile: File
) : IMqttPersistence {
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

    /**
     * Backup persistence database
     * @param destinationFile Destination file
     */
    fun backup(destinationFile: File) {
        this.db.backupTo(destinationFile)
    }

    override fun add(topicName: String, message: MqttMessage) {
        // For this persistence implementation, the persistent id is always SQLite's rowid/id primary key
        // The actual persistence id in this instance will be set when retrieving the record
        val pm = message.toPersistentMessage(topicName, 0)

        db.insertOrThrow(
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
        val BATCH_SIZE = 100

        return Observable.create<MqttPersistentMessage> { emitter ->
            try {
                val parser = MessageRowParser()

                // Group selects into batches, so it's safe for consumers
                // to remove records even on large queues
                var lastId = -1
                while (true) {
                    val batch = this.db.select(
                            tableName = TABLE_NAME
                    )
                            .whereArgs(
                                    select = "${COL_ID} > {${COL_ID}}",
                                    args = *arrayOf(COL_ID to lastId)
                            )
                            .orderBy(COL_ID)
                            .limit(BATCH_SIZE)
                            .exec {
                                asMapSequence()
                                        .map { parser.parseRow(it) }
                                        .toList()
                            }

                    // No results -> done
                    if (batch.size == 0)
                        break

                    // Store last id for next batch/iteration
                    lastId = batch.last().persistentId

                    // Emit message batch
                    batch.forEach { emitter.onNext(it) }
                }

                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    override fun remove(message: MqttPersistentMessage) {
        this.db.delete(
                tableName = TABLE_NAME,
                whereClause = "${COL_ID} = {${COL_ID}}",
                args = *arrayOf(COL_ID to message.persistentId)
        )
    }

    override fun count(): Map<String, Int> {
        // Aggregate column
        val COL_ID_COUNT = "COUNT(${COL_ID})"

        return this.db
                .select(
                        tableName = TABLE_NAME,
                        columns = *arrayOf(COL_ID_COUNT, COL_TOPIC)
                )
                .groupBy(COL_TOPIC)
                .exec {
                    // Parser for aggregate result
                    val parser = object : MapRowParser<Pair<String, Int>> {
                        override fun parseRow(columns: Map<String, Any?>): Pair<String, Int> =
                                Pair(columns.getString(COL_TOPIC), columns.getInt(COL_ID_COUNT))
                    }

                    // Convert to map
                    asMapSequence().map {
                        parser.parseRow(it)
                    }
                            .toMap()
                }
    }
}
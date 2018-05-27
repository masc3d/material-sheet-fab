package sx.android.mqtt

import android.database.sqlite.SQLiteDatabase
import io.reactivex.Observable
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.jetbrains.anko.db.*
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.android.anko.sqlite.getBoolean
import sx.android.anko.sqlite.getByteArray
import sx.android.anko.sqlite.getInt
import sx.android.anko.sqlite.getString
import sx.android.database.sqlite.backupTo
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.mq.mqtt.IMqttPersistence
import sx.mq.mqtt.MqttPersistentMessage
import sx.mq.mqtt.toPersistentMessage
import java.io.File

/**
 * MQTT dispatcher persistence implementation for SQLite on android
 * @param databaseFile Database file to use
 * @param maxInMemoryBatchSize The maximum (in-memory) size of a single batch (in bytes)
 * Created by masc on 16.05.17.
 * TODO: (not yet) thread-safe. currently not suited for sharing among multiple dispatchers (or a dispatcher implementing multi-threaded dequee)
 */
class MqttSqlitePersistence constructor(
        private val databaseFile: File,
        private val maxInMemoryBatchSize: Int = MAX_BATCH_SIZE_BYTES
) : IMqttPersistence {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private companion object {
        const val TABLE_NAME = "mqtt"
        const val COL_ID = "id"
        const val COL_TOPIC = "topic"
        const val COL_QOS = "qos"
        const val COL_RETAINED = "retained"
        const val COL_MESSAGEID = "message_id"
        const val COL_PAYLOAD = "payload"

        const val MAX_BATCH_SIZE = 4096
        const val MAX_BATCH_SIZE_BYTES = 5 * 1024 * 1024
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
        return Observable.create<MqttPersistentMessage> { emitter ->
            try {
                val parser = MessageRowParser()

                // Group selects into batches, so it's safe for consumers
                // to remove records even on large queues
                var lastId = -1

                loop@ while (!emitter.isDisposed) {
                    var batchSize: Int = 0
                    var batchSizeBytes: Int = 0

                    //region Determine batch size
                    // Peek payload sizes
                    val sizes = Stopwatch.createStarted(this, "SELECT BATCH SIZES", { _, _ ->
                        val COL_SIZE = "LENGTH(${COL_PAYLOAD})"
                        this.db.select(TABLE_NAME, COL_SIZE)
                                .orderBy(COL_ID)
                                .limit(MAX_BATCH_SIZE)
                                .exec {
                                    asSequence()
                                            .map { IntParser.parseRow(it) }
                                            .toList()
                                }

                    })

                    log.info { "Maximum batch size ${sizes.count()} -> ${sizes.sum()} bytes" }

                    for (size in sizes) {
                        batchSizeBytes += size
                        if (batchSizeBytes > this.maxInMemoryBatchSize)
                            break
                        batchSize++
                    }
                    batchSize = if (batchSize > 0) batchSize else 1
                    //endregion

                    log.info { "Processing batch size ${batchSize} -> ${batchSizeBytes} bytes" }
                    val batch = Stopwatch.createStarted(this, "SELECT BATCH", { _, _ ->
                        this.db.select(TABLE_NAME)
                                .whereArgs(
                                        select = "${COL_ID} > {${COL_ID}}",
                                        args = *arrayOf(COL_ID to lastId)
                                )
                                .orderBy(COL_ID)
                                .limit(batchSize)
                                .exec {
                                    asMapSequence()
                                            .map { parser.parseRow(it) }
                                            .toList()
                                }

                    })

                    // No results -> done
                    if (batch.size == 0)
                        break@loop

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
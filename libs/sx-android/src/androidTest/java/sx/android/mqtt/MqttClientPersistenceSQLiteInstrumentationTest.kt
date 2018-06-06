package sx.android.mqtt

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish
import org.jetbrains.anko.db.transaction
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import sx.log.slf4j.info
import java.io.File
import java.util.*

/**
 * MQTT client sqlite persistence test
 * Created by masc on 17.05.17.
 */
@RunWith(AndroidJUnit4::class)
class MqttClientPersistenceSQLiteInstrumentationTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val databaseFile by lazy {
        // Storing database in the root project build directory
        val file = InstrumentationRegistry.getContext().getDatabasePath("mqtt.db")
        log.info("Database path [${file}]")
        file.parentFile.mkdirs()
        file.delete()
        file
    }

    private val mqttPersistence by lazy {
        MqttClientPersistenceSQLite(
                databaseFile = this.databaseFile)
    }

    fun createRandomBlob(): ByteArray {
        val b = ByteArray(256)
        Random().nextBytes(b)
        return b
    }

    fun createRandomMessage(): MqttPublish {
        return MqttPublish(
                "topicname",
                MqttMessage(this.createRandomBlob()))
    }

    fun storeMessages(count: Int) {
        for (i in 0..count) {
            mqttPersistence.put(
                    i.toString(),
                    createRandomMessage()
            )
        }
    }

    @Test
    fun testPut() {
        sx.Stopwatch.createStarted("put", { log.info(it) }, { _, _ ->
            this.storeMessages(1000)
        })
    }

    @Test
    fun testKeys() {
        log.info(
                this.mqttPersistence.keys().toList()
        )
    }

    @Test
    fun testContains() {
        log.info(
                this.mqttPersistence.containsKey("123")
        )
    }
}
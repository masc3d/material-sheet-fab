package sx.android.mqtt

import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory
import sx.logging.slf4j.info
import java.io.File
import java.util.*

/**
 * MQTT client sqlite persistence test
 * Created by masc on 17.05.17.
 */
@RunWith(RobolectricTestRunner::class)
class MqttClientPersistenceSQLiteTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val databaseFile by lazy {
        // Storing database in the root project build directory
        val file = File("build/sqlite/mqtt.db")
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
            this.mqttPersistence.put(
                    i.toString(),
                    createRandomMessage()
            )
        }
    }

    @Test
    fun testPut() {
        sx.Stopwatch.createStarted("put", { log.info(it) }, { _, _ ->
            this.storeMessages(1)
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
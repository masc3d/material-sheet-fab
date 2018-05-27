package sx.android.mqtt

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import sx.Stopwatch
import sx.log.slf4j.info
import java.io.File
import java.util.*

/**
 * MqttPersistenceSqlite test
 * Created by masc on 20.12.17.
 */
@RunWith(RobolectricTestRunner::class)
class MqttPersistenceSqliteTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val databaseFile by lazy {
        // Storing database in the root project build directory
        val file = File("build/sqlite/mqtt.db")
        log.info { "Database path [${file}]" }
        file.parentFile.mkdirs()
        file.delete()
        file
    }

    private val mqttPersistence by lazy {
        MqttSqlitePersistence(
                databaseFile = this.databaseFile)
    }

    companion object {

    }

    @Before
    fun setup() {
        val random = Random()

        log.trace("")
        Stopwatch.createStarted(this, "ADDING", Level.TRACE, {
            for (i in 1..1000) {
                val buf = ByteArray(1 + random.nextInt(1000))
                random.nextBytes(buf)
                this.mqttPersistence.add(
                        "topic1",
                        MqttMessage(buf)
                )
            }
        })
    }

    @Test
    fun testKeys() {
        log.info {
            this.mqttPersistence.count()
        }
    }

    @Test
    fun testGetTopics() {
        log.info {
            this.mqttPersistence.getTopics()
        }
    }

    @Test
    fun testGet() {
        this.mqttPersistence.get()
                .blockingIterable()
                .also {
                    log.info { "READ ${it.count()}" }

                }
    }

    @Test
    fun testGetWithTopic() {
        this.mqttPersistence.get("topic1")
                .blockingIterable()
                .also {
                    log.info { "READ ${it.count()}" }

                }
    }

    @Test
    fun testGetAndRemove() {
        this.mqttPersistence.get()
                .concatMap { msg ->
                    Observable.fromCallable {
                        this.mqttPersistence.remove(msg)
                    }
                }
                .subscribeOn(Schedulers.single())
                .blockingIterable()
                .also {
                    log.info { "REMOVED ${it.count()}"}
                }
    }
}
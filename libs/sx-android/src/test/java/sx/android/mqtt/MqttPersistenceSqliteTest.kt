package sx.android.mqtt

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory
import sx.log.slf4j.info
import sx.rx.toSingletonObservable
import java.io.File

/**
 * Created by masc on 20.12.17.
 */
@RunWith(RobolectricTestRunner::class)
class MqttPersistenceSqliteTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val databaseFile by lazy {
        // Storing database in the root project build directory
        val file = File("build/sqlite/mqtt.db")
        log.info("Database path [${file}]")
        file.parentFile.mkdirs()
        file.delete()
        file
    }

    private val mqttPersistence by lazy {
        MqttSqlitePersistence(
                databaseFile = this.databaseFile)
    }

    @Test
    fun testKeys() {
        log.info(
                this.mqttPersistence.count()
        )
    }

    @Test
    fun testGet() {
        this.mqttPersistence.get()
                .blockingSubscribe {
                    log.info(it)
                }
    }

    @Test
    fun testGetAndRemove() {
        this.mqttPersistence.get()
                .concatMap { msg ->
                    Observable.fromCallable {
                        log.info(msg)
                    }.concatWith(Observable.fromCallable {
                        this.mqttPersistence.remove(msg)
                    })
                }
                .subscribeOn(Schedulers.single())
                .blockingSubscribe()
    }
}
package sx.util

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.log.slf4j.debug
import sx.log.slf4j.trace
import sx.text.toHexString
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.TimeUnit

/**
 * Created by masc on 24.03.18.
 */
class UUIDTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testUUID() {
        val uid = UUID.randomUUID()

        log.debug {
            "${uid} ${uid.mostSignificantBits.shr(8*4).toInt().toHexString()}"
        }
    }

    @Ignore
    @Test
    fun testMostSignificantBitsCollisions() {
        val TEST_TIME = Duration.ofMinutes(1)

        val bits = mutableSetOf<Long>()

        Observable.generate<UUID> {
            it.onNext(UUID.randomUUID())
        }
                .buffer(10000)
                .flatMap {
                    Observable
                            .just(it)
                            .map {
                                synchronized(bits) {
                                    it.forEach {
                                        if (bits.contains(it.mostSignificantBits))
                                            throw IllegalStateException("collision detected")

                                        bits.add(it.mostSignificantBits)
                                    }
                                }
                            }
                            .subscribeOn(Schedulers.computation())
                }
                .ignoreElements()
                .subscribeOn(Schedulers.computation())
                .blockingAwait(TEST_TIME.toMillis(), TimeUnit.MILLISECONDS)

        log.trace { "Checked ${bits.count()} most significant bits of uuids for collisions"}
    }
}
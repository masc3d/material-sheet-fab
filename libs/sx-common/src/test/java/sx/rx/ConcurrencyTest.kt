package sx.rx

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.internal.schedulers.SchedulerWhen
import io.reactivex.rxkotlin.merge
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.log.slf4j.*

/**
 * Created by masc on 21.11.17.
 */
class ConcurrencyTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Distributes tasks across thread with concurrency limit
     */
    @Test
    fun testConcurrencyLimitWithFlatMap() {
        Observable.fromIterable(0..100)
                .flatMap({ i ->
                    Observable.create<Int> {
                        log.trace("processing ${i}")
                        Thread.sleep(200)
                        it.onNext(i)
                        it.onComplete()
                    }
                            .subscribeOn(Schedulers.io())
                    // Process maximum 4 simultaneously
                }, 4)
                .blockingSubscribe {
                    log.trace { it }
                }
    }

    @Test
    fun testConcurrencyLimitWithScheduleWhen() {
        val scheduler = Schedulers.io().limit(4)

        (0..100).map { i ->
            Observable.create<Int> {
                log.trace("processing ${i}")
                Thread.sleep(200)
                it.onNext(i)
                it.onComplete()
            }
                    .subscribeOn(scheduler)
        }
                .merge()
                .blockingSubscribe {
                    log.trace { it }
                }

    }
}
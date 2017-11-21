package sx.rx

import io.reactivex.Observable
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
    fun testConcurrencyLimit() {
        Observable.fromIterable(0..100)
                .flatMap({ i ->
                    Observable.create<Int> {
                        log.trace("processing ${i}")
                        it.onNext(i)
                        it.onComplete()
                    }
                            .subscribeOn(Schedulers.io())
                    // Process maximum 4 simultaneously
                }, 4)
                .blockingSubscribe {
                    log.trace(it)
                }

    }
}
package sx.rx

import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by masc on 23/06/16.
 */
class BlockingObservableTest {

    private fun <T> concat(observables: List<Observable<T>>): Observable<T> {
        if (observables.isEmpty())
            return Observable.empty()

        val o1 = observables[0]
        if (observables.size == 1)
            return o1

        val o2 = observables[1]
        val n = o1.concatWith(o2)

        val nl = arrayListOf(n)
        nl.addAll(observables.takeLast(observables.size - 2))

        return concat(nl)
    }

    val log = LoggerFactory.getLogger(this.javaClass)
    val DELAY = 150L
    val COUNT = 10

    /**
     * Single observable emitting numbers with a sleep/delay
     */
    fun observableDelayedNumbers(count: Int, delay: Long, onNext: (Int) -> Unit = {}): Observable<Int> {
        return Observable.create<Int> {
            var i = 1
            while (i <= count) {
                log.info("${Thread.currentThread().id} Emitting ${i}")
                Thread.sleep(delay)
                onNext(i)
                it.onNext(i++)
            }
            it.onComplete()
        }
    }

    /**
     * Merged observable emitting numbers with a sleep/delay
     */
    fun observableMergedDelayedNumbers(count: Int, delay: Long, onNext: (Int) -> Unit = {}): Observable<Int> {
        return Observable.merge((1..count).map { i ->
            Observable.unsafeCreate<Int> {
                log.info("${Thread.currentThread().id} Emitting ${i}")
                Thread.sleep(delay)
                try {
                    onNext(i)
                    it.onNext(i)
                    it.onComplete()
                } catch(e: Exception) {
                    it.onError(e)
                }
            }
        })
    }

    @Test
    fun testRxReplaySingle() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableDelayedNumbers(COUNT, DELAY, { _ -> emitCount.incrementAndGet() })

        val cov = ov
                .subscribeOn(Schedulers.newThread())
                .replay()

        cov.connect()

        // Wait until ~half the items have been emitted
        Thread.sleep(DELAY * COUNT / 2)

        cov.subscribe({ i ->
            Assert.assertFalse("Blocking until the party is over.",
                    i < COUNT && emitCount.get() == COUNT)
            log.info(String.format("Observed [%d]", i))
        })

        cov.ignoreElements().blockingAwait()
    }

    @Test
    fun testRxReplayMerged() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableMergedDelayedNumbers(COUNT, DELAY, { _ -> emitCount.incrementAndGet() })

        val cov = ov
                .subscribeOn(Schedulers.newThread())
                .replay()

        cov.connect()

        // Wait until ~half the items have been emitted
        Thread.sleep(DELAY * COUNT / 2)

        var error: Throwable? = null
        cov
                .doOnNext { i ->
                    Assert.assertFalse("Blocking until the party is over.",
                            i < COUNT && emitCount.get() == COUNT)
                    log.info(String.format("Observed [%d]", i))
                }
                .doOnError {
                    error = it
                }
                .blockingSubscribe()

        if (error != null) throw error!!
    }

    @Test
    fun testRxCacheSingle() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableDelayedNumbers(COUNT, DELAY, { _ -> emitCount.incrementAndGet() })

        val cov = ov
                .subscribeOn(Schedulers.newThread())
                .cache()

        cov.subscribe {
            i ->
            log.info("F ${i}")
        }

        // Wait until ~half the items have been emitted
        Thread.sleep(DELAY * COUNT / 2)

        var error: Throwable? = null
        cov

                .doOnNext { i ->
                    Assert.assertFalse("Blocking until the party is over.",
                            i < COUNT && emitCount.get() == COUNT)
                    log.info(String.format("Observed [%d]", i))
                }
                .doOnError {
                    error = it
                }
                .blockingSubscribe()

        if (error != null) throw error!!
    }

    @Test
    fun testRxCacheMerged() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableMergedDelayedNumbers(COUNT, DELAY, { _ -> emitCount.incrementAndGet() })

        val cov = ov
                .subscribeOn(Schedulers.newThread())
                .cache()

        cov.subscribe {
            i ->
            log.info("F ${i}")
        }

        // Wait until ~half the items have been emitted
        Thread.sleep(DELAY * COUNT / 2)

        var error: Throwable? = null
        cov
                .doOnNext { i ->
                    Assert.assertFalse("Blocking until the party is over.",
                            i < COUNT && emitCount.get() == COUNT)
                    log.info(String.format("Observed [%d]", i))
                }
                .doOnError {
                    error = it
                }
                .blockingSubscribe()

        if (error != null) throw error!!
    }

    @Test
    fun testRxCacheCompletableAfterCompletion() {
        val o = this.observableDelayedNumbers(COUNT, DELAY)
                .subscribeOn(Schedulers.newThread())
                .cache()

        o.subscribeBy(
                onNext = { i ->
                    log.info("${i}")
                })

        Thread.sleep((COUNT + 2) * DELAY)

        o.blockingSubscribe()
    }

    @Test
    fun testRxSubscribeAwaitable() {
        val emitCount = AtomicInteger(0)
        this.observableDelayedNumbers(COUNT, DELAY, { _ -> emitCount.incrementAndGet() })
                .subscribeOn(Schedulers.newThread())
                .doOnNext {
                    log.info("Observed $it")
                }
                .doOnError {
                    log.info("$it")
                }
                .blockingSubscribe()

        Assert.assertTrue(emitCount.get() == COUNT)
    }

    // TODO: migrate test cases to rxjava2

//    @Test
//    fun testRxSubscribeAwaitableCancellation() {
//        var seenError = false
//        val a = this.observableDelayedNumbers(COUNT, DELAY)
//                .subscribeOn(Schedulers.newThread())
//                .doOnNext {
//                    log.info("Observed $it")
//                }
//                .doOnError {
//                    seenError = true
//                    log.info("$it")
//                }
//
//        Thread.sleep(COUNT / 2 * DELAY)
//
//        a.cancel()
//
//        try {
//            a.await()
//            Assert.fail("Expected ${CancellationException::class}")
//        } catch(e: CancellationException) {
//        }
//
//        Assert.assertTrue(seenError)
//    }

//    @Test
//    fun testRxSubscribeAwaitableAwaitAfterCompletion() {
//        val emitCount = AtomicInteger(0)
//        val a = this.observableDelayedNumbers(COUNT, DELAY, { i -> emitCount.incrementAndGet() })
//                .subscribeOn(Schedulers.newThread())
//                .subscribeAwaitableWith {
//                    onNext {
//                        log.info("Observed $it")
//                    }
//                    onError {
//                        log.info("$it")
//                    }
//                }
//
//        Thread.sleep((COUNT + 2) * DELAY)
//        a.await()
//
//        Assert.assertTrue(emitCount.get() == COUNT)
//    }
}
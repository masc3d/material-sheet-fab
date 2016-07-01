package sx.rx

import org.slf4j.LoggerFactory
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import rx.Observable
import rx.lang.kotlin.subscribeWith
import rx.schedulers.Schedulers
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicInteger


fun <T> concat(observables: List<Observable<T>>): Observable<T> {
    if (observables.size == 0)
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

/**
 * Created by masc on 23/06/16.
 */
class RxTest {
    val log = LoggerFactory.getLogger(this.javaClass)
    final val DELAY = 150L
    final val COUNT = 10

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
            it.onCompleted()
        }
    }

    /**
     * Merged observable emitting numbers with a sleep/delay
     */
    fun observableMergedDelayedNumbers(count: Int, delay: Long, onNext: (Int) -> Unit = {}): Observable<Int> {
        return Observable.merge((1..count).map { i ->
            Observable.create<Int> {
                log.info("${Thread.currentThread().id} Emitting ${i}")
                Thread.sleep(delay)
                onNext(i)
                it.onNext(i)
                it.onCompleted()
            }
        })
    }

    @Ignore
    @Test
    fun testRxReplaySingle() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableDelayedNumbers(COUNT, DELAY, { i -> emitCount.incrementAndGet() })

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

        cov.toCompletable().await()
    }

    @Ignore
    @Test
    fun testRxReplayMerged() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableMergedDelayedNumbers(COUNT, DELAY, { i -> emitCount.incrementAndGet() })

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

        cov.toCompletable().await()
    }

    @Ignore
    @Test
    fun testRxCacheSingle() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableDelayedNumbers(COUNT, DELAY, { i -> emitCount.incrementAndGet() })

        val cov = ov
                .subscribeOn(Schedulers.newThread())
                .cache()

        cov.subscribe {
            i ->
            log.info("F ${i}")
        }

        // Wait until ~half the items have been emitted
        Thread.sleep(DELAY * COUNT / 2)

        cov.subscribe({ i ->
            Assert.assertFalse("Blocking until the party is over.",
                    i < COUNT && emitCount.get() == COUNT)
            log.info(String.format("Observed [%d]", i))
        })

        cov.toCompletable().await()
    }

    @Ignore
    @Test
    fun testRxCacheMerged() {
        val emitCount = AtomicInteger(0)

        val ov = this.observableMergedDelayedNumbers(COUNT, DELAY, { i -> emitCount.incrementAndGet() })

        val cov = ov
                .subscribeOn(Schedulers.newThread())
                .cache()

        cov.subscribe {
            i ->
            log.info("F ${i}")
        }

        // Wait until ~half the items have been emitted
        Thread.sleep(DELAY * COUNT / 2)

        cov.subscribe({ i ->
            Assert.assertFalse("Blocking until the party is over.",
                    i < COUNT && emitCount.get() == COUNT)
            log.info(String.format("Observed [%d]", i))
        })

        cov.toCompletable().await()
    }

    @Ignore
    @Test
    fun testRxCacheCompletableAfterCompletion() {
        val o = this.observableDelayedNumbers(COUNT, DELAY)
                .subscribeOn(Schedulers.newThread())
                .cache()

        o.subscribeWith({
            onNext { i ->
                log.info(i)
            }
        })


        Thread.sleep((COUNT + 2) * DELAY)

        o.toCompletable().await()
    }

    @Test
    fun testRxSubscribeAwaitable() {
        val emitCount = AtomicInteger(0)
        val a = this.observableDelayedNumbers(COUNT, DELAY, { i -> emitCount.incrementAndGet() })
                .subscribeOn(Schedulers.newThread())
                .subscribeAwaitableWith {
                    onNext {
                        log.info("Observed $it")
                    }
                    onError {
                        log.info("$it")
                    }
                }.await()

        Assert.assertTrue(emitCount.get() == COUNT)
    }

    @Test
    fun testRxSubscribeAwaitableCancellation() {
        var seenError = false
        val a = this.observableDelayedNumbers(COUNT, DELAY)
                .subscribeOn(Schedulers.newThread())
                .subscribeAwaitableWith {
                    onNext {
                        log.info("Observed $it")
                    }
                    onError {
                        seenError = true
                        log.info("$it")
                    }
                }

        Thread.sleep(COUNT / 2 * DELAY)

        a.cancel()

        try {
            a.await()
            Assert.fail("Expected ${CancellationException::class}")
        } catch(e: CancellationException) {
        }

        Assert.assertTrue(seenError)
    }

    @Test
    fun testRxSubscribeAwaitableAwaitAfterCompletion() {
        val emitCount = AtomicInteger(0)
        val a = this.observableDelayedNumbers(COUNT, DELAY, { i -> emitCount.incrementAndGet() })
                .subscribeOn(Schedulers.newThread())
                .subscribeAwaitableWith {
                    onNext {
                        log.info("Observed $it")
                    }
                    onError {
                        log.info("$it")
                    }
                }

        Thread.sleep((COUNT + 2) * DELAY)
        a.await()

        Assert.assertTrue(emitCount.get() == COUNT)
    }
}
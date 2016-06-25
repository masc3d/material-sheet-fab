package sx.rx

import org.junit.Ignore
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers


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
    @Ignore
    @Test
    fun testRxReplay() {
        val delay = 500L
        val itemCount = 10

        val obs = Observable.create<Int> {
            var i = 0
            while (true) {
                Thread.sleep(delay)
                it.onNext(i++)
            }
        }

        val obsList = (0..itemCount).map { i ->
            Observable.create<Int> {
                println("${Thread.currentThread().id} Emitting ${i}")
                Thread.sleep(delay)
                it.onNext(i)
                it.onCompleted()
            }
        }

        val obsList2 = (0..itemCount).map {
            Observable.fromCallable {
                Thread.sleep(delay)
                it
            }
        }

        val cobs = Observable.merge(obsList)
                .subscribeOn(Schedulers.newThread())
                .replay()

        cobs.connect()

        // Wait a short while
        Thread.sleep(delay * 4)

        println("Current ${Thread.currentThread().id}")
        cobs
                .toBlocking()
                .subscribe { println("${Thread.currentThread().id} Observing ${it}") }
    }
}
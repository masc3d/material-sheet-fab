package sx.rx

import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by masc on 23.04.17.
 */
class RetryTest {
    @Test
    fun testRetryWhen() {
        Observable.create<Int> {
            it.onNext(1)
            it.onNext(2)
            it.onError(IllegalStateException())
        }
                .doOnNext {
                    println("Item ${it}")
                }
                .doOnError {
                    println("Error ${it}")
                }
                .retryWith(2,
                        { retry, error ->
                            println("Retry attempt ${retry} ${error}")
                            Observable.timer(1, TimeUnit.SECONDS)
                        })
                .blockingSubscribe()
    }
}
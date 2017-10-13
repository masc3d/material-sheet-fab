package sx.rx

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by masc on 23.04.17.
 */
class RetryTest {
    @Test
    fun testRetryWhen() {
        try {
            var error: Throwable? = null

            Observable.create<Int> {
                it.onNext(1)
                it.onNext(2)
                it.onError(InterruptedException())
            }
                    .doOnNext {
                        println("Item ${it}")
                    }
                    .doOnError {
                        println("Error ${it}")
                    }
                    .retryWith(2,
                            { retry, e ->
                                println("Retry attempt ${retry} ${e}")
                                Observable.timer(1, TimeUnit.SECONDS)
                            })
                    .blockingSubscribe({}, {
                        error = it
                    })

            if (error != null)
                throw error!!

            Assert.fail()
        } catch(e: InterruptedException) {
            // ok
        }
    }
}
package sx.concurrent

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import java.lang.Thread
import kotlin.concurrent.thread

/**
 * Created by masc on 29/06/16.
 */
class Thread {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testInterrupt() {
        val t = thread {
            try {
                Thread.sleep(Long.MAX_VALUE)
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }

        Thread.sleep(500)
        t.interrupt()
        t.join()
    }

    // TODO: migrate test case to rxjava2
//    @Test
//    fun testObservableFutureInterrupt() {
//        val f = Executors.newSingleThreadExecutor().submit {
//            try {
//                Thread.sleep(Long.MAX_VALUE)
//            } catch(e: Exception) {
//                log.error(e.message, e)
//            }
//        }
//
//        val s = Observable.from(f)
//                .subscribeOn(Schedulers.newThread())
//                .subscribeAwaitableWith {
//                    onCompleted {
//                        log.info("Completed")
//                    }
//                    onError { e ->
//                        log.error(e.message, e)
//                    }
//                }
//
//        Thread.sleep(3000)
//        s.cancel()
//        try {
//            s.await()
//            Assert.fail("Expected ${CancellationException::class}")
//        } catch(e: CancellationException) {
//            log.error(e.message, e)
//        }
//    }
}
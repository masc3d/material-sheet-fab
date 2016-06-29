package sx.concurrent

import org.apache.commons.logging.LogFactory
import org.junit.Ignore
import org.junit.Test
import rx.Observable
import rx.Scheduler
import rx.lang.kotlin.subscribeWith
import rx.schedulers.Schedulers
import sx.rx.subscribeAwaitableWith
import java.lang.Thread
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * Created by masc on 29/06/16.
 */
class Thread {
    val log = LogFactory.getLog(this.javaClass)

    @Ignore
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

    @Ignore
    @Test
    fun testObservableFutureInterrupt() {
        val f = Executors.newSingleThreadExecutor().submit {
            try {
                Thread.sleep(Long.MAX_VALUE)
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }

        val s = Observable.from(f)
                .subscribeOn(Schedulers.newThread())
                .subscribeAwaitableWith {
                    onCompleted {
                        log.info("Completed")
                    }
                    onError { e ->
                        log.error(e.message, e)
                    }
                }

        s.unsubscribe()
        s.await()
    }
}
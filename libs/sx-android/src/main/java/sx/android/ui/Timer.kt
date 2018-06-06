package sx.android.ui

import android.os.Handler
import android.os.Looper
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.Disposable

/**
 * Android main looper timer
 * @property interval Timer interval
 * Created by masc on 04.09.17.
 */
class Timer(
        val interval: Duration) : Disposable {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val intervalMillis by lazy {
        interval.toMillis()
    }

    private val handler = Handler(Looper.getMainLooper())
    private var refCount = 0

    private val tickEventSubject = PublishSubject.create<Unit>()
    val tickEvent = tickEventSubject
            .hide()
            .doOnSubscribe {
                refCount++
                if (refCount == 1) {
                    this.start()
                }
            }
            .doOnDispose {
                refCount--
                if (refCount == 0) {
                    this.stop()
                }
            }

    private val callback: Runnable = object : Runnable {
        override fun run() {
            this@Timer.tickEventSubject.onNext(Unit)
            handler.postDelayed(this, intervalMillis)
        }
    }

    init {
    }

    @Synchronized private fun start() {
        log.trace("Starting timer")
        handler.postDelayed(this.callback, intervalMillis)
    }

    @Synchronized private fun stop() {
        log.trace("Stopping timer")
        handler.removeCallbacks(this.callback)
    }

    val isStarted
        get() = refCount > 0

    override fun close() {
        this.stop()
    }
}
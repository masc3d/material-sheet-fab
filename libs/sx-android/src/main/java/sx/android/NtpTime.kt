package sx.android

import android.content.Context
import com.instacart.library.truetime.TrueTimeRx
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Ntp time implementation based on truetime
 * Created by prangenberg on 07.11.17.
 */
open class NtpTime(
        private val context: Context,
        private val ntpHost: String,
        private val maxRetryCount: Int = 5,
        private val trueTimeInternalLoggingEnabled: Boolean = true
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * This observable emits every 5 minutes the current Date/Time offset.
     */
    val offsetObservable: Observable<Float?> = Observable.create {
        Schedulers.computation().schedulePeriodicallyDirect(
                {
                    val offset = getOffset()
                    if (offset == null) {
                        log.warn("TrueTime not yet initialized")
                    } else {
                        it.onNext(offset)
                    }
                },
                0,
                5,
                TimeUnit.MINUTES)
    }

    init {
        TrueTimeRx.build()
                .withSharedPreferences(this.context)
                .withLoggingEnabled(this.trueTimeInternalLoggingEnabled)
                .withRetryCount(this.maxRetryCount)
                .initializeRx(this.ntpHost)
                .ignoreElements()
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onComplete = {
                            log.trace("TrueTime initialization succeeded")
                        },

                        onError = {
                            log.error("TrueTime initialization failed: ${it.message}")
                        }
                )
    }

    fun currentNtpDateTime(): Date? {
        if (!TrueTimeRx.isInitialized()) {
            return null
        }

        return TrueTimeRx.now()
    }

    private fun getOffset(): Float? =
            if (!TrueTimeRx.isInitialized())
                null
            else
                (this.currentNtpDateTime()!!.time - Date().time) / 1000F
}
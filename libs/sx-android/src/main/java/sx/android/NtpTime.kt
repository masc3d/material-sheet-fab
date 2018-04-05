package sx.android

import android.content.Context
import com.instacart.library.truetime.TrueTimeRx
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import java.util.*

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
                            log.trace("TrueTime initialization [${this.ntpHost}] succeeded")
                        },

                        onError = {
                            log.error("TrueTime initialization [${this.ntpHost}] failed: ${it.message}")
                        }
                )
    }

    /**
     * Ntp time
     */
    val time: Date?
        get() = if (!TrueTimeRx.isInitialized())
            null
        else
            TrueTimeRx.now()

    /**
     * Deviation to system time
     */
    val deviation: Duration?
        get() = this.time?.let {
            Duration.ofMillis(it.time - Date().time)
        }
}
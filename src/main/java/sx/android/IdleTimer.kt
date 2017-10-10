package sx.android

import android.annotation.SuppressLint
import android.content.Context
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.Disposable
import sx.concurrent.Service
import sx.rx.ObservableRxProperty
import sx.time.threeten.toInstantBp
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import kotlin.properties.Delegates

@SuppressLint("CommitPrefEdits")

/**
 * Idle timer for tracking user activity
 * Created by masc on 09.10.17.
 */
class IdleTimer(
        private val context: Context,
        private val executor: ScheduledExecutorService
) : Disposable {
    companion object {
        val SHAREDPREFS_TAG = "IDLE_TIMER"
        val SHAREDPREFS_LASTACTIVE = "LAST_ACTIVE"
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val sharedPrefs by lazy {
        context.getSharedPreferences(SHAREDPREFS_TAG, Context.MODE_PRIVATE)
    }

    /** Last active time */
    var lastActive by Delegates.observable<Date>(Date(), { _, _, v ->
        // Persist state to shared prefs
        this.sharedPrefs.edit().also {
            it.putLong(SHAREDPREFS_LASTACTIVE, v.time)
            it.apply()
        }

        this.update()
    })
        private set

    /**
     * Internal service, checking for idle state periodically
     */
    private val service = object : Service(
            executorService = this.executor,
            initialDelay = Duration.ZERO,
            period = Duration.ofSeconds(1)) {

        override fun run() {
            this@IdleTimer.update()
        }
    }

    /** Current idle timespan */
    val idleDuration: Duration
        get() = Duration.between(
                this.lastActive.toInstantBp(),
                Date().toInstantBp())

    /** Observable property indicating current idle state */
    val isIdleProperty = ObservableRxProperty(false)
    var isIdle by isIdleProperty
        private set

    /**
     * The idle timespan to notify about (via reactive isIdleProperty)
     */
    var notifyIdleDuration by Delegates.observable<Duration?>(null, { _, _, _ ->
        this.update()
    })

    /** c'tor */
    init {
        // Restore state from shared prefs
        val lastActive = this.sharedPrefs.getLong(SHAREDPREFS_LASTACTIVE, 0)
        this.lastActive = if (lastActive > 0) Date(lastActive) else Date()

        this.isIdleProperty
                .subscribe {
                    log.trace("IDLE [${it.value}] idle duration [${this.idleDuration}] last active [${this.lastActive}]")
                }

        this.service.start()
    }

    /**
     * Updates internal state.
     * Checks idle state and notifies subscribers
     */
    private fun update() {
        this.isIdle = this.notifyIdleDuration?.let {
            this.idleDuration >= it
        } ?: false
    }

    /**
     * Reset idle timer.
     * Should be called on any user activity eg.
     */
    fun reset() {
        this.lastActive = Date()
    }

    override fun close() {
        this.service.close()
    }
}
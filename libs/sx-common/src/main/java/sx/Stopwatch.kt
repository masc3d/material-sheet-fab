package sx

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

// ** Borrowed from guava, converted to kt.

/**
 * An object that measures elapsed time in nanoseconds. It is useful to measure
 * elapsed time using this class instead of direct calls to [ ][System.nanoTime] for a few reasons:
 *
 *  * An alternate time source can be substituted, for testing or performance
 * reasons.
 *  * As documented by `nanoTime`, the value returned has no absolute
 * meaning, and can only be interpreted as relative to another timestamp
 * returned by `nanoTime` at a different time. `Stopwatch` is a
 * more effective abstraction because it exposes only these relative values,
 * not the absolute ones.
 *
 *
 * Basic usage:
 *
 * Stopwatch stopwatch = Stopwatch.[createStarted][.createStarted]();
 * doSomething();
 * stopwatch.[stop][.stop](); // optional
 * long millis = stopwatch.elapsed(MILLISECONDS);
 * log.info("time: " + stopwatch); // formatted string like "12.3 ms"
 *
 * Stopwatch methods are not idempotent; it is an error to start or stop a
 * stopwatch that is already in the desired state.
 *
 * When testing code that uses this class, use
 * [.createUnstarted] or [.createStarted] to
 * supply a fake or mock ticker.
 *  This allows you to
 * simulate any valid behavior of the stopwatch.
 *
 * **Note:** This class is not thread-safe.
 * @author Kevin Bourrillion
 * @since 10.0
 */
private typealias LogAction = (message: String) -> Unit

class Stopwatch {
    /**
     * A time source; returns a time value representing the number of nanoseconds elapsed since some
     * fixed but arbitrary point in time. Note that most users should use [Stopwatch] instead of
     * interacting with this class directly.
     *
     * **Warning:** this interface can only be used to measure elapsed time, not wall time.
     * @author Kevin Bourrillion
     * @since 10.0
     * *     ([mostly source-compatible](https://github.com/google/guava/wiki/Compatibility) since 9.0)
     */
    abstract class Ticker protected constructor() {
        /**
         * Returns the number of nanoseconds elapsed since this ticker's fixed
         * point of reference.
         */
        abstract fun read(): Long

        companion object {
            /**
             * A ticker that reads the current time using [System.nanoTime].

             * @since 10.0
             */
            fun systemTicker(): Ticker {
                return SYSTEM_TICKER
            }

            private val SYSTEM_TICKER = object : Ticker() {
                override fun read(): Long {
                    return System.nanoTime()
                }
            }
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.

     * @param expression a boolean expression
     * *
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     * *     string using [String.valueOf]
     * *
     * @throws IllegalStateException if `expression` is false
     */
    private fun checkState(expression: Boolean, errorMessage: Any) {
        if (!expression) {
            throw IllegalStateException(errorMessage.toString())
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.

     * @param reference an object reference
     * *
     * @return the non-null reference that was validated
     * *
     * @throws NullPointerException if `reference` is null
     */
    private fun <T> checkNotNull(reference: T?): T {
        if (reference == null) {
            throw NullPointerException()
        }
        return reference
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     * *     string using [String.valueOf]
     * @return the non-null reference that was validated
     * @throws NullPointerException if `reference` is null
     */
    private fun <T> checkNotNull(reference: T?, errorMessage: Any): T {
        if (reference == null) {
            throw NullPointerException(errorMessage.toString())
        }
        return reference
    }

    private val ticker: Ticker
    /**
     * Returns `true` if [.start] has been called on this stopwatch,
     * and [.stop] has not been called since the last call to `start()`.
     */
    var isRunning: Boolean = false
        private set
    private var elapsedNanos: Long = 0
    private var startTick: Long = 0

    internal constructor() {
        this.ticker = Ticker.systemTicker()
    }

    internal constructor(ticker: Ticker) {
        this.ticker = checkNotNull(ticker, "ticker")
    }

    /**
     * Starts the stopwatch.
     * @return this `Stopwatch` instance
     * @throws IllegalStateException if the stopwatch is already running.
     */
    fun start(): Stopwatch {
        checkState(!isRunning, "This stopwatch is already running.")
        isRunning = true
        startTick = ticker.read()
        return this
    }

    /**
     * Stops the stopwatch. Future reads will return the fixed duration that had
     * elapsed up to this point.
     * @return this `Stopwatch` instance
     * @throws IllegalStateException if the stopwatch is already stopped.
     */
    fun stop(): Stopwatch {
        val tick = ticker.read()
        checkState(isRunning, "This stopwatch is already stopped.")
        isRunning = false
        elapsedNanos += tick - startTick
        return this
    }

    /**
     * Sets the elapsed time for this stopwatch to zero,
     * and places it in a stopped state.
     * @return this `Stopwatch` instance
     */
    fun reset(): Stopwatch {
        elapsedNanos = 0
        isRunning = false
        return this
    }

    /**
     * Restart stopwatch
     * @return this `Stopwatch`instance
     */
    fun restart(): Stopwatch {
        this.reset()
        this.start()
        return this
    }

    private fun elapsedNanos(): Long {
        return if (isRunning) ticker.read() - startTick + elapsedNanos else elapsedNanos
    }

    /**
     * Returns the current elapsed time shown on this stopwatch, expressed
     * in the desired time unit, with any fraction rounded down.
     *
     * Note that the overhead of measurement can be more than a microsecond, so
     * it is generally not useful to specify [TimeUnit.NANOSECONDS]
     * precision here.
     * @since 14.0 (since 10.0 as `elapsedTime()`)
     */
    fun elapsed(desiredUnit: TimeUnit): Long {
        return desiredUnit.convert(elapsedNanos(), TimeUnit.NANOSECONDS)
    }

    /**
     * Returns a string representation of the current elapsed time.
     */
    override fun toString(): String {
        val nanos = elapsedNanos()

        val unit = chooseUnit(nanos)
        val value = nanos.toDouble() / TimeUnit.NANOSECONDS.convert(1, unit)

        // Too bad this functionality is not exposed as a regular method call
        return String.format(Locale.ROOT, "%.4g %s", value, abbreviate(unit))
    }

    companion object {
        /**
         * Creates (but does not start) a new stopwatch using [System.nanoTime]
         * as its time source.
         * @since 15.0
         */
        fun createUnstarted(): Stopwatch {
            return Stopwatch()
        }

        /**
         * Creates (but does not start) a new stopwatch, using the specified time
         * source.
         * @since 15.0
         */
        fun createUnstarted(ticker: Ticker): Stopwatch {
            return Stopwatch(ticker)
        }

        /**
         * Creates (and starts) a new stopwatch using [System.nanoTime]
         * as its time source.
         * @since 15.0
         */
        fun createStarted(): Stopwatch {
            return Stopwatch().start()
        }

        /**
         * Creates (and starts) a new stopwatch, using the specified time
         * source.
         * @since 15.0
         */
        fun createStarted(ticker: Ticker): Stopwatch {
            return Stopwatch(ticker).start()
        }

        private fun createLogMessage(stopwatch: Stopwatch, name: String): String {
            return "${name} [${stopwatch}]"
        }

        /**
         * Creates (and starts) a new stopwatch and executes a block with automatic logging
         * @param name Name of operation to measure
         * @param log Log action to perform
         * @param block Block to execute/measure
         */
        fun <T> createStarted(name: String, log: LogAction = {}, block: (Stopwatch, LogAction) -> T): T {
            val sw = Stopwatch.createStarted()
            try {
                return block(sw, log)
            } finally {
                log.invoke(this.createLogMessage(sw, name))
            }
        }

        /**
         * Creates (and starts) a new stopwatch and executes a block with automatic logging via slf4j
         * @param instance Instane using stopwatch( (used for logging)
         * @param name Name of operation to measure
         * @param block Block to execute/measure
         */
        fun <T> createStarted(instance: Any, name: String, block: (Stopwatch, Logger) -> T): T {
            val log = LoggerFactory.getLogger(instance.javaClass)
            val sw = Stopwatch.createStarted()
            try {
                return block(sw, log)
            } finally {
                log.info(this.createLogMessage(sw, name))
            }
        }

        private fun chooseUnit(nanos: Long): TimeUnit {
            if (TimeUnit.DAYS.convert(nanos, TimeUnit.NANOSECONDS) > 0) {
                return TimeUnit.DAYS
            }
            if (TimeUnit.HOURS.convert(nanos, TimeUnit.NANOSECONDS) > 0) {
                return TimeUnit.HOURS
            }
            if (TimeUnit.MINUTES.convert(nanos, TimeUnit.NANOSECONDS) > 0) {
                return TimeUnit.MINUTES
            }
            if (TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0) {
                return TimeUnit.SECONDS
            }
            if (TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0) {
                return TimeUnit.MILLISECONDS
            }
            if (TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0) {
                return TimeUnit.MICROSECONDS
            }
            return TimeUnit.NANOSECONDS
        }

        private fun abbreviate(unit: TimeUnit): String {
            when (unit) {
                TimeUnit.NANOSECONDS -> return "ns"
                TimeUnit.MICROSECONDS -> return "\u03bcs" // μs
                TimeUnit.MILLISECONDS -> return "ms"
                TimeUnit.SECONDS -> return "s"
                TimeUnit.MINUTES -> return "min"
                TimeUnit.HOURS -> return "h"
                TimeUnit.DAYS -> return "d"
                else -> throw AssertionError()
            }
        }
    }
}

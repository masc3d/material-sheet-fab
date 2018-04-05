package sx.time

import java.util.*

/**
 * Time span
 * Created by masc on 08.09.17.
 */
class TimeSpan(
        val totalMillis: Long
)
    : Comparable<TimeSpan>
{
    val hours by lazy { (totalMillis / (1000 * 60 * 60)).toInt() }
    val minutes by lazy { ((totalMillis / (1000 * 60)) % 60).toInt() }
    val seconds by lazy { ((totalMillis / 1000) % 60).toInt() }

    val absHours by lazy { Math.abs(this.hours) }
    val absMinutes by lazy { Math.abs(this.minutes) }
    val absSeconds by lazy { Math.abs(this.seconds) }

    val totalSeconds: Double by lazy { totalMillis.toDouble() / 1000 }
    val totalMinutes: Double by lazy { totalMillis.toDouble() / (1000 * 60) }
    val totalHours: Double by lazy { totalMillis.toDouble() / (1000 * 60 * 60) }

    companion object {
        fun between(a: Date, b: Date): TimeSpan = TimeSpan(b.time - a.time)

        val ZERO = TimeSpan(0)
    }

    fun format(withHours: Boolean = true, withSeconds: Boolean = true): String {
        val sign = if (this.totalMillis < 0) "-" else ""

        val components = when (withHours) {
            true -> arrayOf(
                    // Only include hours when greater than zero
                    if (this.absHours > 0) "%02d".format(this.absHours) else "",
                    "%02d".format(this.absMinutes)
            )
            false -> arrayOf(
                    "%02d".format(this.absHours * 60 + this.absMinutes)
            )
        }.plus(if (withSeconds) "%02d".format(this.absSeconds) else "")

        return "${sign}${components.filter { it.isNotBlank() }.joinToString(":")}"

    }

    override fun compareTo(other: TimeSpan): Int =
            this.totalMillis.compareTo(other.totalMillis)

    override fun toString(): String = this.format()
}
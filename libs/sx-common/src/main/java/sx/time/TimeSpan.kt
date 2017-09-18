package sx.time

import java.util.*

/**
 * Time span
 * Created by masc on 08.09.17.
 */
class TimeSpan(
        val totalMillis: Long
) {
    val hours by lazy { (totalMillis / (1000 * 60 * 60)).toInt() }
    val minutes by lazy { ((totalMillis / (1000 * 60)) % 60).toInt() }
    val seconds by lazy { ((totalMillis / 1000) % 60).toInt() }

    val absHours by lazy { Math.abs(this.hours) }
    val absMinutes by lazy { Math.abs(this.minutes) }
    val absSeconds by lazy { Math.abs(this.seconds) }

    val totalSeconds by lazy { totalMillis / 1000 }
    val totalMinutes by lazy { totalMillis / (1000 * 60) }
    val totalHours by lazy { totalMillis / (1000 * 60 * 60) }

    companion object {
        fun between(a: Date, b: Date): TimeSpan = TimeSpan(a.time - b.time)
    }

    fun format(withSeconds: Boolean = true): String {
        val sign = if (this.totalMillis < 0) "-" else ""

        val components = arrayOf(
                "%02d".format(this.absHours),
                "%02d".format(this.absMinutes),
                if (withSeconds) "%02d".format(this.absSeconds) else ""
        )

        return "${sign}${components.filter { it.isNotBlank() }.joinToString(":")}"
    }

    override fun toString(): String {
        return this.format()
    }
}
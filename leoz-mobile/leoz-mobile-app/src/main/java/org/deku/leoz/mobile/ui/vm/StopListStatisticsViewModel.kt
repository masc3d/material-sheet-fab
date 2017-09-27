package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import org.deku.leoz.mobile.model.entity.AppointmentState
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.model.entity.appointmentState
import org.slf4j.LoggerFactory
import sx.android.databinding.toField
import sx.time.toCalendar
import java.util.*

/**
 * Stop list statistics view model
 * Created by masc on 08.09.17.
 */
class StopListStatisticsViewModel(
        val stops: List<StopEntity>,
        val timerEvent: Observable<Unit>
) : BaseObservable() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private data class Stats(
            val upcomingCount: Int,
            val soonCount: Int,
            val overdueCount: Int
    )

    /**
     * Merge tick event with a static, so it ticks once initially to avoid deferred rendering
     */
    private val tickEvent = Observable.merge(
            Observable.just(Unit),
            timerEvent
    )

    val stopCount: String by lazy {
        stops.count().toString()
    }

    /**
     * Observable aggregating stats on timer
     */
    private val stats by lazy {
        this.tickEvent.map {
            var upcomingCount = 0
            var soonCount = 0
            var overdueCount = 0
            this.stops.forEach {
                when (it.appointmentState) {
                    AppointmentState.UPCOMING -> upcomingCount++
                    AppointmentState.SOON -> soonCount++
                    AppointmentState.OVERDUE -> overdueCount++
                    else -> Unit
                }
            }
            Stats(
                    upcomingCount = upcomingCount,
                    soonCount = soonCount,
                    overdueCount = overdueCount
            )
        }
    }

    private val systemTime by lazy {
        this.tickEvent.map { Date().toCalendar() }
    }

    val clockHours by lazy {
        this.systemTime.map { it.get(Calendar.HOUR) }.toField()
    }

    val clockMinutes by lazy {
        this.systemTime.map { it.get(Calendar.MINUTE) }.toField()
    }

    val appointmentUpcomingCount by lazy {
        this.stats.map { it.upcomingCount.toString() }.toField()
    }

    val appointmentUpcomingCountVisible by lazy {
        this.stats.map { it.upcomingCount > 0 }.toField()
    }

    val appointmentSoonCount by lazy {
        this.stats.map { it.soonCount.toString() }.toField()
    }

    val appointmentSoonCountVisible by lazy {
        this.stats.map { it.soonCount > 0 }.toField()
    }

    val appointmentOverdueCount by lazy {
        this.stats.map { it.overdueCount.toString() }.toField()
    }

    val appointmentOverdueCountVisible by lazy {
        this.stats.map { it.overdueCount > 0 }.toField()
    }
}

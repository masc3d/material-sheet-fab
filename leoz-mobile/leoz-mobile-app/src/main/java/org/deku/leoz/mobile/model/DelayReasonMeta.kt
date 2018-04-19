package org.deku.leoz.mobile.model

import android.content.Context
import android.support.annotation.StringRes
import org.deku.leoz.mobile.R
import org.deku.leoz.model.DelayedAppointmentReason

data class DelayedAppointmentReasonMeta(
        val value: DelayedAppointmentReason,
        @StringRes val text: Int?
) {
    fun textOrName(context: Context): String =
            if (this.text != null) context.getString(this.text) else this.value.name
}

private val meta = listOf(
        DelayedAppointmentReasonMeta(
                DelayedAppointmentReason.BAD_WEATHER,
                R.string.delay_reason_bad_weather
        ),
        DelayedAppointmentReasonMeta(
                DelayedAppointmentReason.CAR_ACCIDENT,
                R.string.delay_reason_car_accident
        ),
        DelayedAppointmentReasonMeta(
                DelayedAppointmentReason.TRAFFIC_JAM,
                R.string.delay_reason_traffic_jam
        ),
        DelayedAppointmentReasonMeta(
                DelayedAppointmentReason.VEHICLE_BREAKDOWN,
                R.string.delay_reason_vehicle_breakdown
        ),
        DelayedAppointmentReasonMeta(
                DelayedAppointmentReason.OTHER,
                R.string.delay_reason_other
        )
)

private val metaByReason = mapOf(*meta.map { Pair(it.value, it) }.toTypedArray())

val DelayedAppointmentReason.mobile: DelayedAppointmentReasonMeta
    get() = metaByReason.withDefault { DelayedAppointmentReasonMeta(this, R.string.delay_reason_other) }.getValue(this)
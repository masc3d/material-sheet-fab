package org.deku.leoz.mobile.model

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.deku.leoz.mobile.R
import org.deku.leoz.model.VehicleType

/**
 * Created by prangenberg on 02.11.17.
 */
class VehicleTypeMeta(
        val value: VehicleType,
        @StringRes var name: Int? = null,
        @DrawableRes var icon: Int? = null
) {
    fun name(context: Context): String =
            if (this.name == null) this.value.name else context.getString(this.name!!)
}

private val meta: List<VehicleTypeMeta> =
        VehicleType.values().map {
            VehicleTypeMeta(value = it).also {
                when (it.value) {
                    VehicleType.BIKE -> {
                        it.icon = R.drawable.ic_bike
                        it.name = R.string.bike
                    }

                    VehicleType.CAR -> {
                        it.icon = R.drawable.ic_car
                        it.name = R.string.car
                    }

                    VehicleType.TRUCK -> {
                        it.icon = R.drawable.ic_truck
                        it.name = R.string.truck
                    }

                    VehicleType.VAN -> {
                        it.icon = R.drawable.ic_van
                        it.name = R.string.van
                    }

                    else -> {
                        it.name = null
                        it.icon = R.drawable.ic_alert_outline
                    }
                }
            }
        }

private val metaByVehicleType = mapOf(*meta.map { Pair(it.value, it) }.toTypedArray())

val VehicleType.mobile: VehicleTypeMeta
    get() = metaByVehicleType.getValue(this)
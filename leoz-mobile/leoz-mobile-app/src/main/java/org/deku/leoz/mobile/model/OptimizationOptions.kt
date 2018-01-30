package org.deku.leoz.mobile.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import org.parceler.Parcel

/**
 * Mobile optimization options
 * Created by masc on 30.01.18.
 */
@Parcel(Parcel.Serialization.BEAN)
data class OptimizationOptions(
        @get:Bindable
        var omitAppointments: Boolean = false,
        @get:Bindable
        var shiftAppointments: Boolean = true,
        @get:Bindable
        var traffic: Boolean = true,
        @get:Bindable
        var stationNo: Int? = null
) : BaseObservable()
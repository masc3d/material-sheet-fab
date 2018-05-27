package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.Bindable
import org.parceler.Parcel
import sx.io.serialization.Serializable

/**
 * Mobile optimization options
 * Created by masc on 30.01.18.
 */
@Serializable
data class OptimizationOptionsViewModel(
        @get:Bindable
        var omitAppointments: Boolean = false,
        @get:Bindable
        var traffic: Boolean = true
) : BaseObservable() {

}
package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import org.deku.leoz.mobile.model.Parcel

/**
 * Parcel view model
 * Created by masc on 26.06.17.
 */
class ParcelViewModel(val parcel: Parcel) : BaseObservable() {

    val number: String
        get() = this.parcel.number
}
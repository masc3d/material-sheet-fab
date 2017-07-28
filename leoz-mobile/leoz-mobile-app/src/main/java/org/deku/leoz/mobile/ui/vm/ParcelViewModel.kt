package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import org.deku.leoz.mobile.model.entity.Parcel
import sx.format.format
import java.text.SimpleDateFormat
import java.util.*

/**
 * Parcel view model
 * Created by masc on 26.06.17.
 */
class ParcelViewModel(val parcel: Parcel) : BaseObservable() {

    private fun Double.toDimensionFormat(): String {
        return this.format(1)
    }

    val length: String
        get() = this.parcel.length.toDimensionFormat()

    val height: String
        get() = this.parcel.height.toDimensionFormat()

    val width: String
        get() = this.parcel.width.toDimensionFormat()

    val weight: String
        get() = "${this.parcel.weight.format(1)}"

    val modificationTime: String
        get() = if (this.parcel.modificationTime != null)
            SimpleDateFormat("dd.MM HH:mm:ss", Locale.getDefault()).format(this.parcel.modificationTime)
        else ""

    val number: String
        get() = this.parcel.number
}
package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.view.View
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.model.UnitNumber
import sx.format.format
import java.text.SimpleDateFormat
import java.util.*

/**
 * Parcel view model
 * Created by masc on 26.06.17.
 */
class ParcelViewModel(
        val parcel: Parcel,
        val showOrderTask: Boolean = true
) : BaseObservable() {

    private fun Double.toDimensionFormat(): String = this.format(1)

    private val unitNumber by lazy { UnitNumber.parse(this.parcel.number).value }

    val length: String
        get() = this.parcel.length.toDimensionFormat()

    val height: String
        get() = this.parcel.height.toDimensionFormat()

    val width: String
        get() = this.parcel.width.toDimensionFormat()

    val weight: String
        get() = this.parcel.weight.format(1)

    val modificationTime: String
        get() = if (this.parcel.modificationTime != null)
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(this.parcel.modificationTime)
        else ""

    val number: String by lazy { "${this.unitNumber.value}-${this.unitNumber.labelCheckDigit}" }

    val orderTask by lazy {
        OrderTaskViewModel(orderTask = parcel.order.deliveryTask)
    }

    val orderTaskVisbility
        get() = if (this.showOrderTask) View.VISIBLE else View.GONE
}
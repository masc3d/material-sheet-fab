package org.deku.leoz.mobile.ui.process

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_vehicletypes.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.model.VehicleType
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty


/**
 * Vehicle type picker
 * Created by masc on 12.05.18.
 */
class VehicleTypePicker : FrameLayout {
    private val log = LoggerFactory.getLogger(this.javaClass)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    val selectedProperty = ObservableRxProperty<VehicleType?>(null)
    var selected by selectedProperty

    private fun init() {
        inflate(this.context, R.layout.view_vehicletypes, this)

        this.uxVehicleTypes.selectedProperty
                .subscribe {
                    this.selected = when (it.value) {
                        uxVan -> VehicleType.VAN
                        uxTruck -> VehicleType.TRUCK
                        uxBike -> VehicleType.BIKE
                        uxCar -> VehicleType.CAR
                        else -> null
                    }
                }
    }
}

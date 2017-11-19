package org.deku.leoz.mobile.ui.dialog

import android.os.Bundle
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.viewholders.FlexibleViewHolder
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Dialog
import org.deku.leoz.model.VehicleType
import sx.LazyInstance
import sx.android.ui.flexibleadapter.SimpleVmHeaderItem
import sx.android.ui.flexibleadapter.VmItem

/**
 * Created by phpr on 24.06.2017.
 * A simple dialog where the user can choose whether he is delivering by bike, car, or van
 */
class VehicleTypeSelectionDialog : Dialog(dialogLayoutId = R.layout.dialog_vehicle_type_selection) {

    private val types = VehicleType.values()

    private val flexibleAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    VmItem<
                            *, Any>>>({
        FlexibleAdapter(listOf())
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
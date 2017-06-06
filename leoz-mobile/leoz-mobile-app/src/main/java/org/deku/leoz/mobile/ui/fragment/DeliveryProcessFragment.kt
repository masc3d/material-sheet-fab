package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_delivery_process.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.model.Stop


class DeliveryProcessFragment(val stop: Stop) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_delivery_process, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxStreet.text = stop.address.street
        this.uxStreetNo.text = stop.address.streetNo
        this.uxZip.text = stop.address.zipCode
        this.uxCity.text = stop.address.city
        this.uxReceipient.text = stop.address.addressLine1
        this.uxAppointment.text = "${stop.appointment.dateFrom} - ${stop.appointment.dateTo}"

        //region TODO To be replaced by a custom adapter
        val parcelList = mutableListOf<Order.Parcel>()

        stop.order.forEach {
            parcelList.addAll(it.parcel)
        }

        val parcelRefList: List<String> = parcelList.map {
            it.labelReference ?: ""
        }
        
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_list_item_1, parcelRefList)

        this.uxParcelList.adapter = arrayAdapter
        //endregion
    }

}

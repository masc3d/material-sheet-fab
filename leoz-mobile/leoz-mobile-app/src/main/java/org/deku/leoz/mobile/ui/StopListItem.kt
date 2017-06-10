package org.deku.leoz.mobile.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.deku.leoz.mobile.R

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_stop_overview.view.*
import org.deku.leoz.mobile.model.Stop
import java.text.SimpleDateFormat

/**
 * Created by phpr on 06.06.2017.
 */

class StopListItem(val context: Context, val stop: Stop, val rootViewGroup: ViewGroup? = null) : AbstractFlexibleItem<org.deku.leoz.mobile.ui.StopListItem.ViewHolder>() {

    var parcelCount: Int = 0
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")

    init {
        stop.order.forEach {
            it.parcel.forEach {
                parcelCount++
            }
        }
    }

    override fun equals(inObject: Any?): Boolean {
        if (inObject is StopListItem) {
            return this == inObject
        } else if (inObject is Stop) {
            return this.stop == inObject
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_stop_overview
    }

    override fun createViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater!!.inflate(R.layout.item_stop_overview, rootViewGroup), adapter!!)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>?, holder: ViewHolder?, position: Int,
                                payloads: List<*>?) {
        holder!!.street.text = stop.address.street
        holder.streetNo.text = stop.address.streetNo
        holder.zip.text = stop.address.zipCode
        holder.city.text = stop.address.city
        holder.receipient.text = stop.address.addressLine1
        holder.appointment.text = "${simpleDateFormat.format(stop.appointment.dateFrom)} - ${simpleDateFormat.format(stop.appointment.dateTo)}"
        holder.ordercount.text = stop.order.size.toString()
        holder.parcelcount.text = parcelCount.toString()

        this.isEnabled = true
        this.isDraggable = true
        this.isSwipeable = true
    }

    class ViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val street = view.uxStreet
        val streetNo = view.uxStreetNo
        val zip = view.uxZip
        val city = view.uxCity
        val receipient = view.uxReceipient
        val appointment = view.uxAppointment
        val ordercount = view.uxOrderCount
        val parcelcount = view.uxParcelCount
    }
}

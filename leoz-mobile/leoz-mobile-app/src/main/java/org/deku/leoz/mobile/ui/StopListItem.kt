package org.deku.leoz.mobile.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.deku.leoz.mobile.R

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_stop.view.*
import org.deku.leoz.mobile.model.Stop
import sx.time.toCalendar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by phpr on 06.06.2017.
 */

class StopListItem(
        val context: Context,
        val stop: Stop)
    :
        AbstractFlexibleItem<StopListItem.ViewHolder>() {

    var parcelCount: Int = 0
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")

    init {
        stop.orders.forEach {
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
        return R.layout.item_stop
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>?,
                                holder: ViewHolder,
                                position: Int,
                                payloads: List<*>?) {
        holder.street.text = stop.address.street
        holder.streetNo.text = stop.address.streetNo
        holder.zip.text = stop.address.zipCode
        holder.city.text = stop.address.city
        holder.recipient.text = stop.address.addressLine1
        holder.addressLine2.text = stop.address.addressLine2
        holder.appointmentFrom.text = simpleDateFormat.format(stop.appointment.dateFrom)
        holder.appointmentTo.text = simpleDateFormat.format(stop.appointment.dateTo)

        holder.appointmentClock.hour = stop.appointment.dateFrom.toCalendar().get(Calendar.HOUR)
        holder.appointmentClock.minute = stop.appointment.dateFrom.toCalendar().get(Calendar.MINUTE)

        holder.ordercount.text = stop.orders.size.toString()
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
        val recipient = view.uxRecipient
        val addressLine2 = view.uxAddressLine2
        val appointmentFrom = view.uxAppointmentFrom
        val appointmentTo = view.uxAppointmentTo
        val appointmentClock = view.uxAppointmentClock
        val ordercount = view.uxOrderCount
        val parcelcount = view.uxParcelCount
    }
}

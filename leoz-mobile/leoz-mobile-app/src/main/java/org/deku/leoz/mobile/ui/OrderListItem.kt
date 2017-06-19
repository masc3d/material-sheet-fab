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
import kotlinx.android.synthetic.main.item_order_short.view.*
import org.deku.leoz.mobile.model.Order

/**
 * Created by phpr on 06.06.2017.
 */

class OrderListItem(val context: Context, val order: Order, val rootViewGroup: ViewGroup? = null) : AbstractFlexibleItem<org.deku.leoz.mobile.ui.OrderListItem.ViewHolder>() {

    override fun equals(inObject: Any?): Boolean {
        if (inObject is OrderListItem) {
            return this == inObject
        } else if (inObject is Order) {
            return this.order == inObject
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_stop
    }

    override fun createViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater!!.inflate(R.layout.item_order_short, parent, false), adapter!!)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>?, holder: ViewHolder?, position: Int,
                                payloads: List<*>?) {
        holder!!.recipient.text = order.addresses.first { it.classification == Order.Address.Classification.DELIVERY }.addressLine1
        holder.sender.text = order.addresses.first { it.classification == Order.Address.Classification.PICKUP }.addressLine1
        holder.cityTo.text = order.addresses.first { it.classification == Order.Address.Classification.DELIVERY }.city
        holder.cityFrom.text = order.addresses.first { it.classification == Order.Address.Classification.PICKUP }.city
        holder.zipTo.text = order.addresses.first { it.classification == Order.Address.Classification.DELIVERY }.zipCode
        holder.zipFrom.text = order.addresses.first { it.classification == Order.Address.Classification.PICKUP }.zipCode

        this.isEnabled = true
        this.isDraggable = false
        this.isSwipeable = false
    }

    class ViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val recipient = view.uxReceipient
        val sender = view.uxSender
        val cityTo = view.uxCityTo
        val cityFrom = view.uxCityFrom
        val zipTo = view.uxZipTo
        val zipFrom = view.uxZipFrom
    }
}

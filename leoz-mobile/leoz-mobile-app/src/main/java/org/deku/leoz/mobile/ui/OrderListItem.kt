package org.deku.leoz.mobile.ui

import android.content.Context
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

    override fun equals(other: Any?): Boolean {
        if (other is OrderListItem) {
            return this == other
        } else if (other is Order) {
            return this.order == other
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_order_short
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>?, holder: ViewHolder?, position: Int,
                                payloads: List<*>?) {
        holder!!.recipient.text = order.deliveryAddress.line1
        holder.sender.text = order.pickupAddress.line1
        holder.cityTo.text = order.deliveryAddress.city
        holder.cityFrom.text = order.pickupAddress.city
        holder.zipTo.text = order.deliveryAddress.zipCode
        holder.zipFrom.text = order.pickupAddress.zipCode

        this.isEnabled = true
        this.isDraggable = false
        this.isSwipeable = false
    }

    class ViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val recipient = view.uxRecipient
        val sender = view.uxSender
        val cityTo = view.uxCityTo
        val cityFrom = view.uxCityFrom
        val zipTo = view.uxZipTo
        val zipFrom = view.uxZipFrom
    }
}



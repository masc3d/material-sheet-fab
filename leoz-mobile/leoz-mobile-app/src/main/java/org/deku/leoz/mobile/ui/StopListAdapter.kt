package org.deku.leoz.mobile.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.deku.leoz.mobile.model.Stop
import android.view.LayoutInflater
import android.widget.TextView
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.row_tour_overview.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.R.id.uxZip
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * Created by 27694066 on 22.05.2017.
 */
class StopListAdapter(val context: Context, val data: List<Stop>, val rootViewGroup: ViewGroup? = null): BaseAdapter() {

    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vi: View = convertView ?: inflater.inflate(R.layout.row_tour_overview, rootViewGroup)
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
        var parcelCount = 0

        data[position].order.forEach {
            parcelCount += it.parcel.size
        }

        vi.uxStreet.text = data[position].address.street
        vi.uxStreetNo.text = data[position].address.streetNo
        vi.uxZip.text = data[position].address.zipCode
        vi.uxCity.text = data[position].address.city
        vi.uxReceipient.text = data[position].address.addressLine1
        vi.uxAppointment.text = "${simpleDateFormat.format(data[position].appointment.dateFrom)} - ${simpleDateFormat.format(data[position].appointment.dateTo)}"
        vi.uxOrderCount.text = data[position].order.size.toString()
        vi.uxOrderCount.text = parcelCount.toString()

        return vi
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }
}
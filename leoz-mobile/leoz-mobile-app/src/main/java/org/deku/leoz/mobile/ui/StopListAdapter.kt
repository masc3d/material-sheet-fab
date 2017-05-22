package org.deku.leoz.mobile.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.deku.leoz.mobile.model.Stop
import android.view.LayoutInflater
import android.widget.TextView
import org.deku.leoz.mobile.R
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.text.DateFormat


/**
 * Created by 27694066 on 22.05.2017.
 */
class StopListAdapter(val context: Context, val data: List<Stop>, val rootViewGroup: ViewGroup? = null): BaseAdapter() {

    lateinit var inflater: LayoutInflater

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vi: View = convertView ?: inflater.inflate(R.layout.row_tour_overview, rootViewGroup)
        var parcelCount = 0
        val dateFormat: DateFormat = android.text.format.DateFormat.getDateFormat(context)

        data[position].order.forEach {
            parcelCount += it.parcel.size
        }

        (vi.findViewById(R.id.uxStreet) as TextView).text = data[position].address.addressLine1
        (vi.findViewById(R.id.uxStreetNo) as TextView).text = data[position].address.addressLineNo1
        (vi.findViewById(R.id.uxZip) as TextView).text = data[position].address.zipCode
        (vi.findViewById(R.id.uxCity) as TextView).text = data[position].address.city
        (vi.findViewById(R.id.uxReceipient) as TextView).text = data[position].address.contactPerson
        (vi.findViewById(R.id.uxAppointment) as TextView).text = data[position].appointment
        (vi.findViewById(R.id.uxOrderCount) as TextView).text = data[position].order.size.toString()
        (vi.findViewById(R.id.uxParcelCount) as TextView).text = parcelCount.toString()

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
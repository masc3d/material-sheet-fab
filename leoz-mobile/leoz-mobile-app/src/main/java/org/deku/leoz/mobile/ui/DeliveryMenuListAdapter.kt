package org.deku.leoz.mobile.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.deku.leoz.mobile.R

/**
 * Created by 27694066 on 23.05.2017.
 */
class DeliveryMenuListAdapter (val context: Context, val entry: List<DeliveryMenuEntry>, val rootViewGroup: ViewGroup): BaseAdapter() {
    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vi: View = convertView ?: inflater.inflate(R.layout.row_delivery_menue_entry, rootViewGroup)

        (vi.findViewById(R.id.uxMenuDescription) as TextView).text = entry[position].description
        (vi.findViewById(R.id.uxMenuIcon) as ImageView).setImageDrawable(entry[position].icon)

        if (entry[position].counter == 0) {
            (vi.findViewById(R.id.uxCount) as TextView).visibility = GONE
        } else {
            (vi.findViewById(R.id.uxCount) as TextView).visibility = VISIBLE
        }
        (vi.findViewById(R.id.uxCount) as TextView).text = entry[position].counter.toString()

        return vi
    }

    override fun getItem(position: Int): Any {
        return entry[position]
    }

    override fun getItemId(position: Int): Long {
        return entry[position].entryType.id
    }

    override fun getCount(): Int {
        return entry.size
    }

    data class DeliveryMenuEntry (val entryType: Entry, val description: String, val counter: Int, val icon: Drawable) {
        enum class Entry(val id: Long) {
            ORDERLIST(0),
            LOADING(1)
        }
    }
}
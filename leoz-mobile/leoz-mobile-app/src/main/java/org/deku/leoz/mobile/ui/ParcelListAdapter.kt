package org.deku.leoz.mobile.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.item_delivery_process_parcel_list.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Parcel


/**
 * Created by 27694066 on 22.05.2017.
 */
class ParcelListAdapter(val context: Context, val data: List<Parcel>, val rootViewGroup: ViewGroup? = null, val readOnly: Boolean = false): BaseAdapter() {

    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vi: View = convertView ?: inflater.inflate(R.layout.item_delivery_process_parcel_list, rootViewGroup)

        vi.uxParcelReference.text = data[position].number
        vi.uxParcelStatus.setImageDrawable(context.resources.getDrawable(R.drawable.ic_cancel_black))

        if (readOnly) {
            vi.uxParcelStatus.visibility = View.GONE
        }

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
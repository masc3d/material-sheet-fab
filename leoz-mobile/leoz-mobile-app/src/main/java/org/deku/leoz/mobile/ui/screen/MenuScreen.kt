package org.deku.leoz.mobile.ui.screen


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.content.res.AppCompatResources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.item_delivery_menu_entry.view.*
import kotlinx.android.synthetic.main.screen_menu.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.ui.ScreenFragment
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import sx.android.getLayoutInflater

/**
 * Delivery main fragment
 *
 * Presents the main entry point within the delivery process.
 */
class MenuScreen : ScreenFragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    data class MenuEntry(val entryType: Entry, val description: String, var counter: Int, var counter2: Int, val icon: Drawable) {
        enum class Entry(val value: Long) {
            ORDERLIST(0),
            LOADING(1)
        }

        constructor(entryType: Entry, description: String, counter: Int, icon: Drawable) : this(entryType, description, counter, 0, icon)
    }

    /**
     * Created by 27694066 on 23.05.2017.
     */
    class MenuListAdapter(
            val context: Context,
            val entries: List<MenuEntry>): BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = context.getLayoutInflater()

            val v: View = convertView ?: inflater.inflate(R.layout.item_delivery_menu_entry, parent, false)

            v.uxMenuDescription.text = entries[position].description
            v.uxMenuIcon.setImageDrawable(entries[position].icon)

            if (entries[position].counter == 0) {
                v.uxCount.visibility = View.GONE
            } else {
                v.uxCount.visibility = View.VISIBLE
            }
            if (entries[position].counter2 == 0) {
                v.uxCount2.visibility = View.GONE
            } else {
                v.uxCount2.visibility = View.VISIBLE
            }
            v.uxCount.text = entries[position].counter.toString()
            v.uxCount2.text = entries[position].counter2.toString()

            return v
        }

        override fun getItem(position: Int): Any {
            return entries[position]
        }

        override fun getItemId(position: Int): Long {
            return entries[position].entryType.value
        }

        override fun getCount(): Int {
            return entries.size
        }
    }

    private var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "mobileX Tour"
        this.headerImage = R.drawable.img_street_1a

        this.aidcEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_menu, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       this.uxMenuList.adapter = MenuListAdapter(
                context = context,
                entries = mutableListOf(
                        MenuEntry(
                                entryType = MenuEntry.Entry.LOADING,
                                description = "Fahrzeugbeladung",
                                counter = delivery.countParcelsToBeVehicleLoaded(),
                                counter2 = delivery.countParcelsToBeVehicleUnLoaded(),
                                icon = AppCompatResources.getDrawable(context, R.drawable.ic_truck_delivery)!!
                        ),
                        MenuEntry(
                                entryType = MenuEntry.Entry.ORDERLIST,
                                description = "Auftragsliste",
                                counter = delivery.stopList.filter {
                                    it.state == Stop.State.PENDING &&
                                            it.stopTasks.map { it.order }.firstOrNull { it.state == Order.State.LOADED } != null }.size,
                                icon = AppCompatResources.getDrawable(context, R.drawable.ic_format_list_bulleted)!!
                        )
                ))

        this.uxMenuList.setOnItemClickListener { parent, view, position, id ->
            onEntryPressed(
                    entry = (this.uxMenuList.getItemAtPosition(position) as MenuEntry)
            )
        }
    }

    fun onEntryPressed(entry: MenuEntry) {
        if (listener != null) {
            listener!!.onDeliveryMenuChoosed(entry.entryType)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context as Listener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface Listener {
        // TODO: Update argument type and name
        fun onDeliveryMenuChoosed(entryType: MenuEntry.Entry)
    }
}

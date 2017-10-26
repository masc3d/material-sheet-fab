package org.deku.leoz.mobile.ui.screen


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
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
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.ui.Headers
import org.deku.leoz.mobile.ui.ScreenFragment
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import sx.android.getDrawableCompat
import sx.android.getLayoutInflater

/**
 * Delivery main fragment
 *
 * Presents the main entry point within the delivery process.
 */
class MenuScreen : ScreenFragment<Any>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    enum class EntryType {
        DELIVERY,
        LOADING,
        UNLOADING
    }

    inner class Entry(
            val type: EntryType,
            val description: String,
            var counter1: Int,
            var counter2: Int,
            val icon: Drawable) {

        constructor(entryTypeType: EntryType, description: String, counter: Int, icon: Drawable) : this(entryTypeType, description, counter, 0, icon)

        val isSelectable: Boolean
            get() = when (this.type) {
                EntryType.LOADING -> {
                    true
                }
                EntryType.DELIVERY -> {
                    delivery.pendingStops.blockingFirst().value.count() > 0
                }
                EntryType.UNLOADING -> {
                    deliveryList.loadedParcels.get().count() > 0
                }
            }
    }

    /**
     * Created by 27694066 on 23.05.2017.
     */
    class MenuListAdapter(
            val context: Context,
            val entries: List<Entry>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = context.getLayoutInflater()

            val v: View = convertView ?: inflater.inflate(R.layout.item_delivery_menu_entry, parent, false)

            val entry = entries[position]

            v.uxMenuDescription.text = entry.description
            v.uxMenuIcon.setImageDrawable(entry.icon)

            v.uxCount.visibility = if (entry.counter1 > 0) View.VISIBLE else View.GONE
            v.uxCount2.visibility = if (entry.counter2 > 0) View.VISIBLE else View.GONE
            v.alpha = if (entry.isSelectable) 1.0F else 0.5F

            v.uxCount.text = entry.counter1.toString()
            v.uxCount2.text = entry.counter2.toString()

            return v
        }

        override fun getItem(position: Int): Any = entries[position]

        override fun getItemId(position: Int): Long = entries[position].type.hashCode().toLong()

        override fun getCount(): Int = entries.size
    }

    private val listener by lazy { this.activity as? Listener }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "mobileX Tour"
        this.headerImage = Headers.street

        this.aidcEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.screen_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxMenuList.adapter = MenuListAdapter(
                context = context,
                entries = mutableListOf(
                        Entry(
                                type = EntryType.LOADING,
                                description = this.getText(R.string.vehicle_loading).toString(),
                                counter1 = deliveryList.pendingParcels.get().count(),
                                counter2 = deliveryList.loadedParcels.blockingFirst().value.count(),
                                icon = this.context.getDrawableCompat(R.drawable.ic_truck_loading)
                        ),
                        Entry(
                                entryTypeType = EntryType.DELIVERY,
                                description = this.getText(R.string.tour).toString(),
                                counter = delivery.pendingStops.blockingFirst().value.count(),
                                icon = this.context.getDrawableCompat(R.drawable.ic_format_list_bulleted)
                        ),
                        Entry(
                                type = EntryType.UNLOADING,
                                description = this.getText(R.string.vehicle_unloading).toString(),
                                counter1 = deliveryList.loadedParcels.get().count(),
                                counter2 = deliveryList.pendingParcels.blockingFirst().value.count(),
                                icon = this.context.getDrawableCompat(R.drawable.ic_truck_unloading)
                        )
                ))

        this.uxMenuList.setOnItemClickListener { _, _, position, _ ->
            onEntryPressed(
                    entry = (this.uxMenuList.getItemAtPosition(position) as Entry)
            )
        }
    }

    fun onEntryPressed(entry: Entry) {
        if (entry.isSelectable)
            listener?.onDeliveryMenuSelection(entry.type)
    }

    interface Listener {
        // TODO: Update argument type and name
        fun onDeliveryMenuSelection(entryType: EntryType)
    }
}

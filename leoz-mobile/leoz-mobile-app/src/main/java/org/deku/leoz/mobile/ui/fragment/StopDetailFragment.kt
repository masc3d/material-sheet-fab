package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_stop_detail.uxOrderList
import kotlinx.android.synthetic.main.fragment_stop_detail.uxServiceList

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.OrderListItem
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import org.jetbrains.anko.layoutInflater
import sx.LazyInstance


/**
 * A simple [Fragment] subclass.
 */
class StopDetailFragment : Fragment() {

    private lateinit var stop: Stop
    private var order: Order? = null
    private var serviceDescriptions: MutableList<String> = mutableListOf()

    private val flexibleAdapterInstance = LazyInstance<FlexibleAdapter<OrderListItem>>({
        FlexibleAdapter(
                //Orders to be listed
                stop.orders
                        .filter {
                            it.state == Order.State.LOADED
                        }
                        .map {
                            OrderListItem(context, it)
                        },
                //Listener
                this
        )
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    companion object {
        fun create(stop: Stop): StopDetailFragment {
            val f = StopDetailFragment()
            f.stop = stop
            return f
        }

        fun create(order: Order): StopDetailFragment {
            TODO("NOT IMPLEMENTED")
            val f = StopDetailFragment()
            f.order = order
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_stop_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (this.activity as DeliveryActivity).showDeliverFabButtons()

        stop.orders
                .flatMap { it.services }
                .forEach { serviceDescriptions.add(it.service.toString()) }

        this.uxServiceList.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, serviceDescriptions)

        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        this.uxOrderList.adapter = flexibleAdapter
        this.uxOrderList.layoutManager = LinearLayoutManager(context)
        //this.uxStopList.addItemDecoration(dividerItemDecoration)

        flexibleAdapter.isLongPressDragEnabled = true
        flexibleAdapter.isHandleDragEnabled = true
        flexibleAdapter.isSwipeEnabled  = false
    }

    override fun onResume() {
        super.onResume()
    }

}

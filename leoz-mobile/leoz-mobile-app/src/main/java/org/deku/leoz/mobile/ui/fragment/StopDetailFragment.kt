package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_stop_detail.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.activity.DeliveryActivity


/**
 * A simple [Fragment] subclass.
 */
class StopDetailFragment : Fragment() {

    private lateinit var stop: Stop
    private var order: Order? = null
    private var serviceDescriptions: MutableList<String> = mutableListOf()

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

        if (serviceDescriptions.size == 0)
            serviceDescriptions.add("No Service")

        this.uxServiceList.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, serviceDescriptions)
    }

    override fun onResume() {
        super.onResume()
    }

}

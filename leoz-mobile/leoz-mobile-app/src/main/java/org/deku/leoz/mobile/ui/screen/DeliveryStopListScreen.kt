package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.StopListItem
import org.slf4j.LoggerFactory
import android.support.annotation.CallSuper
import kotlinx.android.synthetic.main.screen_delivery_stop_list.*
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import sx.LazyInstance


/**
 * A simple [Fragment] subclass.
 */
class DeliveryStopListScreen : ScreenFragment(), FlexibleAdapter.OnItemMoveListener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private val flexibleAdapterInstance = LazyInstance<FlexibleAdapter<IFlexible<*>>>( {
        FlexibleAdapter(getItemList(delivery.stopList.filter { it.state == Stop.State.PENDING }), this)
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    //region Listener interface implementation
    /**
     * @see FlexibleAdapter.OnItemMoveListener
     */

    override fun onActionStateChanged(p0: RecyclerView.ViewHolder?, p1: Int) {
        log.debug("ONACTIONSTATECHANGED")
        log.debug("ViewHolder [${p0.toString()}] Value [$p1]")
    }

    @CallSuper
    override fun onItemMove(p0: Int, p1: Int) {
        log.debug("ONITEMMOVE value [$p0] value [$p1]")
    }

    override fun shouldMoveItem(p0: Int, p1: Int): Boolean {
        log.debug("SHOULDMOVEITEM value [$p0] value [$p1]")
        return true
    }

    private val onItemClickListener = FlexibleAdapter.OnItemClickListener { item ->
        log.debug("ONITEMCLICK")

        activity.showScreen(
                DeliveryProcessScreen.create((flexibleAdapter.getItem(item) as StopListItem).stop)
        )

        true
    }
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_delivery_stop_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        this.uxStopList.setOnItemClickListener { parent, view, position, id ->
//            val dialog = StopListDialog(this.uxStopList.getItemAtPosition(position) as Stop)
//            dialog.show(childFragmentManager, "TOURLISTDIALOG")
//        }
//
//        this.uxStopList.adapter = StopListAdapter(context, delivery.stopList.filter { it.state == Stop.State.PENDING })

//        flexibleAdapter.expandItemsAtStartUp()
//        flexibleAdapter.setAnimationOnScrolling(true)

        //val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager(context).orientation)


        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        this.uxStopList.adapter = flexibleAdapter
        this.uxStopList.layoutManager = LinearLayoutManager(context)
        //this.uxStopList.addItemDecoration(dividerItemDecoration)

        flexibleAdapter.isLongPressDragEnabled = true
        flexibleAdapter.isHandleDragEnabled = true
        flexibleAdapter.isSwipeEnabled  =true
        flexibleAdapter.addListener(onItemClickListener)
//        flexibleAdapter.setDisplayHeadersAtStartUp(true)
//        flexibleAdapter.setStickyHeaders(true)
    }

    fun getItemList(stops: List<Stop>): List<IFlexible<*>> {
        val list = mutableListOf<IFlexible<*>>()
        list.addAll(stops.map {
            StopListItem(context, it)
        })
        return list
    }
}

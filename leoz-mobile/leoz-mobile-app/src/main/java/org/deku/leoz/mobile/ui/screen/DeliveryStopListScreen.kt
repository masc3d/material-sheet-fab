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

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.model.Stop
import org.slf4j.LoggerFactory
import android.support.annotation.CallSuper
import kotlinx.android.synthetic.main.screen_delivery_stop_list.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.vm.FlexibleViewModelItem
import org.deku.leoz.mobile.ui.vm.StopItemViewModel
import sx.LazyInstance


/**
 * A simple [Fragment] subclass.
 */
class DeliveryStopListScreen : ScreenFragment(), FlexibleAdapter.OnItemMoveListener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private val flexibleAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleViewModelItem<
                            StopItemViewModel>>>({
        val adapter = FlexibleAdapter(
                // Items
                delivery.stopList
                        .filter {
                            it.state == Stop.State.PENDING && it.orders.any {
                                it.state == Order.State.LOADED
                            }
                        }
                        .map {
                            val item = FlexibleViewModelItem(
                                    R.layout.item_stop,
                                    BR.stop,
                                    StopItemViewModel(it)
                            )

                            item.isEnabled = true
                            item.isDraggable = true
                            item.isSwipeable = true

                            item
                        },
                // Listener
                this)

        adapter
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.scrollWithCollapsingToolbarEnabled = true
        this.title = "Delivery Stops"
        this.headerImage = R.drawable.img_parcels_1
    }

    //region Listener interface implementation
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

        val stop = flexibleAdapter.getItem(item)?.viewModel?.stop

        if (stop != null) {
            activity.showScreen(
                    DeliveryProcessScreen.create(
                            stop = stop
                    )
            )
        }

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

        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        this.uxStopList.adapter = flexibleAdapter
        this.uxStopList.layoutManager = LinearLayoutManager(context)

        flexibleAdapter.isLongPressDragEnabled = true
        flexibleAdapter.isHandleDragEnabled = true
        flexibleAdapter.isSwipeEnabled = true
        flexibleAdapter.addListener(onItemClickListener)
    }
}

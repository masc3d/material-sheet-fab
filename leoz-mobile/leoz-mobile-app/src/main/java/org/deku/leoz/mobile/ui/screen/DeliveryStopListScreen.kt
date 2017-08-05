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
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.entity.Stop
import org.slf4j.LoggerFactory
import android.support.annotation.CallSuper
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.screen_delivery_stop_list.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import sx.android.ui.flexibleadapter.FlexibleVmItem
import org.deku.leoz.mobile.ui.vm.StopItemViewModel
import sx.LazyInstance
import sx.android.aidc.*


/**
 * A simple [Fragment] subclass.
 */
class DeliveryStopListScreen : ScreenFragment(), FlexibleAdapter.OnItemMoveListener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val aidcReader: sx.android.aidc.AidcReader by Kodein.global.lazy.instance()

    private val flexibleAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleVmItem<
                            StopItemViewModel>>>({
        val adapter = FlexibleAdapter(
                // Items
                delivery.pendingStops.blockingFirst()
                        .map {
                            val item = FlexibleVmItem(
                                    R.layout.item_stop,
                                    BR.stop,
                                    StopItemViewModel(it)
                            )

                            item.isEnabled = true
                            item.isDraggable = true
                            item.isSwipeable = false

                            item
                        },
                // Listener
                this)

        adapter
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
        this.title = "Delivery Stops"
        this.aidcEnabled = true
    }

    override fun onResume() {
        super.onResume()

        aidcReader.decoders.set(
                Interleaved25Decoder(true, 11, 12),
                DatamatrixDecoder(true),
                Ean8Decoder(true),
                Ean13Decoder(true),
                Code128Decoder(true)
        )

        aidcReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }
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
                    StopDetailScreen.create(
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

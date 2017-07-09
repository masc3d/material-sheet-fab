package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_vehicleloading.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.*

import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.slf4j.LoggerFactory
import sx.LazyInstance


/**
 * Vehicle loading screen
 */
class VehicleLoadingScreen : ScreenFragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val aidcReader: sx.android.aidc.AidcReader by com.github.salomonbrys.kodein.Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()
    private val loadedParcels: MutableList<Parcel> = mutableListOf()

    private val parcelListAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleVmSectionableItem<
                            ParcelViewModel>>>({

        val header1 = FlexibleVmHeaderItem<ParcelListHeaderViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = ParcelListHeaderViewModel()
        )

        val adapter = FlexibleAdapter(
                // Items
                delivery.stopList
                        .flatMap {
                            it.orders
                                    .flatMap { it.parcel }
                        }
                        .map {
                            val item = FlexibleVmSectionableItem(
                                    viewRes = R.layout.item_parcel,
                                    variableId = BR.parcel,
                                    viewModel = ParcelViewModel(it)
                            )

                            item.header = header1
                            item.isEnabled = true
                            item.isDraggable = true
                            item.isSwipeable = false

                            item
                        },
                // Listener
                this)

        adapter.headerItems.add(header1)
        adapter.setDisplayHeadersAtStartUp(true)
        adapter.setStickyHeaders(true)
        adapter.showAllHeaders()

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    override fun onCreateView(inflater: android.view.LayoutInflater,
                              container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicleloading, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Flexible adapter needs to be re-created with views
        this.parcelListAdapterInstance.reset()

        this.uxParcelList.adapter = parcelListAdapter
        this.uxParcelList.layoutManager = LinearLayoutManager(context)

//        parcelListAdapter.addListener(onItemClickListener)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_vehicle_loading_exception,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_circle_cancel,
                        menu = this.activity.inflateMenu(R.menu.menu_vehicle_loading_exception)
                )
        )
    }

    override fun onResume() {
        super.onResume()

        aidcReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    processLabelScan(it.data)
                }

//        this.activity.actionEvent
//                .bindUntilEvent(this, FragmentEvent.PAUSE)
//                .subscribe {
//                    when (it) {
//                        R.id.action_deliver_fail -> {
//                            showFailureReasons()
//                        }
//                    }
//                }

        this.activity.supportActionBar?.title = "Vehicle loading"
    }

    private fun processLabelScan(data: String) {
        val order = delivery.findOrderByLabelReference(data)

        log.debug("VehicleLoading parcel reference [$data] Orders found [${order.size}] ")

        when (order.size) {
            0 -> {
                //Error, order could not be found
                log.warn("No order with a parcel reference [$data] could be found")

                //Query order from Central services
            }
            1 -> {
                //Continue
                val parcel = order.first().findParcelByReference(data)
                log.debug("Parcel ID [${parcel!!.id}] Order ID [${order.first().id}] State [${parcel.state}]")
//                if (order.first().parcelVehicleLoading(parcel)) {
//                    updateLoadedParcelList(mutableListOf(parcel))
//                }
                log.debug("State after processing [${parcel.state}]")
            }
            else -> {
                //What to do if multiple orders are found?
                log.warn("Multiple orders found with a parcel reference [$data]")
            }
        }
    }
}

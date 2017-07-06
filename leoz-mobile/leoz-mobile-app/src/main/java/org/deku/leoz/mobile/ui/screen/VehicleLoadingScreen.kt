package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_vehicle_loading.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.model.DeliveryList
import org.deku.leoz.mobile.model.Order

import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ParcelListAdapter
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.slf4j.LoggerFactory


/**
 * A simple [Fragment] subclass.
 */
class VehicleLoadingScreen : ScreenFragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val aidcReader: sx.android.aidc.AidcReader by com.github.salomonbrys.kodein.Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()
    private val loadedParcels: MutableList<Order.Parcel> = mutableListOf()

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_vehicle_loading, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        loadedParcels.addAll(delivery.orderList.flatMap { it.parcel }.filter { it.state == Order.Parcel.State.LOADED })
        updateLoadedParcelList(mutableListOf<Order.Parcel>())

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
                if (order.first().parcelVehicleLoading(parcel)) {
                    updateLoadedParcelList(mutableListOf(parcel))
                }
                log.debug("State after processing [${parcel.state}]")
            }
            else -> {
                //What to do if multiple orders are found?
                log.warn("Multiple orders found with a parcel reference [$data]")
            }
        }
    }

    private fun updateLoadedParcelList(parcels: MutableList<Order.Parcel>) {
        parcels.removeAll(loadedParcels)
        loadedParcels.addAll(0, parcels)
        this.uxParcelList.adapter = ParcelListAdapter(context = context, data = loadedParcels, readOnly = true) //ArrayAdapter(context, android.R.layout.simple_list_item_1, loadedParcels.map { it.labelReference })
    }

}

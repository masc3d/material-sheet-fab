package org.deku.leoz.mobile.ui.screen


import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.screen_vehicleloading.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenVehicleloadingBinding
import org.deku.leoz.mobile.device.Tone
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.process.DeliveryList

import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.aidc.*
import sx.android.inflateMenu
import sx.android.ui.flexibleadapter.FlexibleVmHeaderItem
import sx.android.ui.flexibleadapter.FlexibleVmSectionableItem

/**
 * Vehicle loading screen
 */
class VehicleLoadingScreen : ScreenFragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Created by masc on 10.07.17.
     */
    class StatsViewModel(
            val deliveryList: DeliveryList
    )
        : BaseObservable() {

        val stopCounter = CounterViewModel(
                drawableRes = R.drawable.ic_location,
                amount = ObservableField("${this.deliveryList.stopAmount}"),
                totalAmount = ObservableField("${this.deliveryList.stopTotalAmount}")
        )

        val orderCounter = CounterViewModel(
                drawableRes = R.drawable.ic_file_document,
                amount = ObservableField("${this.deliveryList.orderAmount}"),
                totalAmount = ObservableField("${this.deliveryList.orderTotalAmount}")
        )

        val parcelCounter = CounterViewModel(
                drawableRes = R.drawable.ic_package_closed,
                amount = ObservableField("${this.deliveryList.parcelAmount}"),
                totalAmount = ObservableField("${this.deliveryList.parcelTotalAmount}")
        )

        val weightCounter = CounterViewModel(
                drawableRes = R.drawable.ic_scale,
                amount = ObservableField("${this.deliveryList.weight}kg"),
                totalAmount = ObservableField("${this.deliveryList.totalWeight}kg")
        )
    }

    private val tone: Tone by Kodein.global.lazy.instance()
    private val aidcReader: sx.android.aidc.AidcReader by com.github.salomonbrys.kodein.Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()

    private val parcelListAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleVmSectionableItem<
                            ParcelViewModel>>>({

        val header1 = FlexibleVmHeaderItem<ParcelListHeaderViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = ParcelListHeaderViewModel(
                        this.deliveryList
                )
        )

        val adapter = FlexibleAdapter(
                // Items
                delivery.stopList
                        .flatMap { it.tasks }
                        .flatMap { it.order.parcels }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "Vehicle loading"

        this.aidcEnabled = true
    }

    override fun onCreateView(inflater: android.view.LayoutInflater,
                              container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {

        val binding: ScreenVehicleloadingBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.screen_vehicleloading,
                container, false)

        // Setup bindings
        binding.stats = StatsViewModel(deliveryList)

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Flexible adapter needs to be re-created with views
        this.parcelListAdapterInstance.reset()

        this.uxParcelList.adapter = parcelListAdapter
        this.uxParcelList.layoutManager = LinearLayoutManager(context)

//        parcelListAdapter.addListener(onItemClickListener)

        this.menu = this.inflateMenu(R.menu.menu_vehicleloading)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_vehicle_loading_exception,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_circle_cancel,
                        menu = this.activity.inflateMenu(R.menu.menu_vehicleloading_exception)
                )
        )
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
                    when {
                        it.data.toLongOrNull() != null && it.data.count() <= 9 -> {
                            deliveryList.load(it.data.toLong())
                        }
                        else -> processLabelScan(it.data)
                    }

                }

        this.activity.menuItemEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    log.trace("MENU ITEM SELECTED [${it}]")
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
    }

    private fun processLabelScan(data: String) {
        val orders = delivery.findOrderByLabelReference(data)

        log.debug("VehicleLoading parcel reference [$data] Orders found [${orders.size}] ")

        when (orders.size) {
            0 -> {
                //Error, order could not be found
                log.warn("No order with a parcel reference [$data] could be found")
                if (data.count() <= 9 && data.toLongOrNull() != null) {
                    log.debug("Check for delivery list with id [$data]")
                    deliveryList.load(data.toLong())
                            .bindToLifecycle(this)
                            .subscribe {
                                if (it.isEmpty()) {
                                    tone.errorBeep()
                                } else {

                                }
                            }
                }
                tone.errorBeep()
                //Query order from Central services
            }
            1 -> {
                //Continue
                val parcel = orders.flatMap { it.parcels }.first { it.number == data }
                log.debug("Parcel ID [${parcel.id}] Order ID [${orders.first().id}] State [${parcel.state}]")
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

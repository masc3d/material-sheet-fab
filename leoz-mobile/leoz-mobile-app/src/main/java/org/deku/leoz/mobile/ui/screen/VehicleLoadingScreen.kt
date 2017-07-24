package org.deku.leoz.mobile.ui.screen


import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
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
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.model.repository.OrderRepository

import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.UnitNumber
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.aidc.*
import sx.android.databinding.toField
import sx.android.inflateMenu
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleVmSectionableItem
import sx.format.format

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
                amount = this.deliveryList.stopAmount.map { it.value.toString() }.toField(),
                totalAmount = this.deliveryList.stopTotalAmount.map { it.value.toString() }.toField()
        )

        val orderCounter = CounterViewModel(
                drawableRes = R.drawable.ic_file_document,
                amount = this.deliveryList.orderAmount.map { it.value.toString() }.toField(),
                totalAmount = this.deliveryList.orderTotalAmount.map { it.value.toString() }.toField()
        )

        val parcelCounter = CounterViewModel(
                drawableRes = R.drawable.ic_package_closed,
                amount = this.deliveryList.parcelAmount.map { it.value.toString() }.toField(),
                totalAmount = this.deliveryList.parcelTotalAmount.map { it.value.toString() }.toField()
        )

        val weightCounter = CounterViewModel(
                drawableRes = R.drawable.ic_scale,
                amount = this.deliveryList.weight.map { "${it.value.format(2)}kg" }.toField(),
                totalAmount = this.deliveryList.totalWeight.map { "${it.value.format(2)}kg" }.toField()
        )
    }

    private val tone: Tone by Kodein.global.lazy.instance()
    private val aidcReader: sx.android.aidc.AidcReader by com.github.salomonbrys.kodein.Kodein.global.lazy.instance()

    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()

    private val parcelListAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleExpandableVmItem<ParcelListHeaderViewModel, ParcelViewModel>
            >>({

        val headerDelivered = FlexibleExpandableVmItem<ParcelListHeaderViewModel, ParcelViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = ParcelListHeaderViewModel(
                        title = this.getText(R.string.delivered).toString(),
                        amountProperty = this.deliveryList.parcelsLoaded.map { it.count() },
                        totalAmountProperty = this.deliveryList.parcelTotalAmount.map { it.value }
                )
        )

        val flexibleItems = this.orderRepository.entities
                .flatMap { it.parcels }
                .map {
                    val item = FlexibleVmSectionableItem(
                            viewRes = R.layout.item_parcel,
                            variableId = BR.parcel,
                            viewModel = ParcelViewModel(it)
                    )

                    item.isEnabled = true
                    item.isDraggable = true
                    item.isSwipeable = false
                    item.header = headerDelivered

                    item
                }

        headerDelivered.isHidden = false
        headerDelivered.subItems = flexibleItems

        val adapter = FlexibleAdapter(listOf(headerDelivered),
                this,
                true)

        adapter.setStickyHeaders(true)
        adapter.collapseAll()
        adapter.showAllHeaders()

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "Vehicle loading"
        this.headerImage = R.drawable.img_parcels_1a
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed

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
                        id = R.id.action_vehicle_loading_finished,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_check_circle
                ),
                ActionItem(
                        id = R.id.action_vehicle_loading_exception,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_cancel_black,
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
                    this.deliveryList.load(10730061)
                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_vehicle_loading_exception -> {
                            //showFailureReasons()
                        }

                        R.id.action_vehicle_loading_finished -> {
                            val dialog = MaterialDialog.Builder(context)
                                    .title(getString(R.string.question_vehicle_loading_finished))
                                    .negativeText(getString(R.string.no_go_back))
                                    .positiveText(getString(R.string.yes_start_tour))
                                    .show()
                        }

                        else -> log.warn("Unhandled ActionEvent [$it]")
                    }
                }
    }

    private fun processLabelScan(data: String) {
        val unitNumberParseResult = UnitNumber.parseLabel(data)

        when {
            unitNumberParseResult.hasError -> {
                // TODO: display error
            }
            else -> {
                val unitNumber = unitNumberParseResult.value
                val order = this.orderRepository.entities.find { it.parcels.any { it.number == unitNumber.value } }

                log.debug("VehicleLoading parcel reference [$data] orders found [${order}] ")

                when {
                    order != null -> {
                        //Continue
                        val parcel = order.parcels.first { it.number == unitNumber.value }
                        log.debug("Parcel ID [${parcel.id}] Order ID [${order.id}] state [${parcel.loadingState}]")
//                if (order.first().parcelVehicleLoading(parcel)) {
//                    updateLoadedParcelList(mutableListOf(parcel))
//                }
                        log.debug("State after processing [${parcel.loadingState}]")
                    }
                    else -> {
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
                }
            }
        }
    }
}

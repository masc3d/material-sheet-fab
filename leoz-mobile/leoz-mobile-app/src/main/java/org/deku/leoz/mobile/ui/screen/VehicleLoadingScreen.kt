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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.screen_vehicleloading.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenVehicleloadingBinding
import org.deku.leoz.mobile.dev.SyntheticInputs
import org.deku.leoz.mobile.device.Tone
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository

import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.DekuDeliveryListNumber
import org.deku.leoz.model.UnitNumber
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.Stopwatch
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.databinding.toField
import sx.android.inflateMenu
import sx.android.rx.observeOnMainThread
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
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()

    private val parcelListAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleExpandableVmItem<ParcelListHeaderViewModel, ParcelViewModel>
                    >>({

        val headerLoaded = FlexibleExpandableVmItem<ParcelListHeaderViewModel, ParcelViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = ParcelListHeaderViewModel(
                        title = this.getText(R.string.loaded).toString(),
                        amount = this.deliveryList.loadedParcels.map { it.value.count() }
                ),
                isExpandableOnClick = false
        )

        val headerDamaged = FlexibleExpandableVmItem<ParcelListHeaderViewModel, ParcelViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = ParcelListHeaderViewModel(
                        title = this.getText(R.string.damaged).toString(),
                        amount = this.deliveryList.damagedParcels.map { it.value.count() }
                ),
                isExpandableOnClick = false
        )

        val headers = listOf(
                headerLoaded,
                headerDamaged
        )

        val adapter = FlexibleAdapter(
                // Items
                headers,
                // Listener
                this,
                true)

        headers.forEach { header ->
            header.isSelectable = true
        }

        // TODO: unreliable. need to override flexibleadapter for proper reactive event
        adapter.addListener(object : FlexibleAdapter.OnItemClickListener {
            private var previousHeaderItem: Any? = headerLoaded

            override fun onItemClick(position: Int): Boolean {
                val item: Any? = adapter.getItem(position)

                if (item != null && headers.contains(item)) {
                    val changed = (item != this.previousHeaderItem)

                    // Select & collapse
                    adapter.toggleSelection(position)

                    if (changed) {
                        adapter.collapseAll()
                    } else {
                        if (adapter.isExpanded(position)) {
                            adapter.collapse(position)
                        } else {
                            adapter.expand(position)
                        }
                    }

                    this.previousHeaderItem = item
                }

                return true
            }
        })

        this.deliveryList.loadedParcels
                .bindToLifecycle(this)
                .observeOnMainThread()
                .subscribe {
                    // Need to collapse on complete sublist update to prevent weird glitches
                    adapter.collapseAll()

                    headerLoaded.subItems = it.value.map {
                        val item = FlexibleVmSectionableItem(
                                viewRes = R.layout.item_parcel,
                                variableId = BR.parcel,
                                viewModel = ParcelViewModel(it)
                        )

                        item.isEnabled = true
                        item.isDraggable = false
                        item.isSwipeable = false
                        item.isSelectable = false
                        item.header = headerLoaded

                        item
                    }

                    adapter.updateItem(headerLoaded)
                }

        this.deliveryList.damagedParcels
                .bindToLifecycle(this)
                .observeOnMainThread()
                .subscribe {
                    // Need to collapse on complete sublist update to prevent weird glitches
                    adapter.collapseAll()

                    headerDamaged.subItems = it.value.map {
                        val item = FlexibleVmSectionableItem(
                                viewRes = R.layout.item_parcel,
                                variableId = BR.parcel,
                                viewModel = ParcelViewModel(it)
                        )

                        item.isEnabled = true
                        item.isDraggable = false
                        item.isSwipeable = false
                        item.isSelectable = false
                        item.header = headerDamaged

                        item
                    }

                    adapter.updateItem(headerDamaged)
                }


        adapter.mode = FlexibleAdapter.MODE_SINGLE

        adapter.setStickyHeaders(true)
        adapter.showAllHeaders()
        adapter.setAutoCollapseOnExpand(true)
        adapter.collapseAll()

        // TODO: bug preventing expansion of sections when `toggleSelection` is not deferred initially
        this.view?.post {
            adapter.toggleSelection(0)
        }

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
                    this.onAidcRead(it)
                }

        this.activity.menuItemEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it.itemId) {
                        R.id.action_reset -> {
                            this.orderRepository.removeAll().blockingGet()
                        }
                    }
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

        this.parcelRepository.entitiesProperty
                .map { it.value }
                .subscribe { parcels ->
                    this.syntheticInputs = listOf(
                            SyntheticInputs(
                                    name = "Delivery lists",
                                    entries = listOf(
                                            SyntheticInputs.Entry(
                                                    symbologyType = SymbologyType.Interleaved25,
                                                    data = DekuDeliveryListNumber.parse("10730061").value.label
                                            )
                                    )
                            ),
                            SyntheticInputs(
                                    name = "Parcels",
                                    entries = parcels.map {
                                        val unitNumber = UnitNumber.parse(it.number).value
                                        SyntheticInputs.Entry(
                                                symbologyType = SymbologyType.Interleaved25,
                                                data = unitNumber.label
                                        )
                                    }
                            )
                    )

                }
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        try {
            log.trace("AIDC READ $event")

            val stopwatch = Stopwatch.createStarted()
            val result = Observable.concat(
                    Observable.fromCallable { UnitNumber.parseLabel(event.data) },
                    Observable.fromCallable { DekuDeliveryListNumber.parseLabel(event.data) }
            )
                    .takeUntil { !it.hasError }
                    .last(Result(error = IllegalArgumentException("Invalid barcode")))
                    .blockingGet()

            when {
                result.hasError -> {
                    // TODO: display error
                }
                else -> {
                    val resultValue = result.value

                    when (resultValue) {
                        is UnitNumber -> {
                            this.onUnitNumberInput(resultValue)
                        }
                        is DekuDeliveryListNumber -> {
                            this.onDeliveryListNumberInput(resultValue)
                        }
                    }
                }
            }
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    fun onDeliveryListNumberInput(deliveryListNumber: DekuDeliveryListNumber) {
        deliveryList.load(deliveryListNumber.value.toLong())
                .bindToLifecycle(this)
                .subscribeBy(
                    onNext = {

                    },
                    onError = {
                        tone.errorBeep()
                    }
                )
    }

    fun onUnitNumberInput(unitNumber: UnitNumber) {
        log.trace("Unit number input ${unitNumber.value}")
        val parcel = this.parcelRepository.entities.firstOrNull { it.number == unitNumber.value }

        if (parcel != null) {
            parcel.loadingState = Parcel.State.LOADED
            this.parcelRepository.update(parcel).blockingGet()
        }
    }
}

package org.deku.leoz.mobile.ui.process

import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.screen_vehicleloading.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenVehicleloadingBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Feedback
import org.deku.leoz.mobile.log.user
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.process.VehicleLoading
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.rx.composeAsRest
import org.deku.leoz.mobile.rx.toHotIoObservable
import org.deku.leoz.mobile.settings.DebugSettings
import org.deku.leoz.mobile.ui.core.BaseCameraScreen
import org.deku.leoz.mobile.ui.core.Headers
import org.deku.leoz.mobile.ui.core.ScreenFragment
import org.deku.leoz.mobile.ui.core.view.ActionItem
import org.deku.leoz.mobile.ui.process.tour.stop.DamagedParcelCameraScreen
import org.deku.leoz.mobile.ui.vm.CounterViewModel
import org.deku.leoz.mobile.ui.vm.ParcelViewModel
import org.deku.leoz.mobile.ui.vm.SectionViewModel
import org.deku.leoz.mobile.ui.vm.SectionsAdapter
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.TourServiceV1
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.inflateMenu
import sx.android.rx.observeOnMainThreadUntilEvent
import sx.android.rx.observeOnMainThreadWithLifecycle
import sx.android.ui.flexibleadapter.SimpleVmItem
import sx.android.ui.flexibleadapter.VmHeaderItem
import sx.format.format
import sx.log.slf4j.trace
import sx.rx.ObservableRxProperty
import java.util.*

/**
 * Vehicle loading screen
 */
class VehicleLoadingScreen :
        ScreenFragment<Any>(),
        BaseCameraScreen.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        /** List of static tour uids for synthetic input / testing */
        val SYNTHETIC_TOURS = listOf<String>(
        )

        /** List of static parcel numbers for synthetic input / testing */
        val SYNTHETIC_PARCELS = listOf<String>(
        )
    }

    interface Listener {
        fun onVehicleLoadingFinalized()
    }

    val listener by listenerDelegate<Listener>()

    /**
     * Created by masc on 10.07.17.
     */
    class StatsViewModel(
            val vehicleLoading: VehicleLoading
    ) : BaseObservable() {

        val stopCounter = CounterViewModel(
                iconRes = R.drawable.ic_stop,
                amount = this.vehicleLoading.stopAmount.map { it as Number },
                totalAmount = this.vehicleLoading.stopTotalAmount.map { it as Number }
        )

        val orderCounter = CounterViewModel(
                iconRes = R.drawable.ic_order,
                amount = this.vehicleLoading.orderAmount.map { it as Number },
                totalAmount = this.vehicleLoading.orderTotalAmount.map { it as Number }
        )

        val parcelCounter = CounterViewModel(
                iconRes = R.drawable.ic_package_variant_closed,
                amount = this.vehicleLoading.parcelAmount.map { it as Number },
                totalAmount = this.vehicleLoading.parcelTotalAmount.map { it as Number }
        )

        val weightCounter = CounterViewModel(
                iconRes = R.drawable.ic_weight_scale,
                amount = this.vehicleLoading.weight.map { it as Number },
                totalAmount = this.vehicleLoading.totalWeight.map { it as Number },
                format = { "${(it as Double).format(2)}kg" }
        )
    }

    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    private val db: Database by Kodein.global.lazy.instance()

    private val feedback: Feedback by Kodein.global.lazy.instance()
    private val aidcReader: sx.android.aidc.AidcReader by Kodein.global.lazy.instance()

    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val tour: Tour by Kodein.global.lazy.instance()

    private val vehicleLoading: VehicleLoading by Kodein.global.lazy.instance()

    /** The current/most recently selected damaged parcel */
    private var currentDamagedParcel: ParcelEntity? = null

    /**
     * Amounf of detached orders loaded, mainly for disabling the confirmation dialog for
     * users which work mainly with detached orders.
     */
    private var detachedOrderCounter: Int = 0
    private var acceptDetachedOrdersWithoutConfirmation: Boolean = false

    /** Indicates that process is currently busy, controls finish button visibility */
    private val isBusyProperty = ObservableRxProperty(false)
    private var isBusy by isBusyProperty

    // region Sections
    val loadedSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_truck_loading,
                color = R.color.colorDarkGrey,
                background = R.drawable.section_background_green,
                title = this.getText(R.string.loaded).toString(),
                items = this.tour.loadedParcels.map { it.value }
        )
    }

    val damagedSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_damaged,
                color = R.color.colorDarkGrey,
                background = R.drawable.section_background_accent,
                title = this.getString(R.string.event_reason_damaged),
                items = this.tour.damagedParcels.map { it.value }
        )
    }

    val pendingSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_stop_list,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                expandOnSelection = true,
                title = getString(R.string.pending),
                items = this.tour.pendingParcels.map { it.value }
        )
    }

    //endregion

    fun SectionViewModel<ParcelEntity>.toFlexibleItem()
            : VmHeaderItem<SectionViewModel<ParcelEntity>, Any> {

        return VmHeaderItem<SectionViewModel<ParcelEntity>, Any>(
                view = R.layout.item_section_header,
                variable = BR.header,
                viewModel = this
        ).also {
            it.isSelectable = true
        }
    }

    fun ParcelEntity.toFlexibleItem()
            : SimpleVmItem<ParcelViewModel> {

        return SimpleVmItem(
                view = R.layout.item_parcel_card,
                variable = BR.parcel,
                viewModel = ParcelViewModel(this)
        )
    }

    private val parcelListAdapterInstance = LazyInstance<SectionsAdapter>({
        val adapter = SectionsAdapter()

        adapter.addSection(
                sectionVmItemProvider = { this.loadedSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter.addSection(
                sectionVmItemProvider = { this.pendingSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = this.getText(R.string.vehicle_loading).toString()
        this.headerImage = Headers.parcels
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
        binding.stats = StatsViewModel(this.vehicleLoading)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.parcelListAdapterInstance.reset()

        this.uxRecyclerView.adapter = parcelListAdapter
        this.uxRecyclerView.layoutManager = LinearLayoutManager(context)

        this.menu = this.inflateMenu(R.menu.menu_vehicleloading).also {
            if (this.debugSettings.syntheticAidcEnabled) {
                it.add(0, R.id.action_vehicle_loading_dev_mark_all_loaded, 0, "Mark all as loaded").also {
                    it.setIcon(R.drawable.ic_dev)
                }
            }
        }

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_vehicle_loading_finished,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_vehicle_loading_load,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_truck_loading,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_vehicle_loading_damaged,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_damaged,
                        visible = true
                )
        )
    }

    override fun onDestroyView() {
        log.trace("DESTROY VIEW")
        this.parcelListAdapter.dispose()

        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        this.currentDamagedParcel = null

        aidcReader.decoders.set(
                Interleaved25Decoder(true, 6, 12),
                DatamatrixDecoder(true),
                Ean8Decoder(true),
                Ean13Decoder(true),
                Code128Decoder(true),
                QrCodeDecoder(true)
        )

        aidcReader.readEvent
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.onAidcRead(it)
                }

        //region Activity events
        this.activity.menuItemEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it.itemId) {
                        R.id.action_reset -> {
                            tour.reset().subscribe()
                        }

                        R.id.action_vehicle_loading_dev_mark_all_loaded -> {
                            this@VehicleLoadingScreen.vehicleLoading.load(
                                    db.store.select(ParcelEntity::class)
                                            .where(ParcelEntity.STATE.eq(Parcel.State.PENDING))
                                            .get().toList()
                            )
                                    .subscribeBy(
                                            onError = { e ->
                                                log.error(e.message, e)
                                            }
                                    )
                        }
                    }
                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_vehicle_loading_damaged -> {
                            this.parcelListAdapter.addSection(
                                    sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                    vmItemProvider = { it.toFlexibleItem() }
                            )

                            this.parcelListAdapter.selectedSection = this.damagedSection
                        }

                        R.id.action_vehicle_loading_load -> {
                            this.parcelListAdapter.selectedSection = this.loadedSection
                        }

                        R.id.action_vehicle_loading_finished -> {
                            this.listener?.onVehicleLoadingFinalized()
                        }

                        else -> log.warn("Unhandled ActionEvent [$it]")
                    }
                }
        //endregion

        this.parcelListAdapter.selectedSection = this.loadedSection
        this.parcelListAdapter.selectedSectionProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    val section = it.value

                    log.user { "Selects section [${section?.title}]" }

                    this.accentColor = when (section) {
                        loadedSection -> R.color.colorGreen
                        damagedSection -> R.color.colorAccent
                        else -> R.color.colorGrey
                    }

                    when (section) {
                        this.loadedSection -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_vehicle_loading_load }
                                        .visible = false

                                first { it.id == R.id.action_vehicle_loading_damaged }
                                        .visible = true
                            }
                        }
                        else -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_vehicle_loading_load }
                                        .visible = true

                                first { it.id == R.id.action_vehicle_loading_damaged }
                                        .visible = false
                            }
                        }
                    }
                }

        this.parcelListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                activity.expandToolbar(parcelListAdapter.expandedPositions.count() == 0)
            }
        })

        //region Finish button visibility

        Observable.combineLatest(
                // Show finish button when parcels are loaded
                this.tour.loadedParcels
                        .map { it.value.count() > 0 },
                // and process is not busy
                this.isBusyProperty.map { it.value == false },

                BiFunction { t1: Boolean, t2: Boolean -> t1 && t2 }
        )
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe { visible ->
                    this.actionItems = this.actionItems.apply {
                        first { it.id == R.id.action_vehicle_loading_finished }
                                .visible = visible
                    }
                }
        //endregion

        // Damaged parcels
        Observable.combineLatest(
                // Fire when damaged parcels change
                this.tour.damagedParcels
                        .map { it.value }
                        .distinctUntilChanged(),
                // Also fire when selected section changes */
                this.parcelListAdapter.selectedSectionProperty.filter {
                    it.value != this.damagedSection
                },

                BiFunction { _: Any, _: Any ->
                    this.tour.damagedParcels.map { it.value }.blockingFirst()
                }
        )
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    if (it.count() > 0) {
                        this.parcelListAdapter.addSection(
                                sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                vmItemProvider = { it.toFlexibleItem() }
                        )
                    } else {
                        this.parcelListAdapter.removeSection(this.damagedSection)

                        if (this.parcelListAdapter.selectedSection == null) {
                            this.parcelListAdapter.selectedSection = this.loadedSection
                        }
                    }
                }
        //endregion

        //region Synthetic inputs
        if (this.debugSettings.syntheticAidcEnabled) {
            // Synthetic inputs for parcels translated live from entity store
            val ovParcels = this.parcelRepository.entitiesProperty
                    .map {
                        SYNTHETIC_PARCELS.plus(
                                it.value.map { it.number }
                        ).map {
                            DekuUnitNumber.parse(it).value
                        }
                    }
                    .map { unitNumbers ->
                        SyntheticInput(
                                name = "Parcels",
                                entries = unitNumbers.map {
                                    SyntheticInput.Entry(
                                            symbologyType = SymbologyType.Interleaved25,
                                            data = it.label
                                    )
                                }
                        )
                    }

            // Synthetic inputs for delivery lists, retrieved via online service
            val ovTours = Observable.fromCallable {
                val tourService = Kodein.global.instance<TourServiceV1>()
                tourService.get(overview = true)
            }
                    .toHotIoObservable()
                    .composeAsRest(this.activity)
                    .map { tours ->
                        SyntheticInput(
                                name = "Tours",
                                multipleChoice = true,
                                entries = SYNTHETIC_TOURS.map {
                                    SyntheticInput.Entry(
                                            symbologyType = SymbologyType.QrCode,
                                            data = TourIdentification(
                                                    id = 0,
                                                    uid = UUID.fromString(it)).label,
                                            name = it
                                    )
                                }.plus(
                                        tours
                                                .sortedWith(
                                                        compareByDescending<TourServiceV1.Tour> { it.date?.date }
                                                                .thenByDescending { it.id }
                                                )
                                                .map { tour ->
                                                    val id = tour.id!!.toInt()
                                                    val uid = UUID.fromString(tour.uid)

                                                    SyntheticInput.Entry(
                                                            symbologyType = SymbologyType.QrCode,
                                                            data = TourIdentification(
                                                                    id = id,
                                                                    uid = uid).label,
                                                            name = "${tour.date}: t${id} ${tour.optimized?.let { "optimized" }
                                                                    ?: ""}"
                                                    )
                                                }
                                )
                        )
                    }
                    .doOnError {
                        log.error(it.message, it)
                    }
                    .doOnNext {
                        log.trace { it }
                    }
                    .onErrorResumeNext(Observable.empty())

            // Final synthetic inputs observable
            Observable.combineLatest(
                    ovParcels,
                    ovTours,

                    BiFunction { a: SyntheticInput, b: SyntheticInput ->
                        listOf<SyntheticInput>(a, b)
                    }
            )
                    .bindUntilEvent(this, FragmentEvent.PAUSE)
                    .subscribe {
                        this.syntheticInputs = it
                    }

        }
        //endregion
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        val result = Observable.concat(
                Observable.fromCallable {
                    DekuUnitNumber
                            .parseLabel(event.data)
                            .assertAny(
                                    UnitNumber.Type.Parcel,
                                    UnitNumber.Type.Bag)
                },
                Observable.fromCallable {
                    GlsUnitNumber
                            .parseLabel(event.data)
                },
                Observable.fromCallable {
                    DekuDeliveryListNumber.parseLabel(event.data)
                },
                Observable.fromCallable {
                    TourIdentification.parseLabel(event.data)
                }
        )
                .takeUntil { !it.hasError }
                .last(Result(IllegalArgumentException("Invalid barcode")))
                .blockingGet()

        when {
            result.hasError -> {
                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_barcode)
                        .build().show()
            }
            else -> {
                val resultValue = result.value

                when (resultValue) {
                    is DekuUnitNumber -> {
                        this.onInput(resultValue)
                    }
                    is GlsUnitNumber -> {
                        this.onInput(resultValue.toUnitNumber())
                    }
                    is DekuDeliveryListNumber -> {
                        this.onInput(deliveryListNumber = resultValue)
                    }
                    is TourIdentification -> {
                        this.onInput(tourIdent = resultValue)
                    }
                }
            }
        }
    }

    /**
     * On delivery list input
     */
    fun onInput(
            deliveryListNumber: DekuDeliveryListNumber? = null,
            tourIdent: TourIdentification? = null) {
        var loaded = false
        this.isBusy = true

        val loadTour = if (deliveryListNumber != null) {
            tour.load(deliveryListNumber)
        } else if (tourIdent != null) {
            tour.load(tourIdent)
        } else
            throw IllegalArgumentException()

        loadTour
                .bindToLifecycle(this)
                .composeAsRest(this.activity, R.string.error_invalid_tour)
                .subscribeBy(
                        onNext = {
                            loaded = true
                        },
                        onComplete = {
                            this.isBusy = false

                            // Can't rely on complete alone due to rxlifecycle
                            if (loaded) {
                                feedback.acknowledge()
                            }
                        },
                        onError = {
                            this.isBusy = false

                            log.error(it.message, it)
                            feedback.error()
                        }
                )
    }

    /**
     * On unit number input
     */
    fun onInput(unitNumber: UnitNumber) {
        log.trace("Unit number input ${unitNumber.value}")

        val parcel = this.parcelRepository.entities.firstOrNull { it.number == unitNumber.value }

        when {
            parcel != null -> {
                // Parcel fonud -> process
                this.onParcel(parcel)
            }
            else -> {
                // No corresponding order (yet)
                this.tour.retrieveOrder(unitNumber)
                        .bindToLifecycle(this)
                        .composeAsRest(this.activity, R.string.error_no_corresponding_order)
                        .subscribeBy(
                                onNext = { order ->
                                    feedback.acknowledge()

                                    fun mergeOrder() {
                                        this.isBusy = true

                                        this.tour
                                                .mergeOrder(order)
                                                .toSingleDefault(Unit).toObservable()
                                                .concatMap {
                                                    this.parcelRepository
                                                            .findByNumber(unitNumber.value)
                                                            .toObservable()
                                                }
                                                .concatMap {
                                                    this.vehicleLoading.load(it)
                                                            .toObservable<Unit>()
                                                }
                                                .subscribeOn(db.scheduler)
                                                .observeOnMainThreadWithLifecycle(this)
                                                .subscribe({
                                                    this.isBusy = false
                                                }, {
                                                    this.isBusy = false
                                                    log.error("Merging order failed. ${it.message}", it)
                                                    feedback.error()
                                                })
                                    }

                                    if (this.tour.deliveryListIds.get().isEmpty() || acceptDetachedOrdersWithoutConfirmation) {
                                        mergeOrder()
                                    } else {
                                        val dialog = MaterialDialog.Builder(this.activity)
                                                .title(R.string.order_not_on_delivery_list)
                                                .content(R.string.order_not_on_delivery_list_confirmation)
                                                .positiveText(android.R.string.yes)
                                                .negativeText(android.R.string.no)
                                                .onPositive { _, _ ->
                                                    detachedOrderCounter++
                                                    mergeOrder()
                                                }

                                        if (detachedOrderCounter >= 3) {
                                            dialog.checkBoxPrompt(
                                                    getString(R.string.accept_always),
                                                    false,
                                                    { _, isChecked ->
                                                        this.acceptDetachedOrdersWithoutConfirmation = isChecked
                                                    }
                                            )
                                        }

                                        dialog.build().show()
                                    }

                                },
                                onError = {
                                    feedback.error()
                                }
                        )

            }
        }
    }

    /**
     * On valid parcel entry
     */
    fun onParcel(parcel: ParcelEntity) {
        when (parcelListAdapter.selectedSection) {
            damagedSection -> {
                if (parcel.isDamaged) {
                    this.feedback.warning()
                    this.aidcReader.enabled = false

                    MaterialDialog.Builder(this.context)
                            .title(R.string.question_remove_damaged_status_title)
                            .content(R.string.question_remove_damaged_status)
                            .negativeText(getString(android.R.string.no))
                            .positiveText(getString(android.R.string.yes))
                            .dismissListener {
                                this.aidcReader.enabled = true
                            }
                            .onPositive { _, _ ->
                                // Removed damaged parcel status
                                parcel.isDamaged = false

                                this.parcelRepository.update(parcel)
                                        .subscribeOn(db.scheduler)
                                        .subscribe()
                            }
                            .show()
                } else {
                    this.currentDamagedParcel = parcel

                    /** Show camera screen */
                    this.activity.showScreen(DamagedParcelCameraScreen().also {
                        it.setTargetFragment(this, 0)
                        it.parameters = DamagedParcelCameraScreen.Parameters(
                                parcelId = parcel.id
                        )
                    })
                }
            }
            else -> {
                this.vehicleLoading.load(parcel)
                        .subscribeBy(
                                onError = {
                                    log.error(it.message, it)
                                    feedback.error()
                                }
                        )

                this.parcelListAdapter.selectedSection = loadedSection
            }
        }
    }

    override fun onCameraScreenImageSubmitted(sender: Any, jpeg: ByteArray) {
        this.currentDamagedParcel?.also { parcel ->
            parcelRepository.markDamaged(
                    parcel = parcel,
                    jpegPictureData = jpeg
            )
                    .subscribeOn(db.scheduler)
                    .subscribe()
        }
    }
}

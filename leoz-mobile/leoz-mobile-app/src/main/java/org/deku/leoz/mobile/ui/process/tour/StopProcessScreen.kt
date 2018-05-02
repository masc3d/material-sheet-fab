package org.deku.leoz.mobile.ui.process.tour

import android.annotation.SuppressLint
import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
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
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_tour_stop_process.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.databinding.ItemStopMergeDialogBinding
import org.deku.leoz.mobile.databinding.ScreenTourStopProcessBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Feedback
import org.deku.leoz.mobile.log.user
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.process.TourStop
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.model.toMaterialSimpleListItem
import org.deku.leoz.mobile.settings.DebugSettings
import org.deku.leoz.mobile.ui.core.BaseCameraScreen
import org.deku.leoz.mobile.ui.core.Headers
import org.deku.leoz.mobile.ui.core.ScreenFragment
import org.deku.leoz.mobile.ui.core.extension.inflateMenu
import org.deku.leoz.mobile.ui.core.view.ActionItem
import org.deku.leoz.mobile.ui.core.with
import org.deku.leoz.mobile.ui.process.TourScreen
import org.deku.leoz.mobile.ui.process.tour.stop.*
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.*
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.inflateMenu
import sx.android.rx.observeOnMainThread
import sx.android.rx.observeOnMainThreadUntilEvent
import sx.android.rx.observeOnMainThreadWithLifecycle
import sx.android.ui.flexibleadapter.SimpleVmItem
import sx.android.ui.flexibleadapter.VmHeaderItem
import sx.android.ui.materialdialogs.addAll
import sx.format.format
import java.util.concurrent.TimeUnit

/**
 * Delivery stop process screen
 */
class StopProcessScreen :
        ScreenFragment<StopProcessScreen.Parameters>(),
        BaseCameraScreen.Listener,
        SignatureScreen.Listener,
        RecipientScreen.Listener,
        CashScreen.Listener {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @org.parceler.Parcel(org.parceler.Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int
    )

    /**
     * Created by masc on 10.07.17.
     */
    inner class StatsViewModel
        : BaseObservable() {

        val orderCounter = CounterViewModel(
                iconRes = R.drawable.ic_order,
                amount = tourStop.deliveredOrdersAmount.cast(Number::class.java),
                totalAmount = tourStop.orderTotalAmount.cast(Number::class.java)
        )

        val parcelCounter = CounterViewModel(
                iconRes = R.drawable.ic_package_variant_closed,
                amount = tourStop.deliveredParcelAmount.cast(Number::class.java),
                totalAmount = tourStop.parcelTotalAmount.cast(Number::class.java)
        )

        val weightCounter = CounterViewModel(
                iconRes = R.drawable.ic_weight_scale,
                amount = tourStop.deliveredParcelsWeight.cast(Number::class.java),
                totalAmount = tourStop.totalWeight.cast(Number::class.java),
                format = { "${(it as Double).format(2)}kg" }

        )
    }

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val feedback: Feedback by Kodein.global.lazy.instance()
    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    //region Model classes
    private val db: Database by Kodein.global.lazy.instance()

    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val tour: Tour by Kodein.global.lazy.instance()

    private val timer: sx.android.ui.Timer by Kodein.global.lazy.instance()

    private val timerEvent = timer.tickEvent
            .bindToLifecycle(this)

    private val stop: StopEntity by lazy {
        this.stopRepository.entities.first { it.id == this.parameters.stopId }
    }

    private val tourStop: TourStop by lazy {
        this.tour.activeStop ?: throw IllegalArgumentException("Active stop not set")
    }

    /** The current/most recently selected damaged parcel */
    private var currentDamagedParcel: ParcelEntity? = null
    //endregion

    /** Active merge dialog */
    private var mergeDialog: MaterialDialog? = null
        set(value) {
            field?.dismiss()
            field = value
        }

    //region Sections
    private val deliveredSection: SectionViewModel<ParcelEntity> by lazy {
        SectionViewModel(
                icon = R.drawable.ic_delivery,
                color = R.color.colorDarkGrey,
                background = R.drawable.section_background_green,
                title = getString(R.string.delivered),
                items = this.tourStop.deliveredParcels
        )
    }

    private val pendingSection by lazy {
        SectionViewModel(
                icon = R.drawable.ic_stop_list,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                showIfEmpty = false,
                title = getString(R.string.pending),
                items = this.tourStop.pendingParcels
        )
    }

    private val orderSection by lazy {
        SectionViewModel(
                icon = R.drawable.ic_order,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                showIfEmpty = true,
                expandOnSelection = true,
                title = this.getString(R.string.orders),
                items = this.tourStop.orders
        )
    }

    private val missingSection by lazy {
        SectionViewModel(
                icon = R.drawable.ic_missing,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                showIfEmpty = false,
                title = getString(R.string.missing),
                items = this.tourStop.missingParcels
        )
    }

    private val damagedSection by lazy {
        SectionViewModel(
                icon = R.drawable.ic_damaged,
                color = R.color.colorDarkGrey,
                background = R.drawable.section_background_accent,
                showIfEmpty = true,
                title = getString(R.string.event_reason_damaged),
                items = this.tourStop.damagedParcels
        )
    }

    private val excludedSection by lazy {
        SectionViewModel(
                icon = R.drawable.ic_split,
                color = R.color.colorDarkGrey,
                background = R.drawable.section_background_accent,
                showIfEmpty = true,
                title = getString(R.string.excluded),
                items = this.tourStop.excludedParcels
        )
    }

    /**
     * Extension for creating sections from event/reason enum
     */
    private fun EventNotDeliveredReason.toSection(): SectionViewModel<ParcelEntity> {
        return SectionViewModel(
                icon = this.mobile.icon,
                color = R.color.colorDarkGrey,
                background = R.drawable.section_background_accent,
                showIfEmpty = false,
                title = this.mobile.textOrName(context),
                items = tourStop.parcelsByEvent
                        .withDefault { Observable.empty() }
                        .getValue(this)
        )
    }

    /**
     * Section by event/reason
     */
    private val sectionByEvent by lazy {
        mapOf(*this.tourStop.allowedEvents.map {
            Pair(it, it.toSection())
        }.toTypedArray())
    }
    //endregion

    private fun <T> SectionViewModel<T>.toFlexibleItem()
            : VmHeaderItem<SectionViewModel<T>, Any> {

        return VmHeaderItem<SectionViewModel<T>, Any>(
                view = R.layout.item_section_header,
                variable = BR.header,
                viewModel = this
        ).also {
            it.isSelectable = true
        }
    }

    private fun ParcelEntity.toFlexibleItem()
            : SimpleVmItem<ParcelViewModel> {

        return SimpleVmItem(
                view = R.layout.item_parcel_card,
                variable = BR.parcel,
                viewModel = ParcelViewModel(this, showOrderTask = false)
        )
    }

    private fun OrderEntity.toFlexibleItem()
            : SimpleVmItem<OrderTaskViewModel> {

        return SimpleVmItem(
                view = R.layout.item_ordertask,
                variable = BR.orderTask,
                viewModel = OrderTaskViewModel(this.pickupTask)
        )
    }

    private val processAdapterInstance = LazyInstance({
        val adapter = SectionsAdapter()

        adapter.addSection(
                sectionVmItemProvider = { this.deliveredSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter.addSection(
                sectionVmItemProvider = { this.pendingSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter.addSection(
                sectionVmItemProvider = { this.missingSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        this.sectionByEvent.forEach {
            adapter.addSection(
                    sectionVmItemProvider = { it.value.toFlexibleItem() },
                    vmItemProvider = { it.toFlexibleItem() }
            )
        }

        adapter.addSection(
                sectionVmItemProvider = { this.orderSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter
    })
    private val processAdapter get() = processAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_stop_process)
        this.aidcEnabled = true
        this.headerImage = Headers.parcels
        this.toolbarCollapsed = true
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed

        // Set models's active stop when screen is created
        this.tour.activeStop = TourStop(stop)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: ScreenTourStopProcessBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.screen_tour_stop_process,
                container, false)

        // Setup bindings
        binding.stats = StatsViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set screen menu
        if (debugSettings.enabled)
            this.menu = this.inflateMenu(R.menu.menu_delivery_stop_process).also {
                it.add(0, R.id.action_delivery_process_dev_show_cash_screen, 0, "Show Cash screen").also {
                    it.setIcon(R.drawable.ic_service_cash)
                }
            }

        this.processAdapterInstance.reset()

        this.uxRecyclerView.adapter = processAdapter
        this.uxRecyclerView.layoutManager = LinearLayoutManager(context)

        //region Action items

        // Close stop menu
        val closeStopMenu = this.activity.inflateMenu(R.menu.menu_delivery_stop_process_close)

        closeStopMenu.findItem(R.id.action_deliver_postbox).isVisible =
                this.tourStop.services.contains(ParcelService.POSTBOX_DELIVERY) &&
                !this.tourStop.services.contains(ParcelService.NO_ALTERNATIVE_DELIVERY)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_delivery_close_stop,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_delivery_close_stop_extra,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_done_black,
                        menu = closeStopMenu,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_delivery_select_delivered,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_delivery
                ),

                ActionItem(
                        id = R.id.action_delivery_select_event,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_exclamation
                ),
                ActionItem(
                        id = R.id.action_report_delay,
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_appointment_at_risk,
                        visible = !this.tourStop.canClose
                )
        )
        //endregion

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)!!
        binding.stop = StopViewModel(
                isStateVisible = true,
                stop = this.stop,
                timerEvent = this.timerEvent)
    }

    override fun onDestroyView() {
        this.processAdapter.dispose()

        super.onDestroyView()
    }

    @SuppressLint("CheckResult")
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
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.onAidcRead(it)
                }

        this.activity.menuItemEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it.itemId) {
                        R.id.action_reset -> {
                            log.info("Resetting all delivery states / data")

                            this.tourStop
                                    .reset()
                                    .subscribe()

                            this.processAdapter.selectedSection = this.deliveredSection
                        }

                        R.id.action_delivery_process_dev_show_cash_screen -> {
                            this.activity.showScreen(CashScreen())
                        }
                    }
                }

        this.activity.actionEvent
                // As stop process is currently partially stateless (eg. close stop can be triggered multiple times)
                // prevent accidental duplicate events leading to confusing dialog order.
                // This is rather a workaround, stop process should track state at all times.
                .throttleFirst(250, TimeUnit.MILLISECONDS)
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_delivery_select_delivered -> {
                            this.processAdapter.selectedSection = this.deliveredSection
                        }

                        R.id.action_delivery_select_event -> {
                            MaterialDialog.Builder(this.context)
                                    .title(context.getString(org.deku.leoz.mobile.R.string.dialog_title_event_selection))
                                    .adapter(MaterialSimpleListAdapter({ d, _, item ->
                                        when (item.tag) {
                                            is EventNotDeliveredReason -> {
                                                this.onEventSelected(item.tag as EventNotDeliveredReason)
                                            }
                                            else -> {
                                                if (item.id == R.string.exclude.toLong()) {
                                                    this.processAdapter.addSection(
                                                            sectionVmItemProvider = { this.excludedSection.toFlexibleItem() },
                                                            vmItemProvider = { it.toFlexibleItem() }
                                                    )

                                                    this.processAdapter.selectedSection = this.excludedSection
                                                }
                                            }
                                        }
                                        d.dismiss()
                                    }).also {
                                        if (this.tourStop.orders.blockingFirst().count() > 1) {
                                            it.add(MaterialSimpleListItem.Builder(context)
                                                    .backgroundColor(Color.WHITE)
                                                    .icon(R.drawable.ic_split)
                                                    .content(R.string.exclude)
                                                    .id(R.string.exclude.toLong())
                                                    .build()
                                            )
                                        }

                                        it.addAll(
                                                this.tourStop.allowedParcelEvents
                                                        .plus(this.tourStop.allowedStopEvents)
                                                        .reversed()
                                                        .map { it.toMaterialSimpleListItem(context) }

                                        )
                                    }, null)
                                    .build()
                                    .show()
                        }

                        R.id.action_deliver_neighbour -> {
                            this.closeStop(variant = org.deku.leoz.model.EventDeliveredReason.NEIGHBOR)
                        }

                        R.id.action_deliver_postbox -> {
                            this.closeStop(variant = org.deku.leoz.model.EventDeliveredReason.POSTBOX)
                        }

                        R.id.action_delivery_close_stop -> {
                            this.closeStop(org.deku.leoz.model.EventDeliveredReason.NORMAL)
                        }

                    }
                }

        //region Dynamic sections

        // Damaged parcels
        Observable.combineLatest(
                this.tourStop.damagedParcels,
                // Also fire when selected section changes */
                this.processAdapter.selectedSectionProperty.filter {
                    it.value != this.damagedSection
                },

                BiFunction { _: Any, _: Any ->
                    this.tourStop.damagedParcels.blockingFirst()
                }
        )
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    if (it.count() > 0) {
                        this.processAdapter.addSection(
                                sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                vmItemProvider = { it.toFlexibleItem() }
                        )
                    } else {
                        this.processAdapter.removeSection(this.damagedSection)

                        if (this.processAdapter.selectedSection == null) {
                            this.processAdapter.selectedSection = this.deliveredSection
                        }
                    }
                }
        //endregion

        // Excluded orders
        Observable.combineLatest(
                this.tourStop.excludedParcels,

                this.processAdapter.selectedSectionProperty.filter {
                    it.value != this.excludedSection
                },

                BiFunction { _: Any, _: Any ->
                    this.tourStop.excludedParcels.blockingFirst()
                }
        )
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    if (it.count() > 0) {
                        this.processAdapter.addSection(
                                sectionVmItemProvider = { this.excludedSection.toFlexibleItem() },
                                vmItemProvider = { it.toFlexibleItem() }
                        )
                    } else {
                        this.processAdapter.removeSection(this.excludedSection)

                        if (this.processAdapter.selectedSection == null) {
                            this.processAdapter.selectedSection = this.deliveredSection
                        }
                    }
                }

        //region Initially selected section
        val sectionWithMaxEvents = this.sectionByEvent.map {
            Pair(it.value, it.value.items.blockingFirst())
        }
                .filter { it.second.count() > 0 }
                .maxBy { it.second.count() }
                ?.first

        this.processAdapter.selectedSection = sectionWithMaxEvents ?: deliveredSection
        // endregion

        this.processAdapter.selectedSectionProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    val section = it.value

                    log.user { "Selects section [${section?.title}]" }

                    this.accentColor = when {
                        section == this.deliveredSection -> R.color.colorGreen
                        sectionByEvent.values.contains(section) || section == damagedSection -> R.color.colorAccent
                        else -> R.color.colorGrey
                    }

                    section?.color ?: R.color.colorGrey

                    when (section) {
                        this.deliveredSection -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_delivery_select_delivered }
                                        .visible = false

                                first { it.id == R.id.action_delivery_select_event }
                                        .visible = true
                            }
                        }
                        else -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_delivery_select_delivered }
                                        .visible = true

                                first { it.id == R.id.action_delivery_select_event }
                                        .visible = false
                            }
                        }
                    }
                }

        this.processAdapter.itemClickEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe { item ->

                    ((item as? SimpleVmItem<*>)
                            ?.viewModel as? OrderTaskViewModel)
                            ?.also { orderTaskViewModel ->
                                MaterialDialog.Builder(context)
                                        .title(R.string.dialog_title_event_selection)
                                        .adapter(MaterialSimpleListAdapter({ d, _, item ->
                                            item.tag.also {
                                                when (it) {
                                                    is EventNotDeliveredReason -> {
                                                        // Stop level event
                                                        this.tourStop.assignOrderLevelEvent(
                                                                order = orderTaskViewModel.orderTask.order,
                                                                reason = it
                                                        )
                                                                .observeOnMainThread()
                                                                .subscribeBy(
                                                                        onComplete = {
                                                                            this.processAdapter.selectedSection = sectionByEvent.getValue(it)
                                                                        })
                                                    }
                                                    else -> {
                                                        if (item.id == R.string.exclude.toLong()) {
                                                            this.tourStop.excludedOrders = this.tourStop.excludedOrders.plus(
                                                                    orderTaskViewModel.orderTask.order as OrderEntity
                                                            )

                                                            this.processAdapter.addSection(
                                                                    sectionVmItemProvider = { this.excludedSection.toFlexibleItem() },
                                                                    vmItemProvider = { it.toFlexibleItem() }
                                                            )

                                                            this.processAdapter.selectedSection = this.excludedSection
                                                        }
                                                    }
                                                }
                                                d.dismiss()
                                            }
                                        }).also {
                                            if (this.tourStop.orders.blockingFirst().count() > 1) {
                                                it.add(MaterialSimpleListItem.Builder(context)
                                                        .backgroundColor(Color.WHITE)
                                                        .icon(R.drawable.ic_split)
                                                        .content(R.string.exclude)
                                                        .id(R.string.exclude.toLong())
                                                        .build()
                                                )
                                            }

                                            it.addAll(
                                                    this.tourStop.allowedOrderEvents
                                                            .map { it.toMaterialSimpleListItem(context) }
                                            )
                                        }, null)
                                        .build().show()
                            }
                }

        // Synthetic inputs
        Observable.combineLatest(
                this.tourStop.parcels.switchMap {
                    Observable.just(
                            SyntheticInput(
                                    name = "Stop Parcels",
                                    entries = it.map
                                    {
                                        val unitNumber = DekuUnitNumber.parse(it.number).value
                                        SyntheticInput.Entry(
                                                symbologyType = SymbologyType.Interleaved25,
                                                data = unitNumber.label
                                        )
                                    }
                            ))
                },
                this.tour.loadedParcels.map { it.value }.switchMap {
                    Observable.just(
                            SyntheticInput(
                                    name = "Parcels",
                                    entries = it.map
                                    {
                                        val unitNumber = DekuUnitNumber.parse(it.number).value
                                        SyntheticInput.Entry(
                                                symbologyType = SymbologyType.Interleaved25,
                                                data = unitNumber.label
                                        )
                                    }
                            )
                    )
                },
                BiFunction { t1: SyntheticInput, t2: SyntheticInput ->
                    listOf(t1, t2)
                }
        )
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.syntheticInputs = it
                }

        // Observe changes which affect action items
        Observable.merge(
                this.tourStop.pendingParcels,
                this.tourStop.stop
        )
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.actionItems = this.actionItems.apply {
                        first { it.id == R.id.action_delivery_close_stop }
                                .also {
                                    it.visible = tourStop.canClose

                                    if (tourStop.canCloseWithEvent) {
                                        it.colorRes = R.color.colorAccent
                                        it.iconTintRes = android.R.color.black
                                    } else {
                                        it.colorRes = R.color.colorPrimary
                                        it.iconTintRes = android.R.color.white
                                    }
                                }

                        first { it.id == R.id.action_delivery_close_stop_extra }
                                .also {
                                    it.menu?.findItem(R.id.action_deliver_neighbour)
                                            ?.isVisible = tourStop.canCloseWithDeliveryToNeighbor

                                    it.menu?.findItem(R.id.action_deliver_postbox)
                                            ?.isVisible = tourStop.canCloseWithDeliveryToPostbox

                                    it.visible = tourStop.canClose &&
                                            it.menu?.hasVisibleItems() == true
                                }

                        first { it.id == R.id.action_report_delay }
                                .also {
                                    it.visible = !tourStop.canClose
                                }
                    }
                }
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        val result: Result<UnitNumber> = UnitNumber.parseLabel(event.data)

        when {
            result.hasError -> {
                feedback.warning()

                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_barcode)
                        .build().show()
            }
            else -> {
                this.onInput(unitNumber = result.value)
            }
        }
    }

    private fun onInput(unitNumber: UnitNumber) {
        /** Error emission helper */
        fun emitError() {
            feedback.error()

            this.activity.snackbarBuilder
                    .message(R.string.error_invalid_parcel)
                    .build().show()
        }

        // Regular stop parcels
        this.tourStop
                .parcels
                .subscribeOn(db.scheduler)
                .blockingFirst()
                .firstOrNull { it.number == unitNumber.value }
                ?.also {
                    this.onParcel(it)
                    return
                }

        // Other stop parcels (merge support)
        val parcel = this.parcelRepository
                .findByNumber(unitNumber.value)
                .blockingGet()

        if (parcel == null) {
            emitError()
            return
        }

        val sourceTask = parcel.order.deliveryTask
        val sourceStop = sourceTask.stop

        if (sourceStop == null) {
            log.error("No stop for task [${sourceTask}]")
            emitError()
            return
        }

        // Stops may only be merged under specific conditions (eg. zipcode matches)
        val isMergeAllowed = sourceStop.address.isCompatibleStopAddressForMergeWith(this.tourStop.entity.address)

        if (!isMergeAllowed) {
            log.warn("Merge is not allowed")
            emitError()
            return
        }

        // Parcel does not belong to this delivery stop, ask for stop merge
        feedback.warning()

        val runnable: Runnable?
        var reverseRunnable: Runnable? = null
        val animationHandler = Handler()

        this.mergeDialog = MaterialDialog.Builder(context)
                .title(R.string.title_stop_merge)
                .iconRes(R.drawable.ic_merge)
                .cancelable(true)
                .customView(R.layout.dialog_tour_stop_merge, true)
                .positiveText(R.string.proceed)
                .onPositive { _, _ ->
                    log.user { "Merges stop [${sourceStop.address}] into [${tourStop.entity.address}]" }

                    db.store.withTransaction {
                        stopRepository.mergeInto(
                                source = sourceStop,
                                target = tourStop.entity
                        )
                                .blockingAwait()
                    }
                            .toCompletable()
                            .blockingAwait()
                }
                .negativeText(android.R.string.no)
                .build()

        val customView = this.mergeDialog?.customView!!
        val sourceContainer = customView.findViewById<LinearLayout>(R.id.uxSourceStopContainer)!!
        val targetContainer = customView.findViewById<LinearLayout>(R.id.uxtargetStopContainer)!!
        val sourceView = customView.findViewById<View>(R.id.uxSourceStop)!!
        val targetView = customView.findViewById<View>(R.id.uxTargetStop)!!

        runnable = Runnable {
            sourceContainer.animate()
                    .alpha(0f)
                    .translationY(100f)
                    .setDuration(1500)
                    .setStartDelay(2000)
                    .withEndAction(reverseRunnable)
                    .start()

            targetContainer.animate()
                    .translationY(-100f)
                    .setDuration(1500)
                    .setStartDelay(2000)
                    .withEndAction(reverseRunnable)
                    .start()
        }

        reverseRunnable = Runnable {
            sourceContainer.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setStartDelay(1000)
                    .withEndAction(runnable)
                    .start()

            targetContainer.animate()
                    .translationY(0f)
                    .setDuration(500)
                    .setStartDelay(1000)
                    .withEndAction(runnable)
                    .start()
        }

        val bindingSourceStop = DataBindingUtil.bind<ItemStopMergeDialogBinding>(sourceView)!!
        bindingSourceStop.stop = StopViewModel(
                stop = sourceStop,
                timerEvent = Observable.empty()
        )

        val bindingTargetStop = DataBindingUtil.bind<ItemStopMergeDialogBinding>(targetView)!!
        bindingTargetStop.stop = StopViewModel(
                stop = this.tourStop.entity,
                timerEvent = Observable.empty()
        )

        this.mergeDialog?.show()

        animationHandler.postDelayed(runnable, 0)
    }

    /**
     * On valid parcel entry
     */
    private fun onParcel(parcel: ParcelEntity) {
        when (processAdapter.selectedSection) {

            deliveredSection, pendingSection, orderSection -> {
                this.tourStop.deliverParcel(parcel)
                        .subscribe()

                if (this.processAdapter.selectedSection != deliveredSection)
                    this.processAdapter.selectedSection = deliveredSection
            }

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
                    this.activity.showScreen(DamagedParcelCameraScreen().with(
                            target = this,
                            params = DamagedParcelCameraScreen.Parameters(
                                    parcelId = parcel.id
                            )))
                }
            }

            excludedSection -> {
                if (!this.tourStop.excludedOrders.contains(parcel.order)) {
                    this.tourStop.excludedOrders = this.tourStop.excludedOrders.plus(
                            parcel.order as OrderEntity
                    )
                }
            }
        }
    }

    private fun finalizeStop() {
        this.tourStop.finalize()
                .subscribeOn(db.scheduler)
                .observeOnMainThreadWithLifecycle(this)
                .subscribeBy(
                        onComplete = {
                            this.tour.activeStop = null

                            val isLastPendingStop = this.tour.pendingStops.blockingFirst().value.count() == 0

                            this.activity.supportFragmentManager.popBackStack(
                                    TourScreen::class.java.canonicalName,
                                    if (isLastPendingStop) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0)
                        },
                        onError = {
                            log.error(it.message, it)
                        }
                )
    }

    private fun closeStop(variant: EventDeliveredReason) {
        log.user { "Closes stop [${variant}]" }

        this.tourStop.resetCloseStopState()
        this.tourStop.deliveredReason = variant

        when (variant) {
            EventDeliveredReason.NEIGHBOR -> {
                if (this.tourStop.cashAmountToCollect > 0) {
                    this.activity.showScreen(CashScreen().with(target = this))
                } else {
                    this.activity.showScreen(RecipientScreen().with(target = this))
                }
            }

            EventDeliveredReason.POSTBOX -> {
                this.activity.showScreen(PostboxCameraScreen().with(target = this))
            }

            EventDeliveredReason.NORMAL -> {
                when {
                    this.tourStop.isSignatureRequired -> {
                        when {
                            this.tourStop.cashAmountToCollect > 0 -> {
                                //Requires CashScreen to be shown
                                this.activity.showScreen(CashScreen().with(target = this))
                            }
                            else -> {
                                this.activity.showScreen(RecipientScreen().with(
                                        target = this
                                ))
                            }
                        }
                    }
                    else -> {
                        this.finalizeStop()
                    }
                }
            }
        }
    }

    private fun onEventSelected(event: EventNotDeliveredReason) {
        when {
            this.tourStop.allowedParcelEvents.contains(event) -> {
                // Parcel level event
                when (event) {
                    EventNotDeliveredReason.DAMAGED -> {
                        this.processAdapter.addSection(
                                sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                vmItemProvider = { it.toFlexibleItem() }
                        )

                        this.processAdapter.selectedSection = this.damagedSection
                    }

                    else -> {
                    }
                }
            }
            else -> {
                // Stop level event
                this.tourStop.assignStopLevelEvent(event)
                        .observeOnMainThread()
                        .subscribeBy(
                                onComplete = {
                                    this.processAdapter.selectedSection = sectionByEvent.getValue(event)
                                })
            }
        }
    }

    override fun onCameraScreenImageSubmitted(sender: Any, jpeg: ByteArray) {
        when (sender) {
            is DamagedParcelCameraScreen -> {
                this.currentDamagedParcel?.also { parcel ->
                    parcelRepository.markDamaged(
                            parcel = parcel,
                            jpegPictureData = jpeg
                    )
                            .subscribeOn(db.scheduler)
                            .subscribe()
                }
            }

            is PostboxCameraScreen -> {
                this.tourStop.deliverToPostbox(jpeg)
                this.finalizeStop()
            }
        }
    }

    override fun onSignatureSubmitted(signatureSvg: String) {
        this.tourStop.signatureSvg = signatureSvg
        this.finalizeStop()
    }

    override fun onSignatureImageSubmitted(signatureJpeg: ByteArray) {
        this.tourStop.deliverWithSignatureOnPaper(signatureJpeg)
        this.finalizeStop()
    }

    override fun onRecipientScreenComplete(recipientName: String) {
        this.tourStop.recipientName = recipientName
        this.tourStop.deliveredReason = EventDeliveredReason.NEIGHBOR

        this.activity.showScreen(
                SignatureScreen().with(target = this)
        )
    }

    override fun onCashScreenContinue() {
        when (this.tourStop.deliveredReason) {
            EventDeliveredReason.NORMAL, EventDeliveredReason.NEIGHBOR -> {
                this.activity.showScreen(RecipientScreen().with(
                        target = this
                ))
            }

            else -> {
            }
        }
    }
}

package org.deku.leoz.mobile.ui.screen

import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_delivery_process.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.databinding.ScreenDeliveryProcessBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.entity.OrderEntity
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.process.DeliveryStop
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.dialog.EventDialog
import org.deku.leoz.mobile.ui.extension.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.EventDeliveredReason
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.model.ParcelService
import org.deku.leoz.model.UnitNumber
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.databinding.toField
import sx.android.inflateMenu
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleSectionableVmItem
import sx.format.format

/**
 * A simple [Fragment] subclass.
 */
class DeliveryStopProcessScreen :
        ScreenFragment<DeliveryStopProcessScreen.Parameters>(),
        EventDialog.Listener,
        BaseCameraScreen.Listener,
        SignatureScreen.Listener,
        NeighbourDeliveryScreen.Listener,
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
                drawableRes = R.drawable.ic_order,
                amount = deliveryStop.deliveredOrdersAmount.map { it.toString() }.toField(),
                totalAmount = deliveryStop.orderTotalAmount.map { it.toString() }.toField()
        )

        val parcelCounter = CounterViewModel(
                drawableRes = R.drawable.ic_package_variant_closed,
                amount = deliveryStop.deliveredParcelAmount.map { it.toString() }.toField(),
                totalAmount = deliveryStop.parcelTotalAmount.map { it.toString() }.toField()
        )

        val weightCounter = CounterViewModel(
                drawableRes = R.drawable.ic_weight_scale,
                amount = deliveryStop.deliveredParcelsWeight.map { "${it.format(2)}kg" }.toField(),
                totalAmount = deliveryStop.totalWeight.map { "${it.format(2)}kg" }.toField()
        )
    }

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val tones: Tones by Kodein.global.lazy.instance()
    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    private val mqttEndPoints: MqttEndpoints by Kodein.global.lazy.instance()

    //region Model classes
    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val delivery: Delivery by Kodein.global.lazy.instance()

    private val stop: StopEntity by lazy {
        this.stopRepository.entities.first { it.id == this.parameters.stopId }
    }

    private val deliveryStop: DeliveryStop by lazy {
        this.delivery.activeStop ?: throw IllegalArgumentException("Active stop not set")
    }

    /** The current/most recently selected damaged parcel */
    private var currentDamagedParcel: ParcelEntity? = null

    /** Current close stop variant */
    private var currentCloseStopVariatn: EventDeliveredReason? = null
    //endregion

    //region Sections
    val deliveredSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_delivery,
                color = android.R.color.black,
                background = R.drawable.section_background_green,
                title = getString(R.string.delivered),
                items = this.deliveryStop.deliveredParcels
        )
    }

    val pendingSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_format_list_bulleted,
                background = R.drawable.section_background_grey,
                showIfEmpty = false,
                title = getString(R.string.pending),
                items = this.deliveryStop.pendingParcels
        )
    }

    val orderSection by lazy {
        SectionViewModel<OrderEntity>(
                icon = R.drawable.ic_order,
                background = R.drawable.section_background_grey,
                showIfEmpty = true,
                expandOnSelection = true,
                title = this.getString(R.string.orders),
                items = this.deliveryStop.orders
        )
    }

    val missingSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_missing,
                background = R.drawable.section_background_grey,
                showIfEmpty = false,
                title = getString(R.string.missing),
                items = this.deliveryStop.missingParcels
        )
    }

    val damagedSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_damaged,
                color = android.R.color.black,
                background = R.drawable.section_background_accent,
                showIfEmpty = true,
                title = getString(R.string.event_reason_damaged),
                items = this.deliveryStop.damagedParcels
        )
    }

    /**
     * Extension for creating sections from event/reason enum
     */
    fun EventNotDeliveredReason.toSection(): SectionViewModel<ParcelEntity> {
        return SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_event,
                color = android.R.color.black,
                background = R.drawable.section_background_accent,
                showIfEmpty = false,
                title = this.mobile.textOrName(context),
                items = deliveryStop.parcelsByEvent
                        .withDefault { Observable.empty() }
                        .getValue(this)
        )
    }

    /**
     * Section by event/reason
     */
    val sectionByEvent by lazy {
        mapOf(*this.deliveryStop.allowedEvents.map {
            Pair(it, it.toSection())
        }.toTypedArray())
    }
    //endregion

    fun <T> SectionViewModel<T>.toFlexibleItem()
            : FlexibleExpandableVmItem<SectionViewModel<T>, Any> {

        return FlexibleExpandableVmItem<SectionViewModel<T>, Any>(
                view = R.layout.item_section_header,
                variable = BR.header,
                viewModel = this
        ).also {
            it.isSelectable = true
        }
    }

    fun ParcelEntity.toFlexibleItem()
            : FlexibleSectionableVmItem<ParcelViewModel> {

        return FlexibleSectionableVmItem(
                view = R.layout.item_parcel_card,
                variable = BR.parcel,
                viewModel = ParcelViewModel(this, showOrderTask = false)
        )
    }

    fun OrderEntity.toFlexibleItem()
            : FlexibleSectionableVmItem<OrderTaskViewModel> {

        return FlexibleSectionableVmItem(
                view = R.layout.item_ordertask,
                variable = BR.orderTask,
                viewModel = OrderTaskViewModel(this.pickupTask)
        )
    }

    private val parcelListAdapterInstance = LazyInstance<SectionsAdapter>({
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

        adapter.addSection(
                sectionVmItemProvider = { this.orderSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        this.sectionByEvent.forEach {
            adapter.addSection(
                    sectionVmItemProvider = { it.value.toFlexibleItem() },
                    vmItemProvider = { it.toFlexibleItem() }
            )
        }

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_stop_process)
        this.aidcEnabled = true
        this.headerImage = R.drawable.img_parcels_1a
        this.toolbarCollapsed = true
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed

        // Set models's active stop when screen is created
        this.delivery.activeStop = DeliveryStop(stop)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: ScreenDeliveryProcessBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.screen_delivery_process,
                container, false)

        // Setup bindings
        binding.stats = StatsViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set screen menu
        if (debugSettings.enabled)
            this.menu = this.inflateMenu(R.menu.menu_delivery_stop_process).also {
                it.add(0, R.id.action_delivery_process_dev_show_cash_screen, 0, "Show Cash screen").also {
                    it.setIcon(R.drawable.ic_service_cash)
                }
            }

        this.uxRecyclerView.adapter = parcelListAdapter
        this.uxRecyclerView.layoutManager = LinearLayoutManager(context)

        //region Action items

        // Close stop menu
        val closeStopMenu = this.activity.inflateMenu(R.menu.menu_delivery_stop_process_close)

        closeStopMenu.findItem(R.id.action_deliver_postbox).isVisible =
                this.deliveryStop.services.contains(ParcelService.POSTBOX_DELIVERY) &&
                        !this.deliveryStop.services.contains(ParcelService.NO_ALTERNATIVE_DELIVERY)


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
                )
        )
        //endregion

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopViewModel(this.stop)
    }

    override fun onDestroyView() {
        this.parcelListAdapter.dispose()
        this.parcelListAdapterInstance.reset()

        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        log.trace("RESUME")

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
                            this.deliveryStop.reset()
                                    .subscribe()
                        }

                        R.id.action_delivery_process_dev_show_cash_screen -> {
                            this.activity.showScreen(CashScreen())
                        }
                    }
                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOnMainThread()
                .subscribe {
                    when (it) {
                        R.id.action_delivery_select_delivered -> {
                            this.parcelListAdapter.selectedSection = this.deliveredSection
                        }

                        R.id.action_delivery_select_event -> {
                            val eventDialog = EventDialog.Builder(this.context)
                                    .events(
                                            this.deliveryStop.allowedParcelEvents
                                                    .plus(this.deliveryStop.allowedStopEvents)
                                    )
                                    .listener(this)
                                    .build()

                            eventDialog.selectedItemEvent
                                    .bindToLifecycle(this)
                                    .subscribe {
                                        eventDialog.hide()

                                        when {
                                            this.deliveryStop.allowedParcelEvents.contains(it) -> {
                                                // Parcel level event
                                                when (it) {
                                                    EventNotDeliveredReason.DAMAGED -> {
                                                        log.trace("DAMAGED SECTION SELECTED")
                                                        this.parcelListAdapter.addSection(
                                                                sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                                                vmItemProvider = { it.toFlexibleItem() }
                                                        )

                                                        this.parcelListAdapter.selectedSection = this.damagedSection
                                                    }

                                                    else -> {
                                                    }
                                                }
                                            }
                                            else -> {
                                                // Stop level event
                                                this.deliveryStop.assignStopLevelEvent(it)
                                                        .observeOnMainThread()
                                                        .subscribeBy(
                                                                onComplete = {
                                                                    this.parcelListAdapter.selectedSection = sectionByEvent.getValue(it)
                                                                })
                                            }
                                        }
                                    }

                            eventDialog.show()
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
                this.deliveryStop.damagedParcels,
                // Also fire when selected section changes */
                this.parcelListAdapter.selectedSectionProperty.filter {
                    it.value != this.damagedSection
                },

                BiFunction { a: Any, b: Any ->
                    this.deliveryStop.damagedParcels.blockingFirst()
                }
        )
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOnMainThread()
                .subscribe {
                    if (it.count() > 0) {
                        this.parcelListAdapter.addSection(
                                sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                vmItemProvider = { it.toFlexibleItem() }
                        )
                    } else {
                        this.parcelListAdapter.removeSection(this.damagedSection)

                        if (this.parcelListAdapter.selectedSection == null) {
                            this.parcelListAdapter.selectedSection = this.deliveredSection
                        }
                    }
                }
        //endregion

        //region Initially selected section
        val sectionWithMaxEvents = this.sectionByEvent.map {
            Pair(it.value, it.value.items.blockingFirst())
        }
                .filter { it.second.count() > 0 }
                .maxBy { it.second.count() }
                ?.first

        this.parcelListAdapter.selectedSection = sectionWithMaxEvents ?: deliveredSection
        // endregion

        this.parcelListAdapter.selectedSectionProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    val section = it.value

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

        this.parcelListAdapter.itemClickEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe { item ->
                    log.debug("ONITEMCLICK")

                    ((item as? FlexibleSectionableVmItem<*>)
                            ?.viewModel as? OrderTaskViewModel)
                            ?.also { orderTaskViewModel ->
                                val eventDialog = EventDialog.Builder(this.context)
                                        .events(this.deliveryStop.allowedOrderEvents)
                                        .listener(this)
                                        .build()

                                eventDialog.selectedItemEvent
                                        .bindToLifecycle(this)
                                        .subscribe {
                                            eventDialog.hide()

                                            // Stop level event
                                            this.deliveryStop.assignOrderLevelEvent(orderTaskViewModel.orderTask.order, it)
                                                    .observeOnMainThread()
                                                    .subscribeBy(
                                                            onComplete = {
                                                                this.parcelListAdapter.selectedSection = sectionByEvent.getValue(it)
                                                            })
                                        }

                                eventDialog.show()
                            }
                }

        this.syntheticInputs = listOf(
                SyntheticInput(
                        name = "Parcels",
                        entries = this.deliveryStop.parcels.blockingFirst().map {
                            val unitNumber = UnitNumber.parse(it.number).value
                            SyntheticInput.Entry(
                                    symbologyType = SymbologyType.Interleaved25,
                                    data = unitNumber.label
                            )
                        }
                )
        )

        // Observe changes which affect action items

        Observable.merge(
                this.deliveryStop.pendingParcels,
                this.deliveryStop.stop
        )
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOnMainThread()
                .subscribe {
                    this.actionItems = this.actionItems.apply {
                        first { it.id == R.id.action_delivery_close_stop }
                                .also {
                                    it.visible = deliveryStop.canClose

                                    if (deliveryStop.canCloseWithEvent) {
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
                                            ?.isVisible = deliveryStop.canCloseWithDeliveryToNeighbor

                                    it.menu?.findItem(R.id.action_deliver_postbox)
                                            ?.isVisible = deliveryStop.canCloseWithDeliveryToPostbox

                                    it.visible = deliveryStop.canClose &&
                                            it.menu?.hasVisibleItems() ?: false
                                }
                    }
                }
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        log.trace("AIDC READ $event")

        val result = UnitNumber.parseLabel(event.data)

        when {
            result.hasError -> {
                tones.warningBeep()

                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_barcode)
                        .build().show()
            }
            else -> {
                this.onInput(result.value)
            }
        }
    }

    private fun onInput(unitNumber: UnitNumber) {
        var parcel = this.deliveryStop.parcels.blockingFirst().firstOrNull { it.number == unitNumber.value }

        if (parcel == null) {
            // Parcel does not belong to this delivery stop, ask for stop merge
            tones.warningBeep()

            parcel = this.parcelRepository.entities.firstOrNull { it.number == unitNumber.value }
            val sourceStop = parcel?.order?.deliveryTask?.stop

            if (parcel != null && sourceStop != null) {
                MaterialDialog.Builder(context)
                        .title(R.string.title_stop_merge)
                        .cancelable(true)
                        .content(R.string.dialog_content_stop_merge)
                        .positiveText(android.R.string.yes)
                        .onPositive { _, _ ->
                            db.store.withTransaction {
                                stopRepository.mergeInto(
                                        source = sourceStop,
                                        target = deliveryStop.entity
                                )
                                        .blockingAwait()
                            }
                                    .toCompletable()
                                    .subscribeOn(Schedulers.computation())
                                    .subscribeBy(
                                            onError = {
                                                log.error(it.message, it)
                                                tones.errorBeep()
                                            })
                        }
                        .negativeText(android.R.string.no)
                        .build().show()
            } else {
                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_parcel)
                        .build().show()

                return
            }

        }

        this.onParcel(parcel)
    }

    /**
     * On valid parcel entry
     */
    fun onParcel(parcel: ParcelEntity) {
        when (parcelListAdapter.selectedSection) {

            deliveredSection, pendingSection, orderSection -> {
                this.deliveryStop.deliverParcel(parcel)
                        .subscribe()

                if (this.parcelListAdapter.selectedSection != deliveredSection)
                    this.parcelListAdapter.selectedSection = deliveredSection
            }

            damagedSection -> {
                if (parcel.isDamaged) {
                    this.tones.warningBeep()
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
                                        .subscribeOn(Schedulers.computation())
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
                // TODO: add support for scanning/adding parcel to damaged section
                tones.warningBeep()
            }
        }
    }

    private fun finalizeStop() {
        this.deliveryStop.finalize()
                .subscribeOn(Schedulers.computation())
                .observeOnMainThread()
                .subscribeBy(
                        onComplete = {
                            this.delivery.activeStop = null

                            this.activity.supportFragmentManager.popBackStack(
                                    DeliveryStopListScreen::class.java.canonicalName,
                                    0)
                        },
                        onError = {
                            log.error(it.message, it)
                        }
                )
    }

    private fun closeStop(variant: EventDeliveredReason) {
        this.deliveryStop.resetCloseStopState()

        this.currentCloseStopVariatn = variant

        // Show notification dialogs
        val dialogs: List<MaterialDialog> = this.deliveryStop.services
                .filter { it.mobile.ackMessage != null }
                .map {
                    MaterialDialog.Builder(context)
                            .content(this.getString(it.mobile.ackMessage!!))
                            .cancelable(false)
                            .positiveText(R.string.ok)
                            .build()
                }

        // TODO: this will certainly not work. must be reactive
        dialogs.forEach {
            it.show()
        }

        when (variant) {
            EventDeliveredReason.NEIGHBOR -> {
                if (this.deliveryStop.cashAmountToCollect > 0) {
                    this.activity.showScreen(CashScreen().also {
                        it.setTargetFragment(this, 0)
                    })
                } else {
                    this.activity.showScreen(NeighbourDeliveryScreen().also {
                        it.setTargetFragment(this, 0)
                        it.parameters = NeighbourDeliveryScreen.Parameters(
                                stopId = this.stop.id
                        )
                    })
                }
            }

            EventDeliveredReason.POSTBOX -> {
                this.activity.showScreen(PostboxCameraScreen().also {
                    it.setTargetFragment(this, 0)
                })
            }

            EventDeliveredReason.NORMAL -> {
                when {
                    this.deliveryStop.isSignatureRequired -> {
                        when {
                            this.deliveryStop.cashAmountToCollect > 0 -> {
                                //Requires CashScreen to be shown
                                this.activity.showScreen(CashScreen().also {
                                    it.setTargetFragment(this, 0)
                                })
                            }
                            else -> {
                                MaterialDialog.Builder(context)
                                        .title(R.string.recipient)
                                        .cancelable(true)
                                        .content(R.string.recipient_dialog_content)
                                        .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                        .input("Max Mustermann", null, false, { _, charSequence ->
                                            this.deliveryStop.recipientName = charSequence.toString()

                                            this.activity.showScreen(SignatureScreen().also {
                                                it.setTargetFragment(this, 0)
                                                it.parameters = SignatureScreen.Parameters(
                                                        stopId = this.stop.id,
                                                        deliveryReason = EventDeliveredReason.NORMAL,
                                                        recipient = this.deliveryStop.recipientName ?: ""
                                                )
                                            })
                                        })
                                        .build().show()
                            }
                        }
                    }
                    else -> {
                        this.deliveryStop.finalize()
                                .observeOnMainThread()
                                .subscribeBy(
                                        onComplete = {
                                            // TODO: move state control to model
                                            this.activity.supportFragmentManager.popBackStack(DeliveryStopListScreen::class.java.canonicalName, 0)
                                        },
                                        onError = {
                                            log.error(it.message, it)
                                        })
                    }
                }
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
                            .subscribeOn(Schedulers.computation())
                            .subscribe()
                }
            }

            is PostboxCameraScreen -> {
                this.deliveryStop.deliverToPostbox(jpeg)
                this.finalizeStop()
            }
        }
    }

    override fun onSignatureSubmitted(signatureSvg: String) {
        this.deliveryStop.signatureSvg = signatureSvg
        this.finalizeStop()
    }


    override fun onSignatureImageSubmitted(signatureJpeg: ByteArray) {
        this.deliveryStop.deliverWithSignatureOnPaper(signatureJpeg)
        this.finalizeStop()
    }

    override fun onNeighbourDeliveryScreenContinue(neighbourName: String) {
        this.deliveryStop.recipientName = neighbourName
        this.deliveryStop.deliveredReason = EventDeliveredReason.NEIGHBOR

        this.activity.showScreen(
                SignatureScreen().also {
                    it.setTargetFragment(this, 0)
                    it.parameters = SignatureScreen.Parameters(
                            stopId = this.stop.id,
                            deliveryReason = EventDeliveredReason.NEIGHBOR,
                            recipient = neighbourName
                    )
                }
        )
    }

    override fun onCashScreenContinue() {
        when (this.currentCloseStopVariatn) {
            EventDeliveredReason.NORMAL -> {
                MaterialDialog.Builder(context)
                        .title(R.string.recipient)
                        .cancelable(true)
                        .content(R.string.recipient_dialog_content)
                        .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                        .input("Max Mustermann", null, false, { _, charSequence ->
                            this.deliveryStop.recipientName = charSequence.toString()

                            this.activity.showScreen(SignatureScreen().also {
                                it.setTargetFragment(this, 0)
                                it.parameters = SignatureScreen.Parameters(
                                        stopId = this.stop.id,
                                        deliveryReason = EventDeliveredReason.NORMAL,
                                        recipient = this.deliveryStop.recipientName ?: ""
                                )
                            })
                        })
                        .build().show()
            }

            EventDeliveredReason.NEIGHBOR -> {
                this.activity.showScreen(NeighbourDeliveryScreen().also {
                    it.setTargetFragment(this, 0)
                    it.parameters = NeighbourDeliveryScreen.Parameters(
                            stopId = this.stop.id
                    )
                })
            }

            else -> {
            }
        }
    }


    override fun onEventDialogItemSelected(event: EventNotDeliveredReason) {
        log.trace("SELECTEDITEAM VIA LISTENER")
    }
}

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
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_delivery_process.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.DebugSettings

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.databinding.ScreenDeliveryProcessBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.entity.OrderEntity
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.mobile.model.process.DeliveryStop
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
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
        EventDialog.Listener {

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
                amount = deliveryStop.orderAmount.map { it.toString() }.toField(),
                totalAmount = deliveryStop.orderTotalAmount.map { it.toString() }.toField()
        )

        val parcelCounter = CounterViewModel(
                drawableRes = R.drawable.ic_package_variant_closed,
                amount = deliveryStop.parcelAmount.map { it.toString() }.toField(),
                totalAmount = deliveryStop.parcelTotalAmount.map { it.toString() }.toField()
        )

        val weightCounter = CounterViewModel(
                drawableRes = R.drawable.ic_weight_scale,
                amount = deliveryStop.weight.map { "${it.format(2)}kg" }.toField(),
                totalAmount = deliveryStop.totalWeight.map { "${it.format(2)}kg" }.toField()
        )
    }

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val tones: Tones by Kodein.global.lazy.instance()
    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    //region Model classes
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val stop: StopEntity by lazy {
        this.stopRepository.entities.first { it.id == this.parameters.stopId }
    }

    private val deliveryStop: DeliveryStop by lazy {
        this.delivery.activeStop ?: throw IllegalArgumentException("Active stop not set")
    }
    //endregion

    //region Sections
    val deliveredSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_truck_delivery,
                color = R.color.colorGreen,
                background = R.drawable.section_background_green,
                title = this.getText(R.string.delivered).toString(),
                items = this.deliveryStop.deliveredParcels
                        .bindToLifecycle(this)
        )
    }

    val pendingSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_format_list_bulleted,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                showIfEmpty = false,
                title = this.getText(R.string.pending).toString(),
                items = this.deliveryStop.pendingParcels
                        .bindToLifecycle(this)
        )
    }

    val orderSection by lazy {
        SectionViewModel<OrderEntity>(
                icon = R.drawable.ic_order,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                showIfEmpty = true,
                title = this.getText(R.string.orders).toString(),
                items = this.deliveryStop.orders
                        .bindToLifecycle(this)
        )
    }

    /**
     * Extension for creating sections from event/reason enum
     */
    fun EventNotDeliveredReason.toSection(): SectionViewModel<ParcelEntity> {
        return SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_event,
                color = R.color.colorAccent,
                background = R.drawable.section_background_accent,
                showIfEmpty = false,
                title = this.mobile.textOrName(context),
                items = deliveryStop.parcelsByEvent
                        .withDefault { Observable.empty() }
                        .getValue(this)
                        .bindToLifecycle(this@DeliveryStopProcessScreen)
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
        )
    }

    fun ParcelEntity.toFlexibleItem()
            : FlexibleSectionableVmItem<ParcelViewModel> {

        return FlexibleSectionableVmItem(
                view = R.layout.item_parcel,
                variable = BR.parcel,
                viewModel = ParcelViewModel(this)
        )
    }

    fun OrderEntity.toFlexibleItem()
            : FlexibleSectionableVmItem<OrderViewModel> {
        return FlexibleSectionableVmItem(
                view = R.layout.item_order_compact,
                variable = BR.order,
                viewModel = OrderViewModel(this)
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
        this.menu = this.inflateMenu(R.menu.menu_delivery_stop_process)

        // Flexible adapter needs to be re-created with views
        this.parcelListAdapterInstance.reset()

        this.uxRecyclerView.adapter = parcelListAdapter
        this.uxRecyclerView.layoutManager = LinearLayoutManager(context)

        //region Action items

        // Close stop menu
        val closeStopMenu = this.activity.inflateMenu(R.menu.menu_delivery_stop_process_close)

        closeStopMenu.findItem(R.id.action_deliver_postbox).isVisible =
                this.deliveryStop.services.contains(ParcelService.POSTBOX_DELIVERY) &&
                        !this.deliveryStop.services.contains(ParcelService.NO_ALTERNATIVE_DELIVERY)

        closeStopMenu.findItem(R.id.action_deliver_neighbour).isVisible =
                !this.deliveryStop.services.contains(ParcelService.NO_ALTERNATIVE_DELIVERY)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_delivery_select_delivered,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_truck_delivery
                ),

                ActionItem(
                        id = R.id.action_delivery_select_event,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_exclamation
                ),

                ActionItem(
                        id = R.id.action_delivery_close_stop,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        menu = closeStopMenu,
                        visible = false,
                        alignEnd = false
                )
        )
        //endregion

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopViewModel(this.stop)
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
                    }
                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        R.id.action_delivery_select_delivered -> {
                            this.parcelListAdapter.selectedSection = this.deliveredSection
                        }

                        R.id.action_delivery_select_event -> {
                            val eventDialog = EventDialog.Builder(this.context)
                                    .events(this.deliveryStop.allowedEvents)
                                    .listener(this)
                                    .build()

                            eventDialog.selectedItemEvent
                                    .bindToLifecycle(this)
                                    .subscribe {
                                        eventDialog.hide()
                                        this.deliveryStop.assignEventReason(it)
                                                .observeOnMainThread()
                                                .subscribeBy(
                                                        onComplete = {
                                                            this.parcelListAdapter.selectedSection = sectionByEvent.get(it)
                                                        })
                                    }

                            eventDialog.show()
                        }

                        R.id.action_deliver_neighbour -> {
                            delivery.sign(stopId = this.stop.id, reason = EventDeliveredReason.NEIGHBOR)
                        }

                        R.id.action_deliver_postbox -> {
                            delivery.sign(stopId = this.stop.id, reason = EventDeliveredReason.POSTBOX)
                        }

                        R.id.action_deliver_recipient -> {
                            if (this.deliveryStop.isSignatureRequired) {
                                MaterialDialog.Builder(context)
                                        .title(R.string.recipient)
                                        .cancelable(true)
                                        .content(R.string.recipient_dialog_content)
                                        .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                                        .input("Max Mustermann", null, false, { _, charSequence ->
                                            this.deliveryStop.recipientName = charSequence.toString()

                                            this.activity.showScreen(SignatureScreen().also {
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

                        R.id.action_deliver_close -> {
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

        this.parcelListAdapter.selectedSection = this.deliveredSection
        this.parcelListAdapter.selectedSectionProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    val section = it.value

                    this.accentColor = section?.color ?: R.color.colorGrey

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
                                    it.visible = deliveryStop.isCloseAvailable
                                    it.menu?.findItem(R.id.action_deliver_recipient)
                                            ?.isVisible = deliveryStop.isCloseToRecipientAvailable

                                    it.menu?.findItem(R.id.action_deliver_neighbour)
                                            ?.isVisible = deliveryStop.isCloseToRecipientAvailable

                                    it.menu?.findItem(R.id.action_deliver_close)
                                            ?.isVisible = deliveryStop.isCloseWithEventAvailable
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
        val parcel = this.deliveryStop.parcels.blockingFirst().firstOrNull { it.number == unitNumber.value }

        if (parcel == null) {
            tones.warningBeep()

            this.activity.snackbarBuilder
                    .message(R.string.error_invalid_parcel)
                    .build().show()

            return
        }

        this.onParcel(parcel as ParcelEntity)
    }

    /**
     * On valid parcel entry
     */
    fun onParcel(parcel: ParcelEntity) {
        when (parcelListAdapter.selectedSection) {
            deliveredSection, pendingSection, orderSection -> {
                this.deliveryStop.deliver(parcel)
                        .subscribe()

                if (this.parcelListAdapter.selectedSection != deliveredSection)
                    this.parcelListAdapter.selectedSection = deliveredSection
            }
            else -> {
                tones.warningBeep()
            }
        }
    }

    override fun onEventDialogItemSelected(event: EventNotDeliveredReason) {
        log.trace("SELECTEDITEAM VIA LISTENER")
    }
}

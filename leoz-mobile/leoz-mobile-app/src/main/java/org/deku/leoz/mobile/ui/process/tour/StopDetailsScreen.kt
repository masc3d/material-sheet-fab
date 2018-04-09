package org.deku.leoz.mobile.ui.process.tour

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_tour_stop_detail.*
import mobi.upod.timedurationpicker.TimeDurationPicker
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Feedback
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.appointmentTimeLeft
import org.deku.leoz.mobile.model.entity.hasValidPhoneNumber
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.core.ScreenFragment
import org.deku.leoz.mobile.ui.core.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.*
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.aidc.SymbologyType
import sx.android.Device
import sx.android.aidc.*
import sx.android.inflateMenu
import sx.android.rx.observeOnMainThreadUntilEvent
import sx.android.ui.flexibleadapter.VmItem
import sx.android.ui.flexibleadapter.SimpleVmItem
import sx.time.TimeSpan

class StopDetailsScreen
    :
        ScreenFragment<StopDetailsScreen.Parameters>() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @org.parceler.Parcel(org.parceler.Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int
    )

    interface Listener {
        fun onStopDetailUnitNumberInput(unitNumber: UnitNumber)
    }

    private val listener by listenerDelegate<Listener>()

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val feedback: Feedback by Kodein.global.lazy.instance()

    // Model classes
    private val tour: Tour by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val timer: sx.android.ui.Timer by Kodein.global.lazy.instance()

    private val timerEvent = timer
            .tickEvent
            .bindToLifecycle(this)

    private val stop: Stop by lazy {
        this.stopRepository.entities.first { it.id == this.parameters.stopId }
    }

    private val flexibleAdapterInstance = LazyInstance<FlexibleAdapter<
            VmItem<
                    SectionViewModel<Any>, *>
            >>({
        FlexibleAdapter(
                listOf(),
                //Listener
                this
        )
    })
    private val adapter get() = flexibleAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_stop_detailt)
        this.aidcEnabled = false
        this.toolbarCollapsed = true
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
    }

    val serviceSection by lazy {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
    // Inflate the layout for this fragment
            inflater.inflate(R.layout.screen_tour_stop_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)!!
        binding.stop = StopViewModel(
                isStateVisible = true,
                stop = this.stop,
                timerEvent = this.timerEvent)

        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        this.uxDetailList.adapter = adapter
        this.uxDetailList.layoutManager = LinearLayoutManager(context)
        //this.uxStopList.addItemDecoration(dividerItemDecoration)

        adapter.isLongPressDragEnabled = false
        adapter.isHandleDragEnabled = false
        adapter.isSwipeEnabled = false

        // Build detail list

        //region Services
        val services = stop.tasks.flatMap { it.services }
                .filter { it != ParcelService.NO_ADDITIONAL_SERVICE && it.mobile.text != null }
                .distinct()

        val serviceSection = SectionViewModel<Any>(
                icon = R.drawable.ic_service,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                title = this.getText(R.string.services).toString(),
                items = Observable.fromIterable(listOf(services)))

        if (services.count() > 0) {
            adapter.addItem(
                    VmItem<SectionViewModel<Any>, Any>(
                            view = R.layout.item_section_header,
                            variable = BR.header,
                            viewModel = serviceSection
                    ).also {
                        it.subItems = services
                                .map {
                                    SimpleVmItem<Any>(
                                            view = R.layout.item_service,
                                            variable = BR.service,
                                            viewModel = ServiceViewModel(
                                                    context = this.context,
                                                    service = it)
                                    )
                                }
                    }
            )
        }
        //endregion

        //region Orders
        val orders = stop.tasks.map { it.order }.distinct()

        adapter.addItem(
                VmItem<SectionViewModel<Any>, Any>(
                        view = R.layout.item_section_header,
                        variable = BR.header,
                        viewModel = SectionViewModel<Any>(
                                icon = R.drawable.ic_order,
                                color = R.color.colorGrey,
                                background = R.drawable.section_background_grey,
                                title = this.getText(R.string.orders).toString(),
                                items = Observable.fromIterable(listOf(orders))
                        )
                ).also {
                    it.subItems = orders.map {
                        SimpleVmItem<Any>(
                                view = R.layout.item_ordertask,
                                variable = BR.orderTask,
                                viewModel = OrderTaskViewModel(it.pickupTask)
                        )
                    }
                }
        )
        //endregion

        //region Parcels
        val parcels = stop.tasks.flatMap { it.order.parcels }

        adapter.addItem(
                VmItem<SectionViewModel<Any>, Any>(
                        view = R.layout.item_section_header,
                        variable = BR.header,
                        viewModel = SectionViewModel<Any>(
                                icon = R.drawable.ic_package_variant_closed,
                                color = R.color.colorGrey,
                                background = R.drawable.section_background_grey,
                                title = this.getText(R.string.parcels).toString(),
                                items = Observable.fromIterable(listOf(parcels))
                        )
                ).also {
                    it.subItems = parcels.map {
                        SimpleVmItem<Any>(
                                view = R.layout.item_parcel_card,
                                variable = BR.parcel,
                                viewModel = ParcelViewModel(it, showOrderTask = false)
                        )
                    }
                }
        )
        //endregion

        adapter.mode = SelectableAdapter.Mode.SINGLE
        // Since 5.0.0-rc3 click events will only be forwarded to holders when there's a click listener registered
        adapter.addListener(FlexibleAdapter.OnItemClickListener { _, _ -> false })

        adapter.setStickyHeaders(true)
        adapter.showAllHeaders()
        adapter.collapseAll()

        // Expand service section
        adapter.currentItems.firstOrNull {
            it.viewModel == serviceSection
        }?.also {
            adapter.expand(it)
        }

        val stopNoteMenu = this.activity.inflateMenu(R.menu.menu_delivery_stop_add_information)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_deliver_continue,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_delivery,
                        iconTintRes = android.R.color.white
                ),
                ActionItem(
                        id = R.id.action_call,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_phone,
                        visible = this.stop.address.hasValidPhoneNumber,
                        alignEnd = false
                ),
                ActionItem(
                        id = R.id.action_navigate,
                        colorRes = R.color.colorAccent,
                        iconRes = android.R.drawable.ic_menu_mylocation,
                        iconTintRes = android.R.color.black,
                        alignEnd = false
                ),
                ActionItem(
                        id = R.id.action_stop_add_information,
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_comment_alert,
                        alignEnd = false,
                        menu = stopNoteMenu
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
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.onAidcRead(it)
                }

        this.activity.actionEvent
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_deliver_continue -> {
                            this.activity.showScreen(
                                    StopProcessScreen().also {
                                        it.parameters = StopProcessScreen.Parameters(
                                                stopId = stop.id
                                        )
                                    })
                        }

                        R.id.action_navigate -> {
                            val intent: Intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=${stop.address.street}+${stop.address.streetNo}+${stop.address.city}+${stop.address.zipCode}+${stop.address.countryCode}&mode=d")
                                    //Uri.parse("https://www.google.com/maps/dir/?api=1&query=${stop.address.street}+${stop.address.streetNo}+${stop.address.city}+${stop.address.zipCode}")
                            )
                            try {
                                startActivity(intent)
                            } catch (e: Exception) {
                                this.activity.snackbarBuilder
                                        .message("Failed! Navigation App installed?")
                                        .duration(Snackbar.LENGTH_INDEFINITE)
                                        .build().show()
                            }
                        }

                        R.id.action_call -> {
                            val device: Device by Kodein.global.lazy.instance()
                            val dialogBuilder = MaterialDialog.Builder(context)

                            if (device.telephonyEnabled) {
                                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + stop.address.phone))
                                dialogBuilder.title(R.string.title_confirm_call)
                                dialogBuilder.positiveText(R.string.call)
                                dialogBuilder.negativeText(android.R.string.cancel)
                                dialogBuilder.onPositive { _, _ ->
                                    startActivity(intent)
                                }
                            } else {
                                dialogBuilder.title(R.string.title_phone_number)
                                dialogBuilder.neutralText(R.string.dismiss)
                            }

                            dialogBuilder.cancelable(true)
                            dialogBuilder.content(stop.address.phone)
                            dialogBuilder.build().show()
                        }

                        R.id.action_report_delay -> {
                            val dialog = MaterialDialog.Builder(context)
                                    .title("Report delay")
                                    .iconRes(R.drawable.ic_appointment_at_risk)
                                    .cancelable(false)
                                    .customView(R.layout.dialog_delay_picker, true)
                                    .positiveText(R.string.proceed)
                                    .onPositive { _, _ ->

                                    }
                                    .negativeText(android.R.string.cancel)
                                    .build()

//                            val customView = dialog.customView!!
//                            customView.findViewById<TimeDurationPicker>(R.id.uxTimeDurationInput).also {
//                                it.setTimeUnits(1)
//                            }

                            dialog.show()

//                            val dialogBuilder = MaterialDialog.Builder(context).also {
//                                it.title("Report delay")
//                                it.positiveText("Continue")
//                                it.negativeText("Abort")
//                                it.content("Specify a reason for the delay")
//                                it.input(
//                                        "Reason",
//                                        null,
//                                        false,
//                                        MaterialDialog.InputCallback { dialog, input ->
//                                            log.debug("INPUT-CALLBACK [$input]}")
//                                        }
//                                )
//                                it.inputType(InputType.TYPE_CLASS_TEXT)
//                                it.onPositive { dialog, which ->
//                                    val hmsPicker: HmsPickerBuilder = HmsPickerBuilder().also {
//                                        it.setFragmentManager(this.fragmentManager)
//                                        it.setStyleResId(R.style.BetterPickersDialogFragment)
//                                        it.setPlusMinusVisibility(View.VISIBLE)
//                                    }
//
//                                    hmsPicker.show()
//                                }
//                            }.show()
                        }
                    }
                }

        this.syntheticInputs = listOf(
                SyntheticInput(
                        name = "Parcels",
                        entries = this.tour.loadedParcels.blockingFirst().value.map {
                            val unitNumber = DekuUnitNumber.parse(it.number).value
                            SyntheticInput.Entry(
                                    symbologyType = SymbologyType.Interleaved25,
                                    data = unitNumber.label
                            )
                        }
                )
        )
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
        this.listener?.onStopDetailUnitNumberInput(unitNumber)
    }
}

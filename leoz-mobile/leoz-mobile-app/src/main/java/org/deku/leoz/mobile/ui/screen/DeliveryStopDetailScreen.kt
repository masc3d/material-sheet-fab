package org.deku.leoz.mobile.ui.screen


import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_delivery_detail.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.dialog.EventDialog
import org.deku.leoz.mobile.ui.extension.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.model.ParcelService
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.aidc.*
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleSectionableVmItem

class DeliveryStopDetailScreen
    :
        ScreenFragment<DeliveryStopDetailScreen.Parameters>(),
        EventDialog.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @org.parceler.Parcel(org.parceler.Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int
    )

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    // Model classes
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val stop: Stop by lazy {
        this.stopRepository.entities.first { it.id == this.parameters.stopId }
    }

    private val flexibleAdapterInstance = LazyInstance<FlexibleAdapter<
            FlexibleExpandableVmItem<
                    SectionViewModel<Any>, *>
            >>({
        FlexibleAdapter(
                listOf(),
                //Listener
                this
        )
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_stop_detailt)
        this.aidcEnabled = true
        this.toolbarCollapsed = true
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
    }

    val serviceSection by lazy {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_delivery_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopViewModel(this.stop)

        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        this.uxDetailList.adapter = flexibleAdapter
        this.uxDetailList.layoutManager = LinearLayoutManager(context)
        //this.uxStopList.addItemDecoration(dividerItemDecoration)

        flexibleAdapter.isLongPressDragEnabled = false
        flexibleAdapter.isHandleDragEnabled = false
        flexibleAdapter.isSwipeEnabled = false

        // Build detail list

        //region Services
        val services = stop.tasks.flatMap { it.services }.filter { it != ParcelService.NO_ADDITIONAL_SERVICE }.distinct()

        val serviceSection = SectionViewModel<Any>(
                icon = R.drawable.ic_service,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                title = this.getText(R.string.services).toString(),
                items = Observable.fromIterable(listOf(services)))

        if (services.count() > 0) {
            flexibleAdapter.addItem(
                    FlexibleExpandableVmItem<SectionViewModel<Any>, Any>(
                            view = R.layout.item_section_header,
                            variable = BR.header,
                            viewModel = serviceSection
                    ).also {
                        it.subItems = services
                                .filter { it.mobile.text != null }
                                .map {
                                    FlexibleSectionableVmItem<Any>(
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

        flexibleAdapter.addItem(
                FlexibleExpandableVmItem<SectionViewModel<Any>, Any>(
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
                        FlexibleSectionableVmItem<Any>(
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

        flexibleAdapter.addItem(
                FlexibleExpandableVmItem<SectionViewModel<Any>, Any>(
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
                        FlexibleSectionableVmItem<Any>(
                                view = R.layout.item_parcel,
                                variable = BR.parcel,
                                viewModel = ParcelViewModel(it, showOrderTask = false)
                        )
                    }
                }
        )
        //endregion

        flexibleAdapter.setStickyHeaders(true)
        flexibleAdapter.showAllHeaders()
        flexibleAdapter.collapseAll()

        // Expand service section
        flexibleAdapter.currentItems.firstOrNull {
            it.viewModel == serviceSection
        }?.also {
            flexibleAdapter.expand(it)
        }

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_deliver_continue,
                        colorRes = R.color.colorPrimary,
                        iconTintRes = android.R.color.white,
                        iconRes = R.drawable.ic_delivery
                ),
                ActionItem(
                        id = R.id.action_call,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_phone,
                        alignEnd = false
                ),
                ActionItem(
                        id = R.id.action_navigate,
                        colorRes = R.color.colorAccent,
                        iconRes = android.R.drawable.ic_menu_mylocation,
                        iconTintRes = android.R.color.black,
                        alignEnd = false
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

                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        R.id.action_deliver_continue -> {
                            this.activity.showScreen(
                                    DeliveryStopProcessScreen().also {
                                        it.parameters = DeliveryStopProcessScreen.Parameters(
                                                stopId = stop.id
                                        )
                                    })
                        }

                        R.id.action_navigate -> {
                            val intent: Intent = Intent(
                                    Intent.ACTION_VIEW,
                                    //Uri.parse("google.navigation:q=${stop.address.street}+${stop.address.streetNo}+${stop.address.city}+${stop.address.zipCode}&mode=d")
                                    Uri.parse("https://www.google.com/maps/dir/?api=1&query=${stop.address.street}+${stop.address.streetNo}+${stop.address.city}+${stop.address.zipCode}")
                            )
                            startActivity(intent)
                        }

                        R.id.action_call -> {
                            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + stop.address.phone))
                            val dialogBuilder = MaterialDialog.Builder(context)
                            dialogBuilder.title(R.string.title_confirm_call)
                            dialogBuilder.content(stop.address.phone)
                            dialogBuilder.positiveText(R.string.call)
                            dialogBuilder.negativeText(android.R.string.cancel)
                            dialogBuilder.cancelable(true)
                            dialogBuilder.onPositive { materialDialog, dialogAction ->
                                startActivity(intent)
                            }
                            dialogBuilder.build().show()
                        }
                    }
                }
    }

    override fun onEventDialogItemSelected(event: EventNotDeliveredReason) {
        log.trace("SELECTEDITEAM VIA LISTENER")
    }
}

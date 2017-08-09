package org.deku.leoz.mobile.ui.screen


import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_delivery_process.*
import org.deku.leoz.mobile.BR

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.dialog.EventDialog
import org.deku.leoz.mobile.ui.extension.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.ParcelViewModel
import org.deku.leoz.mobile.ui.vm.StopViewModel
import org.deku.leoz.model.EventDeliveredReason
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.model.ParcelService
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.aidc.*
import sx.android.ui.flexibleadapter.FlexibleSectionableVmItem


/**
 * A simple [Fragment] subclass.
 */
class DeliveryStopProcessScreen :
        ScreenFragment<DeliveryStopProcessScreen.Parameters>(),
        EventDialog.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Created by masc on 10.07.17.
     */
    inner class StatsViewModel
        : BaseObservable() {

//        val orderCounter = CounterViewModel(
//                drawableRes = R.drawable.ic_truck,
//                amount = this.deliveryList.orderAmount.map { it.toString() }.toField(),
//                totalAmount = this.deliveryList.orderTotalAmount.map { it.toString() }.toField()
//        )
//
//        val parcelCounter = CounterViewModel(
//                drawableRes = R.drawable.ic_package_variant_closed,
//                amount = this.deliveryList.parcelAmount.map { it.toString() }.toField(),
//                totalAmount = this.deliveryList.parcelTotalAmount.map { it.toString() }.toField()
//        )
//
//        val weightCounter = CounterViewModel(
//                drawableRes = R.drawable.ic_scale,
//                amount = this.deliveryList.weight.map { "${it.format(2)}kg" }.toField(),
//                totalAmount = this.deliveryList.totalWeight.map { "${it.format(2)}kg" }.toField()
//        )
    }

    @org.parceler.Parcel(org.parceler.Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int
    )

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val stop: Stop by lazy {
        this.stopRepository.entities.first { it.id == this.parameters.stopId }
    }

    private val orderList: MutableList<Order> = mutableListOf()
    private val parcelList: MutableList<Parcel> = mutableListOf()
    private var lastRef: String? = null
    private var resultCount: Int = 0

    private val parcelListAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleSectionableVmItem<
                            ParcelViewModel>>>({

        val adapter = FlexibleAdapter(
                // Items
                stop.tasks.map { it.order }
                        .flatMap { it.parcels }
                        .map {
                            val item = FlexibleSectionableVmItem(
                                    view = R.layout.item_parcel,
                                    variable = BR.parcel,
                                    viewModel = ParcelViewModel(it)
                            )

                            item.isEnabled = true
                            item.isDraggable = true
                            item.isSwipeable = false

                            item
                        },
                // Listener
                this)

        adapter.setDisplayHeadersAtStartUp(true)
        adapter.setStickyHeaders(true)
        adapter.showAllHeaders()

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_stop_process)
        this.aidcEnabled = true
        this.headerImage = R.drawable.img_parcels_1a
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_delivery_process, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Flexible adapter needs to be re-created with views
        this.parcelListAdapterInstance.reset()

        this.uxParcelList.adapter = parcelListAdapter
        this.uxParcelList.layoutManager = LinearLayoutManager(context)

        val closeStopMenu = this.activity.inflateMenu(R.menu.menu_deliver_options)

        closeStopMenu.findItem(R.id.action_deliver_postbox).isEnabled =
                this.stop.tasks.any { it.services.contains(ParcelService.POSTBOX_DELIVERY) }

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_delivery_select_regular,
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
                        iconRes = R.drawable.ic_done_black,
                        iconTintRes = android.R.color.white,
                        menu = closeStopMenu,
                        alignEnd = false
                )
        )

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopViewModel(this.stop)
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
                    log.info("Barcode scanned ${it.data}")
                    processLabelRef(ref = it.data)
                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        R.id.action_cancel -> {

                        }
                        R.id.action_delivery_select_event -> {
                            val dialog = EventDialog.Builder(this.context)
                                    .events(this.delivery.allowedEvents)
                                    .listener(this)
                                    .build()

                            dialog.selectedItemEvent
                                    .bindToLifecycle(this)
                                    .subscribe {
                                        log.trace("SELECTEDITEAM VIA RX")
                                    }

                            dialog.show()
                        }
                        R.id.action_deliver_neighbour -> {
                            delivery.sign(stopId = this.stop.id, reason = EventDeliveredReason.Neighbor)
                        }
                        R.id.action_deliver_postbox -> {
                            delivery.sign(stopId = this.stop.id, reason = EventDeliveredReason.Postbox)
                        }
                        R.id.action_deliver_recipient -> {
                            this.activity.showScreen(SignatureScreen().also {
                                it.parameters = SignatureScreen.Parameters(
                                        stopId = this.stop.id,
                                        deliveryReason = EventDeliveredReason.Normal,
                                        recipient = ""
                                )
                            })
                        }
                    }
                }
    }

    private fun processLabelRef(ref: String) {
        val order: Order? = orderList.firstOrNull {
            it.parcels.firstOrNull {
                it.number == ref
            } != null
        }

        hideResultImages()

        if (order != null) {
            showResult(R.drawable.green)
        } else {
            //Parcel is not part of this stop
            if (lastRef.isNullOrBlank() || lastRef != ref) {
                //No (similar) reference scanned previously
                showResult(R.drawable.red)
            } else {
                showResult(R.drawable.red)
            }
        }

        lastRef = ref
    }

    private fun showResult(backgroundResource: Int) {
        hideResultImages()

        val view = if (resultCount % 2 == 0) this.uxResultLeft else this.uxResultRight

        //view.setBackgroundResource(backgroundResource)
        view.setImageDrawable(ContextCompat.getDrawable(this.context, backgroundResource))
        view.visibility = View.VISIBLE

        resultCount++
    }

    private fun hideResultImages() {
        this.uxResultLeft.visibility = View.INVISIBLE
        this.uxResultRight.visibility = View.INVISIBLE
    }

    override fun onEventDialogItemSelected(event: EventNotDeliveredReason) {
        log.trace("SELECTEDITEAM VIA LISTENER")
    }

}

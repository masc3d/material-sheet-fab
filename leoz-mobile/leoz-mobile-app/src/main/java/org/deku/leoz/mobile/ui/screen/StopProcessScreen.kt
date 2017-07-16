package org.deku.leoz.mobile.ui.screen


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
import kotlinx.android.synthetic.main.screen_stop_process.*
import org.deku.leoz.mobile.BR

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.model.Parcel
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import org.deku.leoz.mobile.ui.dialog.EventDialog
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.ParcelViewModel
import org.deku.leoz.mobile.ui.vm.StopItemViewModel
import org.deku.leoz.model.EventDelivered
import org.deku.leoz.model.EventDeliveredReason
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.model.ParcelService
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.aidc.AidcReader
import sx.android.ui.flexibleadapter.FlexibleVmSectionableItem


/**
 * A simple [Fragment] subclass.
 */
class StopProcessScreen :
        ScreenFragment(),
        EventDialog.Listener    {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private lateinit var stop: Stop
    private val orderList: MutableList<Order> = mutableListOf()
    private val parcelList: MutableList<Parcel> = mutableListOf()
    private var lastRef: String? = null
    private var resultCount: Int = 0

    private val parcelListAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleVmSectionableItem<
                            ParcelViewModel>>>({

        val adapter = FlexibleAdapter(
                // Items
                stop.orders
                        .flatMap { it.parcel }
                        .map {
                            val item = FlexibleVmSectionableItem(
                                    viewRes = R.layout.item_parcel,
                                    variableId = BR.parcel,
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

    companion object {
        /**
         * Create instance with parameters. This pattern requires `retainInstance` to be set in `onCreate`!
         */
        fun create(stop: Stop): StopProcessScreen {
            val f = StopProcessScreen()
            f.stop = stop
            f.orderList.addAll(f.stop.orders.filter { it.state == Order.State.LOADED })
            f.orderList.forEach {
                f.parcelList.addAll(it.parcel)
            }
            return f
        }

        /**
         * @param orders List of orders which are supposed to be processed summarized. Note: The list should only contain orders which meet the requirements to be compressed into a single Stop
         */
        fun create(orders: List<Order>): StopProcessScreen {
            val f = StopProcessScreen()
            f.orderList.addAll(orders)
            f.orderList.forEach {
                f.parcelList.addAll(it.parcel)
            }
            f.stop = f.orderList.first().toStop()
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_stop_process, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Flexible adapter needs to be re-created with views
        this.parcelListAdapterInstance.reset()

        this.uxParcelList.adapter = parcelListAdapter
        this.uxParcelList.layoutManager = LinearLayoutManager(context)

        val deliverMenu = this.activity.inflateMenu(R.menu.menu_deliver_options)
        deliverMenu.findItem(R.id.ux_action_deliver_postbox).isEnabled = this.stop.orders.first().getServiceOfInterest().service.contains(ParcelService.POSTBOX_DELIVERY)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_deliver_ok,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_check_circle,
                        menu = deliverMenu
                ),
                ActionItem(
                        id = R.id.action_deliver_fail,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_cancel_black,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_exception)
                )
        )

        this

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopItemViewModel(this.stop)
    }

    override fun onResume() {
        super.onResume()

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
                        R.id.ux_action_cancel -> {

                        }
                        R.id.ux_action_fail -> {
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
                        R.id.ux_action_deliver_neighbour -> {
                            startHandOver(
                                    event = EventDelivered(
                                            reason = EventDeliveredReason.Neighbor
                                    )
                            )
                        }
                        R.id.ux_action_deliver_postbox -> {
                            startHandOver(
                                    event = EventDelivered(
                                            reason = EventDeliveredReason.Postbox
                                    )
                            )
                        }
                        R.id.ux_action_deliver_recipient -> {
                            startHandOver(
                                    event = EventDelivered(
                                            reason = EventDeliveredReason.Normal
                                    )
                            )
                        }
                    }
                }
    }

    fun startHandOver(event: EventDelivered) {
        (this.activity as DeliveryActivity).runServiceWorkflow(stop, event.reason)
    }

    private fun processLabelRef(ref: String) {
        val order: Order? = orderList.firstOrNull {
            it.parcel.firstOrNull {
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

        val view =  if (resultCount%2 == 0) this.uxResultLeft else this.uxResultRight

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

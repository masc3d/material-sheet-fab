package org.deku.leoz.mobile.ui.screen


import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
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
import kotlinx.android.synthetic.main.screen_delivery_detail.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.process.getServiceText
import org.deku.leoz.mobile.ui.OrderListItem
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.dialog.EventDialog
import org.deku.leoz.mobile.ui.extension.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.StopItemViewModel
import org.deku.leoz.model.EventNotDeliveredReason
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.aidc.*

class StopDetailScreen
    :
        ScreenFragment<Any>(),
        EventDialog.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private lateinit var stop: Stop
    private var serviceDescriptions: MutableList<String> = mutableListOf()

    private val flexibleAdapterInstance = LazyInstance<FlexibleAdapter<OrderListItem>>({
        FlexibleAdapter(
                //Orders to be listed
                stop.tasks.map { it.order }.distinct()
                        .map {
                            OrderListItem(context, it)
                        },
                //Listener
                this
        )
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    companion object {
        /**
         * Create instance with parameters. This pattern requires `retainInstance` to be set in `onCreate`!
         */
        fun create(stop: Stop): StopDetailScreen {
            val f = StopDetailScreen()
            f.stop = stop
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_stop_detailt)
        this.aidcEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_delivery_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_list_item_1, parcelRefList)

        //endregion

        serviceDescriptions.clear()

        stop.tasks.flatMap { it.services }
                .forEach { serviceDescriptions.add(context.getServiceText(it)) }

        this.uxServiceList.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, serviceDescriptions)

        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        this.uxOrderList.adapter = flexibleAdapter
        this.uxOrderList.layoutManager = LinearLayoutManager(context)
        //this.uxStopList.addItemDecoration(dividerItemDecoration)

        flexibleAdapter.isLongPressDragEnabled = true
        flexibleAdapter.isHandleDragEnabled = true
        flexibleAdapter.isSwipeEnabled  = false

        showInitFabButtons()

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopItemViewModel(this.stop)

        this.retainInstance = true
    }

    fun showInitFabButtons() {
        log.debug("Show initial FAB buttons")
        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_deliver_continue,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_check_circle
                ),
                ActionItem(
                        id = R.id.action_deliver_action,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_information_outline,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_actions)
                ),
                ActionItem(
                        id = R.id.action_deliver_fail,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_cancel_black,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_exception)
                )
        )
    }

    fun showDeliverFabButtons() {
        log.debug("Show deliver FAB buttons")

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
                            this.activity.showScreen(StopProcessScreen.create(stop = stop))
                            showDeliverFabButtons()
                        }
                        R.id.action_navigate -> {
                            val intent: Intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=${stop.address.street}+${stop.address.streetNo}+${stop.address.city}+${stop.address.zipCode}&mode=d")
                            )
                            startActivity(intent)
                        }
                        R.id.action_contact -> {
                            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + stop.address.phone))
                            val dialogBuilder = MaterialDialog.Builder(context)
                            dialogBuilder.title("Confirm call")
                            dialogBuilder.content(stop.address.phone)
                            dialogBuilder.positiveText("Call")
                            dialogBuilder.negativeText("Cancel")
                            dialogBuilder.cancelable(true)
                            dialogBuilder.onPositive { materialDialog, dialogAction ->
                                startActivity(intent)
                            }
                            dialogBuilder.build().show()
                        }
                        R.id.action_fail -> {
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
                    }
                }
    }

    override fun onEventDialogItemSelected(event: EventNotDeliveredReason) {
        log.trace("SELECTEDITEAM VIA LISTENER")
    }

}

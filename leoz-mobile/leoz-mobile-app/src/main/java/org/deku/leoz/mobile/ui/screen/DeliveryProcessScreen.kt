package org.deku.leoz.mobile.ui.screen


import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
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
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_delivery_process.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import org.deku.leoz.mobile.ui.dialog.EventDialog
import org.deku.leoz.mobile.ui.fragment.StopDetailFragment
import org.deku.leoz.mobile.ui.fragment.StopProcessFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.StopItemViewModel
import org.deku.leoz.model.EventDelivered
import org.deku.leoz.model.EventDeliveredReason
import org.deku.leoz.model.EventNotDeliveredReason
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import sx.android.fragment.util.withTransaction

class DeliveryProcessScreen
    :
        ScreenFragment(),
        EventDialog.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()

    private lateinit var stop: Stop

    companion object {
        /**
         * Create instance with parameters. This pattern requires `retainInstance` to be set in `onCreate`!
         */
        fun create(stop: Stop): DeliveryProcessScreen {
            val f = DeliveryProcessScreen()
            f.stop = stop
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "Delivery Process"

        this.retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_delivery_process, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_list_item_1, parcelRefList)

        //endregion

        showInitFabButtons()

        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, StopDetailFragment.create(this.stop))
        }

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopItemViewModel(this.stop)
    }

    fun showInitFabButtons() {
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
        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_deliver_ok,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_check_circle,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_options)
                ),
                ActionItem(
                        id = R.id.action_deliver_fail,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_cancel_black,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_exception)
                )
        )
    }

    override fun onResume() {
        super.onResume()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        R.id.action_deliver_continue -> {
                            childFragmentManager.withTransaction {
                                it.replace(this.uxContainer.id, StopProcessFragment.create(this.stop))
                            }
                            showDeliverFabButtons()
                        }
                        R.id.ux_action_navigate -> {
                            val intent: Intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=${stop.address.street}+${stop.address.streetNo}+${stop.address.city}+${stop.address.zipCode}&mode=d")
                            )
                            startActivity(intent)
                        }
                        R.id.ux_action_contact -> {
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

    override fun onEventDialogItemSelected(event: EventNotDeliveredReason) {
        log.trace("SELECTEDITEAM VIA LISTENER")
    }

    fun startHandOver(event: EventDelivered) {
        (this.activity as DeliveryActivity).runServiceWorkflow(stop, event.reason)
    }

}

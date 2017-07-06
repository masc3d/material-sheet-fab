package org.deku.leoz.mobile.ui.screen


import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_delivery_process.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.model.FailureReason
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.vm.StopItemViewModel
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import org.deku.leoz.mobile.ui.fragment.StopDetailFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import sx.android.fragment.util.withTransaction

class DeliveryProcessScreen : ScreenFragment() {

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

        this.title ="Delivery Process"

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

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_deliver_ok,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_check_circle,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_options)
                ),
                ActionItem(
                        id = R.id.action_deliver_fail,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_information_outline,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_actions)
                ),
                ActionItem(
                        id = R.id.action_deliver_cancel,
                        colorRes = R.color.colorRed,
                        iconRes = R.drawable.ic_cancel_black,
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_exception)
                )
        )
        (this.activity as DeliveryActivity).showDeliverFabButtons()

        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, StopDetailFragment.create(this.stop))
        }

        val binding = DataBindingUtil.bind<ItemStopBinding>(this.uxStopItem)
        binding.stop = StopItemViewModel(this.stop)
    }

    override fun onResume() {
        super.onResume()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
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
                            showFailureReasons(delivery.allowedEvents)
                        }
                        R.id.ux_action_deliver_neighbour -> {

                        }
                        R.id.ux_action_deliver_postbox -> {

                        }
                        R.id.ux_action_deliver_recipient -> {
                            (this.activity as DeliveryActivity).showSignaturePad("Aufträge: ${stop.orders.count()}\nPakete: X\nEmpfänger: ${stop.address.line1}\nAngenommen von: ${stop.address.line1}")
                        }
                    }
                }

        /**
         * TODO: Action trigger on uxLabelNo TextView
         */
    }

}

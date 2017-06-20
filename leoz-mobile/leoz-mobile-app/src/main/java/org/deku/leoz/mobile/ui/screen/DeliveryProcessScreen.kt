package org.deku.leoz.mobile.ui.screen


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.item_stop.*
import kotlinx.android.synthetic.main.screen_delivery_process.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import org.deku.leoz.mobile.ui.fragment.StopDetailFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import sx.android.fragment.util.withTransaction
import sx.time.toCalendar
import java.text.SimpleDateFormat
import java.util.*

class DeliveryProcessScreen : ScreenFragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")

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

        this.retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.screen_delivery_process, container, false)
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
                        menu = this.activity.inflateMenu(R.menu.menu_deliver_fail)
                )
        )
        (this.activity as DeliveryActivity).showDeliverFabButtons()

        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, StopDetailFragment.create(this.stop))
        }

        /**
         * Set the values of the included layout 'item_stop'
         */
        this.uxRecipient.text = stop.address.addressLine1
        this.uxStreet.text = stop.address.street
        this.uxStreetNo.text = stop.address.streetNo
        this.uxCity.text = stop.address.city
        this.uxZip.text = stop.address.zipCode
        this.uxOrderCount.text = stop.orders.count().toString()
        this.uxParcelCount.text = "X"
        this.uxAppointmentFrom.text = simpleDateFormat.format(stop.appointment.dateFrom)
        this.uxAppointmentTo.text = simpleDateFormat.format(stop.appointment.dateTo)

        this.uxAppointmentClock.hour = stop.appointment.dateFrom.toCalendar().get(Calendar.HOUR)
        this.uxAppointmentClock.minute = stop.appointment.dateFrom.toCalendar().get(Calendar.MINUTE)
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

                        }
                        R.id.ux_action_cancel -> {

                        }
                        R.id.ux_action_fail -> {

                        }
                        R.id.ux_action_deliver_neighbour -> {

                        }
                        R.id.ux_action_deliver_postbox -> {

                        }
                        R.id.ux_action_deliver_recipient -> {
                            (this.activity as DeliveryActivity).showSignaturePad()
                        }
                    }
                }

        /**
         * TODO: Action trigger on uxLabelNo TextView
         */
    }
}

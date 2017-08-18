package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.screen_cash.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.process.DeliveryStop
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.OrderTaskViewModel
import org.deku.leoz.mobile.ui.vm.SectionViewModel
import org.deku.leoz.model.EventDeliveredReason
import org.jetbrains.anko.inputMethodManager
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.hideSoftInput
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleSectionableVmItem
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 */
class CashScreen : ScreenFragment<CashScreen.Parameters>() {

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var stopId: Int,
            var deliveryReason: EventDeliveredReason,
            var recipient: String = ""
    )

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()
    val decimalFormat = DecimalFormat("#0.00")

    private val deliveryStop: DeliveryStop by lazy {
        delivery.activeStop!!
    }

    private val stop: Stop by lazy {
        stopRepository.findById(this.parameters.stopId)
                ?: throw IllegalArgumentException("Illegal stop id [${this.parameters.stopId}]")
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

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(org.deku.leoz.mobile.R.layout.screen_cash, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_cash_collection)
        this.headerImage = R.drawable.img_money_a
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxCashValue.text = "${decimalFormat.format(delivery.activeStop?.cashAmountToCollect)} €"
        this.flexibleAdapterInstance.reset()
        this.uxOrderCashList.adapter = flexibleAdapter
        this.uxOrderCashList.layoutManager = LinearLayoutManager(context)

        flexibleAdapter.isLongPressDragEnabled = false
        flexibleAdapter.isHandleDragEnabled = false
        flexibleAdapter.isSwipeEnabled = false

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
                                title = "Cash-${this.getText(R.string.orders)}",
                                items = Observable.fromIterable(listOf(orders))
                        )
                ).also {
                    it.subItems = orders.map {
                        FlexibleSectionableVmItem<Any>(
                                view = R.layout.item_ordertask, //TODO: To be replaced by an item which includes the cash value (hide zip-code and city)
                                variable = BR.orderTask,
                                viewModel = OrderTaskViewModel(it.pickupTask)
                        )
                    }
                }
        )
        //endregion

        flexibleAdapter.setStickyHeaders(true)
        flexibleAdapter.showAllHeaders()
        flexibleAdapter.collapseAll()

        flexibleAdapter.currentItems.firstOrNull().also {
            flexibleAdapter.expand(it)
        }



        this.uxCashGiven.setOnEditorActionListener { textView, i, keyEvent ->
            val entered: Double? = this.uxCashGiven.text.toString().toDoubleOrNull()
            if (entered != null) {
                if (entered >= this.deliveryStop.cashAmountToCollect) {
                    this.uxCashChange.text = decimalFormat.format((entered - this.deliveryStop.cashAmountToCollect)).toString()
                    this.actionItems = listOf(
                            ActionItem(
                                    id = R.id.action_cash_continue,
                                    colorRes = R.color.colorPrimary,
                                    iconTintRes = android.R.color.white,
                                    iconRes = R.drawable.ic_delivery
                            )
                    )
                } else {
                    this.uxCashGiven.error = "Weniger eingegeben als notwendig!"
                }
            } else {
                this.uxCashGiven.error = "Nur nummerische Eingaben zulässig!"
            }
            this.context.inputMethodManager.hideSoftInput()
            true
        }
    }

    override fun onResume() {
        super.onResume()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_cash_continue -> {
                            this.getSignature()
                        }
                    }
                }
    }

    private fun getSignature() {
        when (this.parameters.deliveryReason) {
            EventDeliveredReason.NORMAL -> {
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
                } else {
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

            EventDeliveredReason.NEIGHBOR -> {
                this.activity.showScreen(NeighbourDeliveryScreen().also {
                    it.parameters = NeighbourDeliveryScreen.Parameters(
                            stopId = this.stop.id
                    )
                })
            }
        }

    }

}

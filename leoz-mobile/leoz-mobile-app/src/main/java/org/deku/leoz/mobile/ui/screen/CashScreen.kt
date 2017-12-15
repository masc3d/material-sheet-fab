package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.screen_cash.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.process.DeliveryStop
import org.deku.leoz.mobile.ui.Headers
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.OrderTaskViewModel
import org.deku.leoz.mobile.ui.vm.SectionViewModel
import org.jetbrains.anko.inputMethodManager
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.hideSoftInput
import sx.android.showSoftInput
import sx.android.ui.flexibleadapter.VmItem
import sx.android.ui.flexibleadapter.SimpleVmItem
import java.text.NumberFormat
import java.util.*

/**
 * Cash screen
 * @param target Event listener target fragment
 */
class CashScreen : ScreenFragment<Any>() {

    /** Screen listener */
    interface Listener {
        fun onCashScreenContinue()
    }

    private val listener by lazy {
        this.targetFragment as? Listener
                ?: this.parentFragment as? Listener
                ?: this.activity as? Listener
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    // Model classes
    private val tour: Tour by Kodein.global.lazy.instance()

    private val deliveryStop: DeliveryStop by lazy {
        tour.activeStop ?: throw IllegalStateException("Active delivery stop not set")
    }

    /** Cash amount to collect */
    private val cashAmountToCollect by lazy {
        this.deliveryStop.cashAmountToCollect
    }

    private val currencyCode by lazy {
        this.deliveryStop
    }

    /** Cash amount given */
    private var cashAmountGiven: Double = 0.0

    /** Indicates if given cash amount is sufficient */
    private val cashAmountSufficient: Boolean
        get() = this.cashAmountGiven >= this.cashAmountToCollect

    /** Cash amount to return */
    private val cashAmountToReturn: Double
        get() = (this.cashAmountGiven - this.cashAmountToCollect).let {
            when {
                it >= 0.0 -> it
                else -> 0.0
            }
        }

    /** Current locale currency format */
    private val currencyFormat by lazy { NumberFormat.getCurrencyInstance(recipientLocale).also {
        it.currency = Currency.getInstance(deliveryStop.cashCurrencyCode)
    } }

    val recipientLocale by lazy {
        this.deliveryStop.recipientCountryCode.toLocale()
    }

    private val flexibleAdapterInstance = LazyInstance<FlexibleAdapter<
            VmItem<
                    SectionViewModel<Any>, *>
            >>({
        FlexibleAdapter(listOf())
    })
    private val adapter get() = flexibleAdapterInstance.get()

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? =
            // Inflate the layout for this fragment
            inflater.inflate(org.deku.leoz.mobile.R.layout.screen_cash, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_cash_collection)
        this.headerImage = Headers.cash
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
        this.toolbarCollapsed = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxCashValue.text = this.currencyFormat.format(this.cashAmountToCollect)
        this.uxCashGiven.locale = recipientLocale

        //"${decimalFormat.format(delivery.activeStop?.cashAmountToCollect)} €"
        this.flexibleAdapterInstance.reset()
        this.uxOrderCashList.adapter = adapter
        this.uxOrderCashList.layoutManager = LinearLayoutManager(context)

        adapter.isLongPressDragEnabled = false
        adapter.isHandleDragEnabled = false
        adapter.isSwipeEnabled = false

        //region Orders
        val orders = this.deliveryStop.orders.blockingFirst()

        adapter.addItem(
                VmItem<SectionViewModel<Any>, Any>(
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
                        SimpleVmItem<Any>(
                                //TODO: To be replaced by an item which includes the cash value (hide zip-code and city)
                                view = R.layout.item_ordertask,
                                variable = BR.orderTask,
                                viewModel = OrderTaskViewModel(it.pickupTask)
                        )
                    }
                }
        )
        //endregion

        adapter.setStickyHeaders(true)
        adapter.showAllHeaders()
        adapter.collapseAll()

        adapter.currentItems.firstOrNull().also {
            adapter.expand(it)
        }

        // Action items
        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_continue,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_delivery,
                        iconTintRes = android.R.color.white,
                        visible = false
                )
        )

        // Initiali focus
        this.uxCashGiven.requestFocus()
        this.context.inputMethodManager.showSoftInput()
    }

    override fun onResume() {
        super.onResume()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_continue -> {
                            this.listener?.onCashScreenContinue()
                        }
                    }
                }

        val ovEditorAction = RxTextView.editorActions(this.uxCashGiven)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        val ovCashTextChanges = RxTextView.textChanges(this.uxCashGiven)
                .bindUntilEvent(this, FragmentEvent.PAUSE)

        ovCashTextChanges
                .subscribe {
                    this.cashAmountGiven = this.uxCashGiven.rawValue / 100.0

                    this.uxCashChange.text = this.currencyFormat.format(this.cashAmountToReturn)

                    // Update action items
                    this.actionItems = this.actionItems.apply {
                        first { it.id == R.id.action_continue }
                                .visible = this@CashScreen.cashAmountSufficient
                    }

                    this.uxCashGiven.error = null
                }

        ovEditorAction
                .subscribe {
                    this.context.inputMethodManager.hideSoftInput()

                    when (this@CashScreen.cashAmountSufficient) {
                        true -> this.uxCashGiven.error = null
                        else -> this.uxCashGiven.error = this.getString(R.string.cash_not_sufficient)
                    }
                }
    }
}

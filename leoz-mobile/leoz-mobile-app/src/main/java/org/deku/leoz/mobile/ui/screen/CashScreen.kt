package org.deku.leoz.mobile.ui.screen


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.screen_cash.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.vm.OrderTaskViewModel
import org.deku.leoz.mobile.ui.vm.SectionViewModel
import org.deku.leoz.model.EventDeliveredReason
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleSectionableVmItem


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

    private val stop: Stop by lazy {
        stopRepository.findById(this.parameters.stopId)
                ?: throw IllegalArgumentException("Illegal stop id [${this.parameters.stopId}]")
    }

    val cashValue: Double by lazy {
        0.0
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

        this.title = "Cash collection"
        this.headerImage = R.drawable.img_money_a
        this.scrollCollapseMode = ScrollCollapseModeType.None
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxCashValue.text = "$cashValue €"
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
                                view = R.layout.item_ordertask, //TODO: To be replaced by an item which shows the cash value (hide zipcode and city)
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
    }

}

package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import eu.davidea.flexibleadapter.FlexibleAdapter

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.process.Delivery
import org.slf4j.LoggerFactory
import android.support.annotation.CallSuper
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.screen_delivery_stop_list.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.ui.ScreenFragment
import sx.android.ui.flexibleadapter.FlexibleVmItem
import org.deku.leoz.mobile.ui.vm.StopViewModel
import org.deku.leoz.model.UnitNumber
import sx.LazyInstance
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.ui.flexibleadapter.customizeScrollBehavior

/**
 * Delivery stop list screen
 */
class DeliveryStopListScreen
    :
        ScreenFragment<Any>(),
        FlexibleAdapter.OnItemMoveListener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()
    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val tones: Tones by Kodein.global.lazy.instance()

    private val flexibleAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleVmItem<
                            StopViewModel>>>({
        val adapter = FlexibleAdapter(
                // Items
                delivery.pendingStops.blockingFirst().value
                        .map {
                            val item = FlexibleVmItem(
                                    view = R.layout.item_stop,
                                    variable = BR.stop,
                                    viewModel = StopViewModel(it)
                            )

                            item.isEnabled = true
                            item.isDraggable = true
                            item.isSwipeable = false

                            item
                        },
                // Listener
                this)

        adapter
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "Delivery Stops"
        this.aidcEnabled = true
        this.headerImage = R.drawable.img_street_1a

        this.toolbarCollapsed = true
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed
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
                    this.onAidcRead(it)
                }

        this.syntheticInputs = listOf(
                SyntheticInput(
                        name = "Parcels",
                        entries = this.deliveryList.loadedParcels.blockingFirst().value.map {
                            val unitNumber = UnitNumber.parse(it.number).value
                            SyntheticInput.Entry(
                                    symbologyType = SymbologyType.Interleaved25,
                                    data = unitNumber.label
                            )
                        }
                )
        )
    }

    //region Listener interface implementation
    override fun onActionStateChanged(p0: RecyclerView.ViewHolder?, p1: Int) {
        log.debug("ONACTIONSTATECHANGED")
        log.debug("ViewHolder [${p0.toString()}] Value [$p1]")
    }

    @CallSuper
    override fun onItemMove(p0: Int, p1: Int) {
        log.debug("ONITEMMOVE value [$p0] value [$p1]")
        // TODO: implement position change/persistence
    }

    override fun shouldMoveItem(p0: Int, p1: Int): Boolean {
        log.debug("SHOULDMOVEITEM value [$p0] value [$p1]")
        return true
    }

    private val onItemClickListener = FlexibleAdapter.OnItemClickListener { item ->
        log.debug("ONITEMCLICK")

        val stop = flexibleAdapter.getItem(item)?.viewModel?.stop

        if (stop != null) {
            activity.showScreen(
                    DeliveryStopDetailScreen().also {
                        it.parameters = DeliveryStopDetailScreen.Parameters(
                                stopId = stop.id
                        )
                    }
            )
        }

        true
    }
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_delivery_stop_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        this.uxStopList.adapter = flexibleAdapter
        this.uxStopList.layoutManager = LinearLayoutManager(context)

        flexibleAdapter.customizeScrollBehavior(
                scrollSpeed = 30.0F
        )

        flexibleAdapter.isLongPressDragEnabled = true
        flexibleAdapter.isHandleDragEnabled = true
        flexibleAdapter.isSwipeEnabled = true

        flexibleAdapter.addListener(onItemClickListener)
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        log.trace("AIDC READ $event")

        val result = UnitNumber.parseLabel(event.data)

        when {
            result.hasError -> {
                tones.warningBeep()

                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_barcode)
                        .build().show()
            }
            else -> {
                this.onInput(result.value)
            }
        }
    }

    private fun onInput(unitNumber: UnitNumber) {
        val stop = this.delivery.pendingStops.blockingFirst().value
                .flatMap { it.tasks }
                .firstOrNull {
                    it.order.parcels.any { it.number == unitNumber.value }
                }
                ?.stop

        if (stop == null) {
            tones.warningBeep()

            this.activity.snackbarBuilder
                    .message(R.string.error_no_corresponding_stop)
                    .build().show()

            return
        }

        this.activity.showScreen(
                DeliveryStopProcessScreen().also {
                    it.parameters = DeliveryStopProcessScreen.Parameters(stopId = stop.id)
                }
        )
    }
}

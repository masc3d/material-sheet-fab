package org.deku.leoz.mobile.ui.screen

import android.databinding.DataBindingUtil
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
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.screen_delivery_stop_list.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenDeliveryStopListBinding
import org.deku.leoz.mobile.databinding.ScreenVehicleloadingBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.Headers
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.vm.StopListStatisticsViewModel
import org.deku.leoz.mobile.ui.vm.StopViewModel
import org.deku.leoz.model.UnitNumber
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.ui.flexibleadapter.*
import sx.rx.ObservableRxProperty

/**
 * Delivery stop list screen
 */
class DeliveryStopListScreen
    :
        ScreenFragment<Any>() {

    interface Listener {
        fun onDeliveryStopListUnitNumberInput(unitNumber: UnitNumber)
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val tones: Tones by Kodein.global.lazy.instance()

    private val listener by lazy { this.activity as? Listener }

    // Model classes
    private val db: Database by Kodein.global.lazy.instance()
    private val schedulers: org.deku.leoz.mobile.rx.Schedulers by Kodein.global.lazy.instance()

    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val timer: sx.android.ui.Timer by Kodein.global.lazy.instance()
    private val timerEvent = timer.tickEvent
            .bindToLifecycle(this)

    private val editModeProperty = ObservableRxProperty(false)
    private var editMode by editModeProperty

    private val flexibleAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleVmItem<
                            *>>>({
        FlexibleAdapter(listOf())
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = getString(R.string.title_stop_list)
        this.aidcEnabled = true
        this.headerImage = Headers.street

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
                        entries = this.parcelRepository.entities.map {
                            val unitNumber = UnitNumber.parse(it.number).value
                            SyntheticInput.Entry(
                                    symbologyType = SymbologyType.Interleaved25,
                                    data = unitNumber.label
                            )
                        }
                )
        )

        // TODO: complete edit mode implementation
//        this.editModeProperty
//                .bindUntilEvent(this, FragmentEvent.PAUSE)
//                .subscribe {
//                    it.value.also { editMode ->
//                        // Update action items
//                        this.actionItems = this.actionItems.apply {
//                            first { it.id == R.id.action_edit }
//                                    .visible = !editMode
//
//                            first { it.id == R.id.action_done }
//                                    .visible = editMode
//                        }
//                    }
//                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_edit -> {
                            this.editMode = true
                        }

                        R.id.action_done -> {
                            this.editMode = false
                        }
                    }
                }

        // Sticky header may not show when fragment is resumed.
        // Workaround is to reset the sticky header flag
        this.flexibleAdapter.setStickyHeaders(false)
        this.flexibleAdapter.setStickyHeaders(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: ScreenDeliveryStopListBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.screen_delivery_stop_list,
                container,
                false)

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: complete edit mode implementation
//        this.actionItems = listOf(
//                ActionItem(
//                        id = R.id.action_edit,
//                        iconRes = R.drawable.ic_pencil,
//                        colorRes = R.color.colorAccent
//                ),
//                ActionItem(
//                        id = R.id.action_done,
//                        iconRes = R.drawable.ic_finish,
//                        iconTintRes = android.R.color.white,
//                        colorRes = R.color.colorPrimary,
//                        visible = false
//                )
//        )

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

        flexibleAdapter.setStickyHeaders(true)
        flexibleAdapter.showAllHeaders()

        flexibleAdapter.addListener(FlexibleAdapter.OnItemClickListener { item ->
            log.trace("ONITEMCLICK")

            val viewModel = flexibleAdapter.getItem(item)?.viewModel

            when (viewModel) {
                is StopViewModel -> {
                    val stop = viewModel.stop

                    activity.showScreen(
                            DeliveryStopDetailScreen().also {
                                it.parameters = DeliveryStopDetailScreen.Parameters(
                                        stopId = stop.id
                                )
                            }
                    )
                }
            }

            true
        })

        flexibleAdapter.addListener(object : FlexibleAdapter.OnItemMoveListener {
            override fun onActionStateChanged(p0: RecyclerView.ViewHolder?, p1: Int) {
            }

            override fun onItemMove(fromPosition: Int, toPosition: Int) {
                log.trace("ONITEMMOVE value [$fromPosition] value [$toPosition]")
            }

            override fun shouldMoveItem(fromPosition: Int, toPosition: Int): Boolean {
                log.trace("ONITEMSHOULDMOVE value [$fromPosition] value [$toPosition]")
                return true
            }

        })

        flexibleAdapter.addListener(FlexibleAdapter.OnUpdateListener { item ->
            log.trace("ONITEMUPDATE")
        })

        // Items
        flexibleAdapter.addItem(
                FlexibleHeaderVmItem(
                        view = R.layout.view_delivery_stop_list_stats,
                        variable = BR.stats,
                        viewModel = StopListStatisticsViewModel(
                                stops = this.delivery.pendingStops.blockingFirst().value,
                                timerEvent = this.timerEvent)
                ).also {
                    it.isSelectable = false
                    it.isDraggable = false
                    it.isEnabled = false
                }
        )

        flexibleAdapter.addItems(flexibleAdapter.itemCount, delivery.pendingStops.blockingFirst().value
                .map {
                    val item = FlexibleVmItem(
                            view = R.layout.item_stop,
                            variable = BR.stop,
                            viewModel = StopViewModel(
                                    stop = it,
                                    timerEvent = this.timerEvent),
                            handleViewId = R.id.uxHandle
                    )

                    item.isEnabled = true
                    item.isDraggable = true
                    item.isSwipeable = false

                    item.itemReleasedEvent
                            .bindUntilEvent(this@DeliveryStopListScreen, FragmentEvent.PAUSE)
                            .subscribe { position ->
                                // Get the item it was moved after
                                val previousItem = when {
                                    position > 0 -> flexibleAdapter.getItem(position - 1)
                                    else -> null
                                }

                                val previousItemViewModel: StopViewModel? = previousItem?.viewModel as? StopViewModel

                                if (previousItem == null || previousItemViewModel != null) {
                                    // Update entity stop postiion acoordingly
                                    db.store.withTransaction {
                                        stopRepository
                                                .move(stop = item.viewModel.stop, after = previousItemViewModel?.stop)
                                                .blockingAwait()
                                    }
                                            .subscribeOn(schedulers.database)
                                            .subscribe()
                                }
                            }

                    item
                })
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
        this.listener?.onDeliveryStopListUnitNumberInput(unitNumber)
    }
}

package org.deku.leoz.mobile.ui.screen

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.tinsuke.icekick.extension.serialState
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.screen_delivery_stop_list.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenDeliveryStopListBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Feedback
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.model.entity.address
import org.deku.leoz.mobile.model.entity.appointmentEnd
import org.deku.leoz.mobile.model.process.Tour
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.Headers
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.extension.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.StopListStatisticsViewModel
import org.deku.leoz.mobile.ui.vm.StopViewModel
import org.deku.leoz.model.*
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.SimpleVmHeaderItem
import sx.android.ui.flexibleadapter.SimpleVmItem
import sx.android.ui.flexibleadapter.VmHolder
import sx.android.ui.flexibleadapter.VmItem
import sx.android.ui.flexibleadapter.ext.customizeScrollBehavior
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
    private val feedback: Feedback by Kodein.global.lazy.instance()

    private val listener by lazy { this.activity as? Listener }

    // Model classes
    private val db: Database by Kodein.global.lazy.instance()

    private val tour: Tour by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val timer: sx.android.ui.Timer by Kodein.global.lazy.instance()
    private val timerEvent = timer.tickEvent
            .bindToLifecycle(this)

    private val editModeProperty = ObservableRxProperty(false)
    /** Edit mode */
    private var editMode by editModeProperty

    private var stopTypeState by serialState(Stop.State.PENDING)

    private val stopTypeProperty = ObservableRxProperty<Stop.State>(this.stopTypeState)
    /** Stop type */
    private var stopType by stopTypeProperty

    private val adapterInstance = LazyInstance<
            FlexibleAdapter<
                    VmItem<*, Any>>>({
        FlexibleAdapter(listOf())
    })
    private val adapter get() = adapterInstance.get()

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
                            val unitNumber = DekuUnitNumber.parse(it.number).value
                            SyntheticInput.Entry(
                                    symbologyType = SymbologyType.Interleaved25,
                                    data = unitNumber.label
                            )
                        }
                )
        )

        // Update action button visibility
        Observable.combineLatest(
                this.stopTypeProperty,
                this.editModeProperty,
                BiFunction { _: Any, _: Any -> Unit }
        )
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    // Update action items
                    this.actionItems = this.actionItems.apply {
                        first { it.id == R.id.action_edit }
                                .visible = !editMode && stopType == Stop.State.PENDING

                        first { it.id == R.id.action_done }
                                .visible = editMode

                        first { it.id == R.id.action_cancel }
                                .visible = editMode

                        first { it.id == R.id.action_sort }
                                .visible = editMode

                        first { it.id == R.id.action_delivery_list_show_pending }
                                .visible = !editMode &&
                                stopType == Stop.State.CLOSED &&
                                tour.pendingStops.blockingFirst().value.count() > 0

                        first { it.id == R.id.action_delivery_list_show_closed }
                                .visible = !editMode &&
                                stopType == Stop.State.PENDING &&
                                tour.closedStops.blockingFirst().value.count() > 0
                    }
                }

        this.stopTypeProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.stopTypeState = it.value

                    this@DeliveryStopListScreen.updateStops()
                }

        this.editModeProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    it.value.also { editMode ->
                        this.aidcReader.enabled = !editMode

                        this.adapter.allBoundViewHolders
                                .mapNotNull { it as? VmHolder }
                                .forEach {
                                    it.beginDelayedTransition()
                                }

                        this.adapter.currentItems
                                .asSequence()
                                .mapNotNull { it.viewModel as? StopViewModel }
                                .forEach {
                                    it.editMode = editMode
                                }
                    }
                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_edit -> {
                            this.editMode = true
                        }

                        R.id.action_delivery_list_show_pending -> {
                            this.stopType = Stop.State.PENDING
                        }

                        R.id.action_delivery_list_show_closed -> {
                            this.stopType = Stop.State.CLOSED
                        }

                        R.id.action_sort_zip_asc -> {
                            this.updateAdapterPositions(
                                    this.tour.pendingStops
                                            .blockingFirst().value
                                            .sortedBy { it.address.zipCode }
                                            .also {
                                                it.forEachIndexed { index, stopEntity ->
                                                    stopEntity.position = index.toDouble()
                                                }
                                            }
                            )
                        }

                        R.id.action_sort_zip_desc -> {
                            this.updateAdapterPositions(
                                    this.tour.pendingStops
                                            .blockingFirst().value
                                            .sortedByDescending { it.address.zipCode }
                                            .also {
                                                it.forEachIndexed { index, stopEntity ->
                                                    stopEntity.position = index.toDouble()
                                                }
                                            }
                            )
                        }

                        R.id.action_sort_appointment_asc -> {
                            this.updateAdapterPositions(
                                    this.tour.pendingStops
                                            .blockingFirst().value
                                            .sortedWith(
                                                    compareBy<StopEntity> { it.appointmentEnd }
                                                            .thenByDescending { it.tasks.any { it.isFixedAppointment } }
                                            )
                                            .also {
                                                it.forEachIndexed { index, stopEntity ->
                                                    stopEntity.position = index.toDouble()
                                                }
                                            }
                            )
                        }

                        R.id.action_sort_distance -> {
                            // TODO add support for sorting by geo distance (need testdata)
                        }

                        R.id.action_done -> {
                            this.stopRepository.entities.forEach {
                                // Persist all positional changes
                                this.stopRepository
                                        .updateAll()
                                        .subscribeOn(db.scheduler)
                                        .subscribe()
                            }

                            this.tour.sendUpdate()
                                    .subscribe()

                            this.editMode = false
                        }

                        R.id.action_cancel -> {
                            this.editMode = false

                            db.store.withTransaction {
                                stopRepository.entities.forEach {
                                    refresh(it, StopEntity.POSITION)
                                }
                            }
                                    .toCompletable()
                                    .subscribeOn(db.scheduler)
                                    .observeOnMainThread()
                                    .subscribe {
                                        this.updateAdapterPositions(
                                                this.tour.pendingStops
                                                        .blockingFirst()
                                                        .value)
                                    }
                        }
                    }
                }

        // Sticky header may not show when fragment is resumed.
        // Workaround is to reset the sticky header flag
        this.adapter.setStickyHeaders(false)
        this.adapter.setStickyHeaders(true)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.actionItems = listOf(
                // Regular mode actions
                ActionItem(
                        id = R.id.action_edit,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_pencil,
                        iconTintRes = android.R.color.white
                ),
                ActionItem(
                        id = R.id.action_delivery_list_show_closed,
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_stop_list_closed,
                        iconTintRes = android.R.color.white
                ),
                ActionItem(
                        id = R.id.action_delivery_list_show_pending,
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_stop_list,
                        iconTintRes = android.R.color.white,
                        visible = false
                ),

                // Edit mode actions
                ActionItem(
                        id = R.id.action_cancel,
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_circle_cancel,
                        iconTintRes = android.R.color.white,
                        alignEnd = false,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_done,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_sort,
                        colorRes = R.color.colorAccent,
                        iconRes = android.R.drawable.ic_menu_sort_by_size,
                        iconTintRes = android.R.color.black,
                        menu = this.activity.inflateMenu(R.menu.menu_delivery_list_sort),
                        visible = false
                )
        )

        // Flexible adapter needs to be re-created with views
        adapterInstance.reset()

        this.uxStopList.adapter = adapter
        this.uxStopList.layoutManager = LinearLayoutManager(context)

        adapter.customizeScrollBehavior(
                scrollSpeed = 30.0F
        )

        adapter.isSwipeEnabled = true

        adapter.setStickyHeaders(true)
        adapter.showAllHeaders()

        adapter.addListener(FlexibleAdapter.OnItemClickListener { pos ->
            // Ignore click/selection in edit mode
            if (this.editMode)
                return@OnItemClickListener true

            adapter.getItem(pos)
                    ?.viewModel
                    ?.also { viewModel ->
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
                    }

            true
        })

        adapter.addListener(FlexibleAdapter.OnItemLongClickListener { pos ->
            if (this.stopType == Stop.State.CLOSED) {
                // Show re-open dialog
                MaterialDialog.Builder(context)
                        .title(R.string.dialog_title_stop_reopen)
                        .content(R.string.dialog_content_stop_reopen)
                        .positiveText(android.R.string.yes)
                        .negativeText(android.R.string.no)
                        .onPositive { dialog, _ ->
                            dialog.dismiss()
                            adapter.getItem(pos)
                                    ?.viewModel
                                    ?.also { viewModel ->
                                        when (viewModel) {
                                            is StopViewModel -> {
                                                this.db.store.withTransaction {
                                                    val stop = viewModel.stop as StopEntity

                                                    // Re-open stop
                                                    stop.state = Stop.State.PENDING

                                                    // Move re-activated stop to top
                                                    stopRepository.move(stop, null)
                                                            .blockingGet()

                                                    stopRepository
                                                            .update(stop)
                                                            .blockingGet()
                                                }
                                                        .subscribeOn(db.scheduler)
                                                        .observeOnMainThread()
                                                        .subscribeBy(onSuccess = {
                                                            // Switch to pending view when done
                                                            this.stopType = Stop.State.PENDING
                                                        })
                                            }
                                        }
                                    }
                        }
                        .build()
                        .show()
            }
        })

        adapter.addListener(object : FlexibleAdapter.OnItemMoveListener {
            override fun onActionStateChanged(p0: RecyclerView.ViewHolder?, p1: Int) {
            }

            override fun onItemMove(fromPosition: Int, toPosition: Int) {
            }

            override fun shouldMoveItem(fromPosition: Int, toPosition: Int): Boolean =
                    // Prevent move before statistics header
                    toPosition >= this@DeliveryStopListScreen.adapterFirstStopItemIndex

        })

        val pendingStopCount = this.tour.pendingStops.blockingFirst().value.count()
        val closedStopCount = this.tour.closedStops.blockingFirst().value.count()

        when (this.stopType) {
            Stop.State.PENDING -> {
                if (pendingStopCount == 0)
                    this.stopType = Stop.State.CLOSED
            }

            Stop.State.CLOSED -> {
                if (closedStopCount == 0)
                    this.stopType = Stop.State.PENDING
            }

            else -> Unit
        }

        this.editMode = false
    }

    fun updateStops() {
        val stops = when (this.stopType) {
            Stop.State.CLOSED -> tour.closedStops.blockingFirst().value
            else -> tour.pendingStops.blockingFirst().value
        }

        adapter.clear()

        // Enable item drag if applicable
        (this.stopType == Stop.State.PENDING).also {
            adapter.isLongPressDragEnabled = it
            adapter.isHandleDragEnabled = it
        }

        // Items
        adapter.addItem(
                SimpleVmHeaderItem<StopListStatisticsViewModel>(
                        view = R.layout.view_delivery_stop_list_stats,
                        variable = BR.stats,
                        viewModel = StopListStatisticsViewModel(
                                context = this.context,
                                stops = stops,
                                timerEvent = this.timerEvent)
                ).also {
                    it.isSelectable = false
                    it.isDraggable = false
                    it.isEnabled = false
                }
        )

        adapter.addItems(
                // Position
                adapter.itemCount,
                // Items
                stops.map {
                    val item = SimpleVmItem(
                            view = R.layout.item_stop,
                            variable = BR.stop,
                            viewModel = StopViewModel(
                                    isStateVisible = this.stopType == Stop.State.CLOSED,
                                    stop = it,
                                    timerEvent = this.timerEvent),
                            dragHandleViewId = R.id.uxHandle
                    )

                    item.isEnabled = true
                    item.isDraggable = true
                    item.isSwipeable = false

                    if (this.stopType == Stop.State.PENDING) {
                        // Item move / position change event handler
                        item.itemReleasedEvent
                                .bindUntilEvent(this@DeliveryStopListScreen, FragmentEvent.PAUSE)
                                .subscribe { position ->
                                    // Get the item it was moved after
                                    val previousItem = when {
                                        position >= this.adapterFirstStopItemIndex -> adapter.getItem(position - 1)
                                        else -> null
                                    }

                                    val previousItemViewModel: StopViewModel? = previousItem?.viewModel as? StopViewModel

                                    // Update entity stop position acoordingly
                                    db.store.withTransaction {
                                        stopRepository
                                                .move(
                                                        stop = item.viewModel.stop,
                                                        after = previousItemViewModel?.stop,
                                                        // Only persist immedaitely when not in edit mode
                                                        persist = !this@DeliveryStopListScreen.editMode
                                                )
                                                .blockingAwait()
                                    }
                                            .subscribeOn(db.scheduler)
                                            .subscribe()

                                    // When not in edit mode, Send stop list order update on every move
                                    if (!this@DeliveryStopListScreen.editMode)
                                        this.tour.sendUpdate()
                                                .subscribe()
                                }
                    }

                    item
                })
    }

    /**
     * First adpater item index which is a stop
     */
    val adapterFirstStopItemIndex by lazy {
        this.adapter.currentItems.firstOrNull {
            it.viewModel is StopViewModel
        }
                ?.let {
                    this.adapter.getGlobalPositionOf(it)
                }
                ?: this.adapter.currentItems.count()
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        log.trace("AIDC READ $event")

        val result: Result<Parcel> = UnitNumber.parseLabel(event.data)

        when {
            result.hasError -> {
                feedback.warning()

                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_barcode)
                        .build().show()
            }
            else -> {
                this.onInput(unitNumber = result.value.number)
            }
        }
    }

    private fun onInput(unitNumber: UnitNumber) {
        this.listener?.onDeliveryStopListUnitNumberInput(unitNumber)
    }

    /**
     * Updates adapter item positions from stop entities
     */
    private fun updateAdapterPositions(stops: List<StopEntity>) {
        // Restore positions
        stops
                .forEachIndexed { index, pendingStop ->
                    val item = adapter.currentItems.first {
                        it.viewModel.let {
                            it is StopViewModel && it.stop == pendingStop
                        }
                    }

                    val currentPosition = adapter.getGlobalPositionOf(item)
                    val desiredPosition = index + this.adapterFirstStopItemIndex

                    if (currentPosition != desiredPosition) {
                        adapter.moveItem(
                                currentPosition,
                                desiredPosition
                        )
                    }
                }
    }
}

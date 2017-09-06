package org.deku.leoz.mobile.ui.screen

import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.screen_vehicleunloading.*
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenVehicleunloadingBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.model.process.VehicleUnloading
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.CounterViewModel
import org.deku.leoz.mobile.ui.vm.ParcelViewModel
import org.deku.leoz.mobile.ui.vm.SectionViewModel
import org.deku.leoz.mobile.ui.vm.SectionsAdapter
import org.deku.leoz.model.UnitNumber
import org.deku.leoz.model.assertAny
import org.deku.leoz.service.internal.DeliveryListService
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.databinding.toField
import sx.android.inflateMenu
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleSectionableVmItem
import sx.format.format
import java.util.concurrent.ExecutorService

/**
 * Vehicle unloading screen
 */
class VehicleUnloadingScreen :
        ScreenFragment<Any>(),
        BaseCameraScreen.Listener {
    private val log = LoggerFactory.getLogger(this.javaClass)

    interface Listener {
        fun onVehicleUnloadingFinalized()
    }

    val listener by lazy { this.activity as? Listener }

    /**
     * Created by masc on 10.07.17.
     */
    class StatsViewModel(
            val vehicleUnloading: VehicleUnloading
    ) : BaseObservable() {

        val parcelCounter = CounterViewModel(
                drawableRes = R.drawable.ic_package_variant_closed,
                amount = this.vehicleUnloading.parcelAmount.map { it.toString() }.toField(),
                totalAmount = this.vehicleUnloading.parcelTotalAmount.map { it.toString() }.toField()
        )

        val weightCounter = CounterViewModel(
                drawableRes = R.drawable.ic_weight_scale,
                amount = this.vehicleUnloading.weight.map { "${it.format(2)}kg" }.toField(),
                totalAmount = this.vehicleUnloading.totalWeight.map { "${it.format(2)}kg" }.toField()
        )
    }

    private val executorService: ExecutorService by Kodein.global.lazy.instance()
    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    private val db: Database by Kodein.global.lazy.instance()

    private val tones: Tones by Kodein.global.lazy.instance()
    private val aidcReader: sx.android.aidc.AidcReader by Kodein.global.lazy.instance()

    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()
    private val deliveryListService: DeliveryListService by Kodein.global.lazy.instance()

    private val vehicleUnloading: VehicleUnloading by Kodein.global.lazy.instance()

    /** The current/most recently selected damaged parcel */
    private var currentDamagedParcel: ParcelEntity? = null

    // region Sections
    val unloadedSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_truck_unloading,
                color = android.R.color.black,
                background = R.drawable.section_background_green,
                title = this.getText(R.string.unloaded).toString(),
                items = this.deliveryList.pendingParcels.map { it.value }
        )
    }

    val damagedSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_damaged,
                color = android.R.color.black,
                background = R.drawable.section_background_accent,
                title = this.getString(R.string.event_reason_damaged),
                items = this.deliveryList.damagedParcels.map { it.value }
        )
    }

    val pendingSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_format_list_bulleted,
                background = R.drawable.section_background_grey,
                expandOnSelection = true,
                title = getString(R.string.pending),
                items = this.deliveryList.loadedParcels.map { it.value }
        )
    }

    val missingSection by lazy {
        SectionViewModel<ParcelEntity>(
                icon = R.drawable.ic_missing,
                background = R.drawable.section_background_grey,
                showIfEmpty = false,
                expandOnSelection = true,
                title = getString(R.string.missing),
                items = this.deliveryList.missingParcels.map { it.value }
        )
    }
    //endregion

    fun SectionViewModel<ParcelEntity>.toFlexibleItem()
            : FlexibleExpandableVmItem<SectionViewModel<ParcelEntity>, Any> {

        return FlexibleExpandableVmItem<SectionViewModel<ParcelEntity>, Any>(
                view = R.layout.item_section_header,
                variable = BR.header,
                viewModel = this
        ).also {
            it.isSelectable = true
        }
    }

    fun ParcelEntity.toFlexibleItem()
            : FlexibleSectionableVmItem<ParcelViewModel> {

        return FlexibleSectionableVmItem(
                view = R.layout.item_parcel_card,
                variable = BR.parcel,
                viewModel = ParcelViewModel(this)
        )
    }

    private val parcelListAdapterInstance = LazyInstance<SectionsAdapter>({
        val adapter = SectionsAdapter()

        adapter.addSection(
                sectionVmItemProvider = { this.unloadedSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter.addSection(
                sectionVmItemProvider = { this.pendingSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter.addSection(
                sectionVmItemProvider = { this.missingSection.toFlexibleItem() },
                vmItemProvider = { it.toFlexibleItem() }
        )

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = this.getText(R.string.vehicle_unloading).toString()
        this.headerImage = R.drawable.img_parcels_1a
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed

        this.aidcEnabled = true
    }

    override fun onCreateView(inflater: android.view.LayoutInflater,
                              container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {

        val binding: ScreenVehicleunloadingBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.screen_vehicleunloading,
                container, false)

        // Setup bindings
        binding.stats = StatsViewModel(this.vehicleUnloading)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxRecyclerView.adapter = parcelListAdapter
        this.uxRecyclerView.layoutManager = LinearLayoutManager(context)

        this.menu = this.inflateMenu(R.menu.menu_vehicleunloading).also {
            if (this.debugSettings.enabled) {
                it.add(0, R.id.action_vehicle_unloading_dev_mark_all_unloaded, 0, "Mark all as unloaded").also {
                    it.setIcon(R.drawable.ic_dev)
                }
            }
        }

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_vehicle_unloading_finished,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white
                ),
                ActionItem(
                        id = R.id.action_vehicle_unloading_unload,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_truck_unloading,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_vehicle_unloading_damaged,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_damaged,
                        visible = true
                )
        )
    }

    override fun onDestroyView() {
        log.trace("DESTROY VIEW")
        this.parcelListAdapter.dispose()
        this.parcelListAdapterInstance.reset()

        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        this.currentDamagedParcel = null

        aidcReader.decoders.set(
                Interleaved25Decoder(true, 6, 12),
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

        //region Activity events
        this.activity.menuItemEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it.itemId) {
                        R.id.action_reset -> {
                            db.store.withTransaction {
                                orderRepository.removeAll()
                                        .blockingAwait()
                            }
                                    .subscribeOn(Schedulers.computation())
                                    .subscribe()
                        }

                        R.id.action_vehicle_unloading_dev_mark_all_unloaded -> {
                            db.store.withTransaction {
                                select(ParcelEntity::class)
                                        .where(ParcelEntity.STATE.eq(Parcel.State.PENDING))
                                        .get()
                                        .forEach {
                                            it.state = Parcel.State.LOADED
                                            parcelRepository.update(it).blockingGet()
                                        }
                            }
                                    .subscribeOn(Schedulers.computation())
                                    .subscribe()
                        }
                    }
                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_vehicle_unloading_damaged -> {
                            this.parcelListAdapter.addSection(
                                    sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                    vmItemProvider = { it.toFlexibleItem() }
                            )

                            this.parcelListAdapter.selectedSection = this.damagedSection
                        }

                        R.id.action_vehicle_unloading_unload -> {
                            this.parcelListAdapter.selectedSection = this.unloadedSection
                        }

                        R.id.action_vehicle_unloading_finished -> {
                            MaterialDialog.Builder(context)
                                    .title(R.string.vehicle_unloading_finalize_dialog_title)
                                    .content(R.string.vehicle_unloading_finalize_dialog)
                                    .negativeText(R.string.no_go_back)
                                    .positiveText(android.R.string.yes)
                                    .onPositive { _, _ ->
                                        this.vehicleUnloading
                                                .finalize()
                                                .observeOnMainThread()
                                                .subscribeBy(
                                                        onComplete = {
                                                            this.listener?.onVehicleUnloadingFinalized()
                                                        },
                                                        onError = {
                                                            log.error(it.message, it)
                                                        }
                                                )
                                    }
                                    .show()
                        }

                        else -> log.warn("Unhandled ActionEvent [$it]")
                    }
                }
        //endregion

        this.parcelListAdapter.selectedSection = this.unloadedSection
        this.parcelListAdapter.selectedSectionProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    val section = it.value

                    this.accentColor = when (section) {
                        unloadedSection -> R.color.colorGreen
                        damagedSection -> R.color.colorAccent
                        else -> R.color.colorGrey
                    }

                    when (section) {
                        this.unloadedSection -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_vehicle_unloading_unload }
                                        .visible = false

                                first { it.id == R.id.action_vehicle_unloading_damaged }
                                        .visible = true
                            }
                        }
                        else -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_vehicle_unloading_unload }
                                        .visible = true

                                first { it.id == R.id.action_vehicle_unloading_damaged }
                                        .visible = false
                            }
                        }
                    }
                }

        // Damaged parcels
        Observable.combineLatest(
                this.deliveryList.damagedParcels,
                // Also fire when selected section changes */
                this.parcelListAdapter.selectedSectionProperty.filter {
                    it.value != this.damagedSection
                },

                BiFunction { a: Any, b: Any ->
                    this.deliveryList.damagedParcels.map { it.value }.blockingFirst()
                }
        )
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOnMainThread()
                .subscribe {
                    if (it.count() > 0) {
                        this.parcelListAdapter.addSection(
                                sectionVmItemProvider = { this.damagedSection.toFlexibleItem() },
                                vmItemProvider = { it.toFlexibleItem() }
                        )
                    } else {
                        this.parcelListAdapter.removeSection(this.damagedSection)

                        if (this.parcelListAdapter.selectedSection == null) {
                            this.parcelListAdapter.selectedSection = this.unloadedSection
                        }
                    }
                }

        //region Synthetic inputs
        run {
            // Synthetic inputs for parcels translated live from entity store
            val ovParcels = this.parcelRepository.entitiesProperty
                    .map {
                        SyntheticInput(
                                name = "Parcels",
                                entries = it.value.map {
                                    val unitNumber = UnitNumber.parse(it.number).value
                                    SyntheticInput.Entry(
                                            symbologyType = SymbologyType.Interleaved25,
                                            data = unitNumber.label
                                    )
                                }
                        )
                    }

            ovParcels
                    .bindUntilEvent(this, FragmentEvent.PAUSE)
                    .subscribe {
                        this.syntheticInputs = listOf(it)
                    }

        }
        //endregion
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        log.trace("AIDC READ $event")

        val result = Observable.concat(
                Observable.fromCallable {
                    UnitNumber
                            .parseLabel(event.data)
                            .assertAny(
                                    UnitNumber.Type.Parcel,
                                    UnitNumber.Type.Bag)
                },
                Observable.empty()
        )
                .takeUntil { !it.hasError }
                .last(Result(error = IllegalArgumentException("Invalid barcode")))
                .blockingGet()

        when {
            result.hasError -> {
                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_barcode)
                        .build().show()
            }
            else -> {
                val resultValue = result.value

                when (resultValue) {
                    is UnitNumber -> {
                        this.onInput(resultValue)
                    }
                }
            }
        }
    }

    /**
     * On unit number input
     */
    fun onInput(unitNumber: UnitNumber) {
        log.trace("Unit number input ${unitNumber.value}")

        val parcel = this.parcelRepository.entities.firstOrNull { it.number == unitNumber.value }

        when {
            parcel != null -> {
                // Parcel fonud -> process
                this.onParcel(parcel)
            }
            else -> {
                tones.errorBeep()

                this.activity.snackbarBuilder
                        .message(R.string.error_invalid_parcel)
                        .build().show()
            }
        }
    }

    /**
     * On valid parcel entry
     */
    fun onParcel(parcel: ParcelEntity) {
        when (parcelListAdapter.selectedSection) {
            damagedSection -> {
                if (parcel.isDamaged) {
                    this.tones.warningBeep()
                    this.aidcReader.enabled = false

                    MaterialDialog.Builder(this.context)
                            .title(R.string.question_remove_damaged_status_title)
                            .content(R.string.question_remove_damaged_status)
                            .negativeText(getString(android.R.string.no))
                            .positiveText(getString(android.R.string.yes))
                            .dismissListener {
                                this.aidcReader.enabled = true
                            }
                            .onPositive { _, _ ->
                                // Removed damaged parcel status
                                parcel.isDamaged = false

                                this.parcelRepository.update(parcel)
                                        .subscribeOn(Schedulers.computation())
                                        .subscribe()
                            }
                            .show()
                } else {
                    this.currentDamagedParcel = parcel

                    /** Show camera screen */
                    this.activity.showScreen(DamagedParcelCameraScreen().also {
                        it.setTargetFragment(this, 0)
                        it.parameters = DamagedParcelCameraScreen.Parameters(
                                parcelId = parcel.id
                        )
                    })
                }
            }
            else -> {
                if (parcel.state == Parcel.State.LOADED) {
                    parcel.state = Parcel.State.PENDING
                    this.parcelRepository.update(parcel)
                            .subscribeOn(Schedulers.computation())
                            .subscribe()
                }

                this.parcelListAdapter.selectedSection = unloadedSection
            }
        }
    }

    override fun onCameraScreenImageSubmitted(sender: Any, jpeg: ByteArray) {
        this.currentDamagedParcel?.also { parcel ->
            parcelRepository.markDamaged(
                    parcel = parcel,
                    jpegPictureData = jpeg
            )
                    .subscribeOn(Schedulers.computation())
                    .subscribe()
        }
    }
}

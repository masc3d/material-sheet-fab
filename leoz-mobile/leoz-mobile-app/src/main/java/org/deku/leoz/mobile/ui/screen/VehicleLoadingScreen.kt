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
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.joinToString
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.screen_vehicleloading.*
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenVehicleloadingBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.rx.toHotRestObservable

import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.composeAsRest
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.DekuDeliveryListNumber
import org.deku.leoz.model.UnitNumber
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.DeliveryListService
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.databinding.toField
import sx.android.inflateMenu
import sx.android.rx.observeOnMainThread
import sx.format.format
import java.util.concurrent.ExecutorService

/**
 * Vehicle loading screen
 */
class VehicleLoadingScreen : ScreenFragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Created by masc on 10.07.17.
     */
    class StatsViewModel(
            val deliveryList: DeliveryList
    )
        : BaseObservable() {

        val stopCounter = CounterViewModel(
                drawableRes = R.drawable.ic_location,
                amount = this.deliveryList.stopAmount.map { it.toString() }.toField(),
                totalAmount = this.deliveryList.stopTotalAmount.map { it.toString() }.toField()
        )

        val orderCounter = CounterViewModel(
                drawableRes = R.drawable.ic_file_document,
                amount = this.deliveryList.orderAmount.map { it.toString() }.toField(),
                totalAmount = this.deliveryList.orderTotalAmount.map { it.toString() }.toField()
        )

        val parcelCounter = CounterViewModel(
                drawableRes = R.drawable.ic_package_variant_closed,
                amount = this.deliveryList.parcelAmount.map { it.toString() }.toField(),
                totalAmount = this.deliveryList.parcelTotalAmount.map { it.toString() }.toField()
        )

        val weightCounter = CounterViewModel(
                drawableRes = R.drawable.ic_scale,
                amount = this.deliveryList.weight.map { "${it.format(2)}kg" }.toField(),
                totalAmount = this.deliveryList.totalWeight.map { "${it.format(2)}kg" }.toField()
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

    // region Sections
    val loadedSection by lazy {
        ParcelSectionViewModel(
                icon = R.drawable.ic_truck,
                color = R.color.colorGreen,
                background = R.drawable.section_background_green,
                title = this.getText(R.string.loaded).toString(),
                parcels = this.deliveryList.loadedParcels.map { it.value }
                        .bindToLifecycle(this)
        )
    }

    val damagedSection by lazy {
        ParcelSectionViewModel(
                icon = R.drawable.ic_damaged,
                color = R.color.colorAccent,
                background = R.drawable.section_background_accent,
                title = this.getText(R.string.damaged).toString(),
                parcels = this.deliveryList.damagedParcels.map { it.value }
                        .bindToLifecycle(this)
        )
    }

    val pendingSection by lazy {
        ParcelSectionViewModel(
                icon = R.drawable.ic_format_list_bulleted,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                isSelectable = false,
                showIfEmpty = false,
                title = this.getText(R.string.pending).toString(),
                parcels = this.deliveryList.pendingParcels.map { it.value }
                        .bindToLifecycle(this)
        )
    }

    val missingSection by lazy {
        ParcelSectionViewModel(
                icon = R.drawable.ic_missing,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                isSelectable = false,
                showIfEmpty = false,
                title = this.getText(R.string.missing).toString(),
                parcels = this.deliveryList.missingParcels.map { it.value }
                        .bindToLifecycle(this)
        )
    }
    //endregion

    private val parcelListAdapterInstance = LazyInstance<ParcelSectionsAdapter>({

        val adapter = ParcelSectionsAdapter()

        adapter.addParcelSection(this.loadedSection)
        adapter.addParcelSection(this.damagedSection)
        adapter.addParcelSection(this.pendingSection)
        adapter.addParcelSection(this.missingSection)

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = this.getText(R.string.vehicle_loading).toString()
        this.headerImage = R.drawable.img_parcels_1a
        this.scrollCollapseMode = ScrollCollapseModeType.ExitUntilCollapsed

        this.aidcEnabled = true
    }

    override fun onCreateView(inflater: android.view.LayoutInflater,
                              container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {

        val binding: ScreenVehicleloadingBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.screen_vehicleloading,
                container, false)

        // Setup bindings
        binding.stats = StatsViewModel(deliveryList)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Flexible adapter needs to be re-created with views
        this.parcelListAdapterInstance.reset()

        this.uxParcelList.adapter = parcelListAdapter
        this.uxParcelList.layoutManager = LinearLayoutManager(context)

        this.menu = this.inflateMenu(R.menu.menu_vehicleloading).also {
            if (this.debugSettings.enabled) {
                it.add(0, R.id.action_vehicle_loading_dev_mark_all_loaded, 0, "Mark all as loaded").also {
                    it.setIcon(R.drawable.ic_dev)
                }
            }
        }

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_vehicle_loading_load,
                        colorRes = R.color.colorGreen,
                        iconRes = R.drawable.ic_truck,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_vehicle_loading_damaged,
                        colorRes = R.color.colorAccent,
                        iconRes = R.drawable.ic_damaged,
                        visible = true
                ),
                ActionItem(
                        id = R.id.action_vehicle_loading_finished,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_done_black,
                        iconTintRes = android.R.color.white,
                        alignEnd = false
                )
        )
    }

    override fun onDestroy() {
        this.parcelListAdapterInstance.reset()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        aidcReader.decoders.set(
                Interleaved25Decoder(true, 10, 12),
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
                            this.orderRepository.removeAll()
                                    .subscribe()
                        }
                        R.id.action_vehicle_loading_dev_mark_all_loaded -> {
                            db.store.withTransaction {
                                select(ParcelEntity::class)
                                        .where(ParcelEntity.LOADING_STATE.eq(Parcel.LoadingState.PENDING))
                                        .get()
                                        .forEach {
                                            it.loadingState = Parcel.LoadingState.LOADED
                                            update(it)
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
                        R.id.action_vehicle_loading_damaged -> {
                            this.parcelListAdapter.selectedSection = this.damagedSection
                        }

                        R.id.action_vehicle_loading_load -> {
                            this.parcelListAdapter.selectedSection = this.loadedSection
                        }

                        R.id.action_vehicle_loading_finished -> {
                            MaterialDialog.Builder(context)
                                    .title(R.string.vehicle_loading_finalize_dialog_title)
                                    .content(R.string.vehicle_loading_finalize_dialog)
                                    .negativeText(R.string.no_go_back)
                                    .positiveText(R.string.yes_start_tour)
                                    .onPositive { _, _ ->
                                        this.deliveryList
                                                .finalize()
                                                .observeOnMainThread()
                                                .subscribeBy(onComplete = {
                                                    this.fragmentManager.popBackStack()
                                                })

                                    }
                                    .show()
                        }

                        else -> log.warn("Unhandled ActionEvent [$it]")
                    }
                }
        //endregion

        this.parcelListAdapter.selectedSection = this.loadedSection
        this.parcelListAdapter.selectedSectionProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    val section = it.value as ParcelSectionViewModel?

                    this.accentColor = section?.color ?: R.color.colorGrey

                    when (section) {
                        this.loadedSection -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_vehicle_loading_load }
                                        .visible = false

                                first { it.id == R.id.action_vehicle_loading_damaged }
                                        .visible = true
                            }
                        }
                        else -> {
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_vehicle_loading_load }
                                        .visible = true

                                first { it.id == R.id.action_vehicle_loading_damaged }
                                        .visible = false
                            }
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

            // Synthetic inputs for delivery lists, retrieved via online service
            val ovDeliveryLists = Observable.fromCallable {
                this.deliveryListService.get(ShortDate("2017-06-20"))
            }
                    .toHotRestObservable()
                    .composeAsRest(this.activity)
                    .doOnError {
                        log.error(it.message, it)
                    }
                    .map {
                        SyntheticInput(
                                name = "Delivery lists",
                                entries = it.map {
                                    SyntheticInput.Entry(
                                            symbologyType = SymbologyType.Interleaved25,
                                            data = DekuDeliveryListNumber.parse(it.id.toString()).value.label
                                    )
                                }
                        )
                    }
                    .onErrorResumeNext(Observable.empty())

            // Final synthetic inputs observable
            Observable.combineLatest(
                    ovParcels,
                    ovDeliveryLists,

                    BiFunction { a: SyntheticInput, b: SyntheticInput ->
                        listOf<SyntheticInput>(a, b)
                    }
            )
                    .bindUntilEvent(this, FragmentEvent.PAUSE)
                    .subscribe {
                        this.syntheticInputs = it
                    }

        }
        //endregion
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        log.trace("AIDC READ $event")

        val result = Observable.concat(
                Observable.fromCallable { UnitNumber.parseLabel(event.data) },
                Observable.fromCallable { DekuDeliveryListNumber.parseLabel(event.data) }
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
                    is DekuDeliveryListNumber -> {
                        this.onInput(resultValue)
                    }
                }
            }
        }
    }

    /**
     * On delivery list input
     */
    fun onInput(deliveryListNumber: DekuDeliveryListNumber) {
        var loaded = false
        deliveryList.load(deliveryListNumber)
                .bindToLifecycle(this)
                .composeAsRest(this.activity, R.string.error_invalid_delivery_list)
                .subscribeBy(
                        onNext = {
                            loaded = true
                            log.info("Current delivery lists [${this.deliveryList.ids.joinToString(", ")}")
                        },
                        onComplete = {
                            // Can't rely on complete alone due to rxlifecycle
                            if (loaded) {
                                tones.beep()
                            }
                        },
                        onError = {
                            tones.errorBeep()
                        }
                )
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
                // No corresponding order (yet)
                this.deliveryList.retrieveOrder(unitNumber)
                        .bindToLifecycle(this)
                        .composeAsRest(this.activity, R.string.error_no_corresponding_order)
                        .subscribeBy(
                                onNext = { order ->
                                    tones.beep()

                                    fun onConfirmed() {
                                        this.deliveryList
                                                .mergeOrder(order)
                                                .observeOnMainThread()
                                                .subscribeBy(onComplete = {
                                                    this.onParcel(
                                                            parcel = this.parcelRepository.entities.first { it.number == unitNumber.value }
                                                    )
                                                })
                                    }

                                    if (this.deliveryList.ids.get().isEmpty()) {
                                        onConfirmed()
                                    } else {
                                        MaterialDialog.Builder(this.activity)
                                                .title(R.string.order_not_on_delivery_list)
                                                .content(R.string.order_not_on_delivery_list_confirmation)
                                                .positiveText(android.R.string.yes)
                                                .negativeText(android.R.string.no)
                                                .onPositive { _, _ ->
                                                    onConfirmed()
                                                }
                                                .build().show()
                                    }

                                },
                                onError = {
                                    tones.errorBeep()
                                }
                        )

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
                                parcel.isDamaged = false
                                this.parcelRepository.update(parcel).blockingGet()
                            }
                            .show()
                } else {
                    // TODO take photo
                    parcel.isDamaged = true

                    this.parcelRepository.update(parcel).blockingGet()
                }
            }
            else -> {
                if (parcel.loadingState == Parcel.LoadingState.LOADED) {
                    this.tones.warningBeep()
                    this.aidcReader.enabled = false

                    MaterialDialog.Builder(this.context)
                            .title(R.string.vehicle_loading_unload_parcel_dialog_title)
                            .content(R.string.vehicle_loading_unload_parcel_dialog)
                            .negativeText(getString(android.R.string.no))
                            .positiveText(getString(android.R.string.yes))
                            .dismissListener {
                                this.aidcReader.enabled = true
                            }
                            .onPositive { _, _ ->
                                parcel.loadingState = Parcel.LoadingState.PENDING
                                this.parcelRepository.update(parcel).blockingGet()
                            }
                            .show()
                } else {
                    parcel.loadingState = Parcel.LoadingState.LOADED
                    this.parcelRepository.update(parcel).blockingGet()
                }
            }
        }
    }
}

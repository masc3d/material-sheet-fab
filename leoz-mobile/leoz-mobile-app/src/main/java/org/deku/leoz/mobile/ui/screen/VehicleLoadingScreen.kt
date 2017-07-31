package org.deku.leoz.mobile.ui.screen


import android.databinding.BaseObservable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
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
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.screen_vehicleloading.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ScreenVehicleloadingBinding
import org.deku.leoz.mobile.dev.SyntheticInput
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.process.DeliveryList
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository

import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.*
import org.deku.leoz.model.DekuDeliveryListNumber
import org.deku.leoz.model.UnitNumber
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Result
import sx.Stopwatch
import sx.aidc.SymbologyType
import sx.android.aidc.*
import sx.android.databinding.toField
import sx.android.inflateMenu
import sx.android.isConnectivityException
import sx.format.format
import sx.rx.subscribeOn
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
                drawableRes = R.drawable.ic_package_closed,
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

    private val tones: Tones by Kodein.global.lazy.instance()
    private val aidcReader: sx.android.aidc.AidcReader by Kodein.global.lazy.instance()

    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()

    // region Sections
    val loadedSection by lazy {
        ParcelSectionViewModel(
                icon = R.drawable.ic_truck,
                color = R.color.colorGreen,
                background = R.drawable.section_background_green,
                title = this.getText(R.string.loaded).toString(),
                parcels = this.deliveryList.loadedParcels.map { it.value }.bindToLifecycle(this)
        )
    }

    val damagedSection by lazy {
        ParcelSectionViewModel(
                icon = R.drawable.ic_damaged,
                color = R.color.colorAccent,
                background = R.drawable.section_background_accent,
                title = this.getText(R.string.damaged).toString(),
                parcels = this.deliveryList.damagedParcels.map { it.value }.bindToLifecycle(this)
        )
    }

    val pendingSection by lazy {
        ParcelSectionViewModel(
                icon = R.drawable.ic_format_list_bulleted,
                color = R.color.colorGrey,
                background = R.drawable.section_background_grey,
                isSelectable = false,
                title = this.getText(R.string.pending).toString(),
                parcels = this.deliveryList.pendingParcels.map { it.value }.bindToLifecycle(this)
        )
    }
    //endregion

    private val parcelListAdapterInstance = LazyInstance<ParcelSectionsAdapter>({

        val adapter = ParcelSectionsAdapter()

        adapter.addParcelSection(this.loadedSection)
        adapter.addParcelSection(this.damagedSection)
        adapter.addParcelSection(this.pendingSection)

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

        this.menu = this.inflateMenu(R.menu.menu_vehicleloading)

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

        this.activity.menuItemEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it.itemId) {
                        R.id.action_reset -> {
                            this.orderRepository.removeAll()
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
                            val dialog = MaterialDialog.Builder(context)
                                    .title(getString(R.string.question_vehicle_loading_finished))
                                    .negativeText(getString(R.string.no_go_back))
                                    .positiveText(getString(R.string.yes_start_tour))
                                    .show()
                        }

                        else -> log.warn("Unhandled ActionEvent [$it]")
                    }
                }

        this.parcelListAdapter.selectedSection = this.loadedSection
        this.parcelRepository.entitiesProperty
                .map { it.value }
                .subscribe { parcels ->
                    this.syntheticInputs = listOf(
                            SyntheticInput(
                                    name = "Delivery lists",
                                    entries = listOf(
                                            SyntheticInput.Entry(
                                                    symbologyType = SymbologyType.Interleaved25,
                                                    data = DekuDeliveryListNumber.parse("10730061").value.label
                                            ),
                                            SyntheticInput.Entry(
                                                    symbologyType = SymbologyType.Interleaved25,
                                                    data = DekuDeliveryListNumber.parse("28725713").value.label
                                            )
                                    )
                            ),
                            SyntheticInput(
                                    name = "Parcels",
                                    entries = parcels.map {
                                        val unitNumber = UnitNumber.parse(it.number).value
                                        SyntheticInput.Entry(
                                                symbologyType = SymbologyType.Interleaved25,
                                                data = unitNumber.label
                                        )
                                    }
                            )
                    )

                }

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
    }

    private fun onAidcRead(event: AidcReader.ReadEvent) {
        log.trace("AIDC READ $event")

        val stopwatch = Stopwatch.createStarted()
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

    fun onInput(deliveryListNumber: DekuDeliveryListNumber) {
        deliveryList.load(deliveryListNumber)
                .bindToLifecycle(this)
                .doOnSubscribe {
                    this.activity.progressIndicator.show()
                }
                .doOnTerminate {
                    this.activity.progressIndicator.hide()
                }
                .subscribeBy(
                        onError = {
                            this.activity.snackbarBuilder
                                    .message(
                                            if (it.isConnectivityException)
                                                R.string.error_connectivity
                                            else
                                                R.string.error_invalid_delivery_list
                                    )
                                    .duration(Snackbar.LENGTH_LONG)
                                    .build().show()

                            tones.errorBeep()
                        }
                )
    }

    fun onInput(unitNumber: UnitNumber) {
        log.trace("Unit number input ${unitNumber.value}")

        val parcel = this.parcelRepository.entities.firstOrNull { it.number == unitNumber.value }

        if (parcel != null) {
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
                    if (parcel.loadingState == Parcel.State.LOADED) {
                        this.tones.warningBeep()
                        this.aidcReader.enabled = false

                        MaterialDialog.Builder(this.context)
                                .title(R.string.question_unload_parcel_title)
                                .content(R.string.question_unload_parcel)
                                .negativeText(getString(android.R.string.no))
                                .positiveText(getString(android.R.string.yes))
                                .dismissListener {
                                    this.aidcReader.enabled = true
                                }
                                .onPositive { _, _ ->
                                    parcel.loadingState = Parcel.State.PENDING
                                    this.parcelRepository.update(parcel).blockingGet()
                                }
                                .show()
                    } else {
                        parcel.loadingState = Parcel.State.LOADED
                        this.parcelRepository.update(parcel).blockingGet()
                    }
                }
            }
        } else {
            // TODO show error
        }
    }
}

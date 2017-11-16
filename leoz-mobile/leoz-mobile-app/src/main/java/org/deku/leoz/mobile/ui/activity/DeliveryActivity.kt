package org.deku.leoz.mobile.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.rxkotlin.subscribeBy
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.SharedPreference
import org.deku.leoz.mobile.device.Feedback
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.ChangelogItem
import org.deku.leoz.mobile.ui.dialog.ChangelogDialog
import org.deku.leoz.mobile.ui.screen.*
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.model.UnitNumber
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import java.util.*

/**
 * Delivery activity
 * Created by 27694066 on 09.05.2017.
 */
class DeliveryActivity : Activity(),
        MenuScreen.Listener,
        VehicleLoadingScreen.Listener,
        VehicleUnloadingScreen.Listener,
        DeliveryStopListScreen.Listener,
        DeliveryStopDetailScreen.Listener {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val sharedPreferences: SharedPreferences by Kodein.global.lazy.instance()

    private val feedback: Feedback by Kodein.global.lazy.instance()

    private val db: Database by Kodein.global.lazy.instance()

    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            //TODO: First fragment to be shown should be the privacy disclaimer (Maybe to be displayed as an dialog?)
            this.showScreen(MenuScreen(), addToBackStack = false)
            //Show privacy disclaimer
            //TODO
            //Show vehicle selection dialog
            //TODO Call the function when the disclaimer is dismissed
            //Check if the changelog dialog should be displayed TODO: Call this function when the vehicle selection is done.
            //queryChangelogDisplay()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            // Exit only allowed via logout
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Determine if changelog should be displayed automatically e.g. after an APP update.
     * Display the dialog only after the user has been logged in
     */
    fun queryChangelogDisplay() {
        var currentVersionNumber = 0
        val savedVersionNumber = sharedPreferences.getInt(SharedPreference.CHANGELOG_VERSION.key, 0)

        try {
            val pi = packageManager.getPackageInfo(packageName, 0)
            currentVersionNumber = pi.versionCode
        } catch (e: Exception) {
            log.error("${e.message}\r\n${e.stackTrace}")
        }

        log.debug("Checking for changelog dialog. Current version [$currentVersionNumber] Recently saved version [$savedVersionNumber]")

        if (currentVersionNumber > savedVersionNumber) {
            // TODO: disabled changelog until finalized | phpr: enabled while piloting alpha though
            if (BuildConfig.DEBUG || true) {
                showChangelogDialog()

                val editor = sharedPreferences.edit()

                editor.putInt(SharedPreference.CHANGELOG_VERSION.key, currentVersionNumber)
                editor.apply()
            }
        }
    }

    fun showChangelogDialog() {
        val entries = listOf(
                ChangelogItem(
                        date = Date(1503612000),
                        version = "0.58-SNAPSHOT",
                        entries = ChangelogItem.ChangelogEntry(
                                title = "Fehlerkorrektur & Foto",
                                description = "Die Zustellung mehrerer Stops zugleich ist nun möglich. \"Beschädigt\" Scannung in der Fahrzeugverladung erfasst nun Fotos."
                        )
                )
        )

        ChangelogDialog.create(entries).show(supportFragmentManager, "DIALOG_CHANGELOG")
    }

    /**
     * Fragment listener
     */
    override fun onDeliveryMenuSelection(entryType: MenuScreen.EntryType) {
        when (entryType) {
            MenuScreen.EntryType.LOADING -> {
                this.orderRepository.hasOutdatedOrders()
                        .bindToLifecycle(this)
                        .subscribeOn(db.scheduler)
                        .observeOnMainThread()
                        .subscribeBy(
                                onError = {
                                    log.error(it.message, it)
                                },
                                onSuccess = {
                                    log.trace("OUTDATED ${it}")

                                    when (it) {
                                        true -> {
                                            MaterialDialog.Builder(this)
                                                    .title(R.string.title_reset_data)
                                                    .content(R.string.dialog_content_outdated_orders)
                                                    .positiveText(android.R.string.yes)
                                                    .negativeText(android.R.string.no)
                                                    .onPositive { _, _ ->
                                                        db.store.withTransaction {
                                                            log.trace("RESETTING DATA")
                                                            orderRepository.removeAll()
                                                                    .blockingAwait()
                                                        }
                                                                .toCompletable()
                                                                .subscribeOn(db.scheduler)
                                                                .observeOnMainThread()
                                                                .subscribeBy(
                                                                        onComplete = {
                                                                            this.showScreen(VehicleLoadingScreen())
                                                                        },
                                                                        onError = {
                                                                            log.error(it.message, it)
                                                                        }
                                                                )
                                                    }
                                                    .build().show()
                                        }
                                        else -> {
                                            this.showScreen(VehicleLoadingScreen())
                                        }
                                    }
                                })
            }

            MenuScreen.EntryType.DELIVERY -> {
                this.showScreen(DeliveryStopListScreen())
            }

            MenuScreen.EntryType.UNLOADING -> {
                this.showScreen(VehicleUnloadingScreen())
            }
        }
    }

    override fun onVehicleLoadingFinalized() {
        this.supportFragmentManager.popBackStack(
                VehicleLoadingScreen::class.java.canonicalName,
                FragmentManager.POP_BACK_STACK_INCLUSIVE)

        this.showScreen(DeliveryStopListScreen())
    }

    override fun onVehicleUnloadingFinalized() {
        this.supportFragmentManager.popBackStack(
                VehicleUnloadingScreen::class.java.canonicalName,
                FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun onDeliveryUnitNumberInput(unitNumber: UnitNumber) {
        val parcel = this.parcelRepository
                .findByNumber(unitNumber.value)
                .blockingGet()

        if (parcel == null) {
            feedback.warning()

            this.snackbarBuilder
                    .message(R.string.error_unknown_parcel)
                    .build().show()

            return
        }

        // If parcel is missing, mark as loaded
        if (parcel.state == Parcel.State.PENDING) {
            parcel.state = Parcel.State.LOADED
            parcelRepository.update(parcel as ParcelEntity)
                    .subscribeOn(db.scheduler)
                    .subscribe()
        }

        val stop = parcel.order.tasks
                .mapNotNull { it.stop }
                .firstOrNull()

        if (stop == null) {
            feedback.warning()

            this.snackbarBuilder
                    .message(R.string.error_no_corresponding_stop)
                    .build().show()

            return
        }

        this.showScreen(
                DeliveryStopProcessScreen().also {
                    it.parameters = DeliveryStopProcessScreen.Parameters(stopId = stop.id)
                }
        )
    }

    override fun onDeliveryStopDetailUnitNumberInput(unitNumber: UnitNumber) {
        this.onDeliveryUnitNumberInput(unitNumber)
    }

    override fun onDeliveryStopListUnitNumberInput(unitNumber: UnitNumber) {
        this.onDeliveryUnitNumberInput(unitNumber)
    }
}
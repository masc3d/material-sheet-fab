package org.deku.leoz.mobile.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.SharedPreference
import org.deku.leoz.mobile.model.process.Delivery
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.ChangelogItem
import org.deku.leoz.mobile.ui.dialog.ChangelogDialog
import org.deku.leoz.mobile.ui.dialog.VehicleLoadingDialog
import org.deku.leoz.mobile.ui.screen.*
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import java.util.*

/**
 * Created by 27694066 on 09.05.2017.
 */
class DeliveryActivity : Activity(),
        MenuScreen.Listener,
        SignatureScreen.Listener,
        VehicleLoadingScreen.Listener,
        VehicleLoadingDialog.OnDialogResultListener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val delivery: Delivery by Kodein.global.lazy.instance()
    private val sharedPreferences: SharedPreferences by Kodein.global.lazy.instance()

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
            queryChangelogDisplay()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            // Exit only allowed via logout
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

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
    override fun onDeliveryMenuSelection(entryType: MenuScreen.MenuEntry.Entry) {
        when (entryType) {
            MenuScreen.MenuEntry.Entry.LOADING -> {
                if (this.orderRepository.hasOutdatedOrders()) {
                    MaterialDialog.Builder(this)
                            .title(R.string.title_reset_data)
                            .content(R.string.dialog_content_outdated_orders)
                            .positiveText(android.R.string.yes)
                            .negativeText(android.R.string.no)
                            .onPositive { _, _->
                                this.orderRepository.removeAll()
                                        .blockingAwait()
                                this.showScreen(VehicleLoadingScreen())
                            }
                            .build().show()
                } else {
                    this.showScreen(VehicleLoadingScreen())
                }
            }

            MenuScreen.MenuEntry.Entry.DELIVERY -> {
                this.showScreen(DeliveryStopListScreen())
            }
        }
    }

    override fun onSignatureCancelled() {
        this.supportFragmentManager.popBackStack()
    }

    override fun onSignatureSubmitted(signatureSvg: String) {
        val activeStop = this.delivery.activeStop

        if (activeStop != null) {
            // Complement active stop and finalize
            activeStop.signatureSvg = signatureSvg
            activeStop.finalize()
                    .subscribeOn(Schedulers.computation())
                    .observeOnMainThread()
                    .subscribeBy(
                            onComplete = {
                                this.delivery.activeStop = null

                                this@DeliveryActivity.supportFragmentManager.popBackStack(
                                        DeliveryStopListScreen::class.java.canonicalName,
                                        0)
                            },
                            onError = {
                                log.error(it.message, it)
                            }
                    )
        }
    }

    override fun onVehicleLoadingFinalized() {
        this.supportFragmentManager.popBackStack(
                VehicleLoadingScreen::class.java.canonicalName,
                FragmentManager.POP_BACK_STACK_INCLUSIVE)

        this.showScreen(DeliveryStopListScreen())
    }

    /**
     * Dialog listener
     * TODO: To be removed / use RX instead
     */

    override fun onDeliveryListEntered(listId: String) {
        //Get delivery list synchronously and continue to VehicleLoadingFragment after process finished
    }

    override fun onDeliveryListSkipped() {
        this.showScreen(VehicleLoadingScreen())
    }

    override fun onCanceled() {
    }
}
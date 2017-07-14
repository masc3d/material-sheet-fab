package org.deku.leoz.mobile.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.SharedPreference
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.ChangelogItem
import org.deku.leoz.mobile.ui.dialog.ChangelogDialog
import org.deku.leoz.mobile.ui.dialog.VehicleLoadingDialog
import org.deku.leoz.mobile.ui.screen.*
import org.deku.leoz.model.EventDeliveredReason
import org.deku.leoz.model.ParcelService
import org.slf4j.LoggerFactory
import sx.android.fragment.CameraFragment
import java.util.*

/**
 * Created by 27694066 on 09.05.2017.
 */
class DeliveryActivity : Activity(),
        MenuScreen.Listener,
        SignatureScreen.Listener,
        VehicleLoadingDialog.OnDialogResultListener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val sharedPreferences: SharedPreferences by Kodein.global.lazy.instance()

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

    fun runSigningProcess(stop: Stop, reason: EventDeliveredReason) {
        when (reason) {
            EventDeliveredReason.Normal -> this.showScreen(SignatureScreen.create(deliveryReason = org.deku.leoz.model.EventDeliveredReason.Normal, stop = stop, recipient = ""))
            EventDeliveredReason.Neighbor -> this.showScreen(NeighbourDeliveryScreen.create(stop = stop))
            EventDeliveredReason.Postbox -> this.showScreen(PostboxDeliveryScreen.create(stop = stop))
            else -> throw NotImplementedError("Reason [${reason.name}]  not implemented.")
        }
        //this.showScreen(SignatureScreen.create(deliveryReason = reason, stop = stop))
    }

    fun runServiceWorkflow(stop: Stop, reason: EventDeliveredReason) {

        val serviceCheck = stop.orders.first().getNextServiceCheck()

        if (serviceCheck == null) {
            runSigningProcess(stop = stop, reason = reason)
        } else {
            when (serviceCheck.service) {
                ParcelService.CASH_ON_DELIVERY -> TODO()
                ParcelService.DOCUMENTED_PERSONAL_DELIVERY -> TODO()
                ParcelService.IDENT_CONTRACT_SERVICE -> TODO()
                ParcelService.PACKAGING_RECIRCULATION -> TODO()
                ParcelService.PHARMACEUTICALS -> TODO()
                ParcelService.PHONE_RECEIPT -> TODO()
                ParcelService.RECEIPT_ACKNOWLEDGEMENT -> TODO()
                ParcelService.SECURITY_RETURN -> TODO()
                ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS -> TODO()
                ParcelService.SUBMISSION_PARTICIPATION -> TODO()
                ParcelService.XCHANGE -> TODO()
            }
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
            showChangelogDialog()

            val editor = sharedPreferences.edit()

            editor.putInt(SharedPreference.CHANGELOG_VERSION.key, currentVersionNumber)
            editor.apply()
        }
    }

    fun showChangelogDialog() {
        val entries = listOf(
                ChangelogItem(
                        date = Date(1498255200),
                        version = "0.15-SNAPSHOT",
                        entries = ChangelogItem.ChangelogEntry(
                                title = "Performance update",
                                description = "We improved the app performance to provide you an better user experience."
                        )
                )
        )

        ChangelogDialog.create(entries).show(supportFragmentManager, "DIALOG_CHANGELOG")
    }

    /**
     * Fragment listener
     */

    override fun onDeliveryMenuChoosed(entryType: MenuScreen.MenuEntry.Entry) {
        when (entryType) {
            MenuScreen.MenuEntry.Entry.LOADING -> {
                this.showScreen(VehicleLoadingScreen())
            }

            MenuScreen.MenuEntry.Entry.ORDERLIST -> {
                this.showScreen(DeliveryStopListScreen())
            }
        }
    }

    override fun onSignatureCancelled() {
    }

    override fun onSignatureSubmitted() {
        this@DeliveryActivity.supportFragmentManager.popBackStack(DeliveryStopListScreen::class.java.canonicalName, 0)
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
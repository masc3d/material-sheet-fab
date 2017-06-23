package org.deku.leoz.mobile.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.main.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.dialog.VehicleLoadingDialog
import org.deku.leoz.mobile.ui.screen.*
import org.slf4j.LoggerFactory
import sx.android.fragment.CameraFragment

/**
 * Created by 27694066 on 09.05.2017.
 */
class DeliveryActivity : Activity(),
        CameraFragment.Listener,
        MenuScreen.Listener,
        SignatureScreen.Listener,
        VehicleLoadingDialog.OnDialogResultListener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    val delivery: Delivery by Kodein.global.lazy.instance()

    companion object {
        const val FRAGMENT_TAG_CAMERA = "fragmentCamera"
        const val FRAGMENT_TAG_SIGNATURE = "fragmentSignature"
        const val FRAGMENT_TAG_TOURSELECTION = "fragmentTourSelection"
        const val FRAGMENT_TAG_TOUROVERVIEW = "fragmentTourOverview"
        const val FRAGMENT_TAG_DELIVERYMENUE = "fragmentDeliveryMenue"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            this.showScreen(MenuScreen(), addToBackStack = false)

            this.supportActionBar?.title = getString(R.string.menu)
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
     * Fragment listener
     */

    override fun onDeliveryMenuChoosed(entryType: MenuScreen.MenuEntry.Entry) {
        when (entryType) {
            MenuScreen.MenuEntry.Entry.LOADING -> {
                /**
                 * Start "vehicle loading" process
                 */
                val dialog: VehicleLoadingDialog = VehicleLoadingDialog(this)
                dialog.show(supportFragmentManager, "LOADINGDIALOG")
            }

            MenuScreen.MenuEntry.Entry.ORDERLIST -> {
                this.showScreen(DeliveryStopListScreen())
            }
        }
    }

    override fun onCameraFragmentPictureTaken(data: ByteArray?) {
        log.debug("ONCAMERAFRAGMENTPICTURETAKEN")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun onCameraFragmentShutter() {
        log.debug("ONCAMERAFRAGMENTSHUTTER")
    }

    override fun onCameraFragmentDiscarded() {
        log.debug("ONCAMERAFRAGMENTDISCARDED")
    }

    override fun onSignatureCancelled() {
    }

    override fun onSignatureSubmitted() {
        this@DeliveryActivity.supportFragmentManager.popBackStack(DeliveryProcessScreen::class.java.canonicalName, 0)
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

    fun showDeliverFabButtons() {
    }

    fun showSignaturePad() {
        this.showScreen(SignatureScreen())
    }
}
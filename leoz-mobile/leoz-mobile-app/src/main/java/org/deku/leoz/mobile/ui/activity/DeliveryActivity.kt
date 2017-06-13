package org.deku.leoz.mobile.ui.activity

import android.os.Bundle
import android.support.v7.view.menu.MenuBuilder
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.main_content.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Delivery
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.dialog.VehicleLoadingDialog
import org.deku.leoz.mobile.ui.screen.DeliveryMainFragment
import org.deku.leoz.mobile.ui.screen.SignatureFragment
import org.deku.leoz.mobile.ui.screen.StopOverviewFragment
import org.deku.leoz.mobile.ui.screen.VehicleLoadingFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.slf4j.LoggerFactory
import sx.android.fragment.CameraFragment
import sx.android.fragment.util.withTransaction

/**
 * Created by 27694066 on 09.05.2017.
 */
class DeliveryActivity : Activity(), CameraFragment.Listener, DeliveryMainFragment.Listener /* TODO: To be removed / use RX instead */, VehicleLoadingDialog.OnDialogResultListener /* TODO: To be removed / use RX instead */ {
    private val log = LoggerFactory.getLogger(this.javaClass)
    val delivery: Delivery by Kodein.global.lazy.instance()

    val deliverFailMenu by lazy {
        val menu = MenuBuilder(this.applicationContext)
        this.menuInflater.inflate(R.menu.menu_deliver_fail, menu)
        menu
    }


    val deliverOkMenu by lazy {
        val menu = MenuBuilder(this.applicationContext)
        this.menuInflater.inflate(R.menu.menu_deliver_options, menu)
        menu
    }

    val deliverActionMenu by lazy {
        val menu = MenuBuilder(this.applicationContext)
        this.menuInflater.inflate(R.menu.menu_deliver_actions, menu)
        menu
    }

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
            this.showScreen(DeliveryMainFragment(), addToBackStack = false)

            this.supportActionBar?.setTitle(R.string.delivery)
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

    override fun onDeliveryMenuChoosed(entryType: DeliveryMainFragment.MenuEntry.Entry) {
        when (entryType) {
            DeliveryMainFragment.MenuEntry.Entry.LOADING -> {
                /**
                 * Start "vehicle loading" process
                 */
                val dialog: VehicleLoadingDialog = VehicleLoadingDialog(this)
                dialog.show(supportFragmentManager, "LOADINGDIALOG")
            }

            DeliveryMainFragment.MenuEntry.Entry.ORDERLIST -> {
                this.showScreen(StopOverviewFragment())
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

    /**
     * Dialog listener
     * TODO: To be removed / use RX instead
     */

    override fun onDeliveryListEntered(listId: String) {
        //Get delivery list synchronously and continue to VehicleLoadingFragment after process finished
    }

    override fun onDeliveryListSkipped() {
        this.showScreen(VehicleLoadingFragment())
    }

    override fun onCanceled() {
    }

    fun showDeliverFabButtons() {
        this.actionItems = listOf(
                ActionItem(2, R.color.colorGreen, R.drawable.ic_check_circle, null, deliverOkMenu),
                ActionItem(1, R.color.colorAccent, R.drawable.ic_information_outline, null, deliverActionMenu),
                ActionItem(0, R.color.colorRed, R.drawable.ic_cancel_black, null, deliverFailMenu)
        )
    }

    fun showSignaturePad() {
        this.showScreen(SignatureFragment())

        this.actionItems = listOf()
    }
}
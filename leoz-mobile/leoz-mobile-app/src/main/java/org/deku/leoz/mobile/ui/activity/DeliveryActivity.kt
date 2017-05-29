package org.deku.leoz.mobile.ui.activity

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.main_content.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Job
import org.deku.leoz.mobile.ui.DeliveryMenuListAdapter
import org.deku.leoz.mobile.ui.dialog.VehicleLoadingDialog
import org.deku.leoz.mobile.ui.fragment.DeliveryFragment
import org.deku.leoz.mobile.ui.fragment.DeliveryProcessFragment
import org.deku.leoz.mobile.ui.fragment.VehicleLoadingFragment
import org.slf4j.LoggerFactory
import sx.android.fragment.CameraFragment
import sx.android.fragment.util.withTransaction

/**
 * Created by 27694066 on 09.05.2017.
 */
class DeliveryActivity: Activity(), CameraFragment.Listener, DeliveryFragment.OnDeliveryMenuChoosed /* TODO: To be removed / use RX instead */, VehicleLoadingDialog.OnDialogResultListener /* TODO: To be removed / use RX instead */ {
    private val log = LoggerFactory.getLogger(this.javaClass)
    val job: Job by Kodein.global.lazy.instance()

    companion object {
        const val FRAGMENT_TAG_CAMERA = "fragmentCamera"
        const val FRAGMENT_TAG_SIGNATURE = "fragmentSignature"
        const val FRAGMENT_TAG_TOURSELECTION = "fragmentTourSelection"
        const val FRAGMENT_TAG_TOUROVERVIEW = "fragmentTourOverview"
        const val FRAGMENT_TAG_DELIVERYMENUE = "fragmentDeliveryMenue"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null) {
            supportFragmentManager.withTransaction {
                it.replace(this.uxContainer.id, DeliveryFragment())
            }

            this.supportActionBar?.setTitle(R.string.delivery)
        }
    }


    /**
     * Fragment listener
     */

    override fun onDeliveryMenuChoosed(entryType: DeliveryMenuListAdapter.DeliveryMenuEntry.Entry) {
        log.debug("ONDELIVERYMENUCHOOSED")
        when(entryType){
            DeliveryMenuListAdapter.DeliveryMenuEntry.Entry.ORDERLIST -> {
                supportFragmentManager.withTransaction {
                    it.replace(this.uxContainer.id, DeliveryProcessFragment())
                }
            }
            DeliveryMenuListAdapter.DeliveryMenuEntry.Entry.LOADING -> {
                /**
                 * Start "vehicle loading" process
                 */
                val dialog: VehicleLoadingDialog = VehicleLoadingDialog(applicationContext, this)
                dialog.show(supportFragmentManager, "LOADINGDIALOG")
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
        supportFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, VehicleLoadingFragment())
        }
    }

    override fun onCanceled() {
    }
}
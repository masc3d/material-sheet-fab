package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.fragment_main.*
import org.deku.leoz.mobile.BuildConfig

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Job
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import sx.android.fragment.CameraFragment
import sx.android.fragment.util.withTransaction


/**
 * A simple [Fragment] subclass.
 */
class DeliveryFragment : Fragment(), CameraFragment.Listener {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()
    private val job: Job by Kodein.global.lazy.instance()
    private var recentFragment: Fragment? = null

    companion object {
        const val FRAGMENT_TAG_CAMERA = "fragmentCamera"
        const val FRAGMENT_TAG_SIGNATURE = "fragmentSignature"
        const val FRAGMENT_TAG_TOURSELECTION = "fragmentTourSelection"
        const val FRAGMENT_TAG_TOUROVERVIEW = "fragmentTourOverview"
        const val FRAGMENT_TAG_DELIVERYMENUE = "fragmentDeliveryMenue"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // Reusing fragment_main (trying)
        return inflater!!.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxLogo.setImageResource(R.drawable.ic_truck_fast)
        this.uxTitle.text = "mobile:Drive"
        this.uxVersion?.text = "v${BuildConfig.VERSION_NAME}"

        if (savedInstanceState == null) {
//            when {
//                job.stopList.isEmpty() -> {
//                    //showTourSelection()
//                    showTourOverview()
//                }
//                job.activeStop != null -> {
//
//                }
//            }
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

    fun showCamera() {
        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, CameraFragment(), FRAGMENT_TAG_CAMERA)
        }
    }

    fun showSignaturePad() {
        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, SignatureFragment(), FRAGMENT_TAG_SIGNATURE)
        }
    }

    fun showTourSelection() {
        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, TourSelectionFragment(), FRAGMENT_TAG_TOURSELECTION)
        }
    }

    fun showTourOverview() {
        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, TourOverviewFragment(), FRAGMENT_TAG_TOUROVERVIEW)
        }
    }

//    fun showDeliveryMenu() {
//        childFragmentManager.withTransaction {
//            it.replace(this.uxContainer.id, DeliveryMenuFragment(), FRAGMENT_TAG_DELIVERYMENUE)
//        }
//    }

}

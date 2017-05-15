package org.deku.leoz.mobile.ui.fragment


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.main_app_bar.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.activity.Activity
import sx.android.fragment.CameraFragment
import sx.android.fragment.util.withTransaction


/**
 * A simple [Fragment] subclass.
 */
class DeliveryFragment : Fragment() {

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


        showSignaturePad()
    }

    fun showCamera() {
        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, CameraFragment())
        }
    }

    fun showSignaturePad() {
        childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, SignatureFragment())
        }

//        this.uxHead.visibility = android.view.View.GONE
//
//        val act = activity as Activity
//
//        act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        act.uxAidcCameraFab.visibility = android.view.View.GONE
//        act.uxHelpFab.visibility = android.view.View.GONE
//        act.supportActionBar?.hide()
    }

}// Required empty public constructor

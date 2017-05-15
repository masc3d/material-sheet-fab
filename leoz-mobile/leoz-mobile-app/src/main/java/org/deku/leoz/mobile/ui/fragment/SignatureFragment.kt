package org.deku.leoz.mobile.ui.fragment


import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_signature.*
import kotlinx.android.synthetic.main.main_app_bar.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.activity.Activity
import org.slf4j.LoggerFactory


/**
 * A simple [Fragment] subclass.
 */
class SignatureFragment : Fragment(), SignaturePad.OnSignedListener {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_signature, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxSubmit.setOnClickListener {
        }

        this.uxCancel.setOnClickListener {
            if (this.uxSignaturePad.isEmpty) {
                //exit process
            } else {
                this.uxSignaturePad.clear()
            }
        }

        this.uxSignaturePad.setOnSignedListener(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val activity : Activity? = activity as org.deku.leoz.mobile.ui.activity.Activity

        activity?.supportActionBar?.hide()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activity?.uxAidcCameraFab?.visibility = android.view.View.GONE
        activity?.uxHelpFab?.visibility = android.view.View.GONE
        activity?.uxHead?.visibility = android.view.View.GONE
    }

    override fun onDetach() {
        super.onDetach()

        val activity : Activity? = activity as org.deku.leoz.mobile.ui.activity.Activity

        activity?.supportActionBar?.show()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity?.uxAidcCameraFab?.visibility = android.view.View.VISIBLE
        activity?.uxHelpFab?.visibility = android.view.View.VISIBLE
        activity?.uxHead?.visibility = android.view.View.VISIBLE
    }

    // SignaturePad listeners
    override fun onStartSigning() {
        log.debug("ONSTARTSIGNING")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClear() {
        log.debug("ONCLEAR")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSigned() {
        log.debug("ONSIGNED")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}// Required empty public constructor

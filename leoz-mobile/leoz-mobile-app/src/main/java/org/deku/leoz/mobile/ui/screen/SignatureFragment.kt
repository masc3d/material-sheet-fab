package org.deku.leoz.mobile.ui.screen


import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.fragment_signature.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.Fragment
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

        val activity : Activity? = activity

        activity?.supportActionBar?.hide()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onDetach() {
        super.onDetach()

        val activity : Activity? = activity

        activity?.supportActionBar?.show()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.fragment_signature.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.slf4j.LoggerFactory

/**
 * A simple [Fragment] subclass.
 */
class SignatureScreen
    :
        ScreenFragment(),
        SignaturePad.OnSignedListener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    interface Listener {
        fun onSignatureCancelled()
        fun onSignatureSubmitted()
    }

    private val listener by lazy { this.activity as? Listener }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_signature, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxSubmit.setOnClickListener {
            this.listener?.onSignatureSubmitted()
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

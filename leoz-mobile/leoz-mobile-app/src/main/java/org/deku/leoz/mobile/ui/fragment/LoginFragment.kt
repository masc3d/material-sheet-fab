package org.deku.leoz.mobile.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.android.FragmentEvent
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_login.*
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.android.Device
import sx.android.aidc.BarcodeReader
import sx.android.aidc.Ean13Decoder
import sx.android.aidc.Ean8Decoder

/**
 * Created by n3 on 26/02/2017.
 */
class LoginFragment : Fragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val bc: BarcodeReader by Kodein.global.lazy.instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val device: Device = Kodein.global.instance()
        this.uxSerialnumber.text = device.serial
    }

    override fun onResume() {
        super.onResume()

        bc.bindFragment(this)

        bc.decoders.set(
                Ean8Decoder(true),
                Ean13Decoder(true)
        )

        bc.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    log.info("Barcode scanned ${it.data}")
                }
    }
}
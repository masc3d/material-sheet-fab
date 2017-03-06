package org.deku.leoz.mobile.ui.fragment

import android.graphics.Color
import android.hardware.Camera
import android.os.Bundle
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle.android.FragmentEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_aidc_camera.*
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import rx.android.schedulers.AndroidSchedulers
import sx.android.aidc.CameraAidcReader
import sx.android.aidc.Ean13Decoder
import sx.android.aidc.Ean8Decoder
import android.support.v4.graphics.drawable.DrawableCompat
import sx.android.aidc.AidcReader
import sx.android.honeywell.aidc.HoneywellAidcReader
import org.apache.commons.logging.LogFactory.release
import rx.Observable
import rx.schedulers.Schedulers
import sx.android.widget.setIconTint

/**
 * Aidc camera fragment
 * Created by masc on 14.04.15.
 */
class AidcCameraFragment : Fragment() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    companion object {
        val scheduler by lazy { Schedulers.newThread() }
    }

    private val cameraReader: CameraAidcReader by Kodein.global.lazy.instance()
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aidc_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.fab_aidc_camera_torch.setOnClickListener {
            this.cameraReader.torch = !this.cameraReader.torch
        }
    }

    override fun onResume() {
        log.trace("RESUME")
        super.onResume()

        this.cameraReader.torchSubject
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.fab_aidc_camera_torch.setIconTint(if (it) R.color.colorAccent else android.R.color.black)
                }

        this.cameraReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log.info("Camera decode ${it.barcodeType} ${it.data}")
                }

        this.cameraReader.decoders.set(
                Ean8Decoder(true),
                Ean13Decoder(true)
        )

        if (this.aidcReader is HoneywellAidcReader) {
            // Disable honeywell reader
            this.aidcReader.enabled = false
        }

        this.cameraReader.enabled = true

        this.uxContainer.addView(
                this.cameraReader.createView(this.context))
    }

    override fun onPause() {
        log.trace("PAUSE")

        // Release camera and remove view finder
        this.cameraReader.enabled = false
        this.uxContainer.removeAllViews()

        if (this.aidcReader is HoneywellAidcReader) {
            // Re-enable global reader
            this.aidcReader.enabled = true
        }

        super.onPause()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}

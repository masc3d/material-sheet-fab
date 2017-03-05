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
import sx.android.aidc.CameraBarcodeReader
import sx.android.aidc.Ean13Decoder
import sx.android.aidc.Ean8Decoder
import android.support.v4.graphics.drawable.DrawableCompat
import sx.android.aidc.BarcodeReader
import sx.android.honeywell.aidc.HoneywellBarcodeReader
import org.apache.commons.logging.LogFactory.release
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Aidc camera fragment
 * Created by masc on 14.04.15.
 */
class AidcCameraFragment : Fragment() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    companion object {
        val scheduler by lazy { Schedulers.newThread() }
    }

    private val cameraReader: CameraBarcodeReader by Kodein.global.lazy.instance()
    private val barcodeReader: BarcodeReader by Kodein.global.lazy.instance()

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

    val ovWaitCamera: Observable<Any>
        get() = Observable.fromCallable {
            // Most stupid way to synchronize camera/availability.
            // As zxing-android-embedded doesn't support sync, this is basically the only way to do it
            var c: Camera? = null
            while (c == null) {
                try {
                    c = Camera.open() // attempt to get a Camera instance
                } catch (e: Exception) {
                }
            }
            c.release()
        }

    override fun onResume() {
        log.trace("RESUME")
        super.onResume()

        this.cameraReader.torchSubject
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val dfab = DrawableCompat.wrap(this.fab_aidc_camera_torch.drawable)
                    DrawableCompat.setTint(dfab, if (it) ContextCompat.getColor(this.context, R.color.colorAccent) else Color.BLACK)
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

        if (this.barcodeReader is HoneywellBarcodeReader) {
            this.barcodeReader.enabled = false

            this.ovWaitCamera
                    .bindUntilEvent(this, FragmentEvent.PAUSE)
                    .subscribeOn(AidcCameraFragment.scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        this.uxContainer.addView(this.cameraReader.createView(this.context))
                        // Honeywell scan API interferes with camera sporadically. need to disable decodes before accessing camera
                        log.trace("CAMERA ENABLE")
                        this.cameraReader.enabled = true
                    }
        } else {
            this.uxContainer.addView(this.cameraReader.createView(this.context))
            this.cameraReader.enabled = true
        }
    }

    override fun onPause() {
        log.trace("PAUSE")
        this.cameraReader.enabled = false
        this.uxContainer.removeAllViews()

        if (this.barcodeReader is HoneywellBarcodeReader) {
            // zxing-android-embedded doesn't allow explicit synchronization with the camera closing process
            // and honeywell reader requires the camera to be closed when decoding
            this.ovWaitCamera
                    // Wait for camera on dedicated thread
                    .subscribeOn(AidcCameraFragment.scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        log.trace("READER ENABLE")
                        // Honeywell scan API interferes with camera sporadically. need to disable decodes before accessing camera
                        this.barcodeReader.enabled = true
                    }

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

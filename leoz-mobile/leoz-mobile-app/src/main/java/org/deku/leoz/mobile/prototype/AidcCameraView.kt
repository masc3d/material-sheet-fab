package org.deku.leoz.mobile.prototype

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle.android.RxLifecycleAndroid
import kotlinx.android.synthetic.main.proto_view_aidc_camera.view.*
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import rx.android.schedulers.AndroidSchedulers
import sx.android.aidc.CameraBarcodeReader
import sx.android.aidc.Ean13Decoder
import sx.android.aidc.Ean8Decoder

/**
 * Implemented as a view for prototyping/testing (if fragments are the cause tor layouting issues)
 * Created by masc on 05/03/2017.
 */
class AidcCameraView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val cameraReader: CameraBarcodeReader by Kodein.global.lazy.instance()

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (!this.isInEditMode) {
            this.addView(this.cameraReader.createView(this.context))

            this.fab_aidc_camera_torch.setOnClickListener {
                this.cameraReader.torch = !this.cameraReader.torch
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!this.isInEditMode) {
            this.cameraReader.torchSubject
                    .compose(RxLifecycleAndroid.bindView(this))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val fab = this.fab_aidc_camera_torch.getDrawable()
                        DrawableCompat.setTint(fab, if (it) ContextCompat.getColor(this.context, R.color.colorAccent) else Color.BLACK)
                    }

            this.cameraReader.readEvent
                    .compose(RxLifecycleAndroid.bindView(this))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        log.info("Camera decode ${it.barcodeType} ${it.data}")
                    }

            this.cameraReader.decoders.set(
                    Ean8Decoder(true),
                    Ean13Decoder(true)
            )

            this.cameraReader.enabled = true
        }
    }

    override fun onDetachedFromWindow() {
        if (!this.isInEditMode) {
            this.cameraReader.enabled = false
        }

        super.onDetachedFromWindow()
    }
}
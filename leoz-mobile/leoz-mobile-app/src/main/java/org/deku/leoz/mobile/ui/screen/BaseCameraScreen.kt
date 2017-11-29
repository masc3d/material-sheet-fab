package org.deku.leoz.mobile.ui.screen

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.wonderkiln.camerakit.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_camera.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.device.Sounds
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.jetbrains.anko.imageBitmap
import org.slf4j.LoggerFactory
import sx.android.Device
import sx.android.aidc.CameraAidcReader
import sx.android.rx.observeOnMainThread
import sx.rx.ObservableRxProperty
import sx.rx.subscribeOn
import java.util.concurrent.ExecutorService

/**
 * Generic camera screen fragment
 *
 * Can be used standalone or derived from for eg. adding an overlay view.
 *
 * @param P Parameter type
 * Created by phpr on 03.08.2017.
 */
abstract class BaseCameraScreen<P> : ScreenFragment<P>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val executorService: ExecutorService by Kodein.global.lazy.instance()
    private val sounds: Sounds by Kodein.global.lazy.instance()
    private val device: Device by Kodein.global.lazy.instance()
    private val cameraAidcReader: CameraAidcReader by Kodein.global.lazy.instance()

    private var pictureJpeg: ByteArray? = null

    private val listener by lazy {
        this.targetFragment as? Listener
                ?: this.parentFragment as? Listener
                ?: this.activity as? Listener
    }

    interface Listener {
        fun onCameraScreenImageSubmitted(sender: Any, jpeg: ByteArray)
    }

    /** Allow multiple pictures */
    protected var allowMultiplePictures = true

    private val torchEnabledProperty = ObservableRxProperty(false)
    private var torchEnabled: Boolean by torchEnabledProperty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
        }

        this.toolbarHidden = true
        this.statusBarHidden = true
        this.lockNavigationDrawer = true
    }

    /** Can be overridden to add an overlay view to the camera screen */
    open protected fun createOverlayView(viewGroup: ViewGroup): View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_camera, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxProgressContainer.visibility = View.VISIBLE

        this.uxCameraView.setMethod(CameraKit.Constants.METHOD_STILL)
        this.uxCameraView.setJpegQuality(90)

        // Currently only set higher resolutions for honeywell devices
        // TODO: Regular phones may crash with either VIDEO_QUALITY_1080P or VIDEO_QUALITY_HIGHEST, this seems ot be an issue with CameraKit and needs to be fixed there.
        //if (true || this.device.manufacturer.type == Device.Manufacturer.Type.Honeywell)
        this.uxCameraView.setVideoQuality(CameraKit.Constants.VIDEO_QUALITY_HIGHEST)

        this.uxCameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE)
        this.uxCameraView.flash = CameraKit.Constants.FLASH_OFF

        this.uxCameraView.addCameraKitListener(object : CameraKitEventListenerAdapter() {
            override fun onEvent(event: CameraKitEvent) {
                when (event.type) {
                    CameraKitEvent.TYPE_CAMERA_OPEN -> {
                        log.trace("CAMERA OPENED")
                        this@BaseCameraScreen.uxProgressContainer.post {
                            this@BaseCameraScreen.uxProgressContainer.visibility = View.INVISIBLE
                        }
                    }
                    CameraKitEvent.TYPE_CAMERA_CLOSE -> {
                        log.trace("CAMERA CLOSED")
                    }
                }
            }

            override fun onError(error: CameraKitError) {
                log.error(error.message, error.exception)
            }

            override fun onImage(image: CameraKitImage) {
                log.trace("PICTURE TAKEN WITH SIZE [${image.bitmap.width}x${image.bitmap.height}]")

                this@BaseCameraScreen.view?.post {
                    // Temporarily store image
                    this@BaseCameraScreen.pictureJpeg = image.jpeg

                    // Hide progress
                    this@BaseCameraScreen.uxProgressContainer.visibility = View.INVISIBLE

                    this@BaseCameraScreen.torchEnabled = false

                    // Create a bitmap
                    this@BaseCameraScreen.uxPreviewImage.imageBitmap = image.bitmap
                    this@BaseCameraScreen.showImageActions()
                }

            }
        })

        this.createOverlayView(this.uxContainer)?.also { overlayView ->
            this.uxContainer.addView(overlayView)
        }

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_camera_trigger,
                        colorRes = R.color.colorPrimary,
                        iconRes = android.R.drawable.ic_menu_camera,
                        iconTintRes = android.R.color.white
                ),
                ActionItem(
                        id = R.id.action_camera_flash,
                        colorRes = R.color.colorDarkGrey,
                        iconRes = R.drawable.ic_flash,
                        iconTintRes = android.R.color.black,
                        alignEnd = false
                ),
                ActionItem(
                        id = R.id.action_camera_save_finish,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        visible = false
                ),
                ActionItem(
                        id = R.id.action_camera_save,
                        colorRes = R.color.colorPrimary,
                        iconRes = R.drawable.ic_done_plus,
                        iconTintRes = android.R.color.white,
                        visible = false
                ),
                ActionItem
                (
                        id = R.id.action_camera_discard,
                        colorRes = R.color.colorGrey,
                        iconRes = R.drawable.ic_circle_cancel,
                        iconTintRes = android.R.color.white,
                        alignEnd = false,
                        visible = false
                )
        )

        this.torchEnabled = false
    }

    override fun onDestroyView() {
        this.uxCameraView.stop()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        this.showCaptureActions()

        this.torchEnabledProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOnMainThread()
                .subscribe {
                    this.uxCameraView.flash = when (it.value) {
                        true -> CameraKit.Constants.FLASH_TORCH
                        false -> CameraKit.Constants.FLASH_OFF
                    }

                    val iconTint: Int = when (it.value) {
                        true -> R.color.colorAccent
                        false -> android.R.color.black
                    }

                    this.actionItems = this.actionItems.apply {
                        first { it.id == R.id.action_camera_flash }
                                .iconTintRes = iconTint
                    }

                }

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe { actionId ->
                    when (actionId) {
                        R.id.action_camera_trigger -> {
                            // Hide trigger button
                            this.actionItems = this.actionItems.apply {
                                first { it.id == R.id.action_camera_trigger }
                                        .visible = false
                            }

                            // Show activity indicator, as capture will take a short while
                            this.uxProgressContainer.visibility = View.VISIBLE

                            this.sounds.playCameraClick()

                            Observable.fromCallable {
                                this@BaseCameraScreen.uxCameraView.captureImage()
                            }
                                    .subscribeOn(executor = executorService)
                                    .subscribe()
                        }

                        R.id.action_camera_flash -> {
                            this.torchEnabled = !this.torchEnabled
                        }

                        R.id.action_camera_discard -> {
                            this.showCaptureActions()
                        }

                        R.id.action_camera_save_finish,
                        R.id.action_camera_save -> {
                            this.pictureJpeg?.also {
                                this@BaseCameraScreen.listener?.onCameraScreenImageSubmitted(
                                        sender = this,
                                        jpeg = it)
                            } ?: log.warn("Image save invoked without picture data being available")

                            when (actionId) {
                                R.id.action_camera_save_finish -> this.fragmentManager?.popBackStack()
                                else -> this.showCaptureActions()
                            }
                        }
                    }
                }

        // Synchronize camera usage with aidc reader
        this.cameraAidcReader
                .isCameraInUse
                .takeUntil { it == false  }
                .observeOnMainThread()
                .subscribeBy(onComplete ={
                    this.uxCameraView.start()
                })
    }

    private fun showCaptureActions() {
        this.uxPreviewImage.visibility = View.GONE
        this.uxCameraView.visibility = View.VISIBLE

        this.actionItems = this.actionItems.apply {
            forEach {
                when (it.id) {
                    R.id.action_camera_trigger,
                    R.id.action_camera_flash -> it.visible = true

                    R.id.action_camera_discard,
                    R.id.action_camera_save,
                    R.id.action_camera_save_finish -> it.visible = false
                }
            }
        }
    }

    private fun showImageActions() {
        this.uxPreviewImage.visibility = View.VISIBLE
        this.uxCameraView.visibility = View.GONE
        this.actionItems = this.actionItems.apply {
            forEach {
                when (it.id) {
                    R.id.action_camera_trigger,
                    R.id.action_camera_flash -> it.visible = false

                    R.id.action_camera_discard,
                    R.id.action_camera_save_finish -> it.visible = true
                    R.id.action_camera_save -> it.visible = allowMultiplePictures
                }
            }
        }
    }
}
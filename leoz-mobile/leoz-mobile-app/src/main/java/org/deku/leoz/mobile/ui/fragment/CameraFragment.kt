package org.deku.leoz.mobile.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flurgle.camerakit.CameraKit
import com.flurgle.camerakit.CameraListener
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.tnt.innight.mobile.Sounds
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_camera.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.jetbrains.anko.imageBitmap
import org.slf4j.LoggerFactory
import sx.rx.subscribeOn
import java.util.concurrent.ExecutorService

/**
 * Generic camera screen fragment
 *
 * Can be used standalone or derived from for eg. adding an overlay view.
 *
 * Created by phpr on 03.08.2017.
 */
open class CameraFragment : ScreenFragment<Any>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val executorService: ExecutorService by Kodein.global.lazy.instance()
    private val sounds: Sounds by Kodein.global.lazy.instance()

    private var pictureJpeg: ByteArray? = null

    private val listener by lazy {
        this.targetFragment as? CameraFragment.Listener
                ?: this.parentFragment as? CameraFragment.Listener
                ?: this.activity as? CameraFragment.Listener
    }

    interface Listener {
        fun onCameraImageTaken(jpeg: ByteArray)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
        }

        this.toolbarHidden = true
    }

    /** Can be overriden to add an overlay view to the camera screen */
    open fun onCreateOverlayView() { }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_camera, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxProgressContainer.visibility = View.VISIBLE

        this.uxCameraView.setJpegQuality(100)
        this.uxCameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE)
        this.uxCameraView.setFlash(CameraKit.Constants.FLASH_OFF)
        this.uxCameraView.setCameraListener(object : CameraListener() {

            override fun onPictureTaken(picture: ByteArray) {
                log.trace("PICTURE TAKEN WITH SIZE [${picture.size}]")

                this@CameraFragment.view?.post {
                    this@CameraFragment.pictureJpeg = picture

                    this@CameraFragment.uxProgressContainer.visibility = View.INVISIBLE

                    // Create a bitmap
                    this@CameraFragment.uxPreviewImage.imageBitmap = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                    this@CameraFragment.showImageActions()
                }
            }

            override fun onCameraOpened() {
                log.trace("CAMERA OPENED")
                this@CameraFragment.uxProgressContainer.post {
                    this@CameraFragment.uxProgressContainer.visibility = View.INVISIBLE
                }
            }

            override fun onCameraClosed() {
                log.trace("CAMERA CLOSED")
            }
        })

        Observable.fromCallable {
            log.trace("STARTING CAMERA")
            this.uxCameraView.start()
        }
                .subscribeOn(executor = this.executorService)
                .subscribe()
    }

    override fun onResume() {
        super.onResume()

        this.showCaptureActions()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
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
                                this@CameraFragment.uxCameraView.captureImage()
                            }
                                    .subscribeOn(executor = executorService)
                                    .subscribe()
                        }

                        R.id.action_camera_flash -> {
                            // TODO: implement torch mode support in CameraKit
                            //this.uxCameraView.setFlash(CameraKit.Constants.FLASH_TORCH)
                        }

                        R.id.action_camera_discard -> {
                            this.showCaptureActions()
                        }

                        R.id.action_camera_save -> {
                            this@CameraFragment.listener?.onCameraImageTaken(this.pictureJpeg!!)
                            this@CameraFragment.activity.supportFragmentManager.popBackStack()
                        }
                    }
                }
    }

    override fun onDestroyView() {
        this.uxCameraView.stop()
        super.onDestroyView()
    }

    private fun showCaptureActions() {
        this.uxPreviewImage.visibility = View.GONE
        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_camera_trigger,
                        iconRes = android.R.drawable.ic_menu_camera,
                        iconTintRes = android.R.color.white,
                        colorRes = R.color.colorPrimary
                )

                // Disabled flash control for now. AUTO should be ok. TORCH requires some work
                // as it's not available via CameraKit.
//                ,ActionItem(
//                        id = R.id.action_camera_flash,
//                        iconRes = R.drawable.ic_flash,
//                        iconTintRes = android.R.color.white,
//                        colorRes = R.color.colorDarkGrey,
//                        alignEnd = false
//                )
        )
    }

    private fun showImageActions() {
        this.uxPreviewImage.visibility = View.VISIBLE
        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_camera_save,
                        iconRes = R.drawable.ic_finish,
                        iconTintRes = android.R.color.white,
                        colorRes = R.color.colorPrimary
                ),
                ActionItem
                (
                        id = R.id.action_camera_discard,
                        iconRes = R.drawable.ic_circle_cancel,
                        iconTintRes = android.R.color.black,
                        colorRes = R.color.colorAccent,
                        alignEnd = false
                )
        )
    }
}
package org.deku.leoz.mobile.ui.screen

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flurgle.camerakit.CameraListener
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.ScreenFragment
import sx.android.fragment.CameraFragment
import kotlinx.android.synthetic.main.screen_postbox_delivery.*
import android.graphics.BitmapFactory
import com.flurgle.camerakit.CameraKit
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import org.deku.leoz.mobile.ui.view.ActionItem
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty


/**
 * Created by phpr on 14.07.2017.
 */
class PostboxDeliveryScreen: ScreenFragment(), CameraFragment.Listener {
    private val log = LoggerFactory.getLogger(this.javaClass)

    var stop: Stop? = null
    var bitmap: Bitmap? = null

    val actionItemCapture = listOf(
            ActionItem(
                    id = R.id.action_camera_trigger,
                    iconRes = R.drawable.ic_menu_camera,
                    colorRes = R.color.colorAccent
            )
    )
    val actionItemImage = listOf(
            ActionItem(
                    id = R.id.action_camera_save,
                    iconRes = R.drawable.ic_check_circle,
                    colorRes = R.color.colorGreen
            ),
            ActionItem
                (
                id = R.id.action_camera_discard,
                iconRes = R.drawable.ic_circle_cancel,
                colorRes = R.color.colorRed
            )
    )


    companion object {
        val stateProperty = ObservableRxProperty<State>(State.TAKE_PICTURE)
        var state by stateProperty

        fun create(stop: Stop): PostboxDeliveryScreen {
            val f = PostboxDeliveryScreen()
            f.stop = stop
            return f
        }

        enum class State {
            TAKE_PICTURE, REVIEW_PICTURE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_postbox_delivery, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        this.childFragmentManager.withTransaction {
//            it.replace(this.uxContainer.id, LoginFragment(), CameraFragment::class.java.canonicalName)
//        }

        this.uxCameraView.setJpegQuality(100)
        this.uxCameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE)
        this.uxCameraView.setFlash(CameraKit.Constants.FLASH_AUTO)
        this.uxCameraView.setCameraListener(object : CameraListener() {
            override fun onPictureTaken(picture: ByteArray?) {
                super.onPictureTaken(picture)
                // Create a bitmap
                bitmap = BitmapFactory.decodeByteArray(picture, 0, picture!!.size)
                state = State.REVIEW_PICTURE
            }
        })
    }

    override fun onResume() {
        super.onResume()
        this.uxCameraView.start()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_camera_trigger -> {
                            this.uxCameraView.captureImage()
                        }

                        R.id.action_camera_flash -> {
                            //this.uxCameraView.toggleFlash()
                        }

                        R.id.action_camera_discard -> {
                            state = State.TAKE_PICTURE
                        }

                        R.id.action_camera_save -> {
                            state = State.TAKE_PICTURE
                        }
                    }
                }

        stateProperty
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    log.debug("State property changed")
                    when (it.value) {
                        State.TAKE_PICTURE -> {
                            this.actionItems = actionItemCapture
                            this.uxContainer.visibility = View.GONE
                        }
                        State.REVIEW_PICTURE -> {
                            this.actionItems = actionItemImage
                            this.uxContainer.visibility = View.VISIBLE
                            this.uxPreviewImage.setImageBitmap(bitmap)
                        }
                    }
                }

        state = state

    }

    override fun onPause() {
        super.onPause()
        this.uxCameraView.stop()
    }

    override fun onCameraFragmentPictureTaken(data: ByteArray?) {

    }

    override fun onCameraFragmentDiscarded() {

    }

    override fun onCameraFragmentShutter() {

    }
}
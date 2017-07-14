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
import sx.android.fragment.util.withTransaction
import kotlinx.android.synthetic.main.screen_postbox_delivery.*
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import android.graphics.BitmapFactory
import com.flurgle.camerakit.CameraKit
import com.flurgle.camerakit.Flash
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import org.deku.leoz.mobile.ui.view.ActionItem


/**
 * Created by phpr on 14.07.2017.
 */
class PostboxDeliveryScreen: ScreenFragment(), CameraFragment.Listener {

    var stop: Stop? = null
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
        fun create(stop: Stop): PostboxDeliveryScreen {
            val f = PostboxDeliveryScreen()
            f.stop = stop
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_postbox_delivery, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true

        this.actionItems = actionItemCapture
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
                val result = BitmapFactory.decodeByteArray(picture, 0, picture!!.size)
                showPreview(bitmap = result)
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

                        R.id.action_camera_discard -> showCamera()
                    }
                }
    }

    override fun onPause() {
        super.onPause()
        this.uxCameraView.stop()
    }

    private fun showPreview(bitmap: Bitmap) {
        //this.uxCameraView.stop()
        //this.uxCameraView.visibility = View.VISIBLE
        this.actionItems = actionItemImage
        this.uxContainer.visibility = View.VISIBLE
        this.uxPreviewImage.setImageBitmap(bitmap)
    }

    private fun showCamera() {
        this.uxContainer.visibility = View.GONE
        this.actionItems = actionItemCapture
    }

    override fun onCameraFragmentPictureTaken(data: ByteArray?) {

    }

    override fun onCameraFragmentDiscarded() {

    }

    override fun onCameraFragmentShutter() {

    }
}
package org.deku.leoz.mobile.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flurgle.camerakit.CameraKit
import com.flurgle.camerakit.CameraListener
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_camera.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.Storage
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by phpr on 03.08.2017.
 */
class CameraFragment : ScreenFragment<Any>() {

    val storage: Storage by Kodein.global.lazy.instance()

    var bitmap: Bitmap? = null
    var order: Order? = null
    var parcel: Parcel? = null
    var type: PictureType? = null

    val actionItemCapture = listOf(
            ActionItem(
                    id = R.id.action_camera_trigger,
                    iconRes = android.R.drawable.ic_menu_camera,
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

    private val listener by lazy { this.activity as? CameraFragment.Listener }

    companion object {
        fun create(order: Order, type: PictureType): CameraFragment {
            val f = CameraFragment()
            return f
        }

        fun create(parcel: Parcel, type: PictureType): CameraFragment {
            val f = CameraFragment()
            return f
        }

        enum class PictureType {
            PARCEL_DAMAGED,
            PARCEL_OTHER,
            POSTBOX
        }
    }

    interface Listener {
        fun onCameraImageTaken(bitmap: Bitmap)
        fun onCameraImageSaved(file: File)
        fun onCameraCancelled()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_camera, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxCameraView.setJpegQuality(100)
        this.uxCameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE)
        this.uxCameraView.setFlash(CameraKit.Constants.FLASH_AUTO)
        this.uxCameraView.setCameraListener(object : CameraListener() {
            override fun onPictureTaken(picture: ByteArray?) {
                super.onPictureTaken(picture)
                // Create a bitmap
                bitmap = BitmapFactory.decodeByteArray(picture, 0, picture!!.size)
                this@CameraFragment.actionItems = actionItemCapture
                this@CameraFragment.uxContainer.visibility = View.GONE
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

                        }

                        R.id.action_camera_discard -> {

                        }

                        R.id.action_camera_save -> {
                            if (order == null && parcel == null)
                                this@CameraFragment.listener?.onCameraImageTaken(bitmap!!)
                            else
                                saveImage(bitmap!!)
                        }
                    }
                }
    }

    fun saveImage(bitmap: Bitmap) {
        val filename = "${type?.name}_${if (order == null) "PARCEL-${parcel?.id}" else "ORDER-${order?.id}"}_${Date().time}.jpg"
        val file: File = File(storage.imageDir, filename)
        val os = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os)
        os.close()

        this@CameraFragment.listener?.onCameraImageSaved(file = file)
    }
}
package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.ScreenFragment
import sx.android.fragment.CameraFragment
import sx.android.fragment.util.withTransaction
import kotlinx.android.synthetic.main.screen_postbox_delivery.*
import org.deku.leoz.mobile.ui.fragment.LoginFragment

/**
 * Created by phpr on 14.07.2017.
 */
class PostboxDeliveryScreen: ScreenFragment(), CameraFragment.Listener {

    var stop: Stop? = null

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
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, LoginFragment(), CameraFragment::class.java.canonicalName)
        }
    }

    override fun onCameraFragmentPictureTaken(data: ByteArray?) {

    }

    override fun onCameraFragmentDiscarded() {

    }

    override fun onCameraFragmentShutter() {

    }
}
package org.deku.leoz.mobile.ui.screen

import android.view.View
import android.view.ViewGroup
import org.deku.leoz.mobile.R

/**
 * Postbox delivery camera screen
 * Created by masc on 24.08.17.
 */
class PostboxCameraScreen : BaseCameraScreen<Any>() {

    override fun createOverlayView(viewGroup: ViewGroup): View? {
        return this.activity.layoutInflater
                .inflate(R.layout.view_delivery_postbox, viewGroup, false)
                .also {
                    it.alpha = 0.75F
                }
    }
}

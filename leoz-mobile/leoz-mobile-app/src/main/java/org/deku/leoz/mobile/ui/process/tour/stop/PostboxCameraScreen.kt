package org.deku.leoz.mobile.ui.process.tour.stop

import android.view.View
import android.view.ViewGroup
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.core.BaseCameraScreen

/**
 * Postbox delivery camera screen
 * Created by masc on 24.08.17.
 */
class PostboxCameraScreen : BaseCameraScreen<Any>() {

    init {
        this.allowMultiplePictures = false
    }

    override fun createOverlayView(viewGroup: ViewGroup): View? {
        return this.activity.layoutInflater
                .inflate(R.layout.view_delivery_postbox, viewGroup, false)
                .also {
                    it.alpha = 0.75F
                }
    }
}

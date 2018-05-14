package org.deku.leoz.mobile.ui.process.tour.stop

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.screen_tour_stop_sign_on_paper.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.core.BaseCameraScreen

/**
 * Signature on paper camera screen
 * Created by masc on 24.08.17.
 */
class SignOnPaperCameraScreen : BaseCameraScreen<SignOnPaperCameraScreen.Parameters>() {

    @Parcelize
    data class Parameters (var name: String): Parcelable
    
    init {
        this.allowMultiplePictures = false
    }

    override fun createOverlayView(viewGroup: ViewGroup): View? {
        return this.activity.layoutInflater
                .inflate(R.layout.screen_tour_stop_sign_on_paper, viewGroup, false)
                .also {
                    it.uxName.text = this.parameters.name
                    it.alpha = 0.75F
                }
    }
}

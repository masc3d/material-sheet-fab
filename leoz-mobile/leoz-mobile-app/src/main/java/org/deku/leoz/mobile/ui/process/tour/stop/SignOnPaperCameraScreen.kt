package org.deku.leoz.mobile.ui.process.tour.stop

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.screen_tour_stop_sign_on_paper.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.core.BaseCameraScreen
import org.parceler.Parcel
import org.parceler.ParcelConstructor

/**
 * Signature on paper camera screen
 * Created by masc on 24.08.17.
 */
class SignOnPaperCameraScreen : BaseCameraScreen<SignOnPaperCameraScreen.Parameters>() {

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var name: String
    )
    
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

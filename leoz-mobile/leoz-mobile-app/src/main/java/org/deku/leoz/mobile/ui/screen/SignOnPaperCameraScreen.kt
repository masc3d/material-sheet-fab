package org.deku.leoz.mobile.ui.screen

import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.view_delivery_sign_on_paper.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.repository.ParcelRepository
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

    override fun createOverlayView(viewGroup: ViewGroup): View? {
        return this.activity.layoutInflater
                .inflate(R.layout.view_delivery_sign_on_paper, viewGroup, false)
                .also {
                    it.uxName.text = this.parameters.name
                    it.alpha = 0.75F
                }
    }
}

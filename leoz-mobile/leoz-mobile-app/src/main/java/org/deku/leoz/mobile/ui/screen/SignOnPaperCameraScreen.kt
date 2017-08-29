package org.deku.leoz.mobile.ui.screen

import android.databinding.DataBindingUtil
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.item_parcel_card.view.*
import kotlinx.android.synthetic.main.item_sign_on_paper.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemParcelBinding
import org.deku.leoz.mobile.databinding.ItemParcelCardBinding
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.ui.vm.ParcelViewModel
import org.deku.leoz.model.EventDeliveredReason
import org.parceler.Parcel
import org.parceler.ParcelConstructor

/**
 * Damaged parcel camera screen
 * Created by masc on 24.08.17.
 */
class SignOnPaperCameraScreen(target: Fragment) : BaseCameraScreen<SignOnPaperCameraScreen.Parameters>(target) {

    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var name: String
    )

    override fun createOverlayView(viewGroup: ViewGroup): View? {
        val view = this.activity.layoutInflater.inflate(R.layout.item_sign_on_paper, viewGroup, false)

        view.uxName.text = this.parameters.name
        view.alpha = 0.75F

        return view
    }
}

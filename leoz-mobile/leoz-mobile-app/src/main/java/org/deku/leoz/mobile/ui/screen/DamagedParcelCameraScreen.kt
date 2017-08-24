package org.deku.leoz.mobile.ui.screen

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.item_parcel_card.view.*
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
class DamagedParcelCameraScreen : BaseCameraScreen<DamagedParcelCameraScreen.Parameters>() {

    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    @Parcel(Parcel.Serialization.BEAN)
    class Parameters @ParcelConstructor constructor(
            var parcelId: Long
    )

    override fun createOverlayView(viewGroup: ViewGroup): View? {
        val binding = DataBindingUtil.inflate<ItemParcelCardBinding>(
                this.activity.layoutInflater,
                R.layout.item_parcel_card,
                viewGroup,
                false)

        binding.root.uxCardView.alpha = 0.75F

        val parcel = parcelRepository.entities.first { it.id == this.parameters.parcelId }
        binding.parcel = ParcelViewModel(
                parcel = parcel,
                parcelIcon = R.drawable.ic_damaged,
                showDimensions = false)

        return binding.root
    }
}

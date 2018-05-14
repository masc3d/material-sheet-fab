package org.deku.leoz.mobile.ui.process.tour.stop

import android.databinding.DataBindingUtil
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.item_parcel_card.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemParcelCardBinding
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.ui.core.BaseCameraScreen
import org.deku.leoz.mobile.ui.vm.ParcelViewModel

/**
 * Damaged parcel camera screen
 * Created by masc on 24.08.17.
 */
class DamagedParcelCameraScreen : BaseCameraScreen<DamagedParcelCameraScreen.Parameters>() {

    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    @Parcelize
    data class Parameters (var parcelId: Long): Parcelable

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

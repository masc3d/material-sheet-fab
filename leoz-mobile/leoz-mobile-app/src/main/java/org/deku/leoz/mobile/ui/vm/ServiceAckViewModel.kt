package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.ObservableField
import android.view.View
import org.deku.leoz.model.ParcelService

/**
 * Service acknowledgement view model
 * Created by masc on 02.05.18.
 */
class ServiceAckViewModel(
        context: Context,
        service: ParcelService)
    : ServiceViewModel(
        context,
        service
) {
    /** Reflects confirmation state */
    val confirmed = ObservableField<Boolean>(false)

    fun onClick(view: View) {
        /** Update confirmation state when view is clicked */
        this.confirmed.set(this.confirmed.get()?.not() ?: false)
    }
}
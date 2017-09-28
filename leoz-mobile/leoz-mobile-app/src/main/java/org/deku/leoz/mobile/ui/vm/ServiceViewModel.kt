package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.support.annotation.DrawableRes
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService

/**
 * Service view model
 * Created by masc on 09.08.17.
 * @param context Context required for on the fly service text translation with default value
 * @param servic Pracel service
 */
class ServiceViewModel(
        val context: Context,
        val service: ParcelService) : BaseObservable() {

    @get:DrawableRes
    val icon: Int = service.mobile.icon
    val text: String = service.mobile.textOrName(context)
}
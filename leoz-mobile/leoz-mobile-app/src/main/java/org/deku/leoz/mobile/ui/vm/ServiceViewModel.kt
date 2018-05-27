package org.deku.leoz.mobile.ui.vm

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.view.View
import org.deku.leoz.mobile.model.mobile
import org.deku.leoz.model.ParcelService

/**
 * Service view model
 * Created by masc on 09.08.17.
 * @param context Context required for on the fly service text translation with default value
 * @param servic Pracel service
 */
open class ServiceViewModel(
        val context: Context,
        val service: ParcelService) : BaseObservable() {

    @get:DrawableRes
    val icon: Int = service.mobile.icon

    val text: String by lazy { service.mobile.textOrName(context) }

    val acknowledgeText: String by lazy { service.mobile.ackMessageText(context) ?: "" }

    /** Reflects confirmation state */
    val confirmed by lazy { ObservableField<Boolean>(false) }

    fun onClick(view: View) {
        /** Update confirmation state when view is clicked */
        this.confirmed.set(this.confirmed.get()?.not() ?: false)
    }
}
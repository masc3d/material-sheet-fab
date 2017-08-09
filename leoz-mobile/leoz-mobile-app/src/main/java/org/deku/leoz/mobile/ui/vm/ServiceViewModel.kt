package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.deku.leoz.mobile.model.process.mobile
import org.deku.leoz.model.ParcelService

/**
 * Service view model
 * Created by masc on 09.08.17.
 */
class ServiceViewModel(val service: ParcelService) : BaseObservable() {

    @get:DrawableRes val icon: Int = service.mobile.icon
    @get:StringRes val text: Int = service.mobile.text
}
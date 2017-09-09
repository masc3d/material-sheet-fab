package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.net.NetworkInfo
import org.slf4j.LoggerFactory
import sx.android.Connectivity
import sx.android.databinding.toField

/**
 * Connectivity view model
 * Created by masc on 08.09.17.
 */
class ConnectivityViewModel(
        val connectivity: Connectivity
) : BaseObservable() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    val isAvailable: ObservableField<Boolean> by lazy { this.connectivity.networkProperty.map { it.value.state == NetworkInfo.State.CONNECTED }.toField() }
}

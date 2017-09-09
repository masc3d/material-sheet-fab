package org.deku.leoz.mobile.ui.vm

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.view_update_indicator.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.service.UpdateService
import org.slf4j.LoggerFactory
import sx.android.databinding.toField

/**
 * Update service view model
 * Created by masc on 08.09.17.
 */
class UpdateServiceViewModel(
        val updateService: UpdateService
) : BaseObservable() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Value from 0..1000 indicating progress */
    val progress: ObservableField<Float> by lazy { this.updateService.downloadProgressEvent.map { it.progress }.toField() }

    /** Progress visibility */
    val isVisible: ObservableField<Boolean> by lazy { this.updateService.downloadProgressEvent.map { it.progress > 0.0F && it.progress < 100.0F }.toField() }
}

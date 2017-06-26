package org.deku.leoz.mobile.ui.vm

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R

/**
 * Flexible view model holder
 * Created by masc on 26.06.17.
 */
class FlexibleViewModelHolder(
        val view: View,
        val adapter: FlexibleAdapter<out IFlexible<*>>)
    :
        FlexibleViewHolder(view, adapter) {

    val binding: ViewDataBinding

    init {
        this.binding = DataBindingUtil.bind(view)
        this.binding.executePendingBindings()
    }
}
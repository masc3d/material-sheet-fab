package org.deku.leoz.mobile.ui.vm

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader

/**
 * Created by masc on 06.07.17.
 */
class FlexibleVmHeaderItem<VM>(
        val viewRes: Int,
        val variableId: Int,
        val viewModel: VM
)
    :
        IHeader<FlexibleVmHolder>,
        IFlexible<FlexibleVmHolder> by FlexibleVmItem(viewRes, variableId, viewModel) {

    init {
        this.isSelectable = false
        this.isHidden = true
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): FlexibleVmHolder {
        return FlexibleVmHolder(view, adapter, true)
    }
}

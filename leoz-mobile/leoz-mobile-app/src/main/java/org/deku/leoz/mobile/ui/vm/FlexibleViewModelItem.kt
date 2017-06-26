package org.deku.leoz.mobile.ui.vm

import android.databinding.ViewDataBinding
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.deku.leoz.mobile.databinding.ItemStopBinding

/**
 * Created by masc on 26.06.17.
 */
class FlexibleViewModelItem<VM>(
        val viewRes: Int,
        val variableId: Int,
        val viewModel: VM
)
    : AbstractFlexibleItem<FlexibleViewModelHolder>() {

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int{
        return super.hashCode()
    }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<out IFlexible<*>>,
            viewHolder: FlexibleViewModelHolder,
            position: Int,
            payloads: MutableList<Any?>) {

        viewHolder.binding.setVariable(this.variableId, this.viewModel)
        viewHolder.binding.executePendingBindings()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): FlexibleViewModelHolder {
        return FlexibleViewModelHolder(view, adapter)
    }

    override fun getLayoutRes(): Int {
        return this.viewRes
    }
}
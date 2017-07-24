package sx.android.ui.flexibleadapter

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible

/**
 * Flexible view model item
 * Created by masc on 26.06.17.
 */
class FlexibleVmItem<VM>(
        val viewRes: Int,
        val variableId: Int,
        val viewModel: VM
)
    : AbstractFlexibleItem<FlexibleVmHolder>() {

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int{
        return super.hashCode()
    }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<out IFlexible<*>>,
            viewHolder: FlexibleVmHolder,
            position: Int,
            payloads: MutableList<Any?>) {

        viewHolder.binding.setVariable(this.variableId, this.viewModel)
        viewHolder.binding.executePendingBindings()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): FlexibleVmHolder {
        return FlexibleVmHolder(view, adapter)
    }

    override fun getLayoutRes(): Int {
        return this.viewRes
    }
}
package sx.android.ui.flexibleadapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Flexible expandable view model holder
 * Created by masc on 26.06.17.
 */
class FlexibleExpandableVmHolder(
        val view: View,
        val adapter: FlexibleAdapter<out IFlexible<*>>,
        isStickyHeader: Boolean = false)
    :
        ExpandableViewHolder(view, adapter, isStickyHeader) {

    val binding: ViewDataBinding

    init {
        this.binding = DataBindingUtil.bind(view)
        this.binding.executePendingBindings()
    }
}
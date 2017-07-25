package sx.android.ui.flexibleadapter

import android.databinding.DataBindingUtil
import android.view.View
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.*
import eu.davidea.viewholders.ExpandableViewHolder
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Flexible expandable view model item
 * Created by masc on 26.06.17.
 */
class FlexibleExpandableVmItem<EVM, IVM>(
        val viewRes: Int,
        val variableId: Int,
        val viewModel: EVM,
        val blurRadius: Float = 1F
)
    : AbstractExpandableHeaderItem<FlexibleExpandableVmHolder, FlexibleVmSectionableItem<IVM>>() {

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<out IFlexible<*>>,
            viewHolder: FlexibleExpandableVmHolder,
            position: Int,
            payloads: MutableList<Any?>) {

        viewHolder.binding.setVariable(this.variableId, this.viewModel)
        viewHolder.binding.executePendingBindings()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): FlexibleExpandableVmHolder {
        // Blur view support
        if (view is BlurView) {
            val rootView = adapter.recyclerView

            // Remove background. it's commonly only set for design preview, as blur view uses custom attribute `blurOverlayColor`
            view.background = null

            view.setupWith(rootView)
                    .windowBackground(adapter.recyclerView.background)
                    .blurAlgorithm(RenderScriptBlur(view.context))
                    .blurRadius(this.blurRadius)
        }

        return FlexibleExpandableVmHolder(view, adapter, true)
    }

    override fun getLayoutRes(): Int {
        return this.viewRes
    }
}
package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.LayoutRes
import android.view.View
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible

/**
 * Flexible expandable view model header/item.
 * Can be used as both an expandable item or header.
 * Created by masc on 26.06.17.
 */
class ExpandableVmItem<EVM, IVM>(
        @LayoutRes val view: Int,
        @AnyRes val variable: Int,
        val viewModel: EVM,
        val blurRadius: Float = 1F,
        val isTransitionsEnabled: Boolean = false,
        var isExpandableOnClick: Boolean = true
)
    : AbstractExpandableHeaderItem<VmHolder, SectionableVmItem<IVM>>() {

    override fun equals(other: Any?): Boolean =
            this === other

    override fun hashCode(): Int =
            super.hashCode()

    override fun bindViewHolder(
            adapter: FlexibleAdapter<out IFlexible<*>>,
            viewHolder: VmHolder,
            position: Int,
            payloads: MutableList<Any?>) {

        viewHolder.binding.setVariable(this.variable, this.viewModel)
        viewHolder.binding.executePendingBindings()
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: VmHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)
        // Unbind view holder
        holder?.binding?.unbind()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): VmHolder {
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

        return VmHolder(
                view = view,
                adapter = adapter,
                isStickyHeader = true,
                isExpandableOnClick = isExpandableOnClick)
    }

    override fun getLayoutRes(): Int =
            this.view
}
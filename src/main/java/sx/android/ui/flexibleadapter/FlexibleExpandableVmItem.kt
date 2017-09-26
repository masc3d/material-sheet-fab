package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.LayoutRes
import android.view.View
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.*

/**
 * Flexible expandable view model header/item.
 * Can be used as both an expandable item or header.
 * Created by masc on 26.06.17.
 */
class FlexibleExpandableVmItem<EVM, IVM>(
        @LayoutRes val view: Int,
        @AnyRes val variable: Int,
        val viewModel: EVM,
        val blurRadius: Float = 1F,
        var isExpandableOnClick: Boolean = true
)
    : AbstractExpandableHeaderItem<FlexibleExpandableVmHolder, FlexibleSectionableVmItem<IVM>>() {

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

        viewHolder.binding.setVariable(this.variable, this.viewModel)
        viewHolder.binding.executePendingBindings()
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: FlexibleExpandableVmHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)
        // Unbind view holder
        holder?.binding?.unbind()
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

        return FlexibleExpandableVmHolder(
                view = view,
                adapter = adapter,
                isStickyHeader = true,
                isExpandableOnClick = isExpandableOnClick)
    }

    override fun getLayoutRes(): Int {
        return this.view
    }
}
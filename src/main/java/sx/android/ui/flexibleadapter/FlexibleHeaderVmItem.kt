package sx.android.ui.flexibleadapter

import android.view.View
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import org.slf4j.LoggerFactory

/**
 * Flexible view model header item.
 * Supports blur view as root layout of the header view and will set it up accordingly.
 * Created by masc on 06.07.17.
 * @param viewRes The view layout resource ID
 * @param variableId View model binding ID
 * @param viewModel The view model
 * @param blurRadius Blur radius (applicable when root layout of view is a BlurView)
 */
class FlexibleHeaderVmItem<VM>(
        val viewRes: Int,
        val variableId: Int,
        val viewModel: VM,
        val blurRadius: Float = 1F
)
    :
        IHeader<FlexibleVmHolder>,
        IFlexible<FlexibleVmHolder> by FlexibleVmItem(viewRes, variableId, viewModel) {

    private val log = LoggerFactory.getLogger(this.javaClass)

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

        return FlexibleVmHolder(view, adapter, true)
    }
}

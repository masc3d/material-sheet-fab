package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import eu.davidea.flexibleadapter.items.IHeader
import org.slf4j.LoggerFactory

/**
 * View model header item
 * Supports blur view as root layout of the header view and will set it up accordingly.
 * Created by masc on 06.07.17.
 * @param view The view layout resource ID
 * @param variable View model binding ID
 * @param viewModel The view model
 * @param blurRadius Blur radius (applicable when root layout of view is a BlurView)
 * @param dragHandleViewId View id of the drag handle
 * @param isExpandableOnClick If subitems should be expandable on click
 */
open class VmHeaderItem<VM, IVM>(
        @LayoutRes view: Int,
        @AnyRes variable: Int,
        viewModel: VM,
        blurRadius: Float = 1F,
        @IdRes dragHandleViewId: Int = 0,
        isExpandableOnClick: Boolean = true
) :
        IHeader<VmHolder>,
        VmItem<VM, IVM>(
                view = view,
                variable = variable,
                viewModel = viewModel,
                blurRadius = blurRadius,
                isExpandableOnClick = isExpandableOnClick,
                dragHandleViewId = dragHandleViewId,
                isHeader = true
        ) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        this.isSelectable = false
        this.isHidden = false
        this.isExpanded = true
    }
}

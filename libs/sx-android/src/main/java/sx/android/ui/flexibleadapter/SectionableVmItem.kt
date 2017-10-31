package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.flexibleadapter.items.ISectionable

/**
 * Flexible sectionable view model item
 * Created by masc on 06.07.17.
 * @param view The view layout resource ID
 * @param variable View model binding ID
 * @param viewModel The view model
 * @param blurRadius Blur radius (applicable when root layout of view is a BlurView)
 * @param dragHandleViewId The view id of the drag handle (wheb moving items is supported)
 */
open class SectionableVmItem<VM>(
        @LayoutRes view: Int,
        @AnyRes variable: Int,
        viewModel: VM,
        @IdRes dragHandleViewId: Int = 0
) :
        ISectionable<VmHolder, IHeader<VmHolder>>,
        VmItem<VM>(
                view = view,
                variable = variable,
                viewModel = viewModel,
                dragHandleViewId = dragHandleViewId) {

    var _header: IHeader<VmHolder>? = null

    override fun setHeader(header: IHeader<VmHolder>?) {
        _header = header
    }

    override fun getHeader(): IHeader<VmHolder>?
            = _header

    init {
        this.isSelectable = false
        this.isHidden = false
    }

    override fun equals(other: Any?): Boolean =
            this === other

    override fun hashCode(): Int =
            super.hashCode()
}

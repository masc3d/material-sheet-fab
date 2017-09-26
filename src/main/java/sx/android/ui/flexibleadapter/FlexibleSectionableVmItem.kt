package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.flexibleadapter.items.ISectionable

/**
 * Created by masc on 06.07.17.
 */
open class FlexibleSectionableVmItem<VM>(
        @LayoutRes view: Int,
        @AnyRes variable: Int,
        viewModel: VM,
        @IdRes handleViewId: Int = 0
)
    :
        ISectionable<FlexibleVmHolder, IHeader<FlexibleExpandableVmHolder>>,
        FlexibleVmItem<VM>(view, variable, viewModel, handleViewId) {

    var _header: IHeader<FlexibleExpandableVmHolder>? = null

    override fun setHeader(header: IHeader<FlexibleExpandableVmHolder>?) {
        _header = header
    }

    override fun getHeader(): IHeader<FlexibleExpandableVmHolder>?
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

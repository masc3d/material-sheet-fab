package sx.android.ui.flexibleadapter

import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.flexibleadapter.items.ISectionable

/**
 * Created by masc on 06.07.17.
 */
class FlexibleVmSectionableItem<VM>(
        val viewRes: Int,
        val variableId: Int,
        val viewModel: VM
)
    :
        ISectionable<FlexibleVmHolder, IHeader<FlexibleExpandableVmHolder>>,
        IFlexible<FlexibleVmHolder> by FlexibleVmItem(viewRes, variableId, viewModel) {

    var _header: IHeader<FlexibleExpandableVmHolder>? = null

    override fun setHeader(header: IHeader<FlexibleExpandableVmHolder>?) {
        _header = header
    }

    override fun getHeader(): IHeader<FlexibleExpandableVmHolder>? {
        return _header
    }

    init {
        this.isSelectable = false
        this.isHidden = false
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.LayoutRes
import eu.davidea.flexibleadapter.items.IHeader
import org.slf4j.LoggerFactory

/**
 * Flexible view model header item.
 * Supports blur view as root layout of the header view and will set it up accordingly.
 * Created by masc on 06.07.17.
 * @param view The view layout resource ID
 * @param variable View model binding ID
 * @param viewModel The view model
 * @param blurRadius Blur radius (applicable when root layout of view is a BlurView)
 */
class SimpleHeaderVmItem<VM>(
        @LayoutRes view: Int,
        @AnyRes variable: Int,
        viewModel: VM,
        blurRadius: Float = 1F
) :
        IHeader<VmHolder>,
        HeaderVmItem<VM, Any>(
                view = view,
                variable = variable,
                viewModel = viewModel,
                blurRadius = blurRadius
        ) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        this.isSelectable = false
    }
}

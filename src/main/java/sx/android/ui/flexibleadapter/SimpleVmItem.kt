package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes

/**
 * Simple view model item without subitems
 *
 * Created by masc on 26.06.17.
 * @param view The view layout resource ID
 * @param variable View model binding ID
 * @param viewModel The view model
 * @param dragHandleViewId View id of the drag handle
 */
open class SimpleVmItem<VM>(
        @LayoutRes view: Int,
        @AnyRes variable: Int,
        viewModel: VM,
        @IdRes dragHandleViewId: Int = 0
) :
        VmItem<VM, Any>(
                view = view,
                variable = variable,
                viewModel = viewModel,
                dragHandleViewId = dragHandleViewId
        ) {
}
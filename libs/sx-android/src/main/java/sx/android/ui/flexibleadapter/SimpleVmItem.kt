package sx.android.ui.flexibleadapter

import android.support.annotation.AnyRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes

/**
 * Flexible view model item
 * Created by masc on 26.06.17.
 * @property view The view layout resource ID
 * @property variable View model binding ID
 * @property viewModel The view model
 * @property dragHandleViewId The view id of the drag handle (wheb moving items is supported)
 * @property beginDelayedTransition Begins a transition everytime the binding is rebound (eg. bound fields change)
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
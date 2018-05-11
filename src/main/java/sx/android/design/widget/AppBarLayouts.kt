package sx.android.design.widget

import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout

/**
 * Indicates if app bar is expanded or not
 */
val AppBarLayout.isExpanded: Boolean
    get() {
        val behavior = (this.layoutParams as CoordinatorLayout.LayoutParams).behavior
        return if (behavior is AppBarLayout.Behavior) behavior.topAndBottomOffset == 0 else false
    }
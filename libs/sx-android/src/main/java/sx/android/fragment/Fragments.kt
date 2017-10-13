package sx.android.fragment

import android.R
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.TypedValue

/**
 * In a view pager contained ina a CoordinatorLayout with scrolling behaviour, child view may
 * be bigger than expected for seemless scrolling. When switching between scrolling and static
 * views/fragments this is not intended. This helper method removes the scrolling "oveerhead"
 * Created by n3 on 18/07/16.
 */
fun Fragment.removeScrollingExcessHeightWithinViewPager() {
    val view = this.view
    val parent = view?.parent
    if (parent is ViewPager) {
        val layoutParams = parent.layoutParams
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            if (layoutParams.behavior is AppBarLayout.ScrollingViewBehavior) {
                val tv = TypedValue()
                this.activity.theme.resolveAttribute(R.attr.actionBarSize, tv, true)
                val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());

                view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, actionBarHeight)
            }
        }
    }
}
package sx.android.ui.flexibleadapter.ext

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback
import eu.davidea.flexibleadapter.items.IFlexible

/**
 * Customize scroll behavior specifics of FlexibleAdapter
 */
fun <T : IFlexible<*>> FlexibleAdapter<T>.customizeScrollBehavior(
        scrollSpeed: Float
) {
    // There's currently a bug in flexibleadapter-5.0rc2 which doesn't detach existing/default
    // ItemTouchHelper, so customizing the callback (which internally attaches another touch helper)
    // must be done early.
    this.itemTouchHelperCallback = object : ItemTouchHelperCallback(this) {
        override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView?, viewSize: Int, viewSizeOutOfBounds: Int, totalSize: Int, msSinceStartScroll: Long): Int {
            val accelTimeFrameMs = 2000
            val timeRatio: Float = when {
                msSinceStartScroll > accelTimeFrameMs -> 1.0F
                else -> 0.3F + (0.7F * (msSinceStartScroll.toFloat() / accelTimeFrameMs))
            }

            val direction = java.lang.Math.signum(viewSizeOutOfBounds.toDouble()).toInt()
            return (scrollSpeed * direction * timeRatio).toInt()
        }
    }
}
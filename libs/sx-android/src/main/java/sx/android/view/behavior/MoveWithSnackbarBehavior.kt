package sx.android.view.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import org.slf4j.LoggerFactory

/**
 * Behavior for views nested within CoordinatorLayout, requiring upward move depending on SnackBar
 * @author masc
 */
class MoveWithSnackbarBehavior : CoordinatorLayout.Behavior<View> {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    constructor() : super() {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean =
            dependency is Snackbar.SnackbarLayout

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val y = Math.min(
                0F,
                dependency.getTranslationY() - dependency.height)

        child.setTranslationY(y)

        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        if (dependency.visibility == View.GONE)
            ViewCompat
                    .animate(child)
                    .translationY(0F)
                    .start()
    }
}
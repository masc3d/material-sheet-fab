package sx.android.widget.behavior

import android.R.attr.translationY
import android.opengl.ETC1.getHeight
import android.R.attr.translationY
import android.content.Context
import android.opengl.ETC1.getHeight
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
class MoveUpwardBehavior : CoordinatorLayout.Behavior<View> {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    constructor() : super() {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val translationY = Math.min(0F,
                ViewCompat.getTranslationY(dependency) - dependency.getHeight())

        ViewCompat.setTranslationY(child, translationY)

        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        if (dependency.visibility == View.GONE)
            ViewCompat.animate(child).translationY(0F).start()
    }
}
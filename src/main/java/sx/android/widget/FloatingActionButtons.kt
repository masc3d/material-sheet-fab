package sx.android.widget

import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat

/**
 * Set floating action button background tint
 */
fun FloatingActionButton.setBackgroundTint(colorRes: Int) {
    this.setBackgroundTintList(ContextCompat.getColorStateList(this.context, colorRes))
}

/**
 * Set floating action button icon tint
 */
fun FloatingActionButton.setIconTint(colorRes: Int) {
    val d = DrawableCompat.wrap(this.drawable)
    DrawableCompat.setTint(d, ContextCompat.getColor(this.context, colorRes))
}
package sx.android.view

import android.support.annotation.ColorRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat

/**
 * Set floating action button background tint
 */
fun FloatingActionButton.setBackgroundTint(@ColorRes colorRes: Int) {
    this.setBackgroundTintList(ContextCompat.getColorStateList(this.context, colorRes))
}

/**
 * Set floating action button icon tint
 */
fun FloatingActionButton.setIconTint(@ColorRes colorRes: Int) {
    DrawableCompat.setTint(
            DrawableCompat.wrap(this.drawable.mutate()),
            ContextCompat.getColor(this.context, colorRes))
}

/**
 * Set floating action button colors
 * @param backgroundTint Color resource for background tint
 * @param iconTint Color resource for icon tint
 */
fun FloatingActionButton.setColors(@ColorRes backgroundTint: Int, @ColorRes iconTint: Int) {
    this.setBackgroundTint(backgroundTint)
    this.setIconTint(iconTint)
}
package sx.android.ui.view

import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.ViewCompat
import android.widget.ImageView

/**
 * Set background tint
 * @param colorRes Color resource
 */
fun ImageView.setBackgroundTintRes(@ColorRes colorRes: Int) {
    ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(this.context, colorRes))
}

/**
 * Set icon tint
 * @param colorRes Color resource
 */
fun ImageView.setIconTintRes(@ColorRes colorRes: Int) {
    this.setIconTint(
            ContextCompat.getColor(this.context, colorRes))
}

/**
 * Set icon tint
 * @param color Color
 */
fun ImageView.setIconTint(@ColorInt color: Int) {
    DrawableCompat.setTint(
            DrawableCompat.wrap(this.drawable.mutate()),
            color)
}

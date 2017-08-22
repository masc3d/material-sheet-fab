package sx.android.view

import android.support.annotation.ColorRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.widget.ImageView

/**
 * Set background tint
 */
fun ImageView.setBackgroundTint(@ColorRes colorRes: Int) {
    this.setBackgroundTintList(ContextCompat.getColorStateList(this.context, colorRes))
}

/**
 * Set icon tint
 */
fun ImageView.setIconTint(@ColorRes colorRes: Int) {
    DrawableCompat.setTint(
            DrawableCompat.wrap(this.drawable.mutate()),
            ContextCompat.getColor(this.context, colorRes))
}

package sx.android.content

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat

// Context compatibility extensions

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable =
        ContextCompat.getDrawable(this, id)
                ?: throw IllegalArgumentException("Invalid drawable id [${id}]")

@ColorInt
fun Context.getColorCompat(@ColorRes id: Int): Int =
        ContextCompat.getColor(this, id)


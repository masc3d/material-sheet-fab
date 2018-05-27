package sx.android

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat

// Context compatibility extensions

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable =
        ContextCompat.getDrawable(this, id)
                ?: throw IllegalArgumentException("Invalid drawable id [${id}]")

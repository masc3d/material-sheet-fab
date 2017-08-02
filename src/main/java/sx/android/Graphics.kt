package sx.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.TypedValue

/** Convert dp to pixels */
fun Context.convertDpToPx(dp: Float): Int {
    return (dp * this.resources.displayMetrics.density).toInt();
}

fun Context.convertPxToDp(px: Int): Float {
    return px / this.resources.displayMetrics.density;
}


/**
 * Scale bitmap to square image, cropping as needed
 * @param src  Source bitmap
 * @param size Square size
 * @return
 */
fun Bitmap.scaleSquare(src: Bitmap, size: Int): Bitmap {
    // Calculate thumbnail scale/crop parameters
    val newWidth: Int
    val newHeight: Int
    val leftOffset: Int
    val topOffset: Int
    if (src.width < src.height) {
        val ratio = src.height.toDouble() / src.width.toDouble()
        newWidth = size
        newHeight = (size * ratio).toInt()
        leftOffset = 0
        topOffset = (newHeight - size) / 2
    } else {
        val ratio = src.width.toDouble() / src.height.toDouble()
        newWidth = (size * ratio).toInt()
        newHeight = size
        leftOffset = (newWidth - 200) / 2
        topOffset = 0
    }

    val scaled = Bitmap.createScaledBitmap(src, newWidth, newHeight, false)
    return Bitmap.createBitmap(scaled, leftOffset, topOffset, size, size)
}

/**
 * Convert drawable to bitmap
 * @return Converted bitmap
 */
fun Drawable.toBitmap(): Bitmap {
    val drawable = this
    var bitmap: Bitmap? = null

    if (drawable is BitmapDrawable) {
        val bitmapDrawable = drawable
        if (bitmapDrawable.bitmap != null) {
            return bitmapDrawable.bitmap
        }
    }

    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
    } else {
        bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    drawable.draw(canvas)
    return bitmap
}
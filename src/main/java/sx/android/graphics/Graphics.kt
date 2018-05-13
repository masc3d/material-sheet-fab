package sx.android.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.Base64
import java.io.ByteArrayOutputStream

/** Convert dp to pixels */
fun Context.convertDpToPx(dp: Float): Int =
        (dp * this.resources.displayMetrics.density).toInt()

/** Convert pixels to do */
fun Context.convertPxToDp(px: Int): Float =
        px / this.resources.displayMetrics.density

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
    var bitmap: Bitmap?

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

/**
 * Create a cross fade transition drawable
 */
fun Drawable.withTransitionTo(drawable: Drawable, durationMillis: Int): TransitionDrawable {
    val old = this
    val new = drawable

    return TransitionDrawable(listOf(
            old,
            new
    ).toTypedArray())
            .also {
                it.isCrossFadeEnabled = true
                it.startTransition(durationMillis)
            }
}

/**
 * Creates a new bitmap with opacity applied
 */
fun Bitmap.copyWithOpacity(alpha: Double): Bitmap {
    val transBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(transBitmap)
    canvas.drawARGB(0, 0, 0, 0)

    val paint = Paint()
    paint.setAlpha((alpha * 255).toInt())
    canvas.drawBitmap(this, 0F, 0F, paint)

    return transBitmap
}

/**
 * Created by phpr on 13.07.2017.
 */
fun Bitmap.toBase64(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): String {
    val byteArrayOS = ByteArrayOutputStream()
    this.compress(compressFormat, quality, byteArrayOS)
    return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
}

fun String.parseBase64Bitmap(): Bitmap {
    val decoded = Base64.decode(this, 0)
    return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
}
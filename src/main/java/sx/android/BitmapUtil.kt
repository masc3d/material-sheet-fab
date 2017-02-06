package sx.android

import android.graphics.Bitmap

/**
 * Created by masc on 02.12.14.
 */
object BitmapUtil {
    /**
     * Scale bitmap to square image, cropping as needed
     * @param src  Source bitmap
     * @param size Square size
     * @return
     */
    @JvmStatic fun scaleSquare(src: Bitmap, size: Int): Bitmap {
        // Calculate thumbnail scale/crop parameters
        var newWidth: Int
        var newHeight: Int
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
}

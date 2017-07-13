package sx.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import android.util.Base64

/**
 * Created by phpr on 13.07.2017.
 */
fun Bitmap.toBase64(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): String {
    val byteArrayOS = ByteArrayOutputStream()
    this.compress(compressFormat, quality, byteArrayOS)
    return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
}

fun Bitmap.fromBase64(base64: String): Bitmap {
    val decoded = Base64.decode(base64, 0)
    return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
}

fun String.toBitmap(base64: String): Bitmap {
    val decoded = Base64.decode(base64, 0)
    return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
}
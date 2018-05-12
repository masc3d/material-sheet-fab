package sx.android.content.res

import android.content.res.TypedArray
import android.support.annotation.ColorInt
import android.support.annotation.StyleableRes

/**
 * Created by masc on 12.05.18.
 */
@ColorInt
fun TypedArray.getColorOrNull(@StyleableRes index: Int): Int? =
        this.getColor(index, 0).let { if (it == 0) null else it }

/**
 * Recycles the typed array before leaving the scope
 */
fun TypedArray.use(block: (types: TypedArray) -> Unit) {
    try {
        block(this)
    } finally {
        this.recycle()
    }
}
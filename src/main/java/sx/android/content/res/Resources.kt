package sx.android.content.res

import android.content.res.Resources
import android.support.annotation.AnyRes

/**
 * Resources extensions
 * Created by masc on 26.02.18.
 */

/**
 * Resource entry name or null
 * @param id resource id
 */
fun Resources.getResourceEntryNameOrNull(@AnyRes id: Int): String? {
    return try {
        this.getResourceEntryName(id)
    } catch(e: Resources.NotFoundException) {
        null
    }
}

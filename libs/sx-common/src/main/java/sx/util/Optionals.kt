package sx.util

import java.util.Optional

/**
 * Created by masc on 25.01.18.
 */

/** Transform java optional to kotlin nullable */
fun <T> Optional<T>.toNullable(): T? =
        this.orElse(null)
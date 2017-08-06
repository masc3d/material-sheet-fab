package sx.requery

import io.requery.query.Result
import io.requery.query.Tuple

/**
 * Get first value of tuple
 */
fun <T> Tuple.scalar(): T {
    return this.get(0)
}

/**
 * Get scalar of tuple result
 */
fun <T> Result<Tuple>.scalarOr(default: T): T {
    return this.firstOrNull()?.scalar() ?: default
}
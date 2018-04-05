package sx.util

/**
 * Transforms if param not null or returns `this`.
 * Calls the specified function [block] with `param` and `this` as receiver and returns the result.
 */
fun <T, P> T.letWithNotNull(param: P?, block: T.(P) -> T): T =
        if (param != null) {
            block(param)
        } else {
            this
        }

/**
 * Transforms if iterable param not null and has items or returns `this`.
 * Calls the specified function [block] with `param` and `this` as receiver and returns the result.
 */
fun <T, P : Iterable<*>> T.letWithItems(param: P?, block: T.(P) -> T): T =
        if (param != null && param.count() > 0) {
            block(param)
        } else {
            this
        }

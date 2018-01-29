package sx.util

/**
 * Transforms if param not null or returns `this`.
 * Calls the specified function [block] with `param` and `this` as receiver and returns the result.
 */
fun <T, P> T.letWithParamNotNull(param: P?, block: T.(P) -> T): T =
        if (param != null) {
            block(param)
        } else {
            this
        }

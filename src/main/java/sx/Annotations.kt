package sx

/**
 * Annotation of type.
 * @throws IllegalArgumentException If type does not have the annotation
 * @param type Type of annotation
 */
fun <T> Class<*>.annotationOfType(type: Class<T>): T{
    return this.annotationOfTypeOrNull(type) ?:
            throw IllegalArgumentException("Object ${this} doesn't have annotation ${type}")
}

/**
 * Annotation of type or null
 * @param type Type of annotation
 */
fun <T> Class<*>.annotationOfTypeOrNull(type: Class<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return this.annotations.find { it.annotationClass.java == type } as T?
}

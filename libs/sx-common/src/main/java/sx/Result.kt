package sx

/**
 * Generic result class
 *
 * Result and error are mutually exclusive.
 * Setting both to a non-null value will yield {@link IllegalStateException}
 *
 * Created by masc on 05.07.17
 * @property value Result
 * @property error Error
 */
class Result<T> @JvmOverloads constructor (
        value: T? = null,
        val error: Throwable? = null
) {
    private val _value: T?

    /**
     * Delivers the result value or throws in case of error condition
     */
    val value: T
        get() = _value ?: throw error!!

    /**
     * Delivers the result value or null in case of error
     */
    val valueOrNull: T?
        get() = _value

    init {
        _value = value
    }

    /** Indicates if result has error condition */
    val hasError = (this.error != null)

    /** Indicates if result has result */
    val hasValue = (this.value != null)

    init {
        if (value != null && error != null)
            throw IllegalStateException("Result value and error are mutually exclusive")
    }
}
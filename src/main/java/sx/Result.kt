package sx

/**
 * Generic result class
 *
 * Result and error are mutually exclusive.
 * Setting both to a non-null value will yield {@link IllegalStateException}
 *
 * Created by masc on 05.07.17
 * @property result Result
 * @property error Error
 */
class Result<T> @JvmOverloads constructor (
        value: T? = null,
        val error: Throwable? = null
) {
    private val _value: T?

    /**
     * Delivers the result or throws in case of error condition
     */
    private val result: T
        get() = _value ?: throw error!!

    init {
        _value = value
    }

    /** Indicates if result has error condition */
    val hasError = (this.error != null)

    /** Indicates if result has result */
    val hasValue = (this.result != null)

    init {
        if (value != null && error != null)
            throw IllegalStateException("Result value and error are mutually exclusive")
    }
}
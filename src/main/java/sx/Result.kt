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
class Result<T> private constructor (
        value: T? = null,
        val error: Throwable? = null
) {
    private val _value: T?

    constructor(value: T): this(value, null)
    constructor(error: Throwable): this(null, error)

    /**
     * Delivers the result value or throws in case of error condition
     */
    val value: T
        get() {
            return _value ?: throw error!!
        }

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
    val hasValue = (_value != null)
}
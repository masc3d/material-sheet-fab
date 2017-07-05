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
        val result: T? = null,
        val error: Throwable? = null
) {
    /** Indicates if result has error condition */
    val hasError = (this.error != null)

    /** Indicates if result has result */
    val hasResult = (this.result != null)

    init {
        if (result != null && error != null)
            throw IllegalStateException("Result and error are mutually exclusive")
    }
}
package sx.requery

import io.reactivex.Maybe
import io.reactivex.Single
import io.requery.query.Result
import io.requery.query.Tuple
import io.requery.reactivex.ReactiveResult
import org.slf4j.LoggerFactory

/**
 * Get first value of tuple
 */
fun <T> Tuple.scalar(): T = this.get(0)

/**
 * Get scalar of tuple result
 */
fun <T> Result<Tuple>.scalar(): T? = this.firstOrNull()?.scalar()

/**
 * Get scalar of tuple result
 */
fun <T> Result<Tuple>.scalarOr(default: T): T = this.firstOrNull()?.scalar() ?: default

/**
 * Get scalar of reactive tuple result
 */
fun <T> ReactiveResult<Tuple>.scalar(): Maybe<T> = this
        .observable()
        .firstElement()
        .filter { it.get<Any?>(0) != null }
        .map { it.scalar<T>() }
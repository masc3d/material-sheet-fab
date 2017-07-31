package sx.requery

import io.reactivex.disposables.Disposable
import io.requery.query.Tuple
import io.requery.reactivex.ReactiveResult
import org.slf4j.LoggerFactory
import sx.rx.ObservableLazyRxProperty

/**
 * Observable query
 * Created by masc on 26.07.17.
 * @param query Reactive query
 * @param transform Transformation lambda
 * @param name Option name for the query. Used for logging
 */
abstract class BaseObservableQuery<Q, T>(
        private val query: ReactiveResult<Q>,
        private val transform: (queryType: Q) -> T,
        val name: String = ""
) : Disposable {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val subscription: Disposable

    /**
     * Observable lazy query result property
     */
    val result = ObservableLazyRxProperty({ listOf<T>() })

    init {
        this.subscription = this.query
                .observableResult()
                .subscribe {
                    val records = it.toList()
                    log.trace("RECORDS CHANGED [${name}] new size ${records.size}")
                    this.result.reset { records.map { transform(it) } }
                }
    }

    override fun isDisposed(): Boolean {
        return this.subscription.isDisposed
    }

    override fun dispose() {
        this.subscription.dispose()
    }
}

/**
 * Observable query
 * Created by masc on 26.07.17.
 * @param query Reactive query
 * @param name Option name for the query. Used for logging
 */
class ObservableQuery<E>(
        query: ReactiveResult<E>,
        name: String = ""
) : BaseObservableQuery<E, E>(
        name = name,
        query = query,
        transform = { it }
)

/**
 * Observable tuple query
 * @param query Reactive query
 * @param transform Lambda transforming the tuple into a specific type
 * @param name Option name for the query. Used for loggin
 */
class ObservableTupleQuery<T>(
        query: ReactiveResult<Tuple>,
        transform: (tuple: Tuple) -> T,
        name: String = ""
) : BaseObservableQuery<Tuple, T>(
        name = name,
        query = query,
        transform = transform
)

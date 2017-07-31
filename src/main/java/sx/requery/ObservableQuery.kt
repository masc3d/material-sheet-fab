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
 */
abstract class BaseObservableQuery<Q, T>(
        private val query: ReactiveResult<Q>,
        private val transform: (queryType: Q) -> T
): Disposable {
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
                    log.trace("RECORDS CHANGED ${this.result.count()}")
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
 */
class ObservableQuery<E>(
        query: ReactiveResult<E>
): BaseObservableQuery<E, E>(
        query = query,
        transform = { it }
)

/**
 * Observable tuple query
 * @param query Reactive query
 * @param transform Lambda transforming the tuple into a specific type
 */
class ObservableTupleQuery<T>(
        private val query: ReactiveResult<Tuple>,
        private val transform: (tuple: Tuple) -> T
): BaseObservableQuery<Tuple, T>(
        query = query,
        transform = transform
)

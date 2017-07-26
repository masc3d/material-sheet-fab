package sx.requery

import io.reactivex.disposables.Disposable
import io.requery.reactivex.ReactiveResult
import org.slf4j.LoggerFactory
import sx.rx.ObservableLazyRxProperty

/**
 * Observable query wrapper
 * Created by masc on 26.07.17.
 */
class ObservableQuery<E>(
        private val reactiveResult: ReactiveResult<E>
): Disposable {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val subscription: Disposable

    val result = ObservableLazyRxProperty({ listOf<E>() })

    init {
        this.subscription = this.reactiveResult
                .observableResult()
                .subscribe {
                    val records = it.toList()
                    log.trace("RECORDS CHANGED ${this.result.count()}")
                    this.result.reset { records }
                }
    }

    override fun isDisposed(): Boolean {
        return this.subscription.isDisposed
    }

    override fun dispose() {
        this.subscription.dispose()
    }
}
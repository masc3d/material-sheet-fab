package sx.requery

import io.reactivex.Observable
import io.requery.reactivex.ReactiveResult
import org.slf4j.LoggerFactory
import sx.rx.ObservableLazyRxProperty

/**
 * Observable query wrapper
 * Created by masc on 26.07.17.
 */
class ObservableQuery<E>(
        private val result: ReactiveResult<E>
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    val records = ObservableLazyRxProperty({ listOf<E>() })

    init {
        this.result
                .observableResult()
                .subscribe {
                    val records = it.toList()
                    log.trace("RECORDS CHANGED ${this.records.count()}")
                    this.records.reset { records }
                }
    }
}
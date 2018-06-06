package sx.lang

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import sx.log.slf4j.info
import sx.rx.limit

/**
 * Close a list of auto-closeables at once, optionally concurrently
 *
 * Created by masc on 06.06.18.
 */
fun List<AutoCloseable>.close(maxConcurrency: Int = this.count()) {
    val log = LoggerFactory.getLogger(AutoCloseable::class.java)

    log.info { "Closing ${this.count()} with concurrency=${maxConcurrency}" }

    val scheduler = Schedulers.io().limit(maxConcurrency)
    Observable.fromIterable(this)
            .subscribeOn(scheduler)
            .flatMap {
                Observable.just(it)
                        .map {
                            log.info { "Closing [${it}]" }
                            try {
                                it.close()
                            } catch (e: Exception) {
                                log.error(e.message, e)
                            }
                            it
                        }
                        .subscribeOn(scheduler)
            }
            .blockingSubscribe()
}
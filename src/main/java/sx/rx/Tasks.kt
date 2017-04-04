package sx.rx

import io.reactivex.Observable

/**
 * RX Task
 * Created by masc on 16/11/2016.
 */
fun <T> task(block: (observer: (onNext: T) -> Unit) -> Unit): Observable<T> {
    return Observable.create<T> { sub ->
        try {
            block {
                sub.onNext(it)
            }
            sub.onComplete()
        } catch(e: Throwable) {
            sub.onError(e)
        }
    }
}
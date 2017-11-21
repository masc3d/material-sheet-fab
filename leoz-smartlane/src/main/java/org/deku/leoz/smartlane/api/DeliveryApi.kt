package org.deku.leoz.smartlane.api

import io.reactivex.Observable
import org.deku.leoz.smartlane.model.Deliveries
import org.deku.leoz.smartlane.model.Delivery
import org.slf4j.LoggerFactory

/**
 * Extension for fetching deliveries. Uses paging to prevent timeouts on large results.
 * Created by masc on 21.11.17.
 */
fun DeliveryApi.getDelivery(q: String): Observable<Delivery> {
    val pagesize = 20

    return Observable.create<Delivery> { emitter ->
        try {
            this.getDelivery(q, pagesize, 1).let { result ->
                result.objects.forEach { emitter.onNext(it) }

                if (result.totalPages > 1) {
                    (2..result.totalPages).map { page ->
                        this.getDelivery(q, pagesize, page)
                                .objects.forEach { emitter.onNext(it) }
                    }
                }
            }
            emitter.onComplete()
        } catch(e: Exception) {
            emitter.onError(e)
        }
    }
}
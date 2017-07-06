package org.deku.leoz.mobile.model

import org.deku.leoz.model.EventNotDeliveredReason
import sx.rx.ObservableRxProperty

/**
 * Created by masc on 05.07.17.
 */

class Events {
    val thrownFailedDeliveryEventProperty = ObservableRxProperty<EventNotDeliveredReason?>(null)
    var thrownFailedDeliveryEvent: EventNotDeliveredReason? by thrownFailedDeliveryEventProperty
}
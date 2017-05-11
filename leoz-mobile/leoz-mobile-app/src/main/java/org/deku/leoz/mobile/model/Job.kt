package org.deku.leoz.mobile.model

import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty

/**
 * Created by 27694066 on 09.05.2017.
 */
class Job {
    private val log = LoggerFactory.getLogger(this.javaClass)

    val pendingStopProperty = ObservableRxProperty<List<Stop>>(mutableListOf())
    val pendingStop: List<Stop> by pendingStopProperty

    val doneStopProperty = ObservableRxProperty<List<Stop>>(mutableListOf())
    val doneStop: List<Stop> by doneStopProperty
}
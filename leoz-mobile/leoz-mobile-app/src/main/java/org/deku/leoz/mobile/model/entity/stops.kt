package org.deku.leoz.mobile.model.entity

import java.util.*

/**
 * Stop address
 * Created by masc on 03.08.17.
 */
val Stop.address: Address
    get() = this.tasks.first().address

val Stop.dateStart: Date?
    get() = this.tasks.map { it.dateStart }.filterNotNull().max()

val Stop.dateEnd: Date?
    get() = this.tasks.map { it.dateEnd }.filterNotNull().min()
package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.model.entity.Address

/**
 * Created by 27694066 on 10.05.2017.
 */
class Stop (
        val orders: MutableList<Order>,
        val address: Address,
        var appointment: Order.Appointment,
        val state: State = Stop.State.PENDING
) {
    enum class State {
        PENDING, DONE, FAILED
    }
}
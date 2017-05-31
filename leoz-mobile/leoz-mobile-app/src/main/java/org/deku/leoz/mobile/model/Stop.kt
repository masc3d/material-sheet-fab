package org.deku.leoz.mobile.model

/**
 * Created by 27694066 on 10.05.2017.
 */
class Stop (
        val order: MutableList<Order>,
        val address: Order.Address,
        var appointment: Order.Appointment,
        var sort: Int,
        val state: State = Stop.State.PENDING
) {

    enum class State {
        PENDING, DONE, FAILED
    }
}
package org.deku.leoz.mobile.model

import java.util.*

/**
 * Created by 27694066 on 10.05.2017.
 */
class Stop (
        val order: Order,
        val address: Order.Address,
        val appointment: Date
) {

    //Nested class Order
    class Order (
            val classification: OrderClassification,
            val status: MutableList<Status>? = null,
            val adresses: MutableList<Address>,
            val appointment: Date,
            val carrier: Carrier
    ) {
        enum class OrderClassification {
            PICKUP, DELIVERY, EXCHANGE_DELIVERY, EXCHANGE_PICKUP
        }

        enum class Carrier {
            DERKURIER
        }

        class Address {
            enum class AddressClassification {
                PICKUP, DELIVERY, EXCHANGE
            }
        }

        class Status {

        }
    }
}
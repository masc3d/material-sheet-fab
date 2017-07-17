package org.deku.leoz.model

import sx.Result

/**
 * DEKU delivery list number
 * Created by masc on 17.07.17.
 */
class DekuDeliveryListNumber(
        /** Delivery list number */
        val value: String) {

    companion object {
        fun parseLabel(value: String): Result<DekuDeliveryListNumber> {
            if (value.length != 10)
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] must have 10 digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] must be numeric"))

            val number = CheckDigits.DEKU.verify(value)
            return when {
                number != null -> Result(DekuDeliveryListNumber(number))
                else -> Result(error = IllegalArgumentException("DEKU delivery list number [${value}] has invalid check digit"))
            }
        }
    }

    val location: Int by lazy {
        this.value.substring(0, 3).toInt()
    }
}
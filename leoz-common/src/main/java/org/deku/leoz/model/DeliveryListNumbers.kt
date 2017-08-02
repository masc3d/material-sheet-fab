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
                return Result(error = IllegalArgumentException("DEKU delivery list label [${value}] must have 9 digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] must be numeric"))

            val number = CheckDigits.DEKU.verify(value)
            return when {
                number != null -> Result(DekuDeliveryListNumber(number))
                else -> Result(error = IllegalArgumentException("DEKU delivery list number [${value}] has invalid check digit"))
            }
        }

        fun parse(value: String): Result<DekuDeliveryListNumber> {
            var str = value
            if (str.length == 8) {
                //TODO: This is supposed to be removed if the "LEO" frontend is able to omit the leading "0" in the delivery list barcode
                str = "0" + value
            }
            if (str.length != 9)
                return Result(error = IllegalArgumentException("DEKU delivery list number [$str] must have 8 digits"))

            return Result(DekuDeliveryListNumber(str))
        }
    }

    val location: Int by lazy {
        this.value.substring(0, 3).toInt()
    }

    /**
     * Delivery list number label content including check digit
     */
    val label: String by lazy {
        "${this.value}${CheckDigits.DEKU.calculate(this.value)}"
    }
}
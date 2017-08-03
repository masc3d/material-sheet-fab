package org.deku.leoz.model

import sx.Result

/**
 * DEKU delivery list number
 * Created by masc on 17.07.17.
 */
class DekuDeliveryListNumber private constructor(
        /** Delivery list number */
        val value: String) {

    companion object {
        /**
         * Parse delivery list barcode/label content
         * @param value Barcode/label content including check-digit
         */
        fun parseLabel(value: String): Result<DekuDeliveryListNumber> {
            if (value.length != 10)
                return Result(error = IllegalArgumentException("DEKU delivery list label [${value}] has invalid length"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] must be numeric"))

            val number = CheckDigits.DEKU.verify(value)
            return when {
                number != null -> Result(DekuDeliveryListNumber(number))
                else -> Result(error = IllegalArgumentException("DEKU delivery list number [${value}] has invalid check digit"))
            }
        }

        /**
         * Parse delivery list number
         * @param value Delivery list number without check-digit
         */
        fun parse(value: String): Result<DekuDeliveryListNumber> {
            if (value.length < 7 || value.length > 9)
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] has invalid length"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] must be numeric"))

            return Result(DekuDeliveryListNumber(value.padStart(9, '0')))
        }
    }

    /**
     * The location/station number
     */
    val station: Int by lazy {
        this.value.substring(0, 3).toInt()
    }

    /**
     * Delivery list number label content including check digit
     */
    val label: String by lazy {
        "${this.value}${CheckDigits.DEKU.calculate(this.value)}"
    }
}
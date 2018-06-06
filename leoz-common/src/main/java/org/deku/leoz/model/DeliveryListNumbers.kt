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
         * Format string based delivery list number value (without checkdigit)
         */
        private fun format(value: String) = value.padStart(9, '0')

        /**
         * Parse delivery list barcode/label content
         * @param value Barcode/label content including check-digit
         */
        fun parseLabel(value: String): Result<DekuDeliveryListNumber> {
            if (value.length < 6 || value.length > 10)
                return Result(error = IllegalArgumentException("DEKU delivery list label [${value}] has invalid length"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] must be numeric"))

            val number = CheckDigits.DEKU.verify(value)

            return when {
                number != null -> Result(DekuDeliveryListNumber(format(number)))
                else -> Result(error = IllegalArgumentException("DEKU delivery list number [${value}] has invalid check digit"))
            }
        }

        /**
         * Parse delivery list number
         * @param value Delivery list number without check-digit
         */
        fun parse(value: String): Result<DekuDeliveryListNumber> {
            if (value.length < 5 || value.length > 9)
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] has invalid length"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("DEKU delivery list number [${value}] must be numeric"))

            return Result(DekuDeliveryListNumber(format(value)))
        }

        /**
         * Create delivery list number
         * @param value Delivery list number
         */
        fun create(value: Long): DekuDeliveryListNumber = parse(value.toString()).value

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

    override fun toString(): String {
        return "${this.javaClass.simpleName}(value=${value}, station=${station}, label=${label})"
    }
}
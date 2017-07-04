package org.deku.leoz.model

/**
 * Parcel
 * Created by masc on 05.07.17.
 */
class Parcel(
        val number: UnitNumber) {

    companion object {
        fun parseLabel(value: String): Parcel {
            val number =
                    try {
                        UnitNumber.parseLabel(value)
                    } catch(e: Throwable) {
                        try {
                            GlsUnitNumber.parseLabel(value).toUnitNumber()
                        } catch(e: Throwable) {
                            throw IllegalArgumentException("Invalid unit label")
                        }
                    }

            return Parcel(number = number)
        }
    }
}
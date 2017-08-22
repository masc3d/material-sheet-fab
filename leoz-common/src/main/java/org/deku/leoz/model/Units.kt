package org.deku.leoz.model

/**
 * Parcel
 * Created by masc on 05.07.17.
 */
class Parcel(
        val number: UnitNumber) {

    companion object {
        fun parseLabel(label: String): Parcel {
            val number =
                    UnitNumber.parseLabel(label).valueOrNull
                            ?: GlsUnitNumber.parseLabel(label).valueOrNull?.toUnitNumber()
                            ?: throw IllegalArgumentException("Invalid unit label")

            return Parcel(number = number)
        }
    }
}
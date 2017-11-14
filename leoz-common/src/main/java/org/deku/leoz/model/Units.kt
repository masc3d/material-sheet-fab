package org.deku.leoz.model

import sx.Result

/**
 * Parcel
 * Created by masc on 05.07.17.
 */
class Parcel(
        val number: UnitNumber) {

    companion object {
        fun parseLabel(label: String): Result<Parcel> {
            val number =
                    UnitNumber.parseLabel(label).valueOrNull
                            ?: GlsUnitNumber.parseLabel(label).valueOrNull?.toUnitNumber()
                            ?: return Result(error = IllegalArgumentException("Invalid unit label"))

            return Result(value = Parcel(number = number))
        }
    }
}
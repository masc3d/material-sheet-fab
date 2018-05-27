package org.deku.leoz.mobile.model.entity

/**
 * Indicates if address is a compatible stop address for another by
 * only taking stop relevant fields into account
 * Created by masc on 03.08.17.
 */
fun Address.isCompatibleStopAddressFor(other: Address): Boolean {
    // All of those must be true, lazy comparison
    return arrayOf(
            { this.line1.equals(other.line1, ignoreCase = true) },
            { this.line2.equals(other.line2, ignoreCase = true) },
            { this.line3.equals(other.line3, ignoreCase = true) },
            { this.street.equals(other.street, ignoreCase = true) },
            { this.streetNo.equals(other.streetNo, ignoreCase = true) },
            { this.zipCode.equals(other.zipCode, ignoreCase = true) },
            { this.countryCode.equals(other.countryCode, ignoreCase = true) },
            { this.city.equals(other.city, ignoreCase = true) }
    ).all { it.invoke() }
}

/**
 * Indicates if address is compatible for merging stops
 */
fun Address.isCompatibleStopAddressForMergeWith(other: Address): Boolean =
        this.zipCode.take(3) == other.zipCode.take(3)

/**
 * Indicates if address has valid phone number
 */
val Address.hasValidPhoneNumber: Boolean
    get() = when {
        this.phone.filter { it.isDigit() }.length > 4 -> true
        else -> false
    }

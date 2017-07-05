package org.deku.leoz.model

import org.slf4j.LoggerFactory
import sx.Result

/**
 * DEKU unit number
 *
 * Created by masc on 04.07.17.
 *
 * @property value Unit number (without check digit)
 */
class UnitNumber private constructor(
        val value: String
) {
    enum class Type {
        Parcel,
        Bag,
        BagId,
        BagBack,
        MainSeal,
        BackSeal,
        ReserveSeal
    }

    /** Numeric unit number (without check digit) */
    private val valueLong by lazy { this.value.toLong() }

    /** Unit number type */
    val type by lazy {
        val l = this.valueLong
        when {
            l >= 10071000000 && l < 10072000000 -> Type.Bag
            l >= 10072000000 && l < 10073000000 -> Type.BagBack
            l >= 90010000000 && l < 90020000000 -> Type.MainSeal
            l >= 90020000000 && l < 90030000000 -> Type.BackSeal
            l >= 90030000000 && l < 90040000000 -> Type.ReserveSeal
            l >= 70010000000 && l < 70019999999 -> Type.BagId
            else -> Type.Parcel
        }
    }

    /**
     * Station nr for this unit number or null if not applicable
     */
    val stationNr by lazy {
        when {
            this.type == Type.Parcel && !this.isGlsParcel -> this.value.substring(0,3).toInt()
            else -> null
        }
    }

    /**
     * Indicates if this parcel is a GLS parcel
     */
    val isGlsParcel by lazy {
        this.value[0] == '8' && this.value[3] == '5'
    }

    companion object {
        private val log = LoggerFactory.getLogger(UnitNumber::class.java)

        /** Parse a plain unit number without check digit */
        fun parse(value: String): Result<UnitNumber> {
            if (value.length != 11)
                return Result(error = IllegalArgumentException("Unit number must have 11 digits"))

            return Result(UnitNumber(value))
        }

        /** Parse label unit number, which includes a check digit */
        fun parseLabel(value: String): Result<UnitNumber> {
            if (value.length != 12)
                return Result(error = IllegalArgumentException("Label based unit number must have 12 digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("Unit number must be numeric"))

            val number = value.substring(0, 11)
            val checkDigit = value.substring(11, 12).toInt()

            if (calculateCheckDigit(number) != checkDigit)
                return Result(error = IllegalArgumentException("Unit number has invalid check digit"))

            return Result(UnitNumber(number))
        }

        /**
         * Calculate DEKU check digit
         * @param value The value to calculate check digit for
         */
        fun calculateCheckDigit(value: String): Int {
            val len: Int = value.length
            var evenSum: Int = 0
            var oddSum: Int = 0

            for (count in len - 1 downTo 1 step 2) {
                evenSum += value.substring(count - 1, count).toInt()
            }

            for (count in len downTo 1 step 2) {
                oddSum += value.substring(count - 1, count).toInt()
            }

            val result = ((10 - (((oddSum * 3) + evenSum) % 10)) % 10)
            return result
        }
    }
}

/**
 * GLS unit number
 *
 * Example: 338500000008
 *
 * Fields:
 * {2,4} - Service
 * 85 -> Express
 *
 * @property value GLS number value without check digit
 */
class GlsUnitNumber private constructor(
        val value: String) {

    /** Service types */
    enum class ServiceType(val value: String) {
        EXPRESS("85");

        companion object {
            val valueMap = mapOf(
                    *ServiceType.values().map { Pair(it.value, it) }.toTypedArray()
            )
        }
    }

    val serviceType by lazy {
        ServiceType.valueMap.getValue(
                this.value.substring(2,4))
    }

    companion object {
        private val log = LoggerFactory.getLogger(GlsUnitNumber::class.java)

        fun parse(value: String): Result<GlsUnitNumber> {
            if (value.length != 11)
                return Result(error = IllegalArgumentException("GLS unit number must have 11 digits"))

            val un = GlsUnitNumber(value)

            if (un.serviceType != ServiceType.EXPRESS)
                return Result(error = IllegalArgumentException("GLS unit number has invalid service type"))

            return Result(GlsUnitNumber(value))
        }

        fun parseLabel(value: String): Result<GlsUnitNumber> {
            if (value.length != 12)
                return Result(error = IllegalArgumentException("Label based GLS unit number must have 12 digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("GLS unit number must be numeric"))

            val number = value.substring(0, 11)
            val checkDigit = value.substring(11, 12).toInt()

            if (calculateCheckDigit(number) != checkDigit)
                return Result(error = IllegalArgumentException("GLS unit number has invalid check digit"))

            return Result(GlsUnitNumber(number))
        }

        /**
         * Calculate GLS check digit
         * @param value The value to calculate check digit for
         */
        fun calculateCheckDigit(value: String): Int {
            val len: Int = value.length
            var evenSum: Int = 0
            var oddSum: Int = 0

            for (count in len - 2 downTo 0 step 2) {
                evenSum += value.substring(count, count + 1).toInt()
            }
            for (count in len - 1 downTo 0 step 2) {
                oddSum += value.substring(count, count + 1).toInt()
            }
            val result = ((10 - (((oddSum * 3) + evenSum + 1) % 10)) % 10)
            return result
        }
    }
}

// Extensinos

/**
 * Transform GLS unit number to DEKU unit number
 */
fun GlsUnitNumber.toUnitNumber(): UnitNumber {
    if (this.serviceType != GlsUnitNumber.ServiceType.EXPRESS)
        throw IllegalArgumentException("GLS unit number has invalid service type for unit number conversion")

    val sb = StringBuilder()

    // Convert GLS unit number by "pulling" 3rd digit to beginning
    val prefix = this.value.substring(0, 2)
    val ident = this.value.substring(2, 3)
    val suffix = this.value.substring(3)

    sb.append(ident, prefix, suffix)

    return UnitNumber.parse(sb.toString()).value
}
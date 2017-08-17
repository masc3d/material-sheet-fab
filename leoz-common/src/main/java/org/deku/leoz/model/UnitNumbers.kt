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

    /**
     * Unit number label content including check digit
     */
    val label by lazy {
        "${this.value}${CheckDigits.DEKU.calculate(this.value)}"
    }

    companion object {
        private val log = LoggerFactory.getLogger(UnitNumber::class.java)

        /** Generates a unit number instance from a database double unit number */
        fun from(number: Double): UnitNumber {
            return UnitNumber(
                    number.toLong().toString().padStart(11, padChar = '0')
            )
        }

        /** Parse a plain unit number without check digit */
        fun parse(value: String, vararg requiredTypes: Type = Type.values()): Result<UnitNumber> {
            if (value.length != 11)
                return Result(error = IllegalArgumentException("Unit number [${value}] must have 11 digits"))

            val unitNo = UnitNumber(value)
            if (!requiredTypes.contains(unitNo.type))
                return Result(error = IllegalArgumentException("Unit number type [${unitNo.type}] is not as required [$requiredTypes]"))

            return Result(unitNo)
        }

        /** Parse label unit number, which includes a check digit */
        fun parseLabel(value: String, vararg requiredTypes: Type = Type.values()): Result<UnitNumber> {
            if (value.length != 12)
                return Result(error = IllegalArgumentException("Label based unit number [${value}] must have 12 digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("Unit number [${value}] must be numeric"))

            val number = CheckDigits.DEKU.verify(value)

            return when {
                number != null -> {
                    val unitNo = UnitNumber(number)
                    if (!requiredTypes.contains(unitNo.type))
                        return Result(error = IllegalArgumentException("Unit number type [${unitNo.type}] is not as required [$requiredTypes]"))

                    Result(unitNo)
                }
                else -> Result(error = IllegalArgumentException("Unit number [${value}] has invalid check digit"))
            }
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
        fun parse(value: String): Result<GlsUnitNumber> {
            if (value.length != 11)
                return Result(error = IllegalArgumentException("GLS unit number [${value}] must have 11 digits"))

            val un = GlsUnitNumber(value)

            if (un.serviceType != ServiceType.EXPRESS)
                return Result(error = IllegalArgumentException("GLS unit number [${value}] has invalid service type"))

            return Result(GlsUnitNumber(value))
        }

        fun parseLabel(value: String): Result<GlsUnitNumber> {
            if (value.length != 12)
                return Result(error = IllegalArgumentException("Label based GLS unit number [${value}] must have 12 digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("GLS unit number [${value}] must be numeric"))

            val number = CheckDigits.GLS.verify(value)

            return when {
                number != null -> Result(GlsUnitNumber(number))
                else -> Result(error = IllegalArgumentException("GLS unit number [${value}] has invalid check digit"))
            }
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
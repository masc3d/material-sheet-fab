package org.deku.leoz.model

import org.slf4j.LoggerFactory
import sx.Result

/**
 * Unit number base class
 *
 * @property value Unit number (without check digit)
 */
abstract class UnitNumber protected constructor(
        val value: String
) {
    /** Static methods */
    companion object {
        /**
         * Universal label parsing for unit numbers
         * @param label Label content to parse
         */
        fun parseLabel(label: String): Result<UnitNumber> {
            return try {
                Result(value = DekuUnitNumber.parseLabel(label).valueOrNull
                        ?: GlsUnitNumber.parseLabel(label).valueOrNull?.toUnitNumber()
                        ?: return Result(error = IllegalArgumentException("Invalid unit label")))
            } catch (e: Throwable) {
                Result(error = e)
            }
        }
    }

    enum class Type {
        Parcel,
        Bag,
        BagId,
        BagBack,
        MainSeal,
        BackSeal,
        ReserveSeal
    }

    open val type: UnitNumber.Type
        get() = UnitNumber.Type.Parcel
}

/**
 * DEKU unit number
 * Created by masc on 04.07.17.
 *
 * @param value Unit number (without check digit)
 */
class DekuUnitNumber private constructor(
        value: String
) :
        UnitNumber(value) {

    /** Numeric unit number (without check digit) */
    private val valueLong by lazy { this.value.toLong() }

    /** Unit number type */
    override val type by lazy {
        val l = this.valueLong
        when {
            l >= 10071000000 && l < 10072000000 -> UnitNumber.Type.Bag
            l >= 10072000000 && l < 10073000000 -> UnitNumber.Type.BagBack
            l >= 90010000000 && l < 90020000000 -> UnitNumber.Type.MainSeal
            l >= 90020000000 && l < 90030000000 -> UnitNumber.Type.BackSeal
            l >= 90030000000 && l < 90040000000 -> UnitNumber.Type.ReserveSeal
            l >= 70010000000 && l < 70019999999 -> UnitNumber.Type.BagId
            else -> UnitNumber.Type.Parcel
        }
    }

    /**
     * Station nr for this unit number or null if not applicable
     */
    val stationNr by lazy {
        when {
            this.type == UnitNumber.Type.Parcel && !this.isGlsParcel -> this.value.substring(0, 3).toInt()
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
     * Unit number label check digit
     */
    val labelCheckDigit by lazy {
        CheckDigits.DEKU.calculate(this.value)
    }

    /**
     * Unit number label content including check digit
     */
    val label by lazy {
        "${this.value}${this.labelCheckDigit}"
    }

    companion object {
        private val log = LoggerFactory.getLogger(DekuUnitNumber::class.java)

        /** Generates a unit number instance from a database double unit number */
        fun from(number: Double): DekuUnitNumber {
            return DekuUnitNumber(
                    number.toLong().toString().padStart(11, padChar = '0')
            )
        }

        private fun parse(value: String, asLabel: Boolean): Result<DekuUnitNumber> {
            val requiredLength = if (asLabel) 12 else 11

            if (value.length != requiredLength)
                return Result(error = IllegalArgumentException("Label based unit number [${value}] must have ${requiredLength} digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("Unit number [${value}] must be numeric"))

            val number = if (asLabel) {
                CheckDigits.DEKU.verify(value)
                        ?: return Result(error = IllegalArgumentException("Unit number [${value}] has invalid check digit"))
            } else {
                value
            }

            return Result(DekuUnitNumber(number))
        }

        /** Parse a plain unit number without check digit */
        fun parse(value: String): Result<DekuUnitNumber> = this.parse(value, asLabel = false)

        /** Parse label unit number, which includes a check digit */
        fun parseLabel(value: String): Result<DekuUnitNumber> = this.parse(value, asLabel = true)
    }
}

/**
 * Assert that specific unit number types are mandatory
 */
fun Result<DekuUnitNumber>.assertAny(vararg types: UnitNumber.Type): Result<DekuUnitNumber> =
        when {
            this.hasValue && !types.contains(this.value.type) ->
                Result(error = IllegalArgumentException("Unit number type [${this.value.type}] not as required [$types]"))
            else -> this
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
 * @param value GLS number value without check digit
 */
class GlsUnitNumber private constructor(
        value: String
) :
        UnitNumber(value) {

    /** Service types */
    enum class ServiceType(val value: String) {
        EXPRESS("85");

        companion object {
            val valueMap by lazy { ServiceType.values().associateBy(ServiceType::value) }
        }
    }

    private val serviceTypeToken by lazy {
        this.value.substring(2, 4)
    }

    val serviceType by lazy {
        ServiceType.valueMap.getValue(this.serviceTypeToken)
    }

    companion object {
        private fun parse(value: String, asLabel: Boolean): Result<GlsUnitNumber> {
            val requiredLength = if (asLabel) 12 else 11

            if (value.length != requiredLength)
                return Result(error = IllegalArgumentException("GLS unit number [${value}] must have ${requiredLength} digits"))

            if (!value.all { it.isDigit() })
                return Result(error = IllegalArgumentException("GLS unit number [${value}] must be numeric"))

            val number = if (asLabel) {
                CheckDigits.GLS.verify(value)
                        ?: return Result(error = IllegalArgumentException("GLS unit number [${value}] has invalid check digit"))
            } else {
                value
            }

            val un = GlsUnitNumber(number)

            // Evaluate/check service type
            try {
                un.serviceType
            } catch (e: Throwable) {
                return Result(error = IllegalArgumentException("GLS unit number [${value}] has unsupported service token [${un.serviceTypeToken}]"))
            }

            if (un.serviceType != ServiceType.EXPRESS)
                return Result(error = IllegalArgumentException("GLS unit number [${value}] has invalid service type [${un.serviceType}]"))

            return Result(un)

        }

        fun parse(value: String): Result<GlsUnitNumber> = this.parse(value, asLabel = false)

        fun parseLabel(value: String): Result<GlsUnitNumber> = this.parse(value, asLabel = true)
    }
}

// Extensinos

/**
 * Transform GLS unit number to DEKU unit number
 */
fun GlsUnitNumber.toUnitNumber(): DekuUnitNumber {
    if (this.serviceType != GlsUnitNumber.ServiceType.EXPRESS)
        throw IllegalArgumentException("GLS unit number has invalid service type for unit number conversion")

    val sb = StringBuilder()

    // Convert GLS unit number by "pulling" 3rd digit to beginning
    val prefix = this.value.substring(0, 2)
    val ident = this.value.substring(2, 3)
    val suffix = this.value.substring(3)

    sb.append(ident, prefix, suffix)

    return DekuUnitNumber.parse(sb.toString()).value
}
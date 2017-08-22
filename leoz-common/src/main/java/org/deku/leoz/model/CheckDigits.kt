package org.deku.leoz.model

/**
 * Created by masc on 17.07.17.
 */
interface CheckDigit {
    /**
     * Verify check digit
     * @return Value without checkdigit or null if invalid
     */
    fun verify(value: String): String? {
        if (value.isEmpty())
            return null

        val content = value.substring(0, value.length - 1)
        val checkDigit = value.substring(value.length - 1, value.length).toInt()

        return when {
            this.calculate(content) == checkDigit -> content
            else -> null
        }
    }

    /** Verify check digit */
    fun isValid(value: String): Boolean {
        return this.verify(value) != null
    }

    /** Add check digit */
    fun append(value: String): String {
        return "${value}${this.calculate(value)}"
    }

    /** Calculate check digit */
    fun calculate(value: String): Int
}

/**
 * Common DEKU check digit
 */
class DekuCheckDigit : CheckDigit {
    override fun calculate(value: String): Int {
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

/**
 * Common GLS check digit
 */
class GlsCheckDigit : CheckDigit {
    override fun calculate(value: String): Int {
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

class CheckDigits {
    companion object {
        val DEKU = DekuCheckDigit()
        val GLS = GlsCheckDigit()
    }
}
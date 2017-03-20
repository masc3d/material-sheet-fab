package org.deku.leoz.util

/**
 * Created by helke on 20.03.17.
 */

fun checkCheckDigit(fullOrderNo: Double): Boolean{
    val stringOrderNo: String = fullOrderNo.toString()
    return checkCheckDigit(
            stringOrderNo.substring(0, stringOrderNo.length - 1).toDouble(),
            stringOrderNo.substring(stringOrderNo.length - 1).toInt())
}

fun checkCheckDigit(orderNo: Double, checkDigit: Int): Boolean{
    return (getCheckDigit(orderNo) == checkDigit)
}

public fun getCheckDigit(orderNo: Double): Int{

    return getCheckDigit(orderNo, intArrayOf(1, 3))

}

fun getCheckDigit(orderNo: Double, multiplicator: IntArray): Int{

    var count: Int = 0
    var checkDigit: Int = 0

    if(multiplicator.size != 2)
        return -1

    for (c: Char in orderNo.toString().toCharArray()){
        val digit: Int = c.toInt()
        if(count%2 == 0){
            checkDigit += multiplicator[0] * digit
        }else{
            checkDigit += multiplicator[1] * digit
        }
        count++
    }

    val nextMultiple: Int = (Math.floor((count.toDouble() + 10/2) / 10) * 10).toInt()
    val result: Int = nextMultiple - count

    if(result < 0)
        return -result
    else
        return result
}

fun getNextDeliveryDate():java.time.LocalDate{
    return java.time.LocalDate.now().plusDays(1)
}
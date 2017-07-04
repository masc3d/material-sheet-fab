package org.deku.leoz.util

/**
 * Created by helke on 20.03.17.
 */


//fun checkCheckDigit(fullOrderNo: Double): Boolean{
/**
 *
 */
fun checkCheckDigit(fullOrderNo: String): Boolean {
    val stringOrderNo: String = fullOrderNo
    //zum test ob numeric
    //val dblTmp:Double=stringOrderNo.toDouble()//stringOrderNo.substring(0, stringOrderNo.length - 1).toDouble()
    try {
        stringOrderNo.toDouble()
    } catch (e: Exception) {
        return false
    }
    //stringOrderNo.toDouble()//stringOrderNo.substring(0, stringOrderNo.length - 1).toDouble()
    return checkCheckDigit(
            stringOrderNo.substring(0, stringOrderNo.length - 1), //stringOrderNo.substring(0, stringOrderNo.length - 1).toDouble(),
            stringOrderNo.substring(stringOrderNo.length - 1).toInt())
}

//fun checkCheckDigit(orderNo: Double, checkDigit: Int): Boolean{
fun checkCheckDigit(orderNo: String, checkDigit: Int): Boolean {
    return (getCheckDigit(orderNo) == checkDigit)
}

/**

 DEKU number, immer 11 stellen zzgl prüfziffer.
 Optional komma als platzhalter für pz (fällt zukünftig weg)

 * erste drei stellen immer stationsnummer
 01000000000,
 010000000001

 GLS numbers
 * 85 pos 3 -> indikator für gls paketnummer
 338500000001  gls

 GLS nuber im DEKU System
 erste stelle 8, 4. Stelle 5
 833500000002  deku
 */

/**
//public fun getCheckDigit(orderNo: Double): Int{
public fun getCheckDigit(orderNo: String): Int{

return getCheckDigit(orderNo, intArrayOf(1, 3))

}
 **/

//fun getCheckDigit(orderNo: Double, multiplicator: IntArray): Int{
fun getCheckDigit(sOrderNo: String): Int {

    //var count: Int = 0
    //var checkDigit: Int = 0


    val iLength: Int = sOrderNo.length
    var iEvenSum: Int = 0
    var iOddSum: Int = 0

    for (count in iLength - 1 downTo 1 step 2) {
        iEvenSum += sOrderNo.substring(count - 1, count).toInt()

    }
    for (count in iLength downTo 1 step 2) {
        iOddSum += sOrderNo.substring(count - 1, count).toInt()
    }
    val result: Int
    result = ((10 - (((iOddSum * 3) + iEvenSum) % 10)) % 10)
    return result
    /**
    for (c: Char in orderNo.toString().toCharArray()){
    val digit: Int = c.toString().toInt()
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
     **/
}

fun checkCheckDigitGLS(fullOrderNo: String): Boolean {
    val stringOrderNo: String = fullOrderNo
    //nur zum test ob numeric
    //val dblTmp:Double=stringOrderNo.toDouble()
    //stringOrderNo.toDouble()
    try {
        stringOrderNo.toDouble()
    } catch (e: Exception) {
        return false
    }
    return checkCheckDigitGLS(
            stringOrderNo.substring(0, stringOrderNo.length - 1),
            stringOrderNo.substring(stringOrderNo.length - 1).toInt())
}

fun checkCheckDigitGLS(orderNo: String, checkDigit: Int): Boolean {
    return (getCheckDigitGLS(orderNo) == checkDigit)
}

fun getCheckDigitGLS(sOrderNo: String): Int {
    //var count: Int = 0
    //var checkDigit: Int = 0

    val iLength: Int = sOrderNo.length
    var iEvenSum: Int = 0
    var iOddSum: Int = 0

    for (count in iLength - 2 downTo 0 step 2) {
        iEvenSum += sOrderNo.substring(count, count + 1).toInt()
    }
    for (count in iLength - 1 downTo 0 step 2) {
        iOddSum += sOrderNo.substring(count, count + 1).toInt()
    }
    val result: Int
    result = ((10 - (((iOddSum * 3) + iEvenSum + 1) % 10)) % 10)
    return result
}

fun getNextDeliveryDate(): java.time.LocalDate {
    return java.time.LocalDate.now().plusDays(1)
}

fun getWorkingDate(): java.time.LocalDate {
    return java.time.LocalDateTime.now().minusHours((6)).toLocalDate()

}

fun getDekuUnitNoFromGlsUnitNo(glsUnitNo: String): String {
    var sDeku = "0"
    try {
        if (glsUnitNo.substring(2, 1).equals("8")) {
            sDeku = "8" + glsUnitNo.substring(0, 2) + glsUnitNo.substring(3)
        }
    } catch(e: Exception) {
        sDeku = "0"
    }
    return sDeku

}

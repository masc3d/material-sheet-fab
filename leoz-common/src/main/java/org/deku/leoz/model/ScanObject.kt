package org.deku.leoz.model

import org.deku.leoz.util.*

/**
 * Created by helke on 08.05.17.
 */

sealed class ScanSymbology {
    class Code128 : ScanSymbology()
    class DataMatrix : ScanSymbology()
    class Interleaved2of5 : ScanSymbology()
    class QRcode : ScanSymbology()
    class unknown : ScanSymbology()
}

sealed class ScanObjectType {
    class Seal(val sealNo: String, val sealType: SealType) : ScanObjectType()
    class ActioUser(val actioNo: String) : ScanObjectType()
    class PrinterId(val printerNo: String) : ScanObjectType()
    class LineScanId(val lineScanNo: String) : ScanObjectType()
    class UnitDeku(val dekuNo: String) : ScanObjectType()
    class UnitGLS(val glsNo: String, val dekuNo: String) : ScanObjectType()
    class BagID(val bagIdNo: String) : ScanObjectType()
    class BagUnitNo(val bagNo: String) : ScanObjectType()
    class BagBackUnitNo(val bagBackNo: String) : ScanObjectType()
    class CargoList(val cargoNo:String) : ScanObjectType()//Ladeliste
    class CartageNote(val cartageNo:String) : ScanObjectType()//Rollkarte
    class Unknown(val errorType: ScanErrorType) : ScanObjectType()
}

sealed class SealType {
    class mainSeal : SealType()
    class backSeal : SealType()
    class reserveSeal : SealType()
}

enum class ScanErrorType {
    WRONG_CHECK_DIGIT,
    NOT_A_NUMBER
}

data class ScanObject(val scanObjectType: ScanObjectType)

fun parse(scanData: String, scanSymbology: ScanSymbology?): ScanObject? {
    var s = scanData
    var so = ScanObject(ScanObjectType.Unknown(ScanErrorType.NOT_A_NUMBER))

    if ((scanSymbology == null) || (scanSymbology is ScanSymbology.unknown)) {
        if (s.length > 12) {
            s = s.substring(0, 12)
        }
        if (s.length < 12) {
            s = s.padStart(12, '0')
        }
        if (!s.endsWith(',')) {
            if (!checkCheckDigit(s)) {

                if (!checkCheckDigitGLS(s)) {
                    so = ScanObject(ScanObjectType.Unknown(ScanErrorType.WRONG_CHECK_DIGIT))
                    return so
                } else {
                    s = s.substring(0, 11)
                    val sDeku = getDekuUnitNoFromGlsUnitNo(s)
                    so = ScanObject(ScanObjectType.UnitGLS(s, sDeku))
                    return so
                }
            }


        }
        s = s.substring(0, 11)
        if (s.toDoubleOrNull() == null) {
            return so
        }

    } else {
        when (scanSymbology) {
            is ScanSymbology.Interleaved2of5 -> {
                //toDo checkdigit, gls-checkdigit, number ranges
                if (!checkCheckDigit(s)) {
                    if (!checkCheckDigitGLS(s)) {
                        so = ScanObject(ScanObjectType.Unknown(ScanErrorType.WRONG_CHECK_DIGIT))
                        return so
                    } else {
                        s = s.substring(0, 11)
                        val sDeku = getDekuUnitNoFromGlsUnitNo(s)
                        so = ScanObject(ScanObjectType.UnitGLS(s, sDeku))
                        return so
                    }
                }
                s = s.substring(0, 11)
            }
        }
    }
    so = ScanObject(ScanObjectType.UnitDeku(s))
    val l = s.toLong()
    if (l >= 10071000000 && l < 10072000000) {
        so = ScanObject(ScanObjectType.BagUnitNo(s))
        return so
    }
    if (l >= 10072000000 && l < 10073000000) {
        so = ScanObject(ScanObjectType.BagBackUnitNo(s))
        return so
    }
    if (l >= 90010000000 && l < 90020000000) {
        so = ScanObject(ScanObjectType.Seal(s,SealType.mainSeal()))
        return so
    }
    if (l >= 90020000000 && l < 90030000000) {
        so = ScanObject(ScanObjectType.Seal(s,SealType.backSeal()))
        return so
    }
    if (l >= 90030000000 && l < 90040000000) {
        so = ScanObject(ScanObjectType.Seal(s,SealType.reserveSeal()))
        return so
    }
    if (l >= 70010000000 && l < 70019999999) {
        so = ScanObject(ScanObjectType.BagID(s))
        return so
    }

    return so
}
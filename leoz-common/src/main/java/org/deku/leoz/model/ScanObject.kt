package org.deku.leoz.model

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
sealed class ScanObjectType{
    class Seal(val sealType:SealType):ScanObjectType()
    class ActioUser:ScanObjectType()
    class PrinterId:ScanObjectType()
    class LineScanId:ScanObjectType()
    class UnitDeku:ScanObjectType()
    class UnitGLS(val dekuNo:Double):ScanObjectType()
    class BagID:ScanObjectType()
    class BagUnitNo:ScanObjectType()
    class BagBackUnitNo:ScanObjectType()
}
sealed class SealType{
    class mainSeal:SealType()
    class backSeal:SealType()
    class reserveSeal:SealType()
}
data class ScanObject(val scanObjectType: ScanObjectType)

fun parse(scanData:String, scanSymbology:ScanSymbology):ScanObject?{

    when(scanSymbology){
        is ScanSymbology.Interleaved2of5 -> {
            //toDo checkdigit, gls-checkdigit, number ranges
            //val so=ScanObject(ScanObjectType.UnitDeku)
            //return so
        }
    }
    //Must return anything, compilation does not work if not
    return null
}
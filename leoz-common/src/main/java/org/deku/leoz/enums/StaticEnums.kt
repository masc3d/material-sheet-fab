package org.deku.leoz.enums

/**
 * Created by helke on 28.04.17.
 * on changes code changes necessary
 */

//statuscodes+data werden von mobiles an WS gesendet-> in scascans importiert, import-automat übersetzt in tblstatus..
//weitere statuscodes bei Auslieferhindernissen bisher aus tblsyscollections (typ=210)->in_out=null,
// sonst Überschneidung mit Linien-Scancodes
enum class ScanStatuscode(val statusvalue:Int) {
    LineScanOk(1),
    LineScanRepeat(2),
    LineScanNoDB(3),
    LineScanWrongDepot(4),
    LineScanNoDtExportDepot(5),
    LineScanLockflagNot0(6),
    LineScanUnitPhoto(10),
    StationDelivered(-1),
    StationPhoto(-2),
    StationImport(-3),
    StationWrongDepot(-4),
    StationWeightCorrection(-5),
    StationUnitMissing(-6),
    StationWrongRouting(-7),
    StationInDelivery(-8),
    StationUnitDamaged(-9),
    StationExport(-10),
    StationExportBagFill(-11)
}

enum class BeepCode(val beepvalue:Int){
    BeepOK(1),//green
    BeepNOK(2),//red
    BeepWOK(3),//repeat-scan->yellow
    BeepDepotOk(4)
}

enum class AppEnabledModul(val enabledvalue:Int){
    non(0),
    HUB(1),
    SMS(2),
    Line(4),
    StationExportBag(8),
    StationImport(16),
    Delivery(32),
    StationExport(64),
    Delivery100(128),
    StartPhone(1024),
    StartWLAN(2048),
    HUBadmin(4096),
    ALL(HUB.enabledvalue+SMS.enabledvalue+Line.enabledvalue+StationImport.enabledvalue+Delivery.enabledvalue+HUBadmin.enabledvalue)
}

enum class ConnStatus(val statusvalue:Int){
    non(0),
    GPRS(1),
    WLAN(2),
    GPRSWLAN(4),
    unknown(8)
}

enum class LineDirection(val directionvalue:Int){
    In(-1),
    Out(1),
    NN(0)
}

enum class Commands(val commandvalue:Int){
    Message(1),
    GetUnitsOut(2),
    SWupdateOptional(3),
    SWupdateMust(4),
    GetUnitsIn(5),
    GetDepotsIn(6),
    GetDepotsOut(7),
    DBkill(8),
    DBclean(9),
    DBsql(10),
    ShowErrorLog(11),
    SendErrLog(12),
    Boot(13),
    DoUpdate(14),
    Drop(15),
    SendDB(16),
    SendDBmsg(17),
    DeleteDB(18),
    ChangePWD(19),
    SendLogData(20),
    MessageBeep(21)
}

enum class SendData{
    getCommands,
    sendGpsData,
    sendAcknowledge,
    sendScans,
    Login,
    LoginLineIn,
    LoginLineOut,
    sendImages,
    sendErrorlog,
    getUnitsLineIn,
    getUnitsLineOut,
    getDepotDataIn,
    getLinesIn,
    sendMsg,
    getLinesOut,
    getDepotsOut_station,
    getUnitsStation,
    getSysCollections,
    getTranslation,
    getUnitsStationImport,
    getUnitsStationExport,
    getBags
}
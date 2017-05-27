package org.deku.leoz.enums

import org.deku.leoz.model.ParcelServiceRestriction

/**
 * Created by helke on 28.04.17.
 * on changes code changes necessary
 */

//statuscodes+data werden von mobiles an WS gesendet-> in scascans importiert, import-automat übersetzt in tblstatus..
//weitere statuscodes bei Auslieferhindernissen bisher aus tblsyscollections (typ=210)->in_out=null,
// sonst Überschneidung mit Linien-Scancodes
enum class ScanStatuscode(val statusvalue: Int) {
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

enum class BeepCode(val beepvalue: Int) {
    BeepOK(1), //green
    BeepNOK(2), //red
    BeepWOK(3), //repeat-scan->yellow
    BeepDepotOk(4)
}

enum class AppEnabledModul(val enabledvalue: Int) {
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
    ALL(HUB.enabledvalue + SMS.enabledvalue + Line.enabledvalue + StationImport.enabledvalue + Delivery.enabledvalue + HUBadmin.enabledvalue)
}

enum class ConnStatus(val statusvalue: Int) {
    non(0),
    GPRS(1),
    WLAN(2),
    GPRSWLAN(4),
    unknown(8)
}

enum class LineDirection(val directionvalue: Int) {
    In(-1),
    Out(1),
    NN(0)
}

enum class Commands(val commandvalue: Int) {
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

enum class SendData {
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

//todo StopClassifizierung noch nicht volltändig
enum class ParcelService(val serviceId: Long, val parcelServiceRestriction: ParcelServiceRestriction, val validForStop: StopClassifikation) {
    noAdditionalService(
            serviceId = 0,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    appointment(
            serviceId = 1,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    suitcaseShipping(
            serviceId = 2,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    weekend(
            serviceId = 4,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    bankHolidayDelivery(
            serviceId = 8,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    latePickup(
            serviceId = 16,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    receiptAcknowledgment(
            serviceId = 32,
            parcelServiceRestriction = ParcelServiceRestriction(
                    paperReceiptNeeded = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassifikation.BOTH),
    selfPickup(
            serviceId = 64,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    cashOnDelivery(
            serviceId = 128,
            parcelServiceRestriction = ParcelServiceRestriction(
                    cash = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassifikation.BOTH),
    valuedPackage(
            serviceId = 256,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    pharmaceuticals(
            serviceId = 512,
            parcelServiceRestriction = ParcelServiceRestriction(
                    personalDeliveryOnly = true,
                    alternateDeliveryAllowed = false
            ),

            validForStop = StopClassifikation.BOTH),
    addressCorrection(
            serviceId = 1024,
            parcelServiceRestriction = ParcelServiceRestriction(), validForStop = StopClassifikation.BOTH),
    waitingPeriodPickUp(
            serviceId = 2048,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    waitingPeriodDelivery(
            serviceId = 4096,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    pickUp(
            serviceId = 8192,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    identContractService(
            serviceId = 16384,
            parcelServiceRestriction = ParcelServiceRestriction(
                    personalDeliveryOnly = true,
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassifikation.BOTH),
    submissionParticipation(
            serviceId = 32768,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    securityReturn(
            serviceId = 65536,
            parcelServiceRestriction = ParcelServiceRestriction(
                    imeiCheckRequired = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassifikation.BOTH),
    lateDelivery(
            serviceId = 131072,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    xChange(
            serviceId = 262144,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.DELIVERY),
    phoneReceipt(
            serviceId = 524288,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    documentedPersonallyDelivery(
            serviceId = 1048576,
            parcelServiceRestriction = ParcelServiceRestriction(
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false,
                    paperReceiptNeeded = true
            ),
            validForStop = StopClassifikation.BOTH),
    higherLiability(
            serviceId = 2097152,
            parcelServiceRestriction = ParcelServiceRestriction(
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassifikation.BOTH),
    departmentDelivery(
            serviceId = 4194304,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    fixedAppointment(
            serviceId = 8388608,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    fairService(
            serviceId = 16777216,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    selfCompletionOfDutyPaymentAntDocuments(
            serviceId = 33554432,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    packagingRecirculation(
            serviceId = 67108864,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    unsuccessfulApproach(
            serviceId = 134217728,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    postboxDelivery(
            serviceId = 268435456,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassifikation.BOTH),
    noAlternativelyDelivery(
            serviceId = 536870912,
            parcelServiceRestriction = ParcelServiceRestriction(
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassifikation.BOTH)
}

/**
 * Created by JT on 24.05.17.
 */
enum class Carrier {
    DERKURIER,
    UNKNOWN
}

/**
 * Created by JT on 24.05.17.
 */
enum class OrderClassifikation {
    PICKUP, DELIVERY, PICKUP_DELIVERY
}

/**
 * Created by JT on 27.05.17.
 */
enum class StopClassifikation {
    PICKUP, DELIVERY, BOTH
}

enum class AdditionalInformationType {
    IMEI, IDENTITYCARDID, LOADINGLISTINFORMATION, DRIVERINFORMATION, CASH
}

package org.deku.leoz.model

/**
 * Created by helke on 28.04.17.
 * on changes code changes necessary
 */

//statuscodes+data werden von mobiles an WS gesendet-> in scascans importiert, import-automat übersetzt in tblstatus..
//weitere statuscodes bei Auslieferhindernissen bisher aus tblsyscollections (typ=210)->in_out=null,
// sonst Überschneidung mit Linien-Scancodes
enum class ScanStatuscode(val value: Int) {
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

enum class BeepCode(val value: Int) {
    BeepOK(1), //green
    BeepNOK(2), //red
    BeepWOK(3), //repeat-scan->yellow
    BeepDepotOk(4)
}

enum class AppEnabledModul(val value: Int) {
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
    ALL(HUB.value + SMS.value + Line.value + StationImport.value + Delivery.value + HUBadmin.value)
}

enum class ConnStatus(val value: Int) {
    non(0),
    GPRS(1),
    WLAN(2),
    GPRSWLAN(4),
    unknown(8)
}

enum class LineDirection(val value: Int) {
    In(-1),
    Out(1),
    NN(0)
}

enum class Commands(val value: Int) {
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
enum class ParcelService(
        val serviceId: Long,
        val parcelServiceRestriction: ParcelServiceRestriction,
        val validForStop: StopClassification) {
    NoAdditionalService(
            serviceId = 0,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    Appointment(
            serviceId = 1,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    SuitcaseShipping(
            serviceId = 2,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    Weekend(
            serviceId = 4,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    BankHolidayDelivery(
            serviceId = 8,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    LatePickup(
            serviceId = 16,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    ReceiptAcknowledgment(
            serviceId = 32,
            parcelServiceRestriction = ParcelServiceRestriction(
                    paperReceiptNeeded = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.Both),
    SelfPickup(
            serviceId = 64,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    CashOnDelivery(
            serviceId = 128,
            parcelServiceRestriction = ParcelServiceRestriction(
                    cash = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.Both),
    ValuedPackage(
            serviceId = 256,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    Pharmaceuticals(
            serviceId = 512,
            parcelServiceRestriction = ParcelServiceRestriction(
                    personalDeliveryOnly = true,
                    alternateDeliveryAllowed = false
            ),

            validForStop = StopClassification.Both),
    AddressCorrection(
            serviceId = 1024,
            parcelServiceRestriction = ParcelServiceRestriction(), validForStop = StopClassification.Both),
    WaitingPeriodPickUp(
            serviceId = 2048,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    WaitingPeriodDelivery(
            serviceId = 4096,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    PickUp(
            serviceId = 8192,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    IdentContractService(
            serviceId = 16384,
            parcelServiceRestriction = ParcelServiceRestriction(
                    personalDeliveryOnly = true,
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.Both),
    SubmissionParticipation(
            serviceId = 32768,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    SecurityReturn(
            serviceId = 65536,
            parcelServiceRestriction = ParcelServiceRestriction(
                    imeiCheckRequired = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.Both),
    LateDelivery(
            serviceId = 131072,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    Xchange(
            serviceId = 262144,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Delivery),
    PhoneReceipt(
            serviceId = 524288,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    DocumentedPersonallyDelivery(
            serviceId = 1048576,
            parcelServiceRestriction = ParcelServiceRestriction(
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false,
                    paperReceiptNeeded = true
            ),
            validForStop = StopClassification.Both),
    HigherLiability(
            serviceId = 2097152,
            parcelServiceRestriction = ParcelServiceRestriction(
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.Both),
    DepartmentDelivery(
            serviceId = 4194304,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    FixedAppointment(
            serviceId = 8388608,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    RairService(
            serviceId = 16777216,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    SelfCompletionOfDutyPaymentAntDocuments(
            serviceId = 33554432,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    PackagingRecirculation(
            serviceId = 67108864,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    UnsuccessfulApproach(
            serviceId = 134217728,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    PostboxDelivery(
            serviceId = 268435456,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.Both),
    NoAlternativelyDelivery(
            serviceId = 536870912,
            parcelServiceRestriction = ParcelServiceRestriction(
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.Both)
}

/**
 * Created by JT on 24.05.17.
 */
enum class Carrier(val value: Int) {
    DerKurier(0),
    Unknown(1)
}

/**
 * Created by JT on 24.05.17.
 */
enum class OrderClassification(val value: Int) {
    PickUp(0),
    Delivery(1),
    PickUpDelivery(2)
}

/**
 * Created by JT on 27.05.17.
 */
enum class StopClassification(val value: Int) {
    PickUp(0),
    Delivery(1),
    Both(2)
}

enum class AdditionalInformationType(val value: Int) {
    IMEI(0),
    IdentityCardId(1),
    LoadingListInformation(2),
    DriverInformation(3),
    Cash(4)
}

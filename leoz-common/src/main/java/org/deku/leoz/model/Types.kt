package org.deku.leoz.model

/**
 * Created by helke on 28.04.17.
 * on changes code changes necessary
 */

//statuscodes+data werden von mobiles an WS gesendet-> in scascans importiert, import-automat übersetzt in tblstatus..
//weitere statuscodes bei Auslieferhindernissen bisher aus tblsyscollections (typ=210)->in_out=null,
// sonst Überschneidung mit Linien-Scancodes
enum class ScanStatuscode(val value: Int) {
    LINE_SCAN_OK(1),
    LINE_SCAN_REPEAT(2),
    LINE_SCAN_NO_DB(3),
    LINE_SCAN_WRONG_DEPOT(4),
    LINE_SCAN_NO_DT_EXPORT_DEPOT(5),
    LINE_SCAN_LOCK_FLAG_NOT_0(6),
    LINE_SCAN_UNIT_PHOTO(10),
    STATION_DELIVERED(-1),
    STATION_PHOTO(-2),
    STATION_IMPORT(-3),
    STATION_WRONG_DEPOT(-4),
    STATION_WEIGHT_CORRECTION(-5),
    STATION_UNIT_MISSING(-6),
    STATION_WRONG_ROUTING(-7),
    STATION_IN_DELIVERY(-8),
    STATION_UNIT_DAMAGED(-9),
    STATION_EXPORT(-10),
    STATION_EXPORT_BAG_FILL(-11)
}

enum class BeepCode(val value: Int) {
    BEEP_OK(1), //green
    BEEP_NOK(2), //red
    BEEP_WOK(3), //repeat-scan->yellow
    BEEP_DEPOT_OK(4)
}

enum class AppEnabledModul(val value: Int) {
    NON(0),
    HUB(1),
    SMS(2),
    LINE(4),
    STATION_EXPORT_BAG(8),
    STATION_IMPORT(16),
    DELIVERY(32),
    STATION_EXPORT(64),
    DELIVERY_100(128),
    START_PHONE(1024),
    START_WLAN(2048),
    HUB_ADMIN(4096),
    ALL(HUB.value + SMS.value + LINE.value + STATION_IMPORT.value + DELIVERY.value + HUB_ADMIN.value)
}

enum class ConnStatus(val value: Int) {
    NON(0),
    GPRS(1),
    WLAN(2),
    GPRS_WLAN(4),
    UNKNOWN(8)
}

enum class LineDirection(val value: Int) {
    IN(-1),
    OUT(1),
    NN(0)
}

enum class Commands(val value: Int) {
    MESSAGE(1),
    GET_UNITS_OUT(2),
    SW_UPDATE_OPTIONAL(3),
    SW_UPDATE_MUST(4),
    GET_UNITS_IN(5),
    GET_DEPOTS_IN(6),
    GET_DEPOTS_OUT(7),
    DB_KILL(8),
    DB_CLEAN(9),
    DB_SQL(10),
    SHOW_ERROR_LOG(11),
    SEND_ERROR_LOG(12),
    BOOT(13),
    DO_UPDATE(14),
    DROP(15),
    SEND_DB(16),
    SEND_DB_MESSAGE(17),
    DELETE_DB(18),
    CHANGE_PASSWORD(19),
    SEND_LOG_DATA(20),
    MESSAGE_BEEP(21)
}

enum class SendData {
    GET_COMMANDX,
    SEND_GPS_DATA,
    SEND_ACKNOWLEDGE,
    SEND_SCANS,
    LOGIN,
    LOGIN_LINE_IN,
    LOGIN_LINE_OUT,
    SEND_IMAGES,
    SEND_ERROR_LOG,
    GET_UNITS_LINE_IN,
    GET_UNITS_LINE_OUT,
    GET_DEPOTS_DATA_IN,
    GET_LINES_IN,
    SEND_MSG,
    GET_LINES_OUT,
    GET_DEPOTS_OUT_STATION,
    GET_UNITS_STATION,
    GET_SYS_COLLECTIONS,
    GET_TRANSLATION,
    GET_UNITS_STATION_IMPORT,
    GET_UNITS_STATION_EXPORT,
    GET_BAGS
}

//todo StopClassifizierung noch nicht volltändig
enum class ParcelService(
        val serviceId: Long,
        val parcelServiceRestriction: ParcelServiceRestriction,
        val validForStop: StopClassification) {
    NO_ADDITIONAL_SERVICE(
            serviceId = 0,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    APPOINTMENT(
            serviceId = 1,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    SUITCASE_SHIPPING(
            serviceId = 2,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    WEEKEND(
            serviceId = 4,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    BANK_HOLIDAY_DELIVERY(
            serviceId = 8,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    LATE_PICKUP(
            serviceId = 16,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    RECEIPT_ACKNOWLEDGEMENT(
            serviceId = 32,
            parcelServiceRestriction = ParcelServiceRestriction(
                    paperReceiptNeeded = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.BOTH),
    SELF_PICKUP(
            serviceId = 64,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    CASH_ON_DELIVERY(
            serviceId = 128,
            parcelServiceRestriction = ParcelServiceRestriction(
                    cash = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.BOTH),
    VALUED_PACKAGE(
            serviceId = 256,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    PHARMACEUTICALS(
            serviceId = 512,
            parcelServiceRestriction = ParcelServiceRestriction(
                    personalDeliveryOnly = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.BOTH),
    ADDRESS_CORRECTION(
            serviceId = 1024,
            parcelServiceRestriction = ParcelServiceRestriction(), validForStop = StopClassification.BOTH),
    WAITING_PERIOD_PICKUP(
            serviceId = 2048,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    WAITING_PERIOD_DELIVERY(
            serviceId = 4096,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    PICKUP(
            serviceId = 8192,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    IDENT_CONTRACT_SERVICE(
            serviceId = 16384,
            parcelServiceRestriction = ParcelServiceRestriction(
                    personalDeliveryOnly = true,
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.BOTH),
    SUBMISSION_PARTICIPATION(
            serviceId = 32768,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    SECURITY_RETURN(
            serviceId = 65536,
            parcelServiceRestriction = ParcelServiceRestriction(
                    imeiCheckRequired = true,
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.BOTH),
    LATE_DELIVERY(
            serviceId = 131072,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    XCHANGE(
            serviceId = 262144,
            parcelServiceRestriction = ParcelServiceRestriction(
                    summarizedDeliveryAllowed = false
            ),
            validForStop = StopClassification.DELIVERY),
    PHONE_RECEIPT(
            serviceId = 524288,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    DOCUMENTED_PERSONAL_DELIVERY(
            serviceId = 1048576,
            parcelServiceRestriction = ParcelServiceRestriction(
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false,
                    paperReceiptNeeded = true
            ),
            validForStop = StopClassification.BOTH),
    HIGHER_LIABILITY(
            serviceId = 2097152,
            parcelServiceRestriction = ParcelServiceRestriction(
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.BOTH),
    DEPARTMENT_DELIVERY(
            serviceId = 4194304,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    FIXED_APPOINTMENT(
            serviceId = 8388608,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    FAIR_SERVICE(
            serviceId = 16777216,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS(
            serviceId = 33554432,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    PACKAGING_RECIRCULATION(
            serviceId = 67108864,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    UNSUCCESSFUL_APPROACH(
            serviceId = 134217728,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    POSTBOX_DELIVERY(
            serviceId = 268435456,
            parcelServiceRestriction = ParcelServiceRestriction(),
            validForStop = StopClassification.BOTH),
    NO_ALTERNATIVE_DELIVERY(
            serviceId = 536870912,
            parcelServiceRestriction = ParcelServiceRestriction(
                    alternateDeliveryAllowed = false
            ),
            validForStop = StopClassification.BOTH);

    companion object {
        val byServiceId by lazy {
            mapOf(
                    *ParcelService.values().map { Pair(it.serviceId, it) }.toTypedArray()
            )
        }
    }
}

/**
 * Created by JT on 24.05.17.
 */
enum class Carrier(val value: Int) {
    DER_KURIER(0),
    UNKNOWN(1)
}

enum class ParcelType(val value: Int) {
    NOT_SET(0),
    UNKNOWN(1),
    LATTICE_BOX(2),
    FLYER(3),
    PARCEL(4),
    WITHOUT_PACKING(5),
    EUROPALLET(6),
    THERMO_BOX(7),
    ENVELOPE(8),
    ROLL(9),
    NB(10),
    VALUABLES(91);

    companion object {
        val valueMap by lazy {
            mapOf(
                    *ParcelType.values().map { Pair(it.value, it) }.toTypedArray()
            )
        }
    }
}

/**
 * Created by JT on 24.05.17.
 */
enum class OrderClassification(val value: Int) {
    PICKUP(0),
    DELIVERY(1),
    PICKUP_DELIVERY(2)
}

/**
 * Created by JT on 27.05.17.
 */
enum class StopClassification(val value: Int) {
    PICKUP(0),
    DELIVERY(1),
    BOTH(2)
}

enum class AdditionalInformationType(val value: Int) {
    IMEI(0),
    IDENTITY_CARD_ID(1),
    LOADING_LIST_INFO(2),
    DRIVER_INFO(3),
    CASH(4)
}

enum class UserRole(val value: Int) {
    ADMIN(10),
    POWERUSER(7),
    USER(6),
    DRIVER(4),
    CUSTOMER(2)
}

/**
 * Enumeration for possible types of vehicles, provided by the mobile app.
 */
enum class VehicleType(val value: String) {
    BIKE("BIKE"),
    CAR("CAR"),
    VAN("VAN"),
    TRUCK("TRUCK")
}

enum class Event(val value: Int, val creator: Char, val concatId: Int) {
    SENDUNG_ERFASST(100, 'A', 1),
    EINGANG_IMPORT(103, 'E', 1),
//    SENDUNG_ERFASST(-1, 'G', 1),
    DFÜ_DATEN_VORHANDEN(112, 'H', 1),
//    HUB_LAGER_EINGANG(-1, 'L', 1),
    EINGANG_EXPORT(101, 'A', 2),
    ROLLKARTENSCAN(104, 'E', 2),
//    EINGANG_EXPORT(-1, 'G', 2),
    HUB_ENTLADEN(114, 'H', 2),
//    HUB_LAGER_AUSGANG(-1, 'L', 2),
    LINIENSCAN(113, 'T', 2),
//    ROLLKARTENSCAN(108, 'E', 3),
    EXPORT_VERLADEN(102, 'A', 4),
    VEREINBARTE_IMPORT_KOSTEN(-1, 'B', 4),
    AUSGELIEFERT(106, 'E', 4),
    HUB_BELADEN(115, 'H', 4),
    RK_BILD(105, 'E', 5),
    ROLLKARTEN_DISPO(109, 'E', 6),
    LAUFZEIT_ANGEPASST(118, 'A', 7),
    IN_ZUSTELLUNG(120, 'E', 7),
    VEREINBARTE_PU_KOSTEN(110, 'B', 8),
    ZUSTELLHINDERNIS(107, 'E', 8),
    HUB_NOTERFASST(140, 'H', 8),
    HUB_BELADEVERSUCH(147, 'H', 9),
    BENUTZERABBRUCH(-1, 'E', 10),
    PACKSTUECK_BESCHAEDIGT(136, 'H', 11),
    ANFORDERUNG_IMPORT_KOSTEN(-1, 'B', 16),
    ANFORDERUNG_PU_KOSTEN(-1, 'B', 32),
    HUB_AENDERUNG(117, 'H', 32),
    PACKSTUECK_GELOESCHT(-1, 'A', 90),
    MEMO(163, 'H', 100),
    VERLADEN_LUFTFRACHT(124, 'H', 124),
    BILATERALER_PAKETVERKEHR(125, 'H', 125),
    CHECK_VOLUMENSCANNER(126, 'H', 126),
    INFO(127, 'H', 127),
    IMPORT___AENDERUNG(139, 'H', 139),
    NOTERFASSUNG(140, 'H', 140),
    EXPORT_AENDERUNG(141, 'H', 141),
    VIDEOUEBERWACHUNG_HUB(142, 'H', 142),
    ROUTINGAENDERUNG_HUB(143, 'H', 143),
    KEINE_DATEN_CHECK_VOLUMENSCANNER(144, 'H', 144),
    ABRECHNUNG_OK(145, 'H', 145),
    SMARTPIC_UPLOAD_OK(146, 'H', 146),
    LINIENVERSPAETUNG(147, 'H', 147),
    HOEHERE_GEWALT(148, 'H', 148),
    STAATLICHE_EINSCHRAENKUNG(149, 'H', 149),
    ENTSCHEIDUNG_SYSTEMZENTRALE(150, 'H', 150),
    QUALITY_CHECK_OK(151, 'H', 151),
    Q_CHECK_NICHT_OK_STORNO_IMP(152, 'H', 152),
    NEUTRALISIERUNG_SANKTION(153, 'H', 153),
    WEITERLEITUNG_SDG(154, 'H', 154),
    URSPRUNGS_SDG(155, 'H', 155),
    UPLOAD_EB_OK(156, 'H', 156),
    HUB_LAGER_EINGANG(157, 'H', 157),
    HUB_LAGER_AUSGANG(158, 'H', 158),
    Q_CHECK_NICHT_OK_STORNO_SK(159, 'H', 159),
    PREPAID_OK(160, 'H', 160),
    KOSTENFREIE_2_ZUSTELLUNG(161, 'H', 161),
    Q_CHECK_NICHT_OK_STORNO_EXP(162, 'H', 162),
    QUALIBLOCK_GESETZT(164, 'H', 164),
    KEINE_PHYSISCHE_PAKETUEBERGABE(165, 'H', 165),
    IDENT_VERTRAG_OK(-1, 'H', 166),
    IDENT_VERTRAG_PRUEFUNG(-1, 'H', 167),
    IDENT_VERTRAG_RUECKLAUF(168, 'H', 168),
    BAG_GESCHLOSSEN(169, 'H', 169),
    BAG_VERSCHLUSSVERSUCH(170, 'H', 170),
    PLOMBE_PASST_NICHT(171, 'H', 171),
    LINIE_ENTLADEN(172, 'T', 172),
    LINIE_BELADEN(173, 'T', 173),
    LINIE_ENTLADEVERSUCH_O_DATEN(-1, 'T', 174),
    LINIE_BELADEVERSUCH_O_DATEN(-1, 'T', 175),
    SDD_KLASSIFIZIERUNG(-1, 'A', 176),
    SDD_PLZ_NICHT_ZUGELASSEN(-1, 'A', 177),
    SDD_ZWEITER_ZUSTELLVERSUCH(-1, 'A', 178),
    EVENINGDELIVERY_KLASSIFIZIERUNG(-1, 'A', 179),
    EVENINGDELIVERY_PLZ_FALSCH(-1, 'A', 180),
    EVENINGDELIVERY_ZWEITER_VERSUCH(-1, 'A', 181),
    ADMIN_ENTLADEN(134, 'H', 256),
    PALETTE(135, 'H', 1028),
    AENDERUNG_GEWICHT(137, 'H', 4112),
    ROUTERLABELDRUCK(138, 'H', 8224)
}
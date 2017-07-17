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
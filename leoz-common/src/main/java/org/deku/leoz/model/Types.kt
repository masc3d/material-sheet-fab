package org.deku.leoz.model

import sx.io.serialization.Serializable

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

/**
 * Parcel service definitions
 */
@Serializable(0x8ef279bd15d711)
enum class ParcelService(
        val serviceId: Long,
        val constraints: Constraints = Constraints()) {

    NO_ADDITIONAL_SERVICE(
            serviceId = 0
    ),
    APPOINTMENT(
            serviceId = 1
    ),
    SUITCASE_SHIPPING(
            serviceId = 2
    ),
    WEEKEND(
            serviceId = 4
    ),
    BANK_HOLIDAY_DELIVERY(
            serviceId = 8
    ),
    LATE_PICKUP(
            serviceId = 16
    ),
    RECEIPT_ACKNOWLEDGEMENT(
            serviceId = 32,
            constraints = Constraints(
                    paperReceiptNeeded = true,
                    alternateDeliveryAllowed = false
            )
    ),
    SELF_PICKUP(
            serviceId = 64
    ),
    CASH_ON_DELIVERY(
            serviceId = 128,
            constraints = Constraints(
                    cash = true,
                    alternateDeliveryAllowed = false)
    ),
    VALUED_PACKAGE(
            serviceId = 256
    ),
    PHARMACEUTICALS(
            serviceId = 512,
            constraints = Constraints(
                    personalDeliveryOnly = true,
                    alternateDeliveryAllowed = false
            )
    ),
    ADDRESS_CORRECTION(
            serviceId = 1024
    ),
    WAITING_PERIOD_PICKUP(
            serviceId = 2048
    ),
    WAITING_PERIOD_DELIVERY(
            serviceId = 4096
    ),
    PICKUP(
            serviceId = 8192
    ),
    IDENT_CONTRACT_SERVICE(
            serviceId = 16384,
            constraints = Constraints(
                    personalDeliveryOnly = true,
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false
            )
    ),
    SUBMISSION_PARTICIPATION(
            serviceId = 32768
    ),
    SECURITY_RETURN(
            serviceId = 65536,
            constraints = Constraints(
                    imeiCheckRequired = true,
                    alternateDeliveryAllowed = false
            )
    ),
    LATE_DELIVERY(
            serviceId = 131072
    ),
    XCHANGE(
            serviceId = 262144,
            constraints = Constraints(
                    validForTasks = listOf(TaskType.DELIVERY),
                    summarizedDeliveryAllowed = false
            )
    ),
    PHONE_RECEIPT(
            serviceId = 524288
    ),
    DOCUMENTED_PERSONAL_DELIVERY(
            serviceId = 1048576,
            constraints = Constraints(
                    identityCheckRequired = true,
                    alternateDeliveryAllowed = false,
                    paperReceiptNeeded = true
            )
    ),
    HIGHER_LIABILITY(
            serviceId = 2097152,
            constraints = Constraints(
                    alternateDeliveryAllowed = false
            )
    ),
    DEPARTMENT_DELIVERY(
            serviceId = 4194304
    ),
    FIXED_APPOINTMENT(
            serviceId = 8388608
    ),
    FAIR_SERVICE(
            serviceId = 16777216
    ),
    SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS(
            serviceId = 33554432
    ),
    PACKAGING_RECIRCULATION(
            serviceId = 67108864
    ),
    UNSUCCESSFUL_APPROACH(
            serviceId = 134217728
    ),
    POSTBOX_DELIVERY(
            serviceId = 268435456
    ),
    NO_ALTERNATIVE_DELIVERY(
            serviceId = 536870912,
            constraints = Constraints(
                    alternateDeliveryAllowed = false
            )
    );

    companion object {
        val byServiceId by lazy {
            mapOf(
                    *ParcelService.values().map { Pair(it.serviceId, it) }.toTypedArray()
            )
        }
    }

    /**
     * Created by 27694066 on 17.05.2017.
     * Configure/Map delivery restrictions and particularities for the delivery/pickup process depending on service
     * The defaults reflect a standard delivery job
     */
    data class Constraints(
            val validForTasks: List<TaskType> = listOf(
                    TaskType.DELIVERY,
                    TaskType.PICKUP),
            val alternateDeliveryAllowed: Boolean = true,
            val personalDeliveryOnly: Boolean = false,
            val paperReceiptNeeded: Boolean = false,
            val identityCheckRequired: Boolean = false,
            val imeiCheckRequired: Boolean = false,
            val cash: Boolean = false,
            val summarizedDeliveryAllowed: Boolean = true
    )
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
        val valueMap by lazy { ParcelType.values().associateBy(ParcelType::value) }
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
 * Order task type.
 * Indicates which task (pickup/delivery) of an order to reference.
 * Referenced in central database, eg. `tad_tour_entry`
 */
enum class TaskType(val value: Int) {
    PICKUP(0),
    DELIVERY(1);

    companion object {
        val valueMap by lazy { TaskType.values().associateBy(TaskType::value) }
    }
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

enum class UserAppPermission(val value: Int, val internal: Boolean = false) {
    MOBILE_TOUR(value = 1),
    MOBILE_STATION(value = 2),
    MOBILE_LINE(value = 4),
    MOBILE_OPS(value = 8, internal = true)
}

enum class UserPreferenceKey {
    MAP_REFRESH_RATE
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
    ADJUSTMENT_WEIGHT(137, 'H', 4112),
    ADMIN_OFFLOADED(134, 'H', 256),
    AGREED_IMPORT_COSTS(-1, 'B', 4),
    AGREED_PICKUP_COSTS(110, 'B', 8),
    AIR_FREIGHT_LOADED(124, 'H', 124),
    BAG_CLOSED(169, 'H', 169),
    BAG_CLOSING_ATTEMPT(170, 'H', 170),
    BILATERAL_TRAFFIC(125, 'H', 125),
    CLEARING_OK(145, 'H', 145),
    DECISION_DERKURIER_HEADQUARTER(150, 'H', 150),
    DELIVERED(106, 'E', 4),
    DELIVERY_FAIL(107, 'E', 8),
    DELIVERY_LIST_IMAGE(105, 'E', 5),
    DELIVERY_LIST_SCANNED(104, 'E', 2),
    DELIVERY_LIST_SCHEDULED(109, 'E', 6),
    DIAL_DATA_AVAILABLE(112, 'H', 1),
    EVENINGDELIVERY_CLASSIFICATION(-1, 'A', 179),
    EVENINGDELIVERY_SECOND_DELIVERY_ATTEMPT(-1, 'A', 181),
    EVENINGDELIVERY_ZIP_NOT_ALLOWED(-1, 'A', 180),
    EXPORT_ADJUSTMENT(141, 'H', 141),
    EXPORT_LOADED(102, 'A', 4),
    EXPORT_RECEIVE(101, 'A', 2),
    FORCE_MAJEURE(148, 'H', 148),
    HUB_ADJUSTMENT(117, 'H', 32),
    HUB_LOADED(115, 'H', 4),
    HUB_LOADING_ATTEMPT(147, 'H', 9),
    HUB_OFFLOADED(114, 'H', 2),
    HUB_POST_CAPTURED(140, 'H', 8),
    HUB_ROUTING_ADJUSTMENT(143, 'H', 143),
    HUB_STORE_IN(157, 'H', 157),
    HUB_STORE_OUT(158, 'H', 158),
    HUB_VIDEO_SURVEILLANCE(142, 'H', 142),
    IDENT_CONTACT_INSPECTION(-1, 'H', 167),
    IDENT_CONTRACT_OK(-1, 'H', 166),
    IDENT_CONTRACT_RETURNS(168, 'H', 168),
    IMPORT_ADJUSTMENT(139, 'H', 139),
    IMPORT_RECEIVE(103, 'E', 1),
    IN_DELIVERY(120, 'E', 7),
    NOT_IN_DELIVERY(174, 'E', 11),
    TOUR_UNLOADED(175, 'E', 12),
    INFO(127, 'H', 127),
    LEADSEAL_DOES_NOT_MATCH(171, 'H', 171),
    LINE_DELAY(147, 'H', 147),
    LINE_LOADED(173, 'T', 173),
    LINE_LOADING_ATTEMPT_WITHOUT_DATA(-1, 'T', 175),
    LINE_OFFLOADED(172, 'T', 172),
    LINE_OFFLOADING_ATTEMPT_WITHOUT_DATA(-1, 'T', 174),
    LINE_SCAN(113, 'T', 2),
    MEMO(163, 'H', 100),
    NEUTRALIZATION_SANCTION(153, 'H', 153),
    NO_PHYSICAL_PARCEL_HANDOVER(165, 'H', 165),
    OPERATIONAL_TIME_ADJUSTED(118, 'A', 7),
    ORDER_CREATED(100, 'A', 1), //TODO: Order correct? Maybe shipment. Origin: "Sendung erfasst"
    ORIGIN_SHIPMENT(155, 'H', 155),
    PALETTE(135, 'H', 1028),
    PARCEL_DAMAGED(136, 'H', 11),
    PARCEL_DELETED(-1, 'A', 90),
    POST_CAPTURED(140, 'H', 140),
    PREPAID_OK(160, 'H', 160),
    QUALITY_BLOCK_ACTIVATED(164, 'H', 164),
    QUALITY_CHECK_NOT_OK_CANCELLATION_EXPORT(162, 'H', 162),
    QUALITY_CHECK_NOT_OK_CANCELLATION_IMPORT(152, 'H', 152),
    QUALITY_CHECK_NOT_OK_CANCELLATION_SK(159, 'H', 159),        //TODO: What is "SK"?
    QUALITY_CHECK_OK(151, 'H', 151),
    REQUEST_IMPORT_COSTS(-1, 'B', 16),
    REQUEST_PICKUP_COSTS(-1, 'B', 32),
    ROUTING_LABEL_PRINT(138, 'H', 8224),
    SDD_CLASSIFICATION(-1, 'A', 176),
    SDD_SECOND_DELIVERY_ATTEMPT(-1, 'A', 178),
    SDD_ZIP_NOT_ALLOWED(-1, 'A', 177),
    SECOND_DELIVERY_FREE_OF_CHARGE(161, 'H', 161),
    SHIPMENT_FORWARD(154, 'H', 154),
    SMARTPIC_UPLOAD_OK(146, 'H', 146),
    STATE_RUN_LIMITATIONS(149, 'H', 149),
    UPLOAD_EB_OK(156, 'H', 156),
    USER_CANCELLATION(-1, 'E', 10),
    VOLUME_SCAN_CHECK(126, 'H', 126),
    VOLUME_SCAN_NO_DATA(144, 'H', 144)
//    EINGANG_EXPORT(-1, 'G', 2),
//    HUB_LAGER_AUSGANG(-1, 'L', 2),
//    HUB_LAGER_EINGANG(-1, 'L', 1),
//    ROLLKARTENSCAN(108, 'E', 3),
//    SENDUNG_ERFASST(-1, 'G', 1),
}

enum class Reason(val id: Int, val oldValue: Int = 0) {
    ADDRESS_WRONG(id = 506, oldValue = 16),
    CANCELLED_NO_DELIVERY(id = 514, oldValue = 50),
    CAPACITY_BOTTLENECK(id = 528, oldValue = 73),
    CUSTOMER_ABSENT(id = 500, oldValue = 10),
    CUSTOMER_DID_OR_COULD_NOT_PAY(id = 521, oldValue = 65),
    CUSTOMER_MOVED(id = 509, oldValue = 20),
    CUSTOMER_REFUSED(id = 503, oldValue = 12),
    CUSTOMER_SELF_PICKUP(id = 505, oldValue = 15),
    CUSTOMER_UNKNOWN(id = 509, oldValue = 21),
    CUSTOMER_VACATION(id = 504, oldValue = 14),
    CUSTOMS_INSPECTIONS(id = 525, oldValue = 70),
    CUSTOMS_TAXES_PAYMENT_SUPPOSED_BY_CONSIGNEE(id = 526, oldValue = 71),
    EXCHANGE_CODE_CHECK_FAILED(id = 516, oldValue = 60),
    EXCHANGE_OBJECT_DAMAGED(id = 517, oldValue = 61),
    EXCHANGE_OBJECT_NOT_READY(id = 529, oldValue = 74),
    EXCHANGE_OBJECT_WRONG(id = 518, oldValue = 62),
    EXPORT_PARTNER_INFORMED(id = 501, oldValue = 11),
    Freischaltung(id = 520, oldValue = 64),
    IDENT_DOCUMENT_NOT_THERE(id = 524, oldValue = 68),
    MEANS_OF_PAYMENT_NOT_AVAILABLE(id = 530, oldValue = 75),
    NEIGHBOUR(id = 100),
    NEW_APPOINTMENT(id = 523, oldValue = 67),
    NORMAL(id = 0),
    PARCEL_DAMAGED(id = 513, oldValue = 31),
    PARCEL_MISSING(id = 512, oldValue = 30),
    PIN_IMEI_CHECK_FAILED(id = 532, oldValue = 77),
    POSTBOX(id = 101),
    SHIPMENT_RETOUR(id = 533, oldValue = 32),
    SHIPMENT_STORED(id = 522, oldValue = 66),
    SIGNATURE_REFUSED(id = 519, oldValue = 63),
    SMS_CODE_NOT_DELIVERED(id = 500, oldValue = 33),
    TRAFFIC_PROBLEMS(id = 527, oldValue = 72),
    WRONG_LOADED(id = 511, oldValue = 42),
    WRONG_ROUTING(id = 510, oldValue = 40)
//  Adresse_falsch(id = 506, oldValue = 19),
//  Adresse_falsch(id = 507, oldValue = 18),
//  Adresse_falsch-SDG_an_Lager(id = 506, oldValue = 17),
//  Annahme_verweigert(id = 502, oldValue = 13),
//  Fehlrouting(id = 510, oldValue = 41),
//  Fehlverladung(id = 511, oldValue = 43),
}

enum class counter(val value: Int) {
    FIELD_HISTORY(19),
    LOADING_LIST(4000)
}

enum class BagStatus(val value: Int) {
    CLOSED_FROM_STATION(value = 2),
    CLOSED_FROM_HUB(value = 6),
    OPENED(value = 5)
}

enum class LoadinglistType {
    NORMAL,
    BAG
}

const val maxWeightForParcelBag = 2.0

/**
 * Universal routing zones
 */
enum class RoutingZone(val value: String) {
    // TODO: document what those values represent
    A("A"),
    B("B"),
    C("C"),
    D("D")
}

/**
 * German national routing zones
 */
enum class RoutingZoneDe(val value: String) {
    // TODO: document what those values represent
    WR("WR"),
    UL("UL")
}
USE dekuclient;
CREATE OR REPLACE ALGORITHM = UNDEFINED
VIEW tad_v_order AS
  SELECT
    OrderID                          AS id,
    AuftragsID                       AS customer_reference,
    Service                          AS service,
    sdgtype,
    sdgstatus,
    ROrderID                         AS reference_id_to_exchange_id,
    DepotNrAD                        AS customer_station,
    DepotNrAbD                       AS pickup_station,
    DepotNrLD                        AS delivery_station,
    PreisNN                          AS cash_amount,
    ColliesGesamt                    AS number_of_parcels,
    GewichtGesamt                    AS weight_of_order,
    Information1                     AS pickup_information1,
    Information2                     AS pickup_information2,
    Info_Rollkarte                   AS delivery_information,
    Inhalt                           AS content,
    FirmaS                           AS pickup_address_line1,
    FirmaS2                          AS pickup_address_line2,
    FirmaS3                          AS pickup_address_line3,
    StrasseS                         AS pickup_address_street,
    StrNrS                           AS pickup_address_street_no,
    lands                            AS pickup_address_country_code,
    PLzs                             AS pickup_address_zip_code,
    orts                             AS pickup_address_city,
    TelefonNrS                       AS pickup_address_phoneNumber,
    FirmaD                           AS delivery_address_line1,
    FirmaD2                          AS delivery_address_line2,
    FirmaD3                          AS delivery_address_line3,
    StrasseD                         AS delivery_address_street,
    StrNrD                           AS delivery_address_street_no,
    landD                            AS delivery_address_country_code,
    PLzD                             AS delivery_address_zip_code,
    ortD                             AS delivery_address_city,
    TelefonNrD                       AS delivery_address_phoneNumber,
    str_to_date(concat(date_format(Verladedatum, '%Y-%m-%d '), date_format(Verladezeit_von, '%H:%i:%s')),
                '%Y-%m-%d %H:%i:%s') AS appointment_pickup_start,
    str_to_date(concat(date_format(Verladedatum, '%Y-%m-%d '), date_format(Verladezeit_bis, '%H:%i:%s')),
                '%Y-%m-%d %H:%i:%s') AS appointment_pickup_end,
    (KZ_erweitert & 128) = 128       AS appointment_pickup_not_before_start,
    str_to_date(concat(date_format(dtAuslieferung, '%Y-%m-%d '), date_format(dttermin_von, '%H:%i:%s')),
                '%Y-%m-%d %H:%i:%s') AS appointment_delivery_start,
    str_to_date(concat(date_format(dtAuslieferung, '%Y-%m-%d '), date_format(dttermin, '%H:%i:%s')),
                '%Y-%m-%d %H:%i:%s') AS appointment_delivery_end,
    (KZ_erweitert & 256) = 256       AS appointment_delivery_not_before_start
  FROM
    tblauftrag;

CREATE OR REPLACE ALGORITHM = UNDEFINED
VIEW tad_v_order_parcel AS
  SELECT
    OrderID * 100 + OrderPos AS id,
    OrderID                  AS order_id,
    CollieBelegNr            AS scan_id,
    RollkartennummerD        AS last_delivery_list_id,
    VerpackungsArt           AS parcel_type,
    Laenge                   AS dimension_length,
    Hoehe                    AS dimension_height,
    Breite                   AS dimension_width,
    GewichtReal              AS dimention_weight
  FROM
    tblauftragcollies
  WHERE CollieBelegNr > 0 AND OrderPos > 0;

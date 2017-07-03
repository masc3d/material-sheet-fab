USE dekuclient;
CREATE OR REPLACE ALGORITHM = UNDEFINED
VIEW trn_v_order AS
  SELECT
    OrderID AS id,
    AuftragsID as customer_reference,
    Service as service,
    sdgtype,
    sdgstatus,
    ROrderID as reference_id_to_exchange_id,
    DepotNrAD as customer_station,
    DepotNrAbD as pickup_station,
    DepotNrLD as delivery_station,
    Nachnahmebetrag as cash_amount,
    ColliesGesamt as number_of_parcels,
    GewichtGesamt as weight_of_order,
    Information1 as pickup_information1,
    Information2 as pickup_information2,
    Info_Rollkarte as delivery_information,
    Inhalt  as content,
    FirmaS as pickup_address_line1,
    FirmaS2 as pickup_address_line2,
    FirmaS3 as pickup_address_line3,
    StrasseS as pickup_address_street,
    StrNrS as pickup_address_street_no,
    lands as pickup_address_country_code,
    PLzs as pickup_address_zip_code,
    orts as pickup_address_city,
    TelefonNrS as pickup_address_phoneNumber,
    FirmaD as delivery_address_line1,
    FirmaD2 as delivery_address_line2,
    FirmaD3 as delivery_address_line3,
    StrasseD as delivery_address_street,
    StrNrD as delivery_address_street_no,
    landD as delivery_address_country_code,
    PLzD as delivery_address_zip_code,
    ortD as delivery_address_city,
    TelefonNrD as delivery_address_phoneNumber,
    str_to_date(concat( date_format( dtAuslieferung, '%Y-%m-%d '),date_format( dttermin_von,'%H:%i:%s')),'%Y-%m-%d %H:%i:%s') as appointment_pickup_start,
    str_to_date(concat( date_format( dtAuslieferung, '%Y-%m-%d '),date_format( dttermin,'%H:%i:%s')),'%Y-%m-%d %H:%i:%s') as appointment_pickup_end,
    (KZ_erweitert & 128)=128 as appointment_pickup_not_before_start,
    str_to_date(concat( date_format( dtAuslieferung, '%Y-%m-%d '),date_format( dttermin_von,'%H:%i:%s')),'%Y-%m-%d %H:%i:%s') as appointment_delivery_start,
    str_to_date(concat( date_format( dtAuslieferung, '%Y-%m-%d '),date_format( dttermin,'%H:%i:%s')),'%Y-%m-%d %H:%i:%s') as appointment_delivery_end,
    (KZ_erweitert & 256)=256 as appointment_delivery_not_before_start
  FROM
    tblauftrag;

CREATE OR REPLACE ALGORITHM = UNDEFINED
VIEW trn_v_order_parcel AS
  SELECT
    OrderID*100+OrderPos as id,
    OrderID AS order_id,
    CollieBelegNr as number,
    VerpackungsArt as parcelType,
    Laenge as dimension_length,
    Hoehe as dimension_height,
    Breite as dimension_width,
    GewichtReal as dimention_weight
  FROM
    tblauftragcollies;

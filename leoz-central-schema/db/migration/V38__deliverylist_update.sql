USE dekuclient;

CREATE
OR REPLACE ALGORITHM = UNDEFINED
VIEW tad_v_deliverylist_details AS
  SELECT
    rkdetails.rollkartennummer AS id,
    rkdetails.position         AS order_position,
    rkdetails.OrderID          AS order_id,
    rkdetails.rknr_akt         AS removed_in_deliverylist,
    rkdetails.lieferdepot      AS delivery_station,
    'DELIVERY'                 AS stopType
  FROM
    rkdetails
  ORDER BY rkdetails.position;

ALTER TABLE tbldepotliste
  ADD COLUMN debitor_id INT NOT NULL DEFAULT 0,
  ADD INDEX `debitorid` (`debitor_id` ASC);

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_deliverylist AS
  SELECT
    rkkopf.rollkartennummer              AS id,
    rkkopf.rollkartendatum               AS delivery_list_date,
    rkkopf.lieferdepot                   AS delivery_station,
    (SELECT debitor_id
     FROM tbldepotliste
     WHERE depotnr = rkkopf.lieferdepot) AS debitor_id,
    rkkopf.druckzeit                     AS create_date
  FROM rkkopf;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_order_parcel AS
  SELECT
    ((tblauftragcollies.OrderID * 100) + tblauftragcollies.OrderPos) AS id,
    tblauftragcollies.OrderID                                        AS order_id,
    tblauftragcollies.CollieBelegNr                                  AS scan_id,
    tblauftragcollies.RollkartennummerD                              AS last_delivery_list_id,
    tblauftragcollies.VerpackungsArt                                 AS parcel_type,
    tblauftragcollies.Laenge                                         AS dimension_length,
    tblauftragcollies.Hoehe                                          AS dimension_height,
    tblauftragcollies.Breite                                         AS dimension_width,
    tblauftragcollies.GewichtReal                                    AS dimention_weight,
    tblauftragcollies.bmpFileName                                    AS signature_path,
    tblauftragcollies.erstlieferstatus                               AS delivered_status,
    tblauftragcollies.lieferfehler                                   AS last_delivered_event_reason,
    tblauftragcollies.mydepotabd                                     AS pickup_station,
    tblauftragcollies.mydepotid2                                     AS delivery_station
  FROM
    tblauftragcollies
  WHERE
    ((tblauftragcollies.CollieBelegNr > 0)
     AND (tblauftragcollies.OrderPos > 0))
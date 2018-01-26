USE `dekuclient`;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_order_parcel AS
  SELECT
    `dekuclient`.`tblauftragcollies`.`parcel_id`         AS `id`,
    `dekuclient`.`tblauftragcollies`.`OrderID`           AS `order_id`,
    `dekuclient`.`tblauftragcollies`.`CollieBelegNr`     AS `scan_id`,
    `dekuclient`.`tblauftragcollies`.`RollkartennummerD` AS `last_delivery_list_id`,
    `dekuclient`.`tblauftragcollies`.`VerpackungsArt`    AS `parcel_type`,
    `dekuclient`.`tblauftragcollies`.`Laenge`            AS `dimension_length`,
    `dekuclient`.`tblauftragcollies`.`Hoehe`             AS `dimension_height`,
    `dekuclient`.`tblauftragcollies`.`Breite`            AS `dimension_width`,
    `dekuclient`.`tblauftragcollies`.`GewichtReal`       AS `dimention_weight`,
    `dekuclient`.`tblauftragcollies`.`bmpFileName`       AS `signature_path`,
    `dekuclient`.`tblauftragcollies`.`erstlieferstatus`  AS `delivered_status`,
    `dekuclient`.`tblauftragcollies`.`lieferfehler`      AS `last_delivered_event_reason`,
    `dekuclient`.`tblauftragcollies`.`mydepotabd`        AS `pickup_station`,
    `dekuclient`.`tblauftragcollies`.`mydepotid2`        AS `delivery_station`,
    `dekuclient`.`tblauftragcollies`.`is_damaged`        AS `is_damaged`
  FROM `dekuclient`.`tblauftragcollies`
  WHERE ((`dekuclient`.`tblauftragcollies`.`CollieBelegNr` > 0) AND (`dekuclient`.`tblauftragcollies`.`OrderPos` > 0));


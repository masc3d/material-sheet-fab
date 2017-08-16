USE `dekuclient`;
CREATE
OR REPLACE ALGORITHM = UNDEFINED
  DEFINER = `root`@`%`
  SQL SECURITY DEFINER
VIEW `tad_v_deliverylist_details` AS
  SELECT
    `rkdetails`.`rollkartennummer` AS `id`,
    `rkdetails`.`position`         AS `order_position`,
    `rkdetails`.`OrderID`          AS `order_id`,
    `rkdetails`.`rknr_akt`         AS `removed_in_deliverylist`,
    'DELIVERY'                     AS `stopType`
  FROM
    `rkdetails`
  ORDER BY `rkdetails`.`position`;

CREATE
OR REPLACE ALGORITHM = UNDEFINED
  DEFINER = `root`@`%`
  SQL SECURITY DEFINER
VIEW `tad_v_order_parcel` AS
  SELECT
    ((`tblauftragcollies`.`OrderID` * 100) + `tblauftragcollies`.`OrderPos`) AS `id`,
    `tblauftragcollies`.`OrderID`                                            AS `order_id`,
    `tblauftragcollies`.`CollieBelegNr`                                      AS `scan_id`,
    `tblauftragcollies`.`RollkartennummerD`                                  AS `last_delivery_list_id`,
    `tblauftragcollies`.`VerpackungsArt`                                     AS `parcel_type`,
    `tblauftragcollies`.`Laenge`                                             AS `dimension_length`,
    `tblauftragcollies`.`Hoehe`                                              AS `dimension_height`,
    `tblauftragcollies`.`Breite`                                             AS `dimension_width`,
    `tblauftragcollies`.`GewichtReal`                                        AS `dimention_weight`,
    `tblauftragcollies`.`bmpFileName`                                        AS `signature_path`,
    `tblauftragcollies`.`erstlieferstatus`                                   AS `delivered_status`
  FROM
    `tblauftragcollies`
  WHERE
    ((`tblauftragcollies`.`CollieBelegNr` > 0)
     AND (`tblauftragcollies`.`OrderPos` > 0));

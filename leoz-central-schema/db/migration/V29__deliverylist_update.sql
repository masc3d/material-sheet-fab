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
    'DELIVERY'                     AS `stopType`
  FROM
    `rkdetails`
  ORDER BY `rkdetails`.`position`;

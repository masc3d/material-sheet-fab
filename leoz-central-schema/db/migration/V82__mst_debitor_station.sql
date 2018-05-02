USE `dekuclient`;

ALTER TABLE mst_debitor_station
  CHANGE COLUMN activ_from  delivery_active_from DATE;
ALTER TABLE mst_debitor_station
  CHANGE COLUMN activ_to  delivery_active_to DATE;
ALTER TABLE mst_debitor_station
  ADD tour_route_provider_id INT(11) NOT NULL DEFAULT 0
  AFTER ts_updated;
ALTER TABLE mst_debitor_station
  ADD pickup_active_from DATE NOT NULL DEFAULT '0000-00-00'
  AFTER delivery_active_to;
ALTER TABLE mst_debitor_station
  ADD pickup_active_to DATE NOT NULL DEFAULT '0000-00-00'
  AFTER pickup_active_from;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW `mst_v_debitor_station_delivery` AS

  SELECT
    id                     AS id,
    debitor_id             AS debitor_id,
    station_id             AS station_id,
    tour_route_provider_id as tour_route_provider_id,
    sync_id                AS sync_id
  FROM mst_debitor_station
  WHERE date(now()) BETWEEN delivery_active_from AND delivery_active_to;

CREATE TABLE mst_ext_provider (
  id          INT(11)   NOT NULL  AUTO_INCREMENT,
  type        INT(11),
  contract_id INT(11)   NULL,                                     #Container-id
  ts_created  TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  ts_updated  TIMESTAMP NOT NULL  DEFAULT '0000-00-00 00:00:00'
  ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_ext_provider');
DELIMITER $$
ALTER TABLE `mst_ext_provider`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_ext_provider_sync_insert`
  BEFORE INSERT
  ON `mst_ext_provider`
  FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_ext_provider');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_ext_provider_sync_update`
  BEFORE UPDATE
  ON `mst_ext_provider`
  FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_ext_provider');
  END $$

DELIMITER ;

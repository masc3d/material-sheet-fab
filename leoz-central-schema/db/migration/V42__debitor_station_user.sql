USE dekutmp;

ALTER TABLE tblauftrag
  ADD COLUMN pickup_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN pickup_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_latitude DOUBLE NOT NULL DEFAULT 0;

ALTER TABLE tblauftragtmp
  ADD COLUMN pickup_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN pickup_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_latitude DOUBLE NOT NULL DEFAULT 0;

USE dekuclient;

ALTER TABLE tblauftrag
  ADD COLUMN pickup_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN pickup_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_latitude DOUBLE NOT NULL DEFAULT 0;

ALTER TABLE tblauftragtmp
  ADD COLUMN pickup_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN pickup_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_latitude DOUBLE NOT NULL DEFAULT 0;

ALTER TABLE tbldepotliste
  DROP COLUMN debitor_id,
  DROP INDEX debitorid;

ALTER TABLE tbldepotliste
  ADD COLUMN ValOk_without_bag INT NOT NULL DEFAULT 0;

ALTER TABLE tad_node_geoposition
    ADD COLUMN debitor_id INT(11) NOT NULL DEFAULT 0;

ALTER TABLE mst_user
  ADD config JSON DEFAULT NULL  NULL;
ALTER TABLE mst_user
  ADD preferences JSON DEFAULT NULL  NULL;
ALTER TABLE mst_user
  DROP allowed_stations;

ALTER TABLE mst_debitor
  ADD parent_id INT DEFAULT 0 NOT NULL;

CREATE TABLE mst_debitor_station (
  id         INT(11)   NOT NULL  AUTO_INCREMENT,
  debitor_id INT(11)   NULL,
  station_id INT(11)   NULL,
  ts_created TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  ts_updated TIMESTAMP NOT NULL  DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  activ_from DATE      NOT NULL  DEFAULT '0000-00-00',
  activ_to   DATE      NOT NULL  DEFAULT '0000-00-00',
  PRIMARY KEY (id)
);

CREATE TABLE mst_station_user (
  id         INT(11)   NOT NULL  AUTO_INCREMENT,
  user_id    INT(11)   NULL,
  station_id INT(11)   NULL,
  ts_created TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  ts_updated TIMESTAMP NOT NULL  DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW tad_v_deliverylist AS
  SELECT
    rkkopf.rollkartennummer AS id,
    rkkopf.rollkartendatum  AS delivery_list_date,
    rkkopf.lieferdepot      AS delivery_station,
    (SELECT debitor_id
     FROM mst_debitor_station
     WHERE station_id =
           (SELECT id
            FROM tbldepotliste
            WHERE depotnr = rkkopf.lieferdepot))
                            AS debitor_id,
    rkkopf.druckzeit        AS create_date
  FROM rkkopf;

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_debitor_station');
DELIMITER $$
ALTER TABLE `mst_debitor_station`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_debitor_station_sync_insert`
BEFORE INSERT ON `mst_debitor_station`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_debitor_station');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_debitor_station_sync_update`
BEFORE UPDATE ON `mst_debitor_station`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_debitor_station');
  END $$

DELIMITER ;

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_station_user');

DELIMITER $$
ALTER TABLE `mst_station_user`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_user_sync_insert`
BEFORE INSERT ON `mst_station_user`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station_user');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_user_sync_update`
BEFORE UPDATE ON `mst_station_user`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station_user');
  END $$

DELIMITER ;
INSERT INTO `sys_sync` (`table_name`) VALUES ('tad_node_geoposition');

DELIMITER $$
ALTER TABLE `tad_node_geoposition`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `tad_node_geoposition_sync_insert`
BEFORE INSERT ON `tad_node_geoposition`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('tad_node_geoposition');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `tad_node_geoposition_sync_update`
BEFORE UPDATE ON `tad_node_geoposition`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('tad_node_geoposition');
  END $$

DELIMITER ;
INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_debitor');

DELIMITER $$
ALTER TABLE `mst_debitor`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_debitor_sync_insert`
BEFORE INSERT ON `mst_debitor`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_debitor');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_debitor_sync_update`
BEFORE UPDATE ON `mst_debitor`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_debitor');
  END $$


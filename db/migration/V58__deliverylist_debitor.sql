USE dekutmp;

ALTER TABLE tblauftragcolliestmp
  ADD COLUMN is_damaged INT NOT NULL DEFAULT 0;

USE dekuclient;

ALTER TABLE tblauftragcollies
  ADD COLUMN is_damaged INT NOT NULL DEFAULT 0;

ALTER TABLE tblauftragcolliestmp
  ADD COLUMN is_damaged INT NOT NULL DEFAULT 0;

ALTER TABLE rkdetails
  ADD COLUMN rk_id INT NOT NULL DEFAULT 0
  AFTER id,
  ADD COLUMN debitor_id INT NOT NULL DEFAULT 0
  AFTER rk_id,
  ADD INDEX ix_rk_id (rk_id ASC),
  ADD INDEX ix_debitor_id (debitor_id ASC);

INSERT INTO `sys_sync` (`table_name`) VALUES ('rkkopf');

DELIMITER $$

ALTER TABLE `rkkopf`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$


CREATE DEFINER = CURRENT_USER TRIGGER `rkkopf_sync_insert`
BEFORE INSERT ON `rkkopf`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('rkkopf');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `rkkopf_sync_update`
BEFORE UPDATE ON `rkkopf`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('rkkopf');
  END $$

DELIMITER ;

INSERT INTO `sys_sync` (`table_name`) VALUES ('rkdetails');

DELIMITER $$

ALTER TABLE `rkdetails`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `rkdetails_sync_insert`
BEFORE INSERT ON `rkdetails`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('rkdetails');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `rkdetails_sync_update`
BEFORE UPDATE ON `rkdetails`
FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('rkdetails');
  END $$

DELIMITER ;

ALTER TABLE tad_parcel_messages
  ADD COLUMN node_id_x INT NULL
  AFTER user_id,
  ADD INDEX tad_parcel_messages_nodeidx_index (node_id_x ASC);

CREATE DATABASE IF NOT EXISTS mobile;

USE mobile;

CREATE TABLE tad_stop_list (
  id            INT(11) NOT NULL AUTO_INCREMENT,
  `user_id`     INT(11)          DEFAULT NULL,
  `node_id`     INT(11)          DEFAULT NULL,
  `consumer_id` INT(11)          DEFAULT NULL,
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

CREATE TABLE tad_stop_list_details (
  id            INT(11) NOT NULL AUTO_INCREMENT,
  stop_list_id  INT(11)          DEFAULT NULL,
  stop_position DOUBLE  NOT NULL DEFAULT 0,
  order_id     BIGINT(20)       DEFAULT NULL,
  is_removed  Int(11) NOT NULL DEFAULT 0
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

CREATE TABLE `tad_parcel_messages` (
  `id`               INT(11)   NOT NULL AUTO_INCREMENT,
  `user_id`          INT(11)            DEFAULT NULL,
  `node_id`          INT(11)            DEFAULT NULL,
  `consumer_id`      INT(11)            DEFAULT NULL,
  `parcel_id`        BIGINT(20)         DEFAULT NULL,
  `scanned`          DATETIME           DEFAULT NULL,
  `event_value`      INT(11)            DEFAULT NULL,
  `reason_id`        INT(11)            DEFAULT NULL,
  `latitude`         DOUBLE             DEFAULT NULL,
  `longitude`        DOUBLE             DEFAULT NULL,
  `additional_info`  JSON               DEFAULT NULL,
  `is_proccessed`    TINYINT(1)         DEFAULT '0',
  `create_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `tad_parcel_messages_userid_index` (`user_id`),
  KEY `tad_parcel_messages_nodeid_index` (`node_id`),
  KEY `tad_parcel_messages_parcelid_index` (`parcel_id`),
  KEY `tad_parcel_messages_scanned_index` (`scanned`),
  KEY `tad_parcel_messages_eventvalue_index` (`event_value`),
  KEY `tad_parcel_messages_reasonid_index` (`reason_id`),
  KEY `tad_parcel_messages_is_proccessed_index` (`is_proccessed`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

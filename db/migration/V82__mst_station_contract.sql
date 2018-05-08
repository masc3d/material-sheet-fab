USE `dekuclient`;

CREATE TABLE mst_station_contract (
  id            INT(11)   NOT NULL  AUTO_INCREMENT,
  debitor_id    INT(11)   NULL,
  station_id    INT(11)   NULL,
  contract_type INT(11)   NOT NULL,
  contract_no   VARCHAR(100),
  active_from   DATE,
  active_to     DATE,
  meta          JSON      NULL,
  sync_id       BIGINT    NOT NULL  DEFAULT 0,

  ts_created    TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  ts_updated    TIMESTAMP NOT NULL  DEFAULT '0000-00-00 00:00:00'
  ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id)
);

ALTER TABLE `mst_station_contract`
  ADD INDEX `ix_sync_id` (`sync_id`);

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_station_contract');

DELIMITER $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_contract_sync_insert`
  BEFORE INSERT
  ON `mst_station_contract`
  FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station_contract');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_contract_sync_update`
  BEFORE UPDATE
  ON `mst_station_contract`
  FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station_contract');
  END $$

DELIMITER ;

DROP VIEW IF EXISTS mst_v_debitor_station;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW `mst_v_station_contract` AS

  SELECT
    id            AS id,
    debitor_id    AS debitor_id,
    station_id    AS station_id,
    contract_type AS contract_type,
    contract_no   AS contract_no,
    active_from   AS active_from,
    active_to     AS active_to,
    meta          AS meta,
    sync_id       AS sync_id
  FROM mst_station_contract
  WHERE date(now()) BETWEEN active_from AND active_to;


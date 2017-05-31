use dekuclient;

drop table mst_mobile_device;

drop table mst_user_scope;

ALTER TABLE `mst_station`
  DROP INDEX `LKZ` ,
  DROP INDEX `DepotTree`,
  ADD COLUMN `debitor_id` INT NOT NULL AFTER `station_nr`;

CREATE TABLE `mst_debitor` (
  `debitor_id` int(11) NOT NULL AUTO_INCREMENT,
  `debitor_nr` double NOT NULL,
  `ts_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ts_updated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`debitor_id`),
  UNIQUE KEY `debitor_uindex` (`debitor_nr`));

# ALTER TABLE `mst_node`
#   ADD COLUMN `debitor_id` INT NOT NULL AFTER `node_id`,
#   ADD COLUMN `key_id` INT NOT NULL AFTER `debitor_id`,
#   ADD COLUMN `typ` VARCHAR(10) NOT NULL default '0' AFTER `key_id`;

ALTER TABLE `dekuclient`.`mst_key`
  CHANGE COLUMN `typ` `typ` VARCHAR(10) NOT NULL DEFAULT '0' ;

USE `dekuclient`;

# Add sync support for mst_station

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_station') $$

ALTER TABLE `mst_station`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

DROP TRIGGER IF EXISTS `mst_station_sync_insert` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_sync_insert` BEFORE INSERT ON `mst_station` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station');
  END $$

DROP TRIGGER IF EXISTS `mst_station_sync_update` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_sync_update` BEFORE UPDATE ON `mst_station` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station');
  END $$

## Add sync support for mst_sector

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_sector') $$

ALTER TABLE `mst_sector`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

DROP TRIGGER IF EXISTS `mst_sector_sync_insert` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_sector_sync_insert` BEFORE INSERT ON `mst_sector` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_sector');
  END $$

DROP TRIGGER IF EXISTS `mst_sector_sync_update` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_sector_sync_update` BEFORE UPDATE ON `mst_sector` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_sector');
  END $$

## Add sync support for mst_country

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_country') $$

ALTER TABLE `mst_country`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

DROP TRIGGER IF EXISTS `mst_country_sync_insert` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_country_sync_insert` BEFORE INSERT ON `mst_country` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_country');
  END $$

DROP TRIGGER IF EXISTS `mst_country_sync_update` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_country_sync_update` BEFORE UPDATE ON `mst_country` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_country');
  END $$

## Add sync support for mst_route

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_route') $$

ALTER TABLE `mst_route`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

DROP TRIGGER IF EXISTS `mst_route_sync_insert` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_route_sync_insert` BEFORE INSERT ON `mst_route` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_route');
  END $$

DROP TRIGGER IF EXISTS `mst_route_sync_update` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_route_sync_update` BEFORE UPDATE ON `mst_route` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_route');
  END $$

## Add sync support for mst_holidayctrl

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_holidayctrl') $$

ALTER TABLE `mst_holidayctrl`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

DROP TRIGGER IF EXISTS `mst_holidayctrl_sync_insert` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_holidayctrl_sync_insert` BEFORE INSERT ON `mst_holidayctrl` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_holidayctrl');
  END $$

DROP TRIGGER IF EXISTS `mst_holidayctrl_sync_update` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_holidayctrl_sync_update` BEFORE UPDATE ON `mst_holidayctrl` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_holidayctrl');
  END $$

## Add sync support for mst_routinglayer

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_routinglayer') $$

ALTER TABLE `mst_routinglayer`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

DROP TRIGGER IF EXISTS `mst_routinglayer_sync_insert` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_routinglayer_sync_insert` BEFORE INSERT ON `mst_routinglayer` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_routinglayer');
  END $$

DROP TRIGGER IF EXISTS `mst_routinglayer_sync_update` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_routinglayer_sync_update` BEFORE UPDATE ON `mst_routinglayer` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_routinglayer');
  END $$

## Add sync support for mst_station_sector

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_station_sector') $$

ALTER TABLE `mst_station_sector`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

DROP TRIGGER IF EXISTS `mst_station_sector_sync_insert` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_sector_sync_insert` BEFORE INSERT ON `mst_station_sector` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station_sector');
  END $$

DROP TRIGGER IF EXISTS `mst_station_sector_sync_update` $$
CREATE DEFINER = CURRENT_USER TRIGGER `mst_station_sector_sync_update` BEFORE UPDATE ON `mst_station_sector` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station_sector');
  END $$


USE `dekuclient`;

## Add sync support for mst_routinglayer

DELIMITER $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_bundle_version') $$

ALTER TABLE `mst_bundle_version`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_bundle_version_sync_insert` BEFORE INSERT ON `mst_bundle_version` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_bundle_version');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `mst_bundle_version_sync_update` BEFORE UPDATE ON `mst_bundle_version` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_bundle_version');
  END $$
USE `dekuclient`;

# Add sync support for XXX

DELIMITER $$

ALTER TABLE `XXX`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

INSERT INTO `sys_sync` (`table_name`) VALUES ('XXX');

CREATE DEFINER = CURRENT_USER TRIGGER `XXX_sync_insert` BEFORE INSERT ON `XXX` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('XXX');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `XXX_sync_update` BEFORE UPDATE ON `XXX` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('XXX');
  END $$


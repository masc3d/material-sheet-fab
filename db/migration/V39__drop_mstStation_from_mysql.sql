use dekuclient;

#todo if process genrateRouting in LEO is updated
drop TABLE mst_station;

# Add sync support for tbldepotliste
DELIMITER $$
ALTER TABLE `tbldepotliste`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`) $$

CREATE DEFINER = CURRENT_USER TRIGGER `tbldepotliste_sync_insert` BEFORE INSERT ON `tbldepotliste` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('tbldepotliste');
  END $$

CREATE DEFINER = CURRENT_USER TRIGGER `tbldepotliste_sync_update` BEFORE UPDATE ON `tbldepotliste` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('tbldepotliste');
  END $$


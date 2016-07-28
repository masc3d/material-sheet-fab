USE `dekuclient`;

CREATE TABLE `sys_sync` (
  `id`         INT(11) NOT NULL,
  `table_name` VARCHAR(45)      DEFAULT NULL,
  `sync_id`    BIGINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)
  ENGINE = MyISAM
  DEFAULT CHARSET = latin1;

INSERT INTO `dekuclient`.`sys_sync` (`id`, `table_name`, `sync_id`) VALUES ('1', 'mst_station', '0');

ALTER TABLE `dekuclient`.`mst_station`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0;

DELIMITER $$

DROP FUNCTION IF EXISTS f_sync_increment_unsafe$$
CREATE FUNCTION f_sync_increment_unsafe(p_table_name VARCHAR(50))
  RETURNS BIGINT
  BEGIN
    UPDATE sys_sync
    SET sync_id = sync_id + 1
    WHERE table_name = p_table_name;

    SET @sync_id = (SELECT sync_id
                    FROM sys_sync
                    WHERE table_name = p_table_name);

    RETURN @sync_id;
  END
$$

DROP FUNCTION IF EXISTS f_sync_increment$$
CREATE FUNCTION f_sync_increment(p_table_name VARCHAR(50))
  RETURNS BIGINT
  BEGIN
    SET @sync_id = (SELECT sync_id
                    FROM sys_sync
                    WHERE table_name = p_table_name FOR UPDATE );
    SET @sync_id = @sync_id + 1;

    UPDATE sys_sync
    SET sync_id = @sync_id
    WHERE table_name = p_table_name;

    RETURN @sync_id;
  END
$$

DROP TRIGGER IF EXISTS dekuclient.mst_station_BEFORE_INSERT$$
CREATE DEFINER = CURRENT_USER TRIGGER `dekuclient`.`mst_station_BEFORE_INSERT` BEFORE INSERT ON `mst_station` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station');
  END
$$

DROP TRIGGER IF EXISTS dekuclient.mst_station_BEFORE_UPDATE$$
CREATE DEFINER = CURRENT_USER TRIGGER `dekuclient`.`mst_station_BEFORE_UPDATE` BEFORE UPDATE ON `mst_station` FOR EACH ROW
  BEGIN
    SET NEW.sync_id = f_sync_increment('mst_station');
  END
$$

USE `dekuclient`;

CREATE TABLE `sys_sync` (
  `id`         INT(11) NOT NULL,
  `table_name` VARCHAR(45)      DEFAULT NULL,
  `id_sync`    BIGINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)
  ENGINE = MyISAM
  DEFAULT CHARSET = latin1;

INSERT INTO `dekuclient`.`sys_sync` (`id`, `table_name`, `id_sync`) VALUES ('1', 'mst_station', '0');

ALTER TABLE `dekuclient`.`mst_station`
  ADD COLUMN `id_sync` BIGINT NOT NULL DEFAULT 0;

DELIMITER $$

DROP FUNCTION IF EXISTS f_sync_increment_unsafe$$
CREATE FUNCTION f_sync_increment_unsafe(p_table_name VARCHAR(50))
  RETURNS BIGINT
  BEGIN
    UPDATE sys_sync
    SET id_sync = id_sync + 1
    WHERE table_name = p_table_name;

    SET @id_sync = (SELECT id_sync
                    FROM sys_sync
                    WHERE table_name = p_table_name);

    RETURN @id_sync;
  END$$

DROP FUNCTION IF EXISTS f_sync_increment$$
CREATE FUNCTION f_sync_increment(p_table_name VARCHAR(50))
  RETURNS BIGINT
  BEGIN
    SET @id_sync = (SELECT id_sync
                    FROM sys_sync
                    WHERE table_name = p_table_name FOR UPDATE );
    SET @id_sync = @id_sync + 1;

    UPDATE sys_sync
    SET id_sync = @id_sync
    WHERE table_name = p_table_name;
    
    RETURN @id_sync;
  END$$

DROP TRIGGER IF EXISTS dekuclient.mst_station_BEFORE_INSERT$$
CREATE DEFINER = CURRENT_USER TRIGGER `dekuclient`.`mst_station_BEFORE_INSERT` BEFORE INSERT ON `mst_station` FOR EACH ROW
  BEGIN
    SET NEW.id_sync = f_sync_increment('mst_station');
  END$$

DROP TRIGGER IF EXISTS dekuclient.mst_station_BEFORE_UPDATE$$
CREATE DEFINER = CURRENT_USER TRIGGER `dekuclient`.`mst_station_BEFORE_UPDATE` BEFORE UPDATE ON `mst_station` FOR EACH ROW
  BEGIN
    SET NEW.id_sync = f_sync_increment('mst_station');
  END$$
DELIMITER ;

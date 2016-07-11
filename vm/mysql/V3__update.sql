CREATE TABLE `sys_synch` (
  `idsys_synch` int(11) NOT NULL,
  `tablename` varchar(45) DEFAULT NULL,
  `counter` BIGINT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`idsys_synch`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `dekuclient`.`sys_synch` (`idsys_synch`, `tablename`, `counter`) VALUES ('1', 'mst_station', '0');

ALTER TABLE `dekuclient`.`mst_station` 
ADD COLUMN `synchid` BIGINT NOT NULL DEFAULT 0 ;

USE `dekuclient`;

DELIMITER $$

DROP TRIGGER IF EXISTS dekuclient.mst_station_BEFORE_INSERT$$
USE `dekuclient`$$
CREATE DEFINER = CURRENT_USER TRIGGER `dekuclient`.`mst_station_BEFORE_INSERT` BEFORE INSERT ON `mst_station` FOR EACH ROW
BEGIN
	update sys_synch set counter=counter+1 where tablename='mst_station';
    set @counter = (select counter from sys_synch where tablename='mst_station');
	set new.synchid= @counter;
END
$$
DELIMITER ;
USE `dekuclient`;

DELIMITER $$

DROP TRIGGER IF EXISTS dekuclient.mst_station_BEFORE_UPDATE$$
USE `dekuclient`$$
CREATE DEFINER = CURRENT_USER TRIGGER `dekuclient`.`mst_station_BEFORE_UPDATE` BEFORE UPDATE ON `mst_station` FOR EACH ROW
BEGIN
	update sys_synch set counter=counter+1 where tablename='mst_station';
    set @counter = (select counter from sys_synch where tablename='mst_station');
	set new.synchid= @counter;
END$$
DELIMITER ;

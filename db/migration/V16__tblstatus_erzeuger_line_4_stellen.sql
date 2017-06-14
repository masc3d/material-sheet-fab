use dekuclient;

ALTER TABLE `dekuclient`.`tblstatus`
  CHANGE COLUMN `Erzeugerstation` `Erzeugerstation` CHAR(4) NOT NULL DEFAULT '' ,
  CHANGE COLUMN `Exportstation` `Exportstation` CHAR(4) NOT NULL DEFAULT '' ;

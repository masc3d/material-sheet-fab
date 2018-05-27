use dekuclient;

ALTER TABLE tblstatus
  CHANGE COLUMN `Erzeugerstation` `Erzeugerstation` CHAR(4) NOT NULL DEFAULT '' ,
  CHANGE COLUMN `Exportstation` `Exportstation` CHAR(4) NOT NULL DEFAULT '' ;

ALTER TABLE sdd_status
  CHANGE COLUMN `Erzeugerstation` `Erzeugerstation` CHAR(4) NOT NULL DEFAULT '' ,
  CHANGE COLUMN `Exportstation` `Exportstation` CHAR(4) NOT NULL DEFAULT '' ;


USE dekuclient;

ALTER TABLE `dekuclient`.`tblauftrag`
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

ALTER TABLE tblauftrag
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

ALTER TABLE tblauftrag_xml
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

ALTER TABLE tblauftrag_trx
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

ALTER TABLE sdd_auftrag
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

ALTER TABLE tblauftragtmp
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

ALTER TABLE tblstatus
  ADD COLUMN `error_code` INT NOT NULL DEFAULT 0;

USE dekutmp;

ALTER TABLE tblauftrag
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

ALTER TABLE tblauftragtmp
  CHANGE COLUMN `processStatus` `processStatus` INT NOT NULL DEFAULT 0 ;

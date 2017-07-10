use dekuclient;
ALTER TABLE `dekuclient`.`tblauftrag`
  CHANGE COLUMN `ROrderID` `ROrderID` DOUBLE NOT NULL DEFAULT 0 ;

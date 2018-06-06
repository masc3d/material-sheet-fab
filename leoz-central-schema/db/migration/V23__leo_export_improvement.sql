ALTER TABLE `dekuclient`.`tblauftrag`
  DROP INDEX `DepotNrLD`,
  DROP INDEX `DepotNrED`,
  ADD COLUMN `sendstatus2` INT NOT NULL DEFAULT 0,
  ADD INDEX `sendstatus2` (`sendstatus2` ASC);

  ALTER TABLE `dekuclient`.`tblstatus`
ADD COLUMN `sendstatus2` INT NOT NULL DEFAULT 0,
ADD INDEX `sendstatus2` (`sendstatus2` ASC );

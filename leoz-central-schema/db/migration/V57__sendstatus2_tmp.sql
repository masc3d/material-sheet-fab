USE dekuclient;

ALTER TABLE tblauftragtmp
  ADD COLUMN `sendstatus2` INT NOT NULL DEFAULT 0;

USE dekutmp;

ALTER TABLE tblauftragtmp
  ADD COLUMN `sendstatus2` INT NOT NULL DEFAULT 0;


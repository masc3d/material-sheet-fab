USE dekuclient;

ALTER TABLE tblauftrag
  ADD COLUMN processStatus VARCHAR(10) NULL,
  ADD COLUMN source_system VARCHAR(10) NULL,
  DROP INDEX sendstatus2,
  ADD INDEX processStatus (processStatus ASC);

ALTER TABLE tblauftrag_xml
  ADD COLUMN pickup_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN pickup_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN processStatus VARCHAR(10) NULL,
  ADD COLUMN source_system VARCHAR(10) NULL;

ALTER TABLE tblauftrag_trx
  ADD COLUMN pickup_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN pickup_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN processStatus VARCHAR(10) NULL,
  ADD COLUMN source_system VARCHAR(10) NULL;

ALTER TABLE sdd_auftrag
  ADD COLUMN pickup_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN pickup_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_longitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN delivery_latitude DOUBLE NOT NULL DEFAULT 0,
  ADD COLUMN processStatus VARCHAR(10) NULL,
  ADD COLUMN source_system VARCHAR(10) NULL;

ALTER TABLE tblauftragtmp
  ADD COLUMN processStatus VARCHAR(10) NULL,
  ADD COLUMN source_system VARCHAR(10) NULL;

ALTER TABLE tblstatus
  ADD COLUMN `source_system` VARCHAR(10) NULL,
  ADD COLUMN `status_code` INT NOT NULL DEFAULT 0,
  ADD COLUMN `event` INT NOT NULL DEFAULT 0,
  ADD COLUMN `reason` INT NOT NULL DEFAULT 0;

USE dekutmp;

ALTER TABLE tblauftrag
  ADD COLUMN processStatus VARCHAR(10) NULL,
  ADD COLUMN source_system VARCHAR(10) NULL;

ALTER TABLE tblauftragtmp
  ADD COLUMN processStatus VARCHAR(10) NULL,
  ADD COLUMN source_system VARCHAR(10) NULL;

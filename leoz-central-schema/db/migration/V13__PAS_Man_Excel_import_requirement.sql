use dekuclient;
ALTER TABLE clareklapositionen
  ADD COLUMN sysovproz_import INT NULL,
  ADD COLUMN import_values VARCHAR(20) NULL;


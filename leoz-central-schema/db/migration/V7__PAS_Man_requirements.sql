use dekuclient;
ALTER TABLE clareklapositionen
  ADD COLUMN sap_taxcode VARCHAR(5) NULL;

ALTER TABLE clamanuelleartikel
  ADD COLUMN sysov INT NOT NULL DEFAULT 0,
  ADD COLUMN sap_taxcode VARCHAR(5) NULL;
USE dekuclient;

ALTER TABLE scnsetcount
  ADD COLUMN charge DOUBLE NOT NULL DEFAULT 0;

ALTER TABLE mst_node
  ADD COLUMN serial VARCHAR(45) NULL,
  ADD COLUMN current_version VARCHAR(45) NULL,
  ADD COLUMN ts_lastlogin TIMESTAMP NULL;

ALTER TABLE mst_user
  ADD COLUMN allowed_stations JSON DEFAULT NULL;

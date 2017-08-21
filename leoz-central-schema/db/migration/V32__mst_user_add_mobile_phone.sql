USE `dekuclient`;
ALTER TABLE mst_user ADD phone_mobile VARCHAR(45) NULL;
ALTER TABLE mst_user
  MODIFY COLUMN phone_mobile VARCHAR(45) AFTER phone;
use dekuclient;
ALTER TABLE mst_user
CHANGE COLUMN `key_id` `key_id` INT (11) NULL DEFAULT NULL,
DROP INDEX `mst_key_uindex`;
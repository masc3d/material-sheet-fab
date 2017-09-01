use dekuclient;
ALTER TABLE mst_user
  CHANGE COLUMN `key_id` `key_id` INT (11) NOT NULL DEFAULT '0';

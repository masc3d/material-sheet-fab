use dekuclient;

ALTER TABLE `dekuclient`.`mst_key`
  CHANGE COLUMN `typ` `type` VARCHAR(10) NOT NULL DEFAULT '0' ;

ALTER TABLE `dekuclient`.`mst_user`
  ADD COLUMN key_id INT NOT NULL DEFAULT 0 AFTER id,
  DROP COLUMN salt,
  DROP COLUMN api_key;



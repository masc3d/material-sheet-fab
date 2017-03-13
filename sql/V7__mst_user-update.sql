USE `dekuclient`;

ALTER TABLE `mst_user`
CHANGE COLUMN `user_id` `user_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '' ,
CHANGE COLUMN `user_name` `user_name` VARCHAR(30) NOT NULL COMMENT '' ,
CHANGE COLUMN `station_nr` `expires_on` TIMESTAMP NULL DEFAULT NULL COMMENT '' ,
CHANGE COLUMN `permission_routing` `password` VARCHAR(50) NOT NULL COMMENT '' ,
CHANGE COLUMN `timestamp` `salt` VARCHAR(45) NOT NULL COMMENT '' ,
ADD COLUMN `api_key` VARCHAR(45) NULL COMMENT '' AFTER `salt`,
ADD COLUMN `active` INT NOT NULL COMMENT '' AFTER `api_key`,
ADD COLUMN `firstname` VARCHAR(45) NOT NULL COMMENT '' AFTER `active`,
ADD COLUMN `lastname` VARCHAR(45) NOT NULL COMMENT '' AFTER `firstname`,
ADD COLUMN `external_user` INT NULL COMMENT '' AFTER `lastname`,
ADD COLUMN `phone` VARCHAR(45) NULL COMMENT '' AFTER `external_user`,
ADD COLUMN `ts_created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '' AFTER `phone`,
ADD COLUMN `ts_updated` TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '' AFTER `ts_created`,
ADD COLUMN `ts_lastlogin` TIMESTAMP NULL COMMENT '' AFTER `ts_updated`,
ADD UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC)  COMMENT '';

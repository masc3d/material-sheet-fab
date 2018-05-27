USE dekuclient;

DROP FUNCTION IF EXISTS f_uuid_to_bin;

# mysql < 8.0 helper functions TODO. https://mysqlserverteam.com/mysql-8-0-uuid-support/

CREATE FUNCTION f_uuid_to_bin(_uuid VARCHAR(36))
  RETURNS BINARY(16) DETERMINISTIC
  RETURN
  UNHEX(CONCAT(
            SUBSTR(_uuid, 1, 8),
            SUBSTR(_uuid, 10, 4),
            SUBSTR(_uuid, 15, 4),
            SUBSTR(_uuid, 20, 4),
            SUBSTR(_uuid, 25)));

DROP FUNCTION IF EXISTS f_uuid_from_bin;

CREATE FUNCTION f_uuid_to_str(_bin BINARY(16))
  RETURNS VARCHAR(36) DETERMINISTIC
  RETURN
  LCASE(CONCAT_WS('-',
                  HEX(SUBSTR(_bin, 1, 4)),
                  HEX(SUBSTR(_bin, 5, 2)),
                  HEX(SUBSTR(_bin, 7, 2)),
                  HEX(SUBSTR(_bin, 9, 2)),
                  HEX(SUBSTR(_bin, 11))
        ));

# mst_user uid

ALTER TABLE mst_user
  ADD COLUMN uid BINARY(16) UNIQUE
  AFTER id,
  ADD COLUMN `key_uid` BINARY(16)
  AFTER `key_id`,
  ADD INDEX `ix_key_uid`(uid);

CREATE TRIGGER mst_user_uid
  BEFORE INSERT
  ON mst_user
  FOR EACH ROW
  SET new.uid = f_uuid_to_bin(UUID());

# Add sync support for mst_user

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_user');

ALTER TABLE `mst_user`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`);

DELIMITER ;;
CREATE DEFINER = CURRENT_USER TRIGGER `mst_user_sync_insert`
  BEFORE INSERT
  ON `mst_user`
  FOR EACH ROW
  BEGIN
    SET new.sync_id = f_sync_increment('mst_user');
  END ;;

CREATE DEFINER = CURRENT_USER TRIGGER `mst_user_sync_update`
  BEFORE UPDATE
  ON `mst_user`
  FOR EACH ROW
  BEGIN
    SET new.sync_id = f_sync_increment('mst_user');
  END ;;
DELIMITER ;

# Add sync support for mst_node

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_node');

ALTER TABLE `mst_node`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`);

DELIMITER ;;
CREATE DEFINER = CURRENT_USER TRIGGER `mst_node_sync_insert`
  BEFORE INSERT
  ON `mst_node`
  FOR EACH ROW
  BEGIN
    SET new.sync_id = f_sync_increment('mst_node');
  END ;;

CREATE DEFINER = CURRENT_USER TRIGGER `mst_node_sync_update`
  BEFORE UPDATE
  ON `mst_node`
  FOR EACH ROW
  BEGIN
    SET new.sync_id = f_sync_increment('mst_node');
  END ;;
DELIMITER ;

# mst_key uid

ALTER TABLE mst_key
  ADD COLUMN uid BINARY(16) UNIQUE
  AFTER key_id;

CREATE TRIGGER mst_key_uid
  BEFORE INSERT
  ON mst_key
  FOR EACH ROW
  SET new.uid = f_uuid_to_bin(UUID());

# Add sync support for mst_key

INSERT INTO `sys_sync` (`table_name`) VALUES ('mst_key');

ALTER TABLE `mst_key`
  ADD COLUMN `sync_id` BIGINT NOT NULL DEFAULT 0,
  ADD INDEX `ix_sync_id` (`sync_id`);

DELIMITER ;;
CREATE DEFINER = CURRENT_USER TRIGGER `mst_key_sync_insert`
  BEFORE INSERT
  ON `mst_key`
  FOR EACH ROW
  BEGIN
    SET new.sync_id = f_sync_increment('mst_key');
  END ;;

CREATE DEFINER = CURRENT_USER TRIGGER `mst_key_sync_update`
  BEFORE UPDATE
  ON `mst_key`
  FOR EACH ROW
  BEGIN
    SET new.sync_id = f_sync_increment('mst_key');
  END ;;
DELIMITER ;

# Generate and update uids

UPDATE mst_user tuser
SET tuser.uid = f_uuid_to_bin(UUID())
WHERE tuser.uid IS NULL;

UPDATE mst_key tkey
SET tkey.uid = f_uuid_to_bin(UUID())
WHERE tkey.uid IS NULL;

UPDATE mst_user tuser
SET tuser.key_uid = (
  SELECT uid
  FROM mst_key tkey
  WHERE tuser.key_id = tkey.key_id
);

# Views

CREATE OR REPLACE VIEW v_mst_user AS
  SELECT
    f_uuid_to_str(mst_user.uid)     AS uuid,
    f_uuid_to_str(mst_user.key_uid) AS key_uuid,
    mst_user.*
  FROM mst_user;


CREATE OR REPLACE VIEW v_mst_key AS
  SELECT
    f_uuid_to_str(mst_key.uid) AS uuid,
    mst_key.*
  FROM mst_key;


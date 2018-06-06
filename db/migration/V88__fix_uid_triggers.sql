DROP TRIGGER mst_user_uid;
DROP TRIGGER mst_key_uid;

DELIMITER $$

CREATE TRIGGER mst_user_uid_insert
  BEFORE INSERT
  ON mst_user
  FOR EACH ROW
  IF new.uid IS NULL
  THEN SET new.uid = f_uuid_to_bin(UUID());
  END IF
$$

CREATE TRIGGER mst_key_uid_insert
  BEFORE INSERT
  ON mst_key
  FOR EACH ROW
  IF new.uid IS NULL
  THEN SET new.uid = f_uuid_to_bin(UUID());
  END IF
$$

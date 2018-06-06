ALTER TABLE mst_node
  ADD COLUMN uid BINARY(16) UNIQUE
  AFTER node_id,
  ADD INDEX `ix_key_uid`(uid);

UPDATE mst_node tnode
SET tnode.uid = f_uuid_to_bin(UUID())
WHERE tnode.uid IS NULL;

CREATE OR REPLACE VIEW v_mst_node AS
  SELECT
    f_uuid_to_str(mst_node.uid) AS uuid,
    mst_node.*
  FROM mst_node;

DELIMITER $$

CREATE TRIGGER mst_node_insert
  BEFORE INSERT
  ON mst_node
  FOR EACH ROW
  SET new.key = f_uuid_to_str(new.uid);
$$

CREATE TRIGGER mst_node_update
  BEFORE UPDATE
  ON mst_node
  FOR EACH ROW
  SET new.key = f_uuid_to_str(new.uid);
$$

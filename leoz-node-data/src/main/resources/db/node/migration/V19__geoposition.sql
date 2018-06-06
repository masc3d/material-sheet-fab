ALTER TABLE tad_node_geoposition
  DROP PRIMARY KEY;
ALTER TABLE tad_node_geoposition
  ALTER COLUMN position_id
  RENAME TO id;
ALTER TABLE tad_node_geoposition
  ALTER COLUMN id IDENTITY;

---ALTER TABLE tad_node_geoposition
---  DROP COLUMN node_id;

ALTER TABLE tad_node_geoposition
  ADD node_uid UUID;
CREATE INDEX idx_tad_node_geoposition_node_uid
  ON tad_node_geoposition (node_uid);

ALTER TABLE mst_node
  ALTER COLUMN key
 RENAME TO uid;
ALTER TABLE mst_node
  ALTER COLUMN uid
  ---ADD uid
  UUID DEFAULT RANDOM_UUID() NOT NULL;
CREATE UNIQUE INDEX idx_mst_node_uid
  ON mst_node (uid);
ALTER TABLE tad_tour ALTER COLUMN deliverylist_id RENAME TO custom_id;
ALTER TABLE tad_tour ALTER COLUMN custom_id VARCHAR NULL;

ALTER TABLE tad_tour ADD COLUMN (parent_id BIGINT NULL) AFTER station_no;
ALTER TABLE tad_tour ADD COLUMN (optimization_meta MEDIUMTEXT NULL) AFTER optimized;

CREATE INDEX ix_tad_tour_user_id
  ON tad_tour (user_id);

CREATE INDEX ix_tad_tour_node_id
  ON tad_tour (node_id);

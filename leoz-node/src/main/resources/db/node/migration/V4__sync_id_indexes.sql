CREATE INDEX idx_mst_station_sync_id
  ON mst_station (sync_id);

CREATE INDEX idx_mst_debitor_sync_id
  ON mst_debitor (sync_id);

CREATE INDEX idx_mst_debitor_station_sync_id
  ON mst_debitor_station(sync_id);

CREATE INDEX idx_tad_node_geoposition_sync_id
  ON tad_node_geoposition(sync_id);

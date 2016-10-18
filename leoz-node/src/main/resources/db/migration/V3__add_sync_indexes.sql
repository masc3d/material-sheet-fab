CREATE INDEX idx_mst_bundle_version_sync_id
  ON "mst_bundle_version"("sync_id");

CREATE INDEX idx_mst_country_sync_id
  ON "mst_country"("sync_id");

CREATE INDEX idx_mst_holiday_ctrl_sync_id
  ON "mst_holiday_ctrl"("sync_id");

CREATE INDEX idx_mst_route_sync_id
  ON "mst_route"("sync_id");

CREATE INDEX idx_mst_routing_layer_sync_id
  ON "mst_routing_layer"("sync_id");

CREATE INDEX idx_mst_sector_sync_id
  ON "mst_sector"("sync_id");

CREATE INDEX idx_mst_station_sync_id
  ON "mst_station"("sync_id");

CREATE INDEX idx_mst_station_sector_sync_id
  ON "mst_station_sector"("sync_id");

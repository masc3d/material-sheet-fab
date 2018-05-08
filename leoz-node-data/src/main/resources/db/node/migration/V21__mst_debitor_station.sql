DROP TABLE IF EXISTS mst_debitor_station;

CREATE TABLE mst_station_contract (
  id            INTEGER,
  debitor_id    INTEGER,
  station_id    INTEGER,
  contract_type INTEGER   NOT NULL DEFAULT 0,
  active_from   TIMESTAMP NOT NULL,
  active_to     TIMESTAMP NOT NULL,
  sync_id       BIGINT    NOT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX ix_mst_station_contract_station_id
  ON mst_station_contract (station_id, contract_type);

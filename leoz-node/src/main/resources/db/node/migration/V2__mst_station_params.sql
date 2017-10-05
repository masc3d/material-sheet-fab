ALTER TABLE mst_station
  ADD COLUMN export_valuables_allowed INTEGER;

ALTER TABLE mst_station
  ADD COLUMN  export_valuables_without_bag_allowed INTEGER ;

CREATE TABLE trn_node_geoposition (
  position_id       INTEGER ,
  user_id           INTEGER ,
  node_id           INTEGER ,
  ts_created        TIMESTAMP NOT NULL,
  ts_updated        TIMESTAMP NOT NULL,
  latitude          DOUBLE,
  longitude         DOUBLE,
  position_datetime DATETIME  NOT NULL,
  speed             FLOAT,
  bearing           FLOAT,
  altitude          DOUBLE,
  accuracy          FLOAT,
  vehicle_type      VARCHAR,
  debitor_id        INTEGER ,
  PRIMARY KEY (position_id)
);

ALTER TABLE mst_station
  ADD COLUMN export_valuables_allowed INTEGER;

ALTER TABLE mst_station
  ADD COLUMN  export_valuables_without_bag_allowed INTEGER ;

CREATE TABLE tad_node_geoposition (
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
  sync_id           BIGINT    NOT NULL,
  PRIMARY KEY (position_id)
);

CREATE TABLE mst_debitor_station (
  id      INTEGER,
  debitor_id        INTEGER,
  station_id        INTEGER,
  ts_created        TIMESTAMP NOT NULL ,
  ts_updated        TIMESTAMP NOT NULL ,
  activ_from        DATE ,
  activ_to          DATE ,
  sync_id           BIGINT    NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mst_debitor(
	debitor_id        INTEGER,
	debitor_nr        DOUBLE ,
	ts_created        TIMESTAMP NOT NULL ,
	ts_updated        TIMESTAMP NOT NULL ,
	parent_id         INTEGER,
  sync_id           BIGINT    NOT NULL,
	PRIMARY KEY (debitor_id)
);

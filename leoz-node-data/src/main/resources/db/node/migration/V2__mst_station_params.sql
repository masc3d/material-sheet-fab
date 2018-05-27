
ALTER TABLE mst_station
  ADD COLUMN export_valuables_allowed INTEGER;

ALTER TABLE mst_station
  ADD COLUMN  export_valuables_without_bag_allowed INTEGER ;

DELETE FROM mst_station;

ALTER TABLE MST_STATION ADD station_id INT NOT NULL;
CREATE UNIQUE INDEX MST_STATION_station_id_uindex ON MST_STATION (station_id);
ALTER TABLE MST_STATION DROP PRIMARY KEY;
ALTER TABLE MST_STATION ADD CONSTRAINT MST_STATION_station_id_pk PRIMARY KEY (station_id);
DROP INDEX PUBLIC.IDX_MST_STATION_SYNC_ID;

DELETE FROM mst_station;

CREATE TABLE tad_node_geoposition (
  position_id       INTEGER ,
  user_id           INTEGER ,
  node_id           INTEGER ,
  ts_created        TIMESTAMP NOT NULL,
  ts_updated        TIMESTAMP,
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
  ts_updated        TIMESTAMP,
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

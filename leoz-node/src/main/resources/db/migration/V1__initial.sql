CREATE TABLE mst_country (
  code           VARCHAR   NOT NULL,
  max_len        INTEGER,
  min_len        INTEGER,
  name_string_id INTEGER,
  routing_typ    INTEGER,
  sync_id        BIGINT    NOT NULL,
  timestamp      TIMESTAMP NOT NULL,
  zip_format     VARCHAR,
  PRIMARY KEY (code)
);
CREATE TABLE mst_holiday_ctrl (
  country     VARCHAR   NOT NULL,
  holiday     TIMESTAMP NOT NULL,
  ctrl_pos    INTEGER,
  description VARCHAR,
  sync_id     BIGINT    NOT NULL,
  timestamp   TIMESTAMP NOT NULL,
  PRIMARY KEY (country, holiday)
);
CREATE TABLE mst_route (
  id           BIGINT    NOT NULL,
  area         VARCHAR,
  country      VARCHAR,
  etod         TIME,
  holiday_ctrl VARCHAR,
  island       INTEGER,
  layer        INTEGER,
  ltodholiday  TIME,
  ltodsa       TIME,
  ltop         TIME,
  saturday_ok  INTEGER,
  station      INTEGER,
  sync_id      BIGINT    NOT NULL,
  term         INTEGER,
  timestamp    TIMESTAMP NOT NULL,
  valid_crtr   INTEGER,
  valid_from   TIMESTAMP,
  valid_to     TIMESTAMP,
  zip_from     VARCHAR,
  zip_to       VARCHAR,
  PRIMARY KEY (id)
);
CREATE INDEX idx_mst_route_layer_country_zip_from_zip_to_valid_from_valid_to
  ON mst_route (layer, country, zip_from, zip_to, valid_from, valid_to);
CREATE INDEX index_mst_route_timestamp
  ON mst_route (timestamp);
CREATE TABLE mst_routing_layer (
  layer       INTEGER   NOT NULL,
  description VARCHAR,
  services    INTEGER,
  sync_id     BIGINT    NOT NULL,
  timestamp   TIMESTAMP NOT NULL,
  PRIMARY KEY (layer)
);
CREATE TABLE mst_sector (
  sector_to   VARCHAR   NOT NULL,
  sector_from VARCHAR   NOT NULL,
  valid_from  TIMESTAMP NOT NULL,
  sync_id     BIGINT    NOT NULL,
  timestamp   TIMESTAMP NOT NULL,
  valid_to    TIMESTAMP,
  via         VARCHAR,
  PRIMARY KEY (sector_to, sector_from, valid_from)
);
CREATE TABLE mst_station (
  station_nr       INTEGER   NOT NULL,
  address1         VARCHAR,
  address2         VARCHAR,
  billing_address1 VARCHAR,
  billing_address2 VARCHAR,
  billing_city     VARCHAR,
  billing_country  VARCHAR,
  billing_housenr  VARCHAR,
  billing_street   VARCHAR,
  billing_zip      VARCHAR,
  city             VARCHAR,
  contact_person1  VARCHAR,
  contact_person2  VARCHAR,
  country          VARCHAR,
  email            VARCHAR,
  housenr          VARCHAR,
  mobile           VARCHAR,
  phone1           VARCHAR,
  phone2           VARCHAR,
  poslat           DOUBLE,
  poslong          DOUBLE,
  sector           VARCHAR,
  service_phone1   VARCHAR,
  service_phone2   VARCHAR,
  strang           INTEGER,
  street           VARCHAR,
  sync_id          BIGINT    NOT NULL,
  telefax          VARCHAR,
  timestamp        TIMESTAMP NOT NULL,
  ustid            VARCHAR,
  web_address      VARCHAR,
  zip              VARCHAR,
  PRIMARY KEY (station_nr)
);
CREATE TABLE mst_station_sector (
  station_nr    INTEGER NOT NULL,
  sector        VARCHAR NOT NULL,
  routing_layer INTEGER,
  sync_id       BIGINT  NOT NULL,
  timestamp     TIMESTAMP,
  PRIMARY KEY (station_nr, sector)
);
CREATE TABLE sys_property (
  station     INTEGER NOT NULL,
  id          INTEGER NOT NULL,
  description VARCHAR,
  is_enabled  BOOLEAN,
  timestamp   TIMESTAMP,
  value       VARCHAR,
  PRIMARY KEY (station, id)
);

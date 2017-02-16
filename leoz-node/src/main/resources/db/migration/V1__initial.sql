CREATE TABLE mst_bundle_version (
  id      BIGINT NOT NULL PRIMARY KEY,
  bundle  VARCHAR          DEFAULT NULL,
  alias   VARCHAR          DEFAULT NULL,
  version VARCHAR          DEFAULT NULL,
  sync_id BIGINT  NOT NULL,
  UNIQUE (alias, bundle)
);

CREATE TABLE mst_country (
  code           VARCHAR   NOT NULL,
  min_len        INTEGER,
  max_len        INTEGER,
  name_string_id INTEGER,
  routing_typ    INTEGER,
  zip_format     VARCHAR,
  timestamp      TIMESTAMP NOT NULL,
  sync_id        BIGINT    NOT NULL,
  PRIMARY KEY (code)
);

CREATE TABLE mst_station (
  station_nr       INTEGER   NOT NULL,
  address1         VARCHAR,
  address2         VARCHAR,
  billing_address1 VARCHAR,
  billing_address2 VARCHAR,
  billing_city     VARCHAR,
  billing_country  VARCHAR,
  billing_house_nr VARCHAR,
  billing_street   VARCHAR,
  billing_zip      VARCHAR,
  city             VARCHAR,
  contact_person1  VARCHAR,
  contact_person2  VARCHAR,
  country          VARCHAR,
  email            VARCHAR,
  house_nr         VARCHAR,
  mobile           VARCHAR,
  phone1           VARCHAR,
  phone2           VARCHAR,
  pos_lat          DOUBLE,
  pos_long         DOUBLE,
  sector           VARCHAR,
  service_phone1   VARCHAR,
  service_phone2   VARCHAR,
  strang           INTEGER,
  street           VARCHAR,
  telefax          VARCHAR,
  ustid            VARCHAR,
  web_address      VARCHAR,
  zip              VARCHAR,
  timestamp        TIMESTAMP NOT NULL,
  sync_id          BIGINT    NOT NULL,
  PRIMARY KEY (station_nr)
);

CREATE TABLE mst_holiday_ctrl (
  id          BIGINT    NOT NULL,
  country     VARCHAR   NOT NULL,
  holiday     TIMESTAMP NOT NULL,
  ctrl_pos    INTEGER,
  description VARCHAR,
  timestamp   TIMESTAMP NOT NULL,
  sync_id     BIGINT    NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (country, holiday)
);

CREATE TABLE mst_routing_layer (
  layer       INTEGER   NOT NULL,
  description VARCHAR,
  services    INTEGER,
  timestamp   TIMESTAMP NOT NULL,
  sync_id     BIGINT    NOT NULL,
  PRIMARY KEY (layer)
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
  term         INTEGER,
  valid_crtr   INTEGER,
  valid_from   TIMESTAMP,
  valid_to     TIMESTAMP,
  zip_from     VARCHAR,
  zip_to       VARCHAR,
  timestamp    TIMESTAMP NOT NULL,
  sync_id      BIGINT    NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX idx_mst_route_layer_country_zip_from_zip_to_valid_from_valid_to
  ON mst_route (layer, country, zip_from, zip_to, valid_from, valid_to);
CREATE INDEX idx_mst_route_timestamp
  ON mst_route (timestamp);

CREATE TABLE mst_sector (
  id          BIGINT    NOT NULL,
  sector_to   VARCHAR   NOT NULL,
  sector_from VARCHAR   NOT NULL,
  valid_from  TIMESTAMP NOT NULL,
  valid_to    TIMESTAMP,
  via         VARCHAR,
  timestamp   TIMESTAMP NOT NULL,
  sync_id     BIGINT    NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (sector_to, sector_from, valid_from)
);

CREATE TABLE mst_station_sector (
  id            BIGINT  NOT NULL,
  station_nr    INTEGER NOT NULL,
  sector        VARCHAR NOT NULL,
  routing_layer INTEGER,
  timestamp     TIMESTAMP,
  sync_id       BIGINT  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (station_nr, sector)
);

CREATE TABLE sys_property (
  key   VARCHAR NOT NULL PRIMARY KEY,
  value VARCHAR,
  UNIQUE (key)
);

CREATE INDEX idx_mst_bundle_version_sync_id
  ON mst_bundle_version (sync_id);

CREATE INDEX idx_mst_country_sync_id
  ON mst_country (sync_id);

CREATE INDEX idx_mst_holiday_ctrl_sync_id
  ON mst_holiday_ctrl (sync_id);

CREATE INDEX idx_mst_route_sync_id
  ON mst_route (sync_id);

CREATE INDEX idx_mst_routing_layer_sync_id
  ON mst_routing_layer (sync_id);

CREATE INDEX idx_mst_sector_sync_id
  ON mst_sector (sync_id);

CREATE INDEX idx_mst_station_sync_id
  ON mst_station (sync_id);

CREATE INDEX idx_mst_station_sector_sync_id
  ON mst_station_sector (sync_id);

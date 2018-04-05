CREATE TABLE mst_key
(
  id        BIGINT                              NOT NULL PRIMARY KEY,
  key       VARCHAR                             NOT NULL,
  type      VARCHAR DEFAULT '0'                 NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE mst_user
(
  id                  BIGINT                              NOT NULL AUTO_INCREMENT PRIMARY KEY,
  key_id              BIGINT                              NOT NULL DEFAULT 0,
  debitor_id          BIGINT                              NOT NULL,
  alias               VARCHAR                             NOT NULL,
  email               VARCHAR                             NOT NULL UNIQUE,
  role                VARCHAR                             NOT NULL,
  password            VARCHAR                             NOT NULL,
  active              INTEGER                             NOT NULL,
  expires_on          DATE                                         DEFAULT '2099-12-31',
  password_expires_on DATE                                         DEFAULT '2099-12-31',
  firstname           VARCHAR                             NOT NULL,
  lastname            VARCHAR                             NOT NULL,
  external_user       INTEGER,
  phone               VARCHAR,
  phone_mobile        VARCHAR,
  ts_created          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  ts_updated          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  ts_lastlogin        TIMESTAMP,
  config              TEXT,
  preferences         TEXT,
  UNIQUE (alias, debitor_id)
);

CREATE TABLE mst_station_user
(
  id                  BIGINT                              NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id              BIGINT                            ,
  station_id          BIGINT                             ,
  ts_created          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  ts_updated          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
);

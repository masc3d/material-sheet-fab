USE dekuclient;

CREATE TABLE mst_node_station (
  id         INT(11)     NOT NULL  AUTO_INCREMENT,
  node_id    INT(11)     NULL,
  path       VARCHAR(10) NULL,
  station_id INT(11)     NULL,
  authorized INT         NOT NULL  DEFAULT 0,
  timestamp  TIMESTAMP   NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY `ix_path` (`path`),
  KEY ix_station_id (station_id)
);

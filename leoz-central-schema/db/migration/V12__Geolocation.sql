USE dekuclient;

CREATE TABLE trn_node_geoposition (
  position_id       INT(11)   NOT NULL  AUTO_INCREMENT,
  user_id           INT(11)   NULL,
  node_id           INT(11)   NULL,
  ts_created        TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  ts_updated        TIMESTAMP NOT NULL  DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  latitude          DOUBLE    NOT NULL  DEFAULT 0,
  longitude         DOUBLE    NOT NULL  DEFAULT 0,
  position_datetime DATETIME  NOT NULL  DEFAULT '0000-00-00 00:00:00',
  speed             FLOAT     NULL,
  bearing           FLOAT     NULL,
  altitude          DOUBLE    NULL,
  accuracy          FLOAT     NULL,
  PRIMARY KEY (position_id)
);

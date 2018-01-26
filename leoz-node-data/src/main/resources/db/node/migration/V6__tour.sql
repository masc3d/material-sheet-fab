CREATE TABLE tad_tour
(
  id              IDENTITY,
  user_id         BIGINT,
  node_id         BIGINT,
  station_no      BIGINT,
  deliverylist_id BIGINT,
  optimized       DATETIME,
  timestamp       TIMESTAMP,
  uid             VARCHAR NOT NULL,
);

CREATE TABLE tad_tour_entry
(
  id              IDENTITY,
  tour_id         BIGINT                              NOT NULL,
  position        DOUBLE DEFAULT '0'                  NOT NULL,
  order_id        BIGINT                              NOT NULL,
  order_task_type INT DEFAULT '1'                     NOT NULL,
  timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  uid             VARCHAR                             NOT NULL,
);

CREATE INDEX ix_tad_tour_entry_tour_id
  ON tad_tour_entry (tour_id);

CREATE INDEX ix_tad_tour_entry_position
  ON tad_tour_entry (position);


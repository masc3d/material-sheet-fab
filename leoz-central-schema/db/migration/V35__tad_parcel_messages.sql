USE `dekuclient`;
CREATE TABLE tad_parcel_messages
(
  id              INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user_id         INT                      DEFAULT NULL,
  node_id         VARCHAR(100)             DEFAULT NULL,
  parcel_id       BIGINT                   DEFAULT NULL,
  parcel_no       VARCHAR(100)             DEFAULT NULL,
  scanned         DATETIME                 DEFAULT NULL,
  event_value     INT                      DEFAULT NULL,
  reason_id       INT                      DEFAULT NULL,
  latitude        DOUBLE                   DEFAULT NULL,
  longitude       DOUBLE                   DEFAULT NULL,
  additional_info JSON                     DEFAULT NULL,
  is_proccessed   BOOLEAN                  DEFAULT 0
);
CREATE INDEX tad_parcel_messages_userid_index
  ON tad_parcel_messages (user_id);
CREATE INDEX tad_parcel_messages_nodeid_index
  ON tad_parcel_messages (node_id);
CREATE INDEX tad_parcel_messages_parcelid_index
  ON tad_parcel_messages (parcel_id);
CREATE INDEX tad_parcel_messages_parcelno_index
  ON tad_parcel_messages (parcel_no);
CREATE INDEX tad_parcel_messages_scanned_index
  ON tad_parcel_messages (scanned);
CREATE INDEX tad_parcel_messages_eventvalue_index
  ON tad_parcel_messages (event_value);
CREATE INDEX tad_parcel_messages_reasonid_index
  ON tad_parcel_messages (reason_id);
CREATE INDEX tad_parcel_messages_is_proccessed_index
  ON tad_parcel_messages (is_proccessed);
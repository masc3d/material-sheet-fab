USE dekuclient;
ALTER TABLE tad_node_geoposition ADD node_uid binary(16) NULL,
  ADD INDEX node_uid_index (node_uid);

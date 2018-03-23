USE dekuclient;

ALTER TABLE mst_node ADD debitor_id int(11) NULL AFTER `key`;
ALTER TABLE mst_node CHANGE configuration config JSON;
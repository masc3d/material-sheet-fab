use dekuclient;

ALTER TABLE mst_event DROP PRIMARY KEY;
ALTER TABLE mst_event ADD id INT NOT NULL PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE mst_event
  MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT FIRST;

ALTER TABLE mst_reason DROP PRIMARY KEY;
ALTER TABLE mst_reason ADD id INT NOT NULL PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE mst_reason
  MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT FIRST;

ALTER TABLE mst_event_reason ADD reason_id INT NULL;
ALTER TABLE mst_event_reason ADD event_id INT NULL;
ALTER TABLE mst_event_reason
  MODIFY COLUMN event_id INT AFTER id,
  MODIFY COLUMN reason_id INT AFTER event_id;

UPDATE mst_event_reason a SET a.event_id = (SELECT b.id FROM mst_event b WHERE b.eventcode = a.eventcode);
UPDATE mst_event_reason a SET a.reason_id = (SELECT b.id FROM mst_reason b WHERE b.reasoncode = a.reasoncode);

ALTER TABLE mst_event_reason MODIFY event_id INT(11) NOT NULL;
ALTER TABLE mst_event_reason MODIFY reason_id INT(11) NOT NULL;

CREATE INDEX `mst_event_reason_event-id_index` ON mst_event_reason (event_id);
CREATE INDEX `mst_event_reason_reason-id_index` ON mst_event_reason (reason_id);
CREATE INDEX mst_event_reason_combination_index ON mst_event_reason (event_id, reason_id);
CREATE INDEX `mst_event_reason_combination-old_index` ON mst_event_reason (status_old, reason_old);

ALTER TABLE mst_reason MODIFY id INT(11) NOT NULL;
ALTER TABLE mst_event MODIFY id INT(11) NOT NULL;
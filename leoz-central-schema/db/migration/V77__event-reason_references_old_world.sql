use dekuclient;

ALTER TABLE tblfehlercodes ADD event_id INT NULL;
ALTER TABLE tblfehlercodes ADD reason_id INT NULL;
ALTER TABLE tblkzstatus ADD event_id INT NULL;
ALTER TABLE tblkzstatus ADD reason_id INT NULL;
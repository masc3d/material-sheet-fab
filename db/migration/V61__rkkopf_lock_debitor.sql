USE dekuclient;

ALTER TABLE rkkopf
  ADD COLUMN debitor_id INT NOT NULL DEFAULT 0;

USE dekuclient;

ALTER TABLE rkkopf
  ADD COLUMN to_synchronize INT NOT NULL DEFAULT 0;

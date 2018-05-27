use dekuclient;

ALTER TABLE scnsetcount
  ADD COLUMN sim_included INT NOT NULL DEFAULT -1;
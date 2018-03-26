ALTER TABLE tad_tour
  DROP COLUMN uid;

ALTER TABLE tad_tour
  ADD uid UUID DEFAULT RANDOM_UUID() NOT NULL;
CREATE UNIQUE INDEX idx_tad_tour_uid
  ON tad_tour (uid);

ALTER TABLE tad_tour_entry
  DROP COLUMN uid;

ALTER TABLE tad_tour_entry
  ADD uid UUID DEFAULT RANDOM_UUID() NOT NULL;
CREATE UNIQUE INDEX idx_tad_tour_entry_uid
  ON tad_tour_entry (uid);

ALTER TABLE tad_tour
  ADD COLUMN debitor_id BIGINT NULL AFTER id;

--- create nullable tour_uid for migration
ALTER TABLE tad_tour_entry
  ADD COLUMN tour_uid UUID NULL AFTER id;

--- migrate existing records to uid
UPDATE tad_tour_entry tte
SET tte.tour_uid = (
  SELECT uid
  FROM tad_tour tt
  WHERE tte.tour_id = tt.id
);

--- remove invalid references
DELETE FROM tad_tour_entry
WHERE tour_uid IS NULL;

--- finalize tour_uid fk
ALTER TABLE tad_tour_entry
  ALTER COLUMN tour_id SET NOT NULL;

CREATE INDEX idx_tad_tour_entry_tour_uid
  ON tad_tour_entry (tour_uid);

--- remove old fk
ALTER TABLE tad_tour_entry
  DROP COLUMN tour_id;


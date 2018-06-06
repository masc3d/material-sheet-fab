-- Remove all node tours for consistency (as central based node id has to change to uid)
DELETE FROM tad_tour_entry
WHERE tour_uid IN (
  SELECT tad_tour.uid
  FROM tad_tour
  WHERE node_id IS NOT NULL
);

DELETE FROM tad_tour
WHERE node_id IS NOT NULL;

-- Replace `node_id` with `node_uid`
ALTER TABLE tad_tour
  ADD COLUMN node_uid UUID DEFAULT NULL AFTER node_id;

ALTER TABLE tad_tour
  DROP COLUMN node_id;

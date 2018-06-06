ALTER TABLE tad_tour_entry
  ADD COLUMN appointment_from TIMESTAMP NULL BEFORE route_meta;

ALTER TABLE tad_tour_entry
  ADD COLUMN appointment_to TIMESTAMP NULL BEFORE route_meta;



ALTER TABLE tad_tour ALTER COLUMN optimization_meta RENAME TO route_meta;
ALTER TABLE tad_tour_entry ADD COLUMN (route_meta MEDIUMTEXT NULL) AFTER order_task_type;

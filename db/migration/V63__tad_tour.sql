USE mobile;

ALTER TABLE tad_stop_list RENAME TO tad_tour;
ALTER TABLE tad_tour DROP stop_list_date;
ALTER TABLE tad_tour DROP consumer_id;

ALTER TABLE tad_stop_list_details RENAME TO tad_tour_entry;
ALTER TABLE tad_tour_entry DROP is_removed;
ALTER TABLE tad_tour_entry DROP system_id;
ALTER TABLE tad_tour_entry ADD order_task_type INT DEFAULT 1 NOT NULL;
ALTER TABLE tad_tour_entry CHANGE stop_list_id tour_id INT NOT NULL;
ALTER TABLE tad_tour_entry CHANGE stop_position position DOUBLE NOT NULL DEFAULT '0';
ALTER TABLE tad_tour_entry MODIFY order_id BIGINT(20) NOT NULL;

ALTER TABLE tad_tour_entry
  MODIFY COLUMN timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER order_task_type;
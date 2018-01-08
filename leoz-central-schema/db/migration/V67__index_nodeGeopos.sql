USE dekuclient;

ALTER TABLE tad_node_geoposition
  ADD INDEX `position_datetime` (`position_datetime` ASC),
  ADD INDEX `debitor_id` (`debitor_id` ASC),
  ADD INDEX `user_id` (`user_id` ASC);


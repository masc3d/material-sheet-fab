USE mobile;

ALTER TABLE tad_tour ADD deliverylist_id INT(11) NULL AFTER node_id;
ALTER TABLE tad_tour ADD optimized DATETIME NULL AFTER deliverylist_id;

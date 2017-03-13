USE `dekuclient`;

DELIMITER ;

# Sync tracking table

CREATE TABLE `sys_sync` (
  `id`         MEDIUMINT   NOT NULL AUTO_INCREMENT,
  `table_name` VARCHAR(50) NOT NULL,
  `sync_id`    BIGINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix_table_name` (`table_name`)
)
  ENGINE = MyISAM
  DEFAULT CHARSET = latin1;

DELIMITER $$

# Function for incrementing sync id for a specific table
# @param Table name
# @returns Incremented sync id for this table

CREATE FUNCTION f_sync_increment(p_table_name VARCHAR(50))
  RETURNS BIGINT
  BEGIN
    UPDATE sys_sync
    SET sync_id = (@sync_id := sync_id + 1)
    WHERE table_name = p_table_name;

    RETURN @sync_id;
  END $$

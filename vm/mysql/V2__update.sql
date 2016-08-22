ALTER TABLE `mst_node`
  DROP PRIMARY KEY,
  DROP COLUMN hostname,
  DROP COLUMN Authorized,
  ADD PRIMARY KEY(`node_id`),
  CHANGE COLUMN sys_info sys_info mediumtext NULL,
  ADD COLUMN configuration mediumtext NULL,
  ADD COLUMN version_alias varchar(45) NOT NULL DEFAULT 'release',
  ADD COLUMN authorized int(11) NULL AFTER `key`,
  ADD COLUMN bundle varchar(45) NOT NULL,
  CHANGE COLUMN timestamp timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;

DROP TABLE IF EXISTS `mst_bundle_version`;
CREATE TABLE `mst_bundle_version` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `bundle` varchar(45) DEFAULT NULL,
  `alias` varchar(45) DEFAULT NULL,
  `version` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `idx_name_bundle` (`alias`,`bundle`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

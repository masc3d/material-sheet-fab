USE `dekuclient`;

CREATE TABLE `sso_s_movepool` (
  `id` double NOT NULL AUTO_INCREMENT,
  `bag_number` double DEFAULT NULL,
  `status` double DEFAULT NULL,
  `status_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastdepot` double DEFAULT NULL,
  `seal_number_green` double DEFAULT NULL,
  `seal_number_yellow` double DEFAULT NULL,
  `seal_number_red` double DEFAULT NULL,
  `orderhub2depot` double DEFAULT NULL,
  `orderdepot2hub` double DEFAULT NULL,
  `init_status` int(11) NOT NULL DEFAULT '0',
  `work_date` date DEFAULT NULL,
  `printed` double DEFAULT NULL,
  `multibag` int(11) NOT NULL DEFAULT '0',
  `movepool` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bag_number` (`bag_number`),
  KEY `orderhub2depot` (`orderhub2depot`),
  KEY `orderdepot2hub` (`orderdepot2hub`),
  KEY `status` (`status`),
  KEY `status_time` (`status_time`),
  KEY `lastdepot` (`lastdepot`),
  KEY `work_date` (`work_date`),
  KEY `init_status` (`init_status`),
  KEY `movepool` (`movepool`)
) ENGINE=InnoDB AUTO_INCREMENT=273065 DEFAULT CHARSET=latin1;

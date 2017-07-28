USE `dekuclient`;
ALTER TABLE dekuclient.tblstatus CHANGE processStatusRating sendstatus2 INT(11) NOT NULL DEFAULT '0';
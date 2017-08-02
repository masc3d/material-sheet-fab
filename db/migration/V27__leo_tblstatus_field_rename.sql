USE `dekuclient`;
ALTER TABLE dekuclient.tblstatus CHANGE sendstatus2 processStatusRating INT(11) NOT NULL DEFAULT '0';
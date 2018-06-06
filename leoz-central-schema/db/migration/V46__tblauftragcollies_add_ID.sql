/**
############ Warning ############
Apply this migration to live only on sunday, before evening
 */

use dekuclient;

ALTER TABLE tblauftragcollies
  AUTO_INCREMENT = 15000000 ,
  CHANGE COLUMN OrderPos OrderPos SMALLINT(6) NOT NULL ,
  ADD COLUMN parcel_id INT(11) NOT NULL AUTO_INCREMENT FIRST ,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (parcel_id);

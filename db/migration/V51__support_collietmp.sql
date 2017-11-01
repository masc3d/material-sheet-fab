USE dekuclient;

ALTER TABLE tblauftragcollies
  CHANGE COLUMN OrderPos OrderPos SMALLINT(6) NOT NULL DEFAULT 0;

ALTER TABLE tblauftragcolliestmp
  CHANGE COLUMN parcel_id parcel_id INT(11) NOT NULL DEFAULT 0,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (OrderID, OrderPos, ClientID);

ALTER TABLE tblauftragcollies_trx
  AUTO_INCREMENT = 15000000,
  CHANGE COLUMN OrderPos OrderPos SMALLINT(6) NOT NULL DEFAULT 0,
  ADD COLUMN parcel_id INT(11) NOT NULL AUTO_INCREMENT
  FIRST,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (parcel_id),
  ADD CONSTRAINT unique_position_in_order UNIQUE (OrderID, OrderPos);

ALTER TABLE tblauftragcollies_xml
  AUTO_INCREMENT = 15000000,
  CHANGE COLUMN OrderPos OrderPos SMALLINT(6) NOT NULL DEFAULT 0,
  ADD COLUMN parcel_id INT(11) NOT NULL AUTO_INCREMENT
  FIRST,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (parcel_id),
  ADD CONSTRAINT unique_position_in_order UNIQUE (OrderID, OrderPos);


USE dekutmp;

ALTER TABLE tblauftragcolliestmp
  CHANGE COLUMN OrderPos OrderPos SMALLINT(6) NOT NULL DEFAULT 0,
  ADD COLUMN parcel_id INT(11) NOT NULL DEFAULT 0
  FIRST;

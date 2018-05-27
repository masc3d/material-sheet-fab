USE dekuclient;

ALTER TABLE tblauftragcolliestmp
  ADD COLUMN parcel_id INT(11) NOT NULL AUTO_INCREMENT
  FIRST,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (parcel_id),
  ADD CONSTRAINT unique_position_in_order UNIQUE (ClientID,OrderID, OrderPos);

ALTER TABLE tblauftragcollies
  ADD CONSTRAINT unique_position_in_order UNIQUE (OrderID, OrderPos);


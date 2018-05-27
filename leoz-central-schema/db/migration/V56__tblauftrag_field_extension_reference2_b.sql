use dekuclient;

ALTER TABLE tblauftragtmp MODIFY Referenz2 VARCHAR(40);

USE dekutmp;

ALTER TABLE tblauftrag MODIFY Referenz2 VARCHAR(40);
ALTER TABLE tblauftragtmp MODIFY Referenz2 VARCHAR(40);

/**
############ Warning ############
Apply this migration to live only on sunday, before evening
 */

use dekuclient;

START TRANSACTION;
ALTER TABLE tblauftragcollies
  MODIFY OrderPos SMALLINT(6) NOT NULL;
ALTER TABLE tblauftragcollies
  DROP PRIMARY KEY;
ALTER TABLE tblauftragcollies
  ADD CONSTRAINT unique_position_in_order UNIQUE (OrderID, OrderPos);
ALTER TABLE tblauftragcollies
  ADD id INT NOT NULL PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE tblauftragcollies
  MODIFY COLUMN id INT AUTO_INCREMENT FIRST;
COMMIT;
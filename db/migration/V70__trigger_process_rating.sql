USE dekuclient;

DELIMITER $$
USE `dekuclient`$$
CREATE DEFINER=`root`@`%` TRIGGER `dekuclient`.`tblstatus_BEFORE_UPDATE` BEFORE UPDATE ON `tblstatus` FOR EACH ROW
  BEGIN
    if NEW.processStatusRating=5 and NEW.orderidsta>0 then
      update tblauftrag set processStatus=1 where orderid=New.orderidsta;
      set new.processStatusRating=6;
    end if;
  END$$
DELIMITER ;

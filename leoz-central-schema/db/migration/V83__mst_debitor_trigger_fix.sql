USE dekuclient;

ALTER TABLE `dekuclient`.`tbldepotliste`
  CHANGE COLUMN `ID` `ID` INT(11) NOT NULL,
  ADD UNIQUE INDEX `id` (`ID` ASC);


DROP TRIGGER IF EXISTS `dekuclient`.`tbldepotliste_update_debitor`;
DELIMITER $$
CREATE DEFINER =`root`@`%` TRIGGER tbldepotliste_update_debitor
  BEFORE UPDATE
  ON tbldepotliste
  FOR EACH ROW
  BEGIN
    IF new.debitornr <> 0 AND (new.debitornr <> old.debitornr OR old.debitornr IS NULL)
    THEN
      IF (SELECT COUNT(*)
          FROM mst_debitor
          WHERE debitor_nr = new.debitornr) = 0
      THEN
        INSERT INTO mst_debitor (debitor_nr) VALUES (new.debitornr);
      END IF;
      SET new.debitor_id = (SELECT debitor_id
                            FROM mst_debitor
                            WHERE debitor_nr = new.debitornr);
    ELSEIF new.debitornr = 0 OR new.debitornr IS NULL
      THEN
        SET new.debitor_id = 0;
    END IF;

    #todo detailed adminitration required
    IF new.id > 0 AND new.debitor_id > 0
    THEN
      IF (SELECT COUNT(*)
          FROM mst_station_contract
          WHERE station_id = new.id AND debitor_id = new.debitor_id) = 0
      THEN
        INSERT INTO mst_station_contract (station_id, debitor_id, active_from, active_to, contract_type)
          SELECT
            new.id,
            new.debitor_id,
            '2015-01-01',
            '2099-12-31',
            0;
      END IF;
    END IF;
  END$$
DELIMITER ;

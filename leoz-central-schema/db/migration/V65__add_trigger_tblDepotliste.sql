use dekuclient;

DELIMITER //
CREATE TRIGGER tbldepotliste_update_debitor BEFORE UPDATE ON tbldepotliste
  FOR EACH ROW
  BEGIN
    IF NEW.DebitorNr <> OLD.DebitorNr THEN
      IF (SELECT COUNT(*) FROM mst_debitor WHERE debitor_nr = NEW.DebitorNr) = 0 THEN
        INSERT INTO mst_debitor (debitor_nr) VALUES (NEW.DebitorNr);
      END IF;
      SET NEW.debitor_id = (SELECT debitor_id FROM mst_debitor WHERE debitor_nr = NEW.DebitorNr);
    END IF;
  END //
  
CREATE TRIGGER tbldepotliste_insert_debitor BEFORE INSERT ON tbldepotliste
  FOR EACH ROW
  BEGIN
    IF NEW.DebitorNr <> 0 AND NEW.DebitorNr IS NOT NULL THEN
      IF (SELECT COUNT(*) FROM mst_debitor WHERE debitor_nr = NEW.DebitorNr) = 0 THEN
        INSERT INTO mst_debitor (debitor_nr) VALUES (NEW.DebitorNr);
      END IF;
      SET NEW.debitor_id = (SELECT debitor_id FROM mst_debitor WHERE debitor_nr = NEW.DebitorNr);
    END IF;
  END //
DELIMITER ;
DELETE FROM mst_debitor;

INSERT INTO mst_debitor (debitor_nr) SELECT DISTINCT DebitorNr
                                     FROM tbldepotliste
                                     WHERE debitornr > 1000;

UPDATE tbldepotliste
  LEFT JOIN mst_debitor ON tbldepotliste.DebitorNr = mst_debitor.debitor_nr
SET tbldepotliste.debitor_id = mst_debitor.debitor_id
WHERE NOT mst_debitor.debitor_id IS NULL;

DELETE FROM mst_user
WHERE email = 'user@deku.org';

INSERT INTO mst_user (debitor_id, email, password, alias, expires_on, role, active, firstname, lastname) VALUES
  ((SELECT debitor_id
    FROM tbldepotliste
    WHERE depotnr = 89), 'user@deku.org', '8a7880ab910b3fc2aebb8603f9a8bdafb2ae1d57', 'testuser', '2099-12-31',
   'POWERUSER', -1,
   'Hans', 'Mustermann');


INSERT INTO mst_user (debitor_id, email, password, alias, expires_on, role, active, firstname, lastname) VALUES
  ((SELECT debitor_id
    FROM tbldepotliste
    WHERE depotnr = 89),
   'dev@leoz', 'e03c1faf96e4484bba2d63cfada94d88d3cdf8ff', 'dev', '2099-12-31', 'USER', 1, 'leoz', 'leoz');

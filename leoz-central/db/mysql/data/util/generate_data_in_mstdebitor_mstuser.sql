DELETE FROM mst_debitor;
INSERT INTO mst_debitor (debitor_nr) SELECT DISTINCT DebitorNr
                                     FROM tbldepotliste
                                     WHERE debitornr > 1000;

DELETE FROM mst_station_contract;
INSERT INTO mst_station_contract (station_id, debitor_id, active_from, active_to, contract_type)
  SELECT
    tbldepotliste.ID,
    mst_debitor.debitor_id,
    '2015-01-01',
    '2099-12-31',
    0
  FROM tbldepotliste
    INNER JOIN mst_debitor ON tbldepotliste.DebitorNr = mst_debitor.debitor_nr;

UPDATE tbldepotliste
  LEFT JOIN mst_debitor ON tbldepotliste.DebitorNr = mst_debitor.debitor_nr
SET tbldepotliste.debitor_id = mst_debitor.debitor_id
WHERE NOT mst_debitor.debitor_id IS NULL;

DELETE FROM mst_user
WHERE email = 'user@deku.org' OR email = 'dev@leoz';

INSERT INTO mst_user (debitor_id, email, password, alias, expires_on, role, active, firstname, lastname) VALUES
  (
    (SELECT debitor_id
     FROM mst_station_contract
     WHERE station_id =
           (SELECT id
            FROM tbldepotliste
            WHERE depotnr = 89))
    , 'user@deku.org', '8a7880ab910b3fc2aebb8603f9a8bdafb2ae1d57', 'testuser', '2099-12-31',
    'POWERUSER', -1,
    'Hans', 'Mustermann');

INSERT INTO mst_user (debitor_id, email, password, alias, expires_on, role, active, firstname, lastname) VALUES
  (
    (SELECT debitor_id
     FROM mst_station_contract
     WHERE station_id =
           (SELECT id
            FROM tbldepotliste
            WHERE depotnr = 89))
    ,
    'dev@leoz', 'e03c1faf96e4484bba2d63cfada94d88d3cdf8ff', 'dev', '2099-12-31', 'ADMIN', 1, 'leoz', 'leoz');

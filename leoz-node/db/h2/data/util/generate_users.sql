DELETE FROM mst_user
WHERE email = 'user@deku.org' OR email = 'dev@leoz';

INSERT INTO mst_user (debitor_id, email, password, alias, expires_on, role, active, firstname, lastname) VALUES
  (
    (SELECT debitor_id
     FROM mst_station_contract
     WHERE station_id =
           (SELECT station_id
            FROM mst_station
            WHERE station_nr = 89))
    , 'user@deku.org', '8a7880ab910b3fc2aebb8603f9a8bdafb2ae1d57', 'testuser', '2099-12-31',
    'POWERUSER', -1,
    'Hans', 'Mustermann');

INSERT INTO mst_user (debitor_id, email, password, alias, expires_on, role, active, firstname, lastname) VALUES
  (
    (SELECT debitor_id
     FROM mst_station_contract
     WHERE station_id =
           (SELECT station_id
            FROM mst_station
            WHERE station_nr = 89))
    ,
    'dev@leoz', 'e03c1faf96e4484bba2d63cfada94d88d3cdf8ff', 'dev', '2099-12-31', 'ADMIN', 1, 'leoz', 'leoz');

USE `dekuclient`;

CREATE

VIEW `sectiondepotlist` AS
  SELECT
    `tbldepotliste`.`DepotNr` AS `depotnr`,
    LPAD(CONVERT( `tbldepotliste`.`DepotNr` USING UTF8),
         3,
         _UTF8'0') AS `Depot`,
    FLOOR((`tbldepotliste`.`Strang` / 100)) AS `section`,
    IF(((`tbldepotliste`.`Strang` % 100) < 50),
       1,
       IF(((`tbldepotliste`.`Strang` % 100) = 96),
          3,
          IF(((`tbldepotliste`.`Strang` % 100) = 97),
             4,
             IF(((`tbldepotliste`.`Strang` % 100) = 98),
                5,
                2)))) AS `position`
  FROM
    `tbldepotliste`
  WHERE
    ((`tbldepotliste`.`DepotNr` < 1000)
     AND (`tbldepotliste`.`DepotNr` <> 0)
     AND (`tbldepotliste`.`IstGueltig` = 1)
     AND (`tbldepotliste`.`DepotLevel` >= 0)
     AND (`tbldepotliste`.`StrangZ` = -(1)))
  ORDER BY `tbldepotliste`.`StrangOrder`
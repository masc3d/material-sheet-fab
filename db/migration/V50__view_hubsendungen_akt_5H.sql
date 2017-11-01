USE `dekuclient`;

CREATE OR REPLACE
  ALGORITHM = UNDEFINED
VIEW `hubsendungenakt` AS
  SELECT
    `tblauftrag`.`KZ_Transportart`          AS `kz_transportart`,
    `tblauftrag`.`lockflag`                 AS `lockflag`,
    `tblauftrag`.`AuftragsID`               AS `AuftragsID`,
    `tblauftrag`.`OrderID`                  AS `OrderID`,
    `tblauftrag`.`Belegnummer`              AS `Belegnummer`,
    `tblauftrag`.`DepotNrAD`                AS `DepotNrAD`,
    `tblauftrag`.`DepotNrLD`                AS `DepotNrLD`,
    `tblauftrag`.`DepotNrAbD`               AS `DepotNrAbD`,
    `tblauftrag`.`Service`                  AS `Service`,
    `tblauftrag`.`Verladedatum`             AS `Verladedatum`,
    `tblauftrag`.`BagIDNrA`                 AS `BagIDNrA`,
    `tblauftrag`.`ClearingArtMaster`        AS `ClearingArtMaster`,
    `tblauftragcollies`.`CollieBelegNr`     AS `CollieBelegNr`,
    `tblauftragcollies`.`LadelistennummerD` AS `LadelistennummerD`,
    `tblauftragcollies`.`OrderPos`          AS `OrderPos`,
    `tblauftragcollies`.`Laenge`            AS `Laenge`,
    `tblauftragcollies`.`Breite`            AS `Breite`,
    `tblauftragcollies`.`Hoehe`             AS `Hoehe`,
    `tblauftragcollies`.`GewichtReal`       AS `GewichtReal`,
    `tblauftragcollies`.`GewichtEffektiv`   AS `GewichtEffektiv`,
    `tblauftragcollies`.`GewichtLBH`        AS `GewichtLBH`,
    `tblauftragcollies`.`lieferstatus`      AS `lieferstatus`,
    `tblauftragcollies`.`lieferfehler`      AS `lieferfehler`,
    `tblauftragcollies`.`erstlieferstatus`  AS `erstlieferstatus`,
    `tblauftragcollies`.`erstlieferfehler`  AS `erstlieferfehler`,
    `tblauftragcollies`.`Frei4`             AS `Frei4`,
    `tblauftragcollies`.`rkposition`        AS `rkposition`,
    `tblauftragcollies`.`Verladelinie`      AS `verladelinie`,
    `tblauftragcollies`.`dtAusgangHup3`     AS `dtAusgangHup3`,
    `tblauftragcollies`.`dtEingangHup3`     AS `dtEingangHup3`,
    `tblauftragcollies`.`dtAusgangDepot2`   AS `dtAusgangDepot2`,
    `tblauftragcollies`.`dtEingangDepot2`   AS `dtEingangDepot2`,
    `tblauftragcollies`.`mydepotid2`        AS `mydepotid2`,
    `tblauftragcollies`.`BagIDNrC`          AS `BagIDNrC`,
    `tblauftragcollies`.`BagBelegNrC`       AS `BagBelegNrC`,
    `tblauftragcollies`.`BagBelegNrAbC`     AS `BagBelegNrAbC`,
    `tblauftragcollies`.`VerpackungsArt`    AS `VerpackungsArt`
  FROM
    (`tblauftrag`
      JOIN `tblauftragcollies` ON ((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`)))
  WHERE
    (((`tblauftrag`.`Service` & 134217728) = 0)
     AND (`tblauftrag`.`KZ_Transportart` = 1)
     AND (`tblauftrag`.`lockflag` = 0)
     AND (`tblauftrag`.`Verladedatum` = CAST((NOW() + INTERVAL -(5) HOUR) AS DATE)));

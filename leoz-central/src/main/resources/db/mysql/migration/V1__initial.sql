-- MySQL dump 10.13  Distrib 5.6.24, for osx10.8 (x86_64)
--
-- Host: 10.0.10.10    Database: dekuclient
-- ------------------------------------------------------
-- Server version	5.5.47-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `mst_country`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_country` (
  `code` char(2) NOT NULL DEFAULT '',
  `name_stringid` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `routing_typ` int(10) NOT NULL DEFAULT '0',
  `min_len` int(11) DEFAULT NULL,
  `max_len` int(11) DEFAULT NULL,
  `zip_format` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_go_routing`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_go_routing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `country` varchar(10) DEFAULT NULL,
  `zip` varchar(10) DEFAULT NULL,
  `wrul` varchar(2) DEFAULT NULL,
  `etod` datetime DEFAULT NULL,
  `route` varchar(10) DEFAULT NULL,
  `station` varchar(10) DEFAULT NULL,
  `linie` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=75782 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_holidayctrl`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_holidayctrl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `holiday` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `country` char(2) NOT NULL DEFAULT '',
  `ctrl_pos` int(11) NOT NULL DEFAULT '0',
  `description` varchar(45) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `key` (`holiday`,`country`)
) ENGINE=MyISAM AUTO_INCREMENT=1467 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_key`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_key` (
  `key_id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(250) NOT NULL DEFAULT 'XX',
  `typ` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`key_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_node`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_node` (
  `node_id` int(11) NOT NULL AUTO_INCREMENT,
  `sys_info` varchar(200) DEFAULT NULL,
  `hostname` varchar(50) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `key` varchar(45) DEFAULT NULL,
  `authorized` int(11) DEFAULT NULL,
  PRIMARY KEY (`node_id`)
) ENGINE=MyISAM AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_route`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_route` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `layer` int(11) NOT NULL,
  `country` varchar(5) NOT NULL,
  `zipfrom` varchar(15) NOT NULL,
  `zipto` varchar(15) DEFAULT NULL,
  `valid_ctrl` int(11) NOT NULL DEFAULT '0',
  `validfrom` datetime NOT NULL DEFAULT '0001-00-00 00:00:00',
  `validto` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `station` int(11) DEFAULT NULL,
  `area` varchar(5) DEFAULT NULL,
  `etod` time DEFAULT NULL,
  `ltop` time DEFAULT NULL,
  `term` int(11) DEFAULT NULL,
  `saturday_ok` int(11) DEFAULT NULL,
  `ltodsa` time DEFAULT NULL,
  `ltodholiday` time DEFAULT NULL,
  `island` int(11) DEFAULT NULL,
  `holidayctrl` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`layer`,`country`,`zipfrom`,`valid_ctrl`,`validfrom`)
) ENGINE=MyISAM AUTO_INCREMENT=69627 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_routinglayer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_routinglayer` (
  `layer` int(10) NOT NULL,
  `services` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`layer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_sector`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_sector` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sectorfrom` varchar(5) NOT NULL,
  `sectorto` varchar(5) NOT NULL,
  `validfrom` datetime NOT NULL DEFAULT '0001-00-00 00:00:00',
  `validto` datetime DEFAULT NULL,
  `via` varchar(45) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `key` (`sectorfrom`,`sectorto`,`validfrom`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_station`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_station` (
  `station_nr` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `address1` varchar(50) DEFAULT NULL,
  `address2` varchar(50) DEFAULT NULL,
  `country` varchar(2) DEFAULT NULL,
  `zip` varchar(8) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `street` varchar(50) DEFAULT NULL,
  `house_nr` varchar(10) DEFAULT NULL,
  `phone1` varchar(50) DEFAULT NULL,
  `phone2` varchar(50) DEFAULT NULL,
  `telefax` varchar(50) DEFAULT NULL,
  `mobile` varchar(50) DEFAULT NULL,
  `service_phone1` varchar(50) DEFAULT NULL,
  `service_phone2` varchar(50) DEFAULT NULL,
  `contact_person1` varchar(50) DEFAULT NULL,
  `contact_person2` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `web_address` varchar(255) DEFAULT NULL,
  `poslong` double DEFAULT NULL,
  `poslat` double DEFAULT NULL,
  `sectors` varchar(50) DEFAULT NULL,
  `ustId` varchar(50) DEFAULT NULL,
  `billing_address1` varchar(50) DEFAULT NULL,
  `billing_address2` varchar(50) DEFAULT NULL,
  `billing_country` varchar(50) DEFAULT NULL,
  `billing_zip` varchar(8) DEFAULT NULL,
  `billing_city` varchar(50) DEFAULT NULL,
  `billing_street` varchar(50) DEFAULT NULL,
  `billing_house_nr` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`station_nr`),
  UNIQUE KEY `DepotTree` (`station_nr`),
  UNIQUE KEY `DepotMatchcode` (`station_nr`),
  UNIQUE KEY `LKZ` (`country`,`zip`,`city`,`station_nr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_station_sector`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_station_sector` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `station_nr` int(11) NOT NULL DEFAULT '0',
  `sector` varchar(5) NOT NULL DEFAULT '',
  `routing_layer` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `key` (`station_nr`,`sector`,`routing_layer`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mst_user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mst_user` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `user_name` varchar(45) DEFAULT NULL,
  `station_nr` int(11) NOT NULL DEFAULT '0',
  `permission_routing` tinyint(4) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldepotliste`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldepotliste` (
  `DepotNr` int(11) NOT NULL DEFAULT '0',
  `DepotLevel` int(11) NOT NULL DEFAULT '1',
  `DepotParent` int(11) NOT NULL DEFAULT '9999',
  `DepotMatchcode` varchar(50) NOT NULL DEFAULT '',
  `LinienNr` smallint(6) NOT NULL DEFAULT '0',
  `Linienankunft` datetime DEFAULT '0000-00-00 00:00:00',
  `Linienabfahrt` datetime DEFAULT '0000-00-00 00:00:00',
  `Aktivierungsdatum` datetime NOT NULL DEFAULT '2002-01-01 00:00:00',
  `Deaktivierungsdatum` datetime NOT NULL DEFAULT '2099-12-31 00:00:00',
  `IstGueltig` smallint(6) NOT NULL DEFAULT '1',
  `Firma1` varchar(50) DEFAULT NULL,
  `Firma2` varchar(50) DEFAULT '',
  `LKZ` char(2) DEFAULT 'DE',
  `PLZ` varchar(8) DEFAULT NULL,
  `Ort` varchar(50) DEFAULT NULL,
  `Strasse` varchar(50) DEFAULT NULL,
  `StrNr` varchar(10) DEFAULT NULL,
  `LVW` smallint(6) DEFAULT '49',
  `OVW` int(11) DEFAULT '0',
  `Telefon1` varchar(50) DEFAULT '0',
  `Telefon2` varchar(50) DEFAULT '0',
  `Telefax` varchar(50) DEFAULT '0',
  `Mobil` varchar(50) DEFAULT '0',
  `Nottelefon1` varchar(50) DEFAULT '0',
  `Nottelefon2` varchar(50) DEFAULT '0',
  `Anprechpartner1` varchar(50) DEFAULT '',
  `Anprechpartner2` varchar(50) DEFAULT '',
  `UstID` varchar(50) DEFAULT '',
  `EKStNr` varchar(50) DEFAULT '',
  `BLZ` varchar(8) DEFAULT '',
  `KtoNr` varchar(12) DEFAULT '',
  `Bank` varchar(25) DEFAULT '',
  `KtoInhaber` varchar(27) DEFAULT '',
  `RName1` varchar(50) DEFAULT '',
  `RName2` varchar(50) DEFAULT '',
  `RLKZ` char(2) DEFAULT 'DE',
  `RPLZ` varchar(8) DEFAULT '',
  `ROrt` varchar(50) DEFAULT '',
  `RStrasse` varchar(50) DEFAULT '',
  `RStrNr` varchar(10) DEFAULT NULL,
  `Info` varchar(255) DEFAULT '',
  `Email` varchar(100) DEFAULT NULL,
  `Samstag` tinyint(4) DEFAULT '0',
  `Sonntag` tinyint(4) DEFAULT '0',
  `Einzug` tinyint(4) DEFAULT '0',
  `UhrzeitAnfang` datetime DEFAULT '1899-12-31 08:00:00',
  `UhrzeitEnde` datetime DEFAULT '1899-12-31 18:00:00',
  `UhrzeitAnfangSa` datetime DEFAULT '0000-00-00 00:00:00',
  `UhrzeitEndeSa` datetime DEFAULT '0000-00-00 00:00:00',
  `UhrzeitAnfangSo` datetime DEFAULT '0000-00-00 00:00:00',
  `UhrzeitEndeSo` datetime DEFAULT '0000-00-00 00:00:00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ServerNameSMTP` varchar(45) DEFAULT NULL,
  `ServerNamePOP3` varchar(45) DEFAULT NULL,
  `UserName` varchar(45) DEFAULT NULL,
  `Password` varchar(45) DEFAULT NULL,
  `IntRoutingLKZ` varchar(45) DEFAULT NULL,
  `MwStShl` int(11) DEFAULT NULL,
  `MwStPflicht` int(11) DEFAULT NULL,
  `F4F` varchar(100) DEFAULT NULL,
  `ExportEmail` varchar(245) DEFAULT NULL,
  `ExportFTPServer` varchar(45) DEFAULT NULL,
  `ExportFTPUser` varchar(45) DEFAULT NULL,
  `ExportFTPPwd` varchar(45) DEFAULT NULL,
  `ExportToGLO` int(11) DEFAULT NULL,
  `ExportToXML` int(11) DEFAULT NULL,
  `Kondition` int(11) DEFAULT NULL,
  `QualiMail` varchar(255) DEFAULT NULL,
  `EBSdgDepot` int(11) DEFAULT NULL,
  `EBDepotAD` int(11) DEFAULT NULL,
  `EBGen` int(11) DEFAULT '0',
  `EBUmvDepot` int(11) DEFAULT NULL,
  `UmRoutung` varchar(250) DEFAULT NULL,
  `Kontokorrentnr` double DEFAULT NULL,
  `Zahlungsbedingungen` int(11) DEFAULT NULL,
  `VerbundenesU` int(11) DEFAULT NULL,
  `DebitorNr` double DEFAULT NULL,
  `KreditorNr` double DEFAULT NULL,
  `RgGutschrift` char(1) DEFAULT NULL,
  `RgRechnung` char(1) DEFAULT NULL,
  `XMLStammdaten` int(11) DEFAULT NULL,
  `Region` varchar(10) DEFAULT NULL,
  `Firmenverbund` varchar(100) DEFAULT NULL,
  `Qualitaet` int(11) DEFAULT NULL,
  `SonntagsLinientyp` int(11) DEFAULT NULL,
  `Com_code` int(11) DEFAULT NULL,
  `eMail_pas` varchar(255) DEFAULT NULL,
  `XML_ACN` int(11) DEFAULT NULL,
  `webemail` varchar(255) DEFAULT NULL,
  `webadresse` varchar(255) DEFAULT NULL,
  `PA_XML` int(11) DEFAULT NULL,
  `PA_PDF` int(11) DEFAULT NULL,
  `ZahlungsbedingungenR` int(11) DEFAULT NULL,
  `EASYOk` int(11) DEFAULT NULL,
  `VofiPrz` double DEFAULT NULL,
  `FELang` varchar(5) DEFAULT NULL,
  `MentorDepotNr` int(11) DEFAULT NULL,
  `TRZProz` double DEFAULT NULL,
  `NNOk` int(11) DEFAULT NULL,
  `COD1` varchar(5) DEFAULT NULL,
  `XLSAuftragOK` int(11) DEFAULT NULL,
  `ID` int(11) DEFAULT NULL,
  `HanReg` varchar(45) DEFAULT NULL,
  `Coloader` int(11) DEFAULT NULL,
  `AbrechDepot` int(11) DEFAULT NULL,
  `LadehilfeWas` varchar(100) DEFAULT NULL,
  `LadehilfeKg` int(11) DEFAULT NULL,
  `LadehilfeAb` varchar(5) DEFAULT NULL,
  `LadehilfeLinie` int(11) DEFAULT NULL,
  `PA_Druck` int(11) DEFAULT NULL,
  `RUP` varchar(15) DEFAULT NULL,
  `MasterVertrag` int(11) DEFAULT NULL,
  `Strang` int(11) DEFAULT NULL,
  `MasterDepot` int(11) DEFAULT NULL,
  `WebshopInit` datetime DEFAULT NULL,
  `MultiBag` double DEFAULT NULL,
  `BagKontingent` int(11) DEFAULT NULL,
  `BagBemerkung` varchar(15) DEFAULT NULL,
  `KonditionAbD` int(11) DEFAULT NULL,
  `KonditionLD` int(11) DEFAULT NULL,
  `BagCo` int(11) DEFAULT NULL,
  `StrangDatum` datetime DEFAULT NULL,
  `StrangZ` double DEFAULT NULL,
  `StrangOrder` double DEFAULT NULL,
  `smspwd` varchar(20) DEFAULT NULL,
  `ValOk` int(10) unsigned DEFAULT NULL,
  `maxValwert` double DEFAULT NULL,
  `maxHoeherhaftung` double DEFAULT NULL,
  `maxWarenwert` double DEFAULT NULL,
  `SAPCostCenter` varchar(10) DEFAULT NULL,
  `AdHocKondiDepot` int(11) DEFAULT NULL,
  `AdrPosNewCalc` int(11) NOT NULL DEFAULT '0',
  `PLZPseudoReal` varchar(10) DEFAULT NULL,
  `ZahlungsbedingungenLHR` int(11) DEFAULT NULL,
  `ZahlungsbedingungenLHG` int(11) DEFAULT NULL,
  `RgGutschriftLH` varchar(1) DEFAULT NULL,
  `RgRechnungLH` varchar(1) DEFAULT NULL,
  `trzProzLH` int(11) DEFAULT NULL,
  `LHJN` int(11) DEFAULT NULL,
  `poslong` double NOT NULL DEFAULT '0',
  `poslat` double NOT NULL DEFAULT '0',
  `sektor` varchar(5) DEFAULT NULL,
  `versicherung` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`DepotNr`),
  UNIQUE KEY `DepotTree` (`DepotParent`,`DepotLevel`,`DepotNr`),
  UNIQUE KEY `DepotMatchcode` (`DepotMatchcode`,`DepotNr`),
  UNIQUE KEY `LKZ` (`LKZ`,`PLZ`,`Ort`,`DepotNr`),
  KEY `IstGueltig` (`IstGueltig`),
  KEY `DepotLevel` (`DepotLevel`),
  KEY `Strang` (`Strang`),
  KEY `MultiBag` (`MultiBag`),
  KEY `StrangDatum` (`StrangDatum`),
  KEY `StrangZ` (`StrangZ`),
  KEY `StrangOrder` (`StrangOrder`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsyscollections`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblsyscollections` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typ` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `idValue` int(11) DEFAULT NULL,
  `txtValue` varchar(255) DEFAULT NULL,
  `txtP1` varchar(255) DEFAULT NULL,
  `txtP2` varchar(255) DEFAULT NULL,
  `txtP3` varchar(255) DEFAULT NULL,
  `txtP4` varchar(255) DEFAULT NULL,
  `txtP5` varchar(255) DEFAULT NULL,
  `txtP6` varchar(255) DEFAULT NULL,
  `txtP7` varchar(255) DEFAULT NULL,
  `txtP8` varchar(255) DEFAULT NULL,
  `txtP9` varchar(255) DEFAULT NULL,
  `bsca` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `typ` (`typ`),
  KEY `idvalue` (`idValue`),
  KEY `sort` (`sort`),
  KEY `txtvalue` (`txtValue`),
  KEY `txtP1` (`txtP1`),
  KEY `txtP2` (`txtP2`),
  KEY `txtP3` (`txtP3`),
  KEY `txtP4` (`txtP4`)
) ENGINE=MyISAM AUTO_INCREMENT=1599 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-23 13:01:06

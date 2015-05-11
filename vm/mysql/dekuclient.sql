CREATE DATABASE  IF NOT EXISTS `dekuclient` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `dekuclient`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: dekuclient
-- ------------------------------------------------------
-- Server version	5.6.17

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
-- Table structure for table `adminbenutzer`
--

DROP TABLE IF EXISTS `adminbenutzer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminbenutzer` (
  `BenutzerID` int(10) unsigned NOT NULL DEFAULT '0',
  `sName` varchar(50) DEFAULT NULL,
  `sVorname` varchar(50) DEFAULT NULL,
  `sKennwort` varchar(50) DEFAULT NULL,
  `sLevel` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`BenutzerID`),
  KEY `sVorname` (`sVorname`),
  KEY `sName` (`sName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminberechtigungen`
--

DROP TABLE IF EXISTS `adminberechtigungen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminberechtigungen` (
  `BerechtigungsID` int(10) unsigned NOT NULL DEFAULT '0',
  `fFormular` varchar(50) DEFAULT NULL,
  `fFeld` varchar(50) DEFAULT NULL,
  `sPage` varchar(50) DEFAULT NULL,
  `iPage` double DEFAULT NULL,
  `bBeschreibung` varchar(50) DEFAULT NULL,
  `bKennzifferSAtxt` longtext,
  `bKennzifferSA` varchar(50) DEFAULT NULL,
  `bKennzifferSnAtxt` longtext,
  `bKennzifferSnA` varchar(50) DEFAULT NULL,
  `bKennzifferNStxt` longtext,
  `bKennzifferNS` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`BerechtigungsID`),
  KEY `fFormular` (`fFormular`),
  KEY `fFeld` (`fFeld`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminberechtigungencode`
--

DROP TABLE IF EXISTS `adminberechtigungencode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminberechtigungencode` (
  `BinID` int(10) unsigned NOT NULL DEFAULT '0',
  `pPosition` varchar(50) DEFAULT NULL,
  `dPositionCode` varchar(200) DEFAULT NULL,
  `bBeschreibung` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`BinID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminerrorlog`
--

DROP TABLE IF EXISTS `adminerrorlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminerrorlog` (
  `ID` int(10) unsigned NOT NULL DEFAULT '0',
  `ErrorDat` datetime DEFAULT NULL,
  `FormName` varchar(100) DEFAULT NULL,
  `FeldName` varchar(100) DEFAULT NULL,
  `sMeldung` varchar(200) DEFAULT NULL,
  `DepotID` varchar(10) DEFAULT NULL,
  `dMeldungNo` double DEFAULT NULL,
  `sErrString` text,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminfieldhist`
--

DROP TABLE IF EXISTS `adminfieldhist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminfieldhist` (
  `ID` int(10) unsigned NOT NULL DEFAULT '0',
  `changeDat` datetime DEFAULT NULL,
  `FormName` varchar(50) DEFAULT NULL,
  `FeldName` varchar(50) DEFAULT NULL,
  `OldValue` varchar(200) DEFAULT NULL,
  `NewValue` varchar(200) DEFAULT NULL,
  `MaVorname` varchar(50) DEFAULT NULL,
  `MaNachname` varchar(50) DEFAULT NULL,
  `refid` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `refid` (`refid`),
  KEY `FormName` (`FormName`),
  KEY `changeDat` (`changeDat`),
  KEY `MaVorname` (`MaVorname`),
  KEY `MaNachname` (`MaNachname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminforms`
--

DROP TABLE IF EXISTS `adminforms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminforms` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `frmName` varchar(50) DEFAULT NULL,
  `frmGruppe` varchar(50) DEFAULT NULL,
  `frmSection` double DEFAULT NULL,
  `frmSectionValue` double DEFAULT NULL,
  `frmFunction` varchar(50) DEFAULT NULL,
  `aktiv` double DEFAULT NULL,
  `frmSectionDefaultValue` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `frmName` (`frmName`),
  KEY `frmGruppe` (`frmGruppe`),
  KEY `frmSectionValue` (`frmSectionValue`),
  KEY `aktiv` (`aktiv`)
) ENGINE=MyISAM AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminformuse`
--

DROP TABLE IF EXISTS `adminformuse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminformuse` (
  `ID` double NOT NULL,
  `sFormUse` varchar(75) DEFAULT NULL,
  `dFormDepot` double DEFAULT NULL,
  `sFormUser` varchar(100) DEFAULT NULL,
  `sLocalPC` varchar(100) DEFAULT NULL,
  `tTimestamp` datetime DEFAULT NULL,
  `sApp` varchar(20) DEFAULT NULL,
  `sLogin` varchar(50) DEFAULT NULL,
  `dIsAdminForm` double DEFAULT NULL,
  `dFormOrder` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `sFormUse` (`sFormUse`),
  KEY `dFormDepot` (`dFormDepot`),
  KEY `sFormUser` (`sFormUser`),
  KEY `tTimestamp` (`tTimestamp`),
  KEY `dIsAdminForm` (`dIsAdminForm`),
  KEY `dFormOrder` (`dFormOrder`),
  KEY `timestamp` (`timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminoutlook`
--

DROP TABLE IF EXISTS `adminoutlook`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminoutlook` (
  `dID` int(10) unsigned NOT NULL DEFAULT '0',
  `dID_Parent` double DEFAULT NULL,
  `sBezeichnung` varchar(200) DEFAULT NULL,
  `sColor` varchar(50) DEFAULT NULL,
  `sFunktion` varchar(200) DEFAULT NULL,
  `sAktion` varchar(200) DEFAULT NULL,
  `sBerechtigung` longtext,
  `dVisible` double DEFAULT NULL,
  `dAktiv` double DEFAULT NULL,
  `sSourceObject` varchar(45) DEFAULT NULL,
  `sFirstfocus` varchar(45) DEFAULT NULL,
  `bOpenWindows` int(11) NOT NULL DEFAULT '0',
  `sBerText` longtext,
  `sIcon` varchar(50) DEFAULT NULL,
  `sichtbar` double NOT NULL DEFAULT '1',
  `with_oBag` double DEFAULT NULL,
  `bAvis` double DEFAULT NULL,
  `bTyp` double DEFAULT NULL,
  `dColl` double DEFAULT NULL,
  `bFrmStat` double DEFAULT NULL,
  `PrinterFalse` double DEFAULT NULL,
  `isTest` double DEFAULT '0',
  `sMinus` varchar(2000) DEFAULT NULL,
  `sPlus` varchar(2000) DEFAULT NULL,
  `dGroup` double DEFAULT '0',
  `dItem` double DEFAULT '0',
  PRIMARY KEY (`dID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminreports`
--

DROP TABLE IF EXISTS `adminreports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminreports` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `repName` varchar(50) DEFAULT NULL,
  `repGruppe` varchar(50) DEFAULT NULL,
  `repSection` double DEFAULT NULL,
  `repSectionValue` double DEFAULT NULL,
  `repFunction` varchar(50) DEFAULT NULL,
  `aktiv` double DEFAULT NULL,
  `repSectionDefaultValue` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `adminstatistiken`
--

DROP TABLE IF EXISTS `adminstatistiken`;
/*!50001 DROP VIEW IF EXISTS `adminstatistiken`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `adminstatistiken` (
  `orderid` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `stat` tinyint NOT NULL,
  `Dat` tinyint NOT NULL,
  `statuszaehler` tinyint NOT NULL,
  `type` tinyint NOT NULL,
  `ld` tinyint NOT NULL,
  `lieferdatum` tinyint NOT NULL,
  `ldlkz` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `admintabellen`
--

DROP TABLE IF EXISTS `admintabellen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admintabellen` (
  `ID` int(10) unsigned NOT NULL DEFAULT '0',
  `sTabelle` varchar(50) DEFAULT NULL,
  `dSort` double DEFAULT NULL,
  `sAlias` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminteltime`
--

DROP TABLE IF EXISTS `adminteltime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminteltime` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ma` int(10) unsigned DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `typ` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=929 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `admintreeview`
--

DROP TABLE IF EXISTS `admintreeview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admintreeview` (
  `dID` int(10) unsigned NOT NULL DEFAULT '0',
  `dID_Parent` double DEFAULT NULL,
  `sBezeichnung` varchar(200) DEFAULT NULL,
  `sColor` varchar(50) DEFAULT NULL,
  `sFunktion` varchar(200) DEFAULT NULL,
  `sAktion` varchar(200) DEFAULT NULL,
  `sBerechtigung` longtext,
  `dVisible` double DEFAULT NULL,
  `dAktiv` double DEFAULT NULL,
  `sSourceObject` varchar(45) DEFAULT NULL,
  `sFirstfocus` varchar(45) DEFAULT NULL,
  `bOpenWindows` int(11) DEFAULT NULL,
  PRIMARY KEY (`dID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adminuserlogin`
--

DROP TABLE IF EXISTS `adminuserlogin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminuserlogin` (
  `ID` double NOT NULL,
  `sName` varchar(145) DEFAULT NULL,
  `sDBVersion` varchar(45) DEFAULT NULL,
  `sTimestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `sTimestamp` (`sTimestamp`),
  KEY `sName` (`sName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `al_view_ac`
--

DROP TABLE IF EXISTS `al_view_ac`;
/*!50001 DROP VIEW IF EXISTS `al_view_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_ac` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dttermin_von` tinyint NOT NULL,
  `dttermin` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `zonea` tinyint NOT NULL,
  `zone` tinyint NOT NULL,
  `dtAuslieferdatum` tinyint NOT NULL,
  `orderid` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_ac_all`
--

DROP TABLE IF EXISTS `al_view_ac_all`;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_all`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_ac_all` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dttermin_von` tinyint NOT NULL,
  `dttermin` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Verladelinie` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_ac_tr`
--

DROP TABLE IF EXISTS `al_view_ac_tr`;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_tr`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_ac_tr` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dttermin_von` tinyint NOT NULL,
  `dttermin` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `zonea` tinyint NOT NULL,
  `zone` tinyint NOT NULL,
  `dtAuslieferdatum` tinyint NOT NULL,
  `orderid` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `Referenz2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_ac_tra`
--

DROP TABLE IF EXISTS `al_view_ac_tra`;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_tra`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_ac_tra` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dttermin_von` tinyint NOT NULL,
  `dttermin` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `zonea` tinyint NOT NULL,
  `zone` tinyint NOT NULL,
  `dtAuslieferdatum` tinyint NOT NULL,
  `orderid` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `Referenz2` tinyint NOT NULL,
  `clearingartmaster` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_ac_trall`
--

DROP TABLE IF EXISTS `al_view_ac_trall`;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_trall`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_ac_trall` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dttermin_von` tinyint NOT NULL,
  `dttermin` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `zonea` tinyint NOT NULL,
  `zone` tinyint NOT NULL,
  `dtAuslieferdatum` tinyint NOT NULL,
  `orderid` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_ac_trb`
--

DROP TABLE IF EXISTS `al_view_ac_trb`;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_trb`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_ac_trb` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dttermin_von` tinyint NOT NULL,
  `dttermin` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `zonea` tinyint NOT NULL,
  `zone` tinyint NOT NULL,
  `dtAuslieferdatum` tinyint NOT NULL,
  `orderid` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `Referenz2` tinyint NOT NULL,
  `clearingartmaster` tinyint NOT NULL,
  `service` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_acs`
--

DROP TABLE IF EXISTS `al_view_acs`;
/*!50001 DROP VIEW IF EXISTS `al_view_acs`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_acs` (
  `OrderID` tinyint NOT NULL,
  `KZ_Statuserzeuger` tinyint NOT NULL,
  `Packstuecknummer` tinyint NOT NULL,
  `Datum` tinyint NOT NULL,
  `Zeit` tinyint NOT NULL,
  `KZ_Status` tinyint NOT NULL,
  `Fehlercode` tinyint NOT NULL,
  `Erzeugerstation` tinyint NOT NULL,
  `text` tinyint NOT NULL,
  `Infotext` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Timestamp2` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_af`
--

DROP TABLE IF EXISTS `al_view_af`;
/*!50001 DROP VIEW IF EXISTS `al_view_af`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_af` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `OldValue` tinyint NOT NULL,
  `NewValue` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `Tabelle` tinyint NOT NULL,
  `FeldName` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_tblauftrag`
--

DROP TABLE IF EXISTS `al_view_tblauftrag`;
/*!50001 DROP VIEW IF EXISTS `al_view_tblauftrag`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_tblauftrag` (
  `OrderID` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrbev` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `DatumNN` tinyint NOT NULL,
  `erhaltenNN` tinyint NOT NULL,
  `Feiertag_1` tinyint NOT NULL,
  `FeiertagShlD` tinyint NOT NULL,
  `FeiertagShlS` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Satzart` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `Referenz2` tinyint NOT NULL,
  `Frei` tinyint NOT NULL,
  `Adr_Nr_Absender` tinyint NOT NULL,
  `Adr_Nr_Empfaenger` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Lieferdatum` tinyint NOT NULL,
  `Lieferzeit_von` tinyint NOT NULL,
  `Lieferzeit_bis` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `KZServiceLeo` tinyint NOT NULL,
  `Sendungsstatus` tinyint NOT NULL,
  `Ausliefertour` tinyint NOT NULL,
  `KZ_erweitert` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Frei5` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `Besteller_Name` tinyint NOT NULL,
  `Ladelisten_Nummer` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `KZ_Zusatz` tinyint NOT NULL,
  `CR` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `d` tinyint NOT NULL,
  `a` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `RechnungsNr` tinyint NOT NULL,
  `Satzart_ava` tinyint NOT NULL,
  `FirmaD3X` tinyint NOT NULL,
  `LandDX` tinyint NOT NULL,
  `PLZDX` tinyint NOT NULL,
  `OrtDX` tinyint NOT NULL,
  `StrasseDX` tinyint NOT NULL,
  `StrNrDX` tinyint NOT NULL,
  `Satzartp` tinyint NOT NULL,
  `PZA_bag` tinyint NOT NULL,
  `Belegnummer_akt` tinyint NOT NULL,
  `UploadStatus` tinyint NOT NULL,
  `SendStatus` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `OrderID_X` tinyint NOT NULL,
  `PZAImageZIP` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `RueckDate` tinyint NOT NULL,
  `ClearingArtMaster` tinyint NOT NULL,
  `ZoneS` tinyint NOT NULL,
  `Locking` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `ROrderID` tinyint NOT NULL,
  `EBOrderID` tinyint NOT NULL,
  `EXAuftragsIDRef` tinyint NOT NULL,
  `EBRueckGrund` tinyint NOT NULL,
  `product_spec` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `al_view_tblauftrag_collies`
--

DROP TABLE IF EXISTS `al_view_tblauftrag_collies`;
/*!50001 DROP VIEW IF EXISTS `al_view_tblauftrag_collies`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `al_view_tblauftrag_collies` (
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dttermin_von` tinyint NOT NULL,
  `dttermin` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `Empfaenger2` tinyint NOT NULL,
  `Verpackungsart` tinyint NOT NULL,
  `Verladelinie` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `creferenz` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `auftrag_profil`
--

DROP TABLE IF EXISTS `auftrag_profil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auftrag_profil` (
  `idauftrag_profil` double NOT NULL AUTO_INCREMENT,
  `profilid` double DEFAULT NULL,
  `profilname` varchar(50) DEFAULT NULL,
  `profilelementalias` varchar(50) DEFAULT NULL,
  `profilelement` varchar(50) DEFAULT NULL,
  `profilelementlabel` varchar(50) DEFAULT NULL,
  `profilreihenfolge` double DEFAULT NULL,
  `profilidtree_parent` int(11) DEFAULT NULL,
  `profilidtree_1stchild` int(11) DEFAULT NULL,
  `profilidtree_2ndchild` int(11) DEFAULT NULL,
  `profilelementaktiv` varchar(10) DEFAULT NULL,
  `profildepot` double DEFAULT NULL,
  `profilaktiv` varchar(10) DEFAULT NULL,
  `profilformname` varchar(50) DEFAULT NULL,
  `profilformalias` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idauftrag_profil`),
  KEY `profildepot` (`profildepot`),
  KEY `profilid` (`profilid`),
  KEY `profilname` (`profilname`)
) ENGINE=MyISAM AUTO_INCREMENT=6641 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auftrag_reihen`
--

DROP TABLE IF EXISTS `auftrag_reihen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auftrag_reihen` (
  `idauftrag_reihen` double NOT NULL AUTO_INCREMENT,
  `profilelementalias` varchar(50) DEFAULT NULL,
  `profilelement` varchar(50) DEFAULT NULL,
  `profilreihenfolge_org` double DEFAULT NULL,
  `profilreihenfolge_neu` double DEFAULT NULL,
  `profilfunktion_a` varchar(200) DEFAULT NULL,
  `profilreihenfolge_a` double DEFAULT NULL,
  `profilfunktion_b` varchar(200) DEFAULT NULL,
  `profilreihenfolge_b` double DEFAULT NULL,
  `profilreihenfolge_tab` double DEFAULT NULL,
  `profilformname` varchar(50) DEFAULT NULL,
  `profilformalias` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idauftrag_reihen`),
  KEY `profilelement` (`profilelement`),
  KEY `profilformname` (`profilformname`),
  KEY `profilreihenfolge_b` (`profilreihenfolge_b`),
  KEY `profilreihenfolge_neu` (`profilreihenfolge_neu`)
) ENGINE=MyISAM AUTO_INCREMENT=152 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `auftragcollie10d`
--

DROP TABLE IF EXISTS `auftragcollie10d`;
/*!50001 DROP VIEW IF EXISTS `auftragcollie10d`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `auftragcollie10d` (
  `OrderID` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `eborderid` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `verladelinie` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `frei2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtLagereingang` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `orderpos` tinyint NOT NULL,
  `dtLagerausgang` tinyint NOT NULL,
  `Lagerplatz` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `checkautomateng`
--

DROP TABLE IF EXISTS `checkautomateng`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkautomateng` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `sAutomat` varchar(50) DEFAULT NULL,
  `sTask` varchar(50) DEFAULT NULL,
  `sValues` varchar(50) DEFAULT NULL,
  `sStatus` varchar(50) DEFAULT NULL,
  `sToleranz` varchar(50) DEFAULT NULL,
  `dToleranz` double DEFAULT NULL,
  `ts` double DEFAULT NULL,
  `dAktVor` double DEFAULT NULL,
  `sAktVor` varchar(50) DEFAULT NULL,
  `sAlarm` varchar(50) DEFAULT NULL,
  `sort` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `run` int(11) DEFAULT NULL,
  `dToleranzGelb` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `run` (`run`),
  KEY `timestamp` (`timestamp`)
) ENGINE=MyISAM AUTO_INCREMENT=8309547 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checkautomatenprofile`
--

DROP TABLE IF EXISTS `checkautomatenprofile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkautomatenprofile` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sAutomat` varchar(250) DEFAULT NULL,
  `SBezeichnung` varchar(50) DEFAULT NULL,
  `sToleranz` varchar(50) DEFAULT NULL,
  `dToleranz` double DEFAULT NULL,
  `erlaeuterungen` varchar(250) DEFAULT NULL,
  `bAktiv` int(11) DEFAULT NULL,
  `grp` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `anz` double DEFAULT NULL,
  `datname` varchar(45) DEFAULT NULL,
  `sqlstring` varchar(200) DEFAULT NULL,
  `msgok` varchar(45) DEFAULT NULL,
  `msgnok` varchar(45) DEFAULT NULL,
  `sqlpruef` varchar(45) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dToleranzGelb` int(11) DEFAULT NULL,
  `Task` varchar(10) DEFAULT NULL,
  `eh` varchar(20) DEFAULT NULL,
  `Limitsign` int(11) DEFAULT NULL,
  `dispPerm` int(11) DEFAULT NULL,
  `sqldrill` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=6200 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checkbackupg`
--

DROP TABLE IF EXISTS `checkbackupg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkbackupg` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `sPfad` varchar(50) DEFAULT NULL,
  `sFile` varchar(50) DEFAULT NULL,
  `sValues` varchar(50) DEFAULT NULL,
  `sStatus` varchar(50) DEFAULT NULL,
  `sToleranz` varchar(50) DEFAULT NULL,
  `dToleranz` double DEFAULT NULL,
  `ts` double DEFAULT NULL,
  `dAktVor` double DEFAULT NULL,
  `sAktVor` varchar(50) DEFAULT NULL,
  `sAlarm` varchar(50) DEFAULT NULL,
  `sort` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=6068961 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checkdisk`
--

DROP TABLE IF EXISTS `checkdisk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkdisk` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `App` varchar(50) DEFAULT NULL,
  `Disk` varchar(50) DEFAULT NULL,
  `Min` varchar(50) DEFAULT NULL,
  `sort` double NOT NULL DEFAULT '0',
  `AppName` varchar(50) DEFAULT NULL,
  `DiskAlias` varchar(50) DEFAULT NULL,
  `bAktiv` int(11) NOT NULL DEFAULT '0',
  `MinGelb` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=39 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checkdiskg`
--

DROP TABLE IF EXISTS `checkdiskg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkdiskg` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TotalBytes` double DEFAULT NULL,
  `TotalFreeBytes` double DEFAULT NULL,
  `TotalSpaceMB` double DEFAULT NULL,
  `FreeSpaceMB` double DEFAULT NULL,
  `AppName` varchar(50) DEFAULT NULL,
  `Disk` varchar(50) DEFAULT NULL,
  `Min` double DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MinGelb` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=1977359 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checkfilesystemg`
--

DROP TABLE IF EXISTS `checkfilesystemg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkfilesystemg` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `sPfad` varchar(50) DEFAULT NULL,
  `sFile` varchar(50) DEFAULT NULL,
  `sValues` varchar(50) DEFAULT NULL,
  `sStatus` varchar(50) DEFAULT NULL,
  `sToleranz` varchar(50) DEFAULT NULL,
  `dToleranz` double DEFAULT NULL,
  `ts` double DEFAULT NULL,
  `dAktVor` double DEFAULT NULL,
  `sAktVor` varchar(50) DEFAULT NULL,
  `sAlarm` varchar(50) DEFAULT NULL,
  `sort` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=32294877 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checkg`
--

DROP TABLE IF EXISTS `checkg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkg` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `sAutomat` varchar(50) DEFAULT NULL,
  `sTask` varchar(50) DEFAULT NULL,
  `sValues` double DEFAULT NULL,
  `sStatus` varchar(50) DEFAULT NULL,
  `sToleranz` varchar(50) DEFAULT NULL,
  `dToleranz` double DEFAULT NULL,
  `ts` double DEFAULT NULL,
  `dAktVor` double DEFAULT NULL,
  `sAktVor` varchar(50) DEFAULT NULL,
  `sAlarm` varchar(250) DEFAULT NULL,
  `sort` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dToleranzGelb` double DEFAULT NULL,
  `run` int(11) DEFAULT NULL,
  `eh` varchar(20) DEFAULT NULL,
  `dispPerm` int(11) DEFAULT NULL,
  `Limitsign` int(11) DEFAULT NULL,
  `sqlstr` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `run` (`run`),
  KEY `timestamp` (`timestamp`),
  KEY `dispPerm` (`dispPerm`)
) ENGINE=MyISAM AUTO_INCREMENT=70707022 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checktabelleng`
--

DROP TABLE IF EXISTS `checktabelleng`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checktabelleng` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `sPfad` varchar(50) DEFAULT NULL,
  `sFile` varchar(50) DEFAULT NULL,
  `sValues` varchar(50) DEFAULT NULL,
  `sStatus` varchar(50) DEFAULT NULL,
  `sToleranz` varchar(50) DEFAULT NULL,
  `dToleranz` double DEFAULT NULL,
  `ts` double DEFAULT NULL,
  `dAktVor` double DEFAULT NULL,
  `sAktVor` varchar(50) DEFAULT NULL,
  `sAlarm` varchar(50) DEFAULT NULL,
  `sort` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=483401 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checktransferg`
--

DROP TABLE IF EXISTS `checktransferg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checktransferg` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `sPfad` varchar(50) DEFAULT NULL,
  `sFile` varchar(50) DEFAULT NULL,
  `sValues` varchar(50) DEFAULT NULL,
  `sStatus` varchar(50) DEFAULT NULL,
  `sToleranz` varchar(50) DEFAULT NULL,
  `dToleranz` double DEFAULT NULL,
  `ts` double DEFAULT NULL,
  `dAktVor` double DEFAULT NULL,
  `sAktVor` varchar(50) DEFAULT NULL,
  `sAlarm` varchar(50) DEFAULT NULL,
  `sort` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=5011306 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cladurchlaufsummen`
--

DROP TABLE IF EXISTS `cladurchlaufsummen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cladurchlaufsummen` (
  `DepotNrAD` int(11) NOT NULL DEFAULT '0',
  `Verladedatum` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `AZ` int(11) DEFAULT NULL,
  PRIMARY KEY (`DepotNrAD`,`Verladedatum`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claflfcbasis`
--

DROP TABLE IF EXISTS `claflfcbasis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claflfcbasis` (
  `GLSNR` double NOT NULL DEFAULT '0',
  `verladedatum` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dtAuslieferung` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `depotnrad` int(11) NOT NULL DEFAULT '0',
  `depotnrld` int(11) NOT NULL DEFAULT '0',
  `belegnummer` double NOT NULL DEFAULT '0',
  `auftragsdepot` int(1) NOT NULL DEFAULT '0',
  `abgangsland` int(1) NOT NULL DEFAULT '0',
  `zustland` int(1) NOT NULL DEFAULT '0',
  `dttermin_von` datetime DEFAULT NULL,
  `dttermin` datetime DEFAULT NULL,
  `ASDZeit` datetime DEFAULT NULL,
  `ASDDate` datetime DEFAULT NULL,
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `noterf` varchar(1) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `saturday` varchar(1) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `lands` varchar(5) DEFAULT '',
  `landd` varchar(5) DEFAULT '',
  `Importweg` varchar(5) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`CollieBelegNr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clakontenrahmen`
--

DROP TABLE IF EXISTS `clakontenrahmen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clakontenrahmen` (
  `KontoNr` int(10) unsigned NOT NULL DEFAULT '0',
  `Beschreibung` varchar(45) DEFAULT NULL,
  `KE` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `BeschreibungIntern` varchar(45) DEFAULT NULL,
  `typ` int(11) DEFAULT NULL,
  PRIMARY KEY (`KontoNr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clala`
--

DROP TABLE IF EXISTS `clala`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clala` (
  `Leistungsart` int(11) NOT NULL DEFAULT '0',
  `Beleg` varchar(255) DEFAULT NULL,
  `PLLAGrp` varchar(255) DEFAULT NULL,
  `Lname` varchar(255) DEFAULT NULL,
  `tdDEKU` varchar(255) DEFAULT NULL,
  `Beschreibung` varchar(255) DEFAULT NULL,
  `SAPKonto` double DEFAULT NULL,
  `SAPKontoBez` varchar(255) DEFAULT NULL,
  `SAPBAB` double DEFAULT NULL,
  `SAPBABBez` varchar(255) DEFAULT NULL,
  `KstPctr` double DEFAULT NULL,
  `Buchungskreis` varchar(255) DEFAULT NULL,
  `Konverter` varchar(255) DEFAULT NULL,
  `Fakturaart` varchar(255) DEFAULT NULL,
  `MglicheLeoStKz` mediumtext,
  `MglicheGLSTaxCodes` varchar(255) DEFAULT NULL,
  `Bemerkung` varchar(255) DEFAULT NULL,
  `PLLA` int(11) DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `KonverterGr` varchar(5) DEFAULT NULL,
  `KE` int(11) DEFAULT NULL,
  PRIMARY KEY (`Leistungsart`),
  KEY `PLLA` (`PLLA`),
  KEY `istInternational` (`istInternational`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clamanuelleartikel`
--

DROP TABLE IF EXISTS `clamanuelleartikel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clamanuelleartikel` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Bezeichnung` varchar(50) DEFAULT NULL,
  `PCtr` varchar(45) DEFAULT NULL,
  `SAPLA` varchar(45) DEFAULT NULL,
  `SAPGrp` int(11) DEFAULT NULL,
  `KE` int(11) DEFAULT NULL,
  `SAPKto` int(11) DEFAULT NULL,
  `TaxMatrix` varchar(5) DEFAULT NULL,
  `TaxCode` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=100 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clanichtabgerechnete`
--

DROP TABLE IF EXISTS `clanichtabgerechnete`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clanichtabgerechnete` (
  `Belegnummer` double NOT NULL AUTO_INCREMENT,
  `verladedatum` datetime NOT NULL,
  `flag` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Belegnummer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clapl`
--

DROP TABLE IF EXISTS `clapl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clapl` (
  `PLID` int(11) NOT NULL DEFAULT '0',
  `PLTyp` int(11) DEFAULT '0',
  `DepotID` int(11) DEFAULT '0',
  `ZuZone` varchar(5) DEFAULT NULL,
  `FahrzArt` int(11) DEFAULT NULL,
  `PauschBetrag` double DEFAULT '0',
  `Text` varchar(45) DEFAULT NULL,
  `ZuLKZ` varchar(45) DEFAULT NULL,
  `GueltigAb` datetime DEFAULT NULL,
  `KgStaffel` double DEFAULT '1',
  `ExKondi` int(11) DEFAULT '0',
  `Dreieck` int(11) DEFAULT '0',
  `DepotIDAbD` int(11) DEFAULT '0',
  `GueltigBis` datetime DEFAULT NULL,
  `PLExportTyp` int(11) DEFAULT NULL,
  `Nummernkreis` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `GLSKennung` int(11) DEFAULT NULL,
  PRIMARY KEY (`PLID`),
  KEY `PLTyp` (`PLTyp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claplabedingung`
--

DROP TABLE IF EXISTS `claplabedingung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claplabedingung` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `PLAID` int(11) DEFAULT NULL,
  `Sql` mediumtext,
  `text` varchar(45) DEFAULT NULL,
  `Typ` varchar(10) DEFAULT NULL,
  `RS` varchar(10) DEFAULT NULL,
  `RS2` varchar(10) DEFAULT NULL,
  `V` varchar(10) DEFAULT NULL,
  `p1` varchar(255) DEFAULT NULL,
  `p2` varchar(255) DEFAULT NULL,
  `p3` varchar(255) DEFAULT NULL,
  `p4` varchar(255) DEFAULT NULL,
  `p5` varchar(255) DEFAULT NULL,
  `p6` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claplsub`
--

DROP TABLE IF EXISTS `claplsub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claplsub` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `PLID` int(11) DEFAULT '0',
  `MengeAb` double DEFAULT '0',
  `Preis` double DEFAULT '0',
  `PauschBtr` double DEFAULT '0',
  `BasisMenge` tinyint(1) DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `BasisMenge` (`BasisMenge`),
  KEY `PLID` (`PLID`),
  KEY `MengeAb` (`MengeAb`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claplsubx`
--

DROP TABLE IF EXISTS `claplsubx`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claplsubx` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `PLID` int(11) DEFAULT '0',
  `MengeAb` double DEFAULT '0',
  `Preis` double DEFAULT '0',
  `PauschBtr` double DEFAULT '0',
  `BasisMenge` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `BasisMenge` (`BasisMenge`),
  KEY `PLID` (`PLID`),
  KEY `MengeAb` (`MengeAb`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clapltyp`
--

DROP TABLE IF EXISTS `clapltyp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clapltyp` (
  `PLTyp` int(11) NOT NULL DEFAULT '0',
  `TypName` varchar(200) DEFAULT NULL,
  `VerkettDepot` varchar(45) DEFAULT NULL,
  `Zone` varchar(5) DEFAULT NULL,
  `Einheit` varchar(10) DEFAULT NULL,
  `Leistungsgrp` varchar(50) DEFAULT NULL,
  `KeinFV` int(11) DEFAULT NULL,
  `PLExportTyp` varchar(50) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `FLFC` int(11) DEFAULT NULL,
  PRIMARY KEY (`PLTyp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clapltypx`
--

DROP TABLE IF EXISTS `clapltypx`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clapltypx` (
  `PLTyp` int(11) NOT NULL DEFAULT '0',
  `TypName` varchar(200) DEFAULT NULL,
  `VerkettDepot` varchar(45) DEFAULT NULL,
  `Zone` varchar(5) DEFAULT NULL,
  `Einheit` varchar(10) DEFAULT NULL,
  `Leistungsgrp` varchar(50) DEFAULT NULL,
  `KeinFV` int(11) DEFAULT NULL,
  `PLExportTyp` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`PLTyp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claplx`
--

DROP TABLE IF EXISTS `claplx`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claplx` (
  `PLID` int(11) NOT NULL DEFAULT '0',
  `PLTyp` int(11) DEFAULT '0',
  `DepotID` int(11) DEFAULT '0',
  `ZuZone` varchar(5) DEFAULT NULL,
  `FahrzArt` int(11) DEFAULT NULL,
  `PauschBetrag` double DEFAULT '0',
  `Text` varchar(45) DEFAULT NULL,
  `ZuLKZ` varchar(45) DEFAULT NULL,
  `GueltigAb` datetime DEFAULT NULL,
  `KgStaffel` double DEFAULT '1',
  `ExKondi` int(11) DEFAULT '0',
  `Dreieck` int(11) DEFAULT '0',
  `DepotIDAbD` int(11) DEFAULT '0',
  `GueltigBis` datetime DEFAULT NULL,
  `PLExportTyp` int(11) DEFAULT NULL,
  PRIMARY KEY (`PLID`),
  KEY `PLTyp` (`PLTyp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claprintctrl`
--

DROP TABLE IF EXISTS `claprintctrl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claprintctrl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clientid` int(11) NOT NULL DEFAULT '0',
  `rptid` int(11) NOT NULL DEFAULT '0',
  `depotnr` int(11) DEFAULT NULL,
  `p1` varchar(45) DEFAULT NULL,
  `masterid` int(11) DEFAULT NULL,
  `rggutschrift` varchar(5) DEFAULT NULL,
  `RgNr` int(10) unsigned DEFAULT NULL,
  `porder` int(10) unsigned DEFAULT NULL,
  `isrechnung` int(11) DEFAULT NULL,
  `GuNr` int(11) DEFAULT NULL,
  `BelNr` int(11) DEFAULT NULL,
  `RepVisible` int(11) DEFAULT NULL,
  `CaptionTop` double DEFAULT NULL,
  `LabelVisible` int(11) DEFAULT NULL,
  `MwStVisible` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `rptid` (`rptid`),
  KEY `clientid` (`clientid`)
) ENGINE=MyISAM AUTO_INCREMENT=891169 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claprozess`
--

DROP TABLE IF EXISTS `claprozess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claprozess` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `ProzessID` int(11) DEFAULT NULL,
  `ProduktID` int(11) DEFAULT NULL,
  `Text` varchar(45) DEFAULT NULL,
  `POrder` int(11) DEFAULT NULL,
  `BerechnungAn` varchar(45) DEFAULT NULL,
  `LandCheck` int(11) DEFAULT NULL,
  `RGGutschrift` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `produktid` (`ProduktID`),
  KEY `porder` (`POrder`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claprozesspla`
--

DROP TABLE IF EXISTS `claprozesspla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claprozesspla` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ProzessID` int(11) DEFAULT NULL,
  `POrder` int(11) DEFAULT NULL,
  `Bedingungen` varchar(200) DEFAULT NULL,
  `Text` mediumtext,
  `PLTypID` int(11) DEFAULT NULL,
  `Leistungsart` varchar(50) DEFAULT NULL,
  `SAPNr` varchar(50) DEFAULT NULL,
  `SachKtn` varchar(50) DEFAULT NULL,
  `Kostenstelle` varchar(50) DEFAULT NULL,
  `LeistungsGrp` int(11) DEFAULT NULL,
  `LeistungsGrpSAP` int(11) DEFAULT NULL,
  `Aktiv` int(11) DEFAULT NULL,
  `PLKZ` varchar(5) DEFAULT NULL,
  `gueltigAb` datetime DEFAULT NULL,
  `gueltigBis` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `PLExportTyp` int(11) DEFAULT NULL,
  `Treibstoffzuschlag` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `prozessid` (`ProzessID`),
  KEY `porder` (`POrder`)
) ENGINE=MyISAM AUTO_INCREMENT=912 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claprozessplax`
--

DROP TABLE IF EXISTS `claprozessplax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claprozessplax` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ProzessID` int(11) DEFAULT NULL,
  `POrder` int(11) DEFAULT NULL,
  `Bedingungen` varchar(200) DEFAULT NULL,
  `Text` mediumtext,
  `PLTypID` int(11) DEFAULT NULL,
  `Leistungsart` varchar(50) DEFAULT NULL,
  `SAPNr` varchar(50) DEFAULT NULL,
  `SachKtn` varchar(50) DEFAULT NULL,
  `Kostenstelle` varchar(50) DEFAULT NULL,
  `LeistungsGrp` int(11) DEFAULT NULL,
  `LeistungsGrpSAP` int(11) DEFAULT NULL,
  `Aktiv` int(11) DEFAULT NULL,
  `PLKZ` varchar(5) DEFAULT NULL,
  `gueltigAb` datetime DEFAULT NULL,
  `gueltigBis` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `PLExportTyp` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `prozessid` (`ProzessID`),
  KEY `porder` (`POrder`)
) ENGINE=MyISAM AUTO_INCREMENT=506 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clareklapositionen`
--

DROP TABLE IF EXISTS `clareklapositionen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clareklapositionen` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ReklaText` varchar(100) DEFAULT NULL,
  `Depotid` int(11) DEFAULT NULL,
  `Betrag` double DEFAULT NULL,
  `RgNr` int(11) DEFAULT NULL,
  `LaufID` int(11) DEFAULT NULL,
  `ReklaDatum` datetime DEFAULT NULL,
  `KDTyp` varchar(1) DEFAULT NULL,
  `Orderid` double DEFAULT NULL,
  `SAPGrp` int(11) DEFAULT NULL,
  `Sendungslauf` varchar(45) DEFAULT NULL,
  `TaxCode` varchar(5) DEFAULT NULL,
  `PCTR` varchar(45) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `finalisiererrs` varchar(200) DEFAULT NULL,
  `RG` int(11) DEFAULT NULL,
  `MwStShl` int(11) DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `UID` varchar(45) DEFAULT NULL,
  `dBetrag` double DEFAULT NULL,
  `BtrMwst` double DEFAULT NULL,
  `Leistungsmonat` varchar(10) DEFAULT NULL,
  `LA` varchar(45) DEFAULT NULL,
  `Belegtext` varchar(45) DEFAULT NULL,
  `RgNrP` int(11) DEFAULT NULL,
  `LaufIDP` int(11) DEFAULT NULL,
  `MA` varchar(45) DEFAULT NULL,
  `Berechnenals` varchar(10) DEFAULT NULL,
  `ArtNr` int(11) DEFAULT NULL,
  `SAPGrpIN` int(11) DEFAULT NULL,
  `SAPKto` int(11) DEFAULT NULL,
  `SAPKtoKE` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `EinzelRg` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Orderid` (`Orderid`)
) ENGINE=MyISAM AUTO_INCREMENT=750947 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clarepositionen`
--

DROP TABLE IF EXISTS `clarepositionen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clarepositionen` (
  `Pos` int(11) NOT NULL DEFAULT '0',
  `Art` char(3) DEFAULT NULL,
  `RGNummer` int(10) unsigned NOT NULL DEFAULT '0',
  `Belegdatum` datetime DEFAULT NULL,
  `Leistungsmonat` varchar(7) DEFAULT NULL,
  `Netto` double DEFAULT NULL,
  `MWST` double DEFAULT NULL,
  `Brutto` double DEFAULT NULL,
  `SapTaxCode` varchar(10) DEFAULT NULL,
  `SapTaxCodeKonverter` varchar(10) DEFAULT NULL,
  `LeistungsArt` varchar(10) DEFAULT NULL,
  `Leistungstext` varchar(50) DEFAULT NULL,
  `RgGLOZusatz` char(2) DEFAULT NULL,
  `DepotNr` int(11) DEFAULT NULL,
  `MasterRun` int(11) DEFAULT NULL,
  `Konto` varchar(10) DEFAULT NULL,
  `RG` varchar(15) DEFAULT NULL,
  `MwstSatz` double DEFAULT NULL,
  `DebitCredit` varchar(5) DEFAULT NULL,
  `SAPLA` int(11) DEFAULT NULL,
  `KstPctr` int(11) DEFAULT NULL,
  `Belegtext` varchar(100) DEFAULT NULL,
  `SAPLAGU` varchar(10) DEFAULT NULL,
  `SAPKstPctr` varchar(10) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ZUONR` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Pos`,`RGNummer`),
  KEY `art` (`Art`),
  KEY `rg` (`RG`),
  KEY `RGNummer` (`RGNummer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claresuldruck`
--

DROP TABLE IF EXISTS `claresuldruck`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claresuldruck` (
  `OrderID` double NOT NULL DEFAULT '0',
  `Belegnummer` double NOT NULL DEFAULT '0',
  `RGGutschrift` char(2) NOT NULL,
  `Betrag` double NOT NULL DEFAULT '0',
  `SKZText` char(250) NOT NULL,
  `TaxText` char(100) DEFAULT NULL,
  `Masterid` int(11) NOT NULL DEFAULT '0',
  `Berechnungandepot` int(11) NOT NULL DEFAULT '0',
  `berechnungan` char(10) NOT NULL DEFAULT '',
  `MwstShl` int(11) DEFAULT NULL,
  `ClientID` int(11) NOT NULL DEFAULT '0',
  `TSZ` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`RGGutschrift`,`Masterid`,`Berechnungandepot`,`Belegnummer`,`berechnungan`,`ClientID`,`SKZText`,`Betrag`) USING BTREE,
  KEY `betrag` (`Betrag`),
  KEY `clientid` (`ClientID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=FIXED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claresultexport`
--

DROP TABLE IF EXISTS `claresultexport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claresultexport` (
  `Belegnummer` double NOT NULL DEFAULT '0',
  `System` double DEFAULT NULL,
  `Linie` double DEFAULT NULL,
  `PU` double DEFAULT NULL,
  `Zustellung` double DEFAULT NULL,
  `SoKo` double DEFAULT NULL,
  `Sum` double DEFAULT NULL,
  `Typ` char(5) NOT NULL DEFAULT '',
  `Services` char(200) DEFAULT NULL,
  `TaxCode` char(5) DEFAULT NULL,
  `MwSt` int(11) DEFAULT NULL,
  `RG` char(5) DEFAULT NULL,
  `ClientID` int(11) NOT NULL DEFAULT '0',
  `Orderid` double NOT NULL DEFAULT '0',
  `TSZ` double DEFAULT NULL,
  PRIMARY KEY (`Typ`,`ClientID`,`Orderid`) USING BTREE,
  KEY `belegnummer` (`Belegnummer`),
  KEY `typ` (`Typ`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=FIXED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claresultexporttmp`
--

DROP TABLE IF EXISTS `claresultexporttmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claresultexporttmp` (
  `verladedatum` datetime DEFAULT NULL,
  `Sendungsnummer` double NOT NULL,
  `SendungsType` varchar(1) DEFAULT NULL,
  `Abrechnungs_Typ` varchar(5) DEFAULT NULL,
  `Abrechnungs_Status` varchar(5) NOT NULL,
  `Services` varchar(200) DEFAULT NULL,
  `TaxCode` varchar(5) DEFAULT NULL,
  `MwSt` int(11) DEFAULT NULL,
  `System` double DEFAULT NULL,
  `Linie` double DEFAULT NULL,
  `PU` double DEFAULT NULL,
  `Zustellung` double DEFAULT NULL,
  `SoKo` double DEFAULT NULL,
  `NettoSumme` double DEFAULT NULL,
  `Fahrzeugart` varchar(35) DEFAULT NULL,
  `KZ` varchar(255) DEFAULT NULL,
  `Auftraggeber` int(11) DEFAULT NULL,
  `Abholer` int(11) DEFAULT NULL,
  `Lieferer` int(11) DEFAULT NULL,
  `Packstuecke` int(11) DEFAULT NULL,
  `Gesamtgewicht` double DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `AbsLand` varchar(5) DEFAULT NULL,
  `AbsPlz` varchar(10) DEFAULT NULL,
  `AbsZone` varchar(5) DEFAULT NULL,
  `EmpfLand` varchar(5) DEFAULT NULL,
  `EmpfPlz` varchar(10) DEFAULT NULL,
  `EmpfZone` varchar(5) DEFAULT NULL,
  `AbZeitVonx` datetime DEFAULT NULL,
  `AbZeitBisx` datetime DEFAULT NULL,
  `ZuZeitVonx` datetime DEFAULT NULL,
  `ZuZeitBisx` datetime DEFAULT NULL,
  `Zustelldatum_sollx` datetime DEFAULT NULL,
  `Zustelldatum_istx` datetime DEFAULT NULL,
  `Auslieferzeitx` datetime DEFAULT NULL,
  `Empfaengername` varchar(50) DEFAULT NULL,
  `PU_XC_Referenz_Sendungsnummer` double DEFAULT NULL,
  `Referenz_1` varchar(255) DEFAULT NULL,
  `Referenz_2` varchar(255) DEFAULT NULL,
  `Referenz_3` varchar(255) DEFAULT NULL,
  `Referenz_4` varchar(255) DEFAULT NULL,
  `Erstellungsdatumx` varchar(255) DEFAULT NULL,
  `Orderid` double NOT NULL DEFAULT '0',
  `tsz` double DEFAULT NULL,
  `Referenz_5` varchar(255) DEFAULT NULL,
  `SSZAbVon` datetime DEFAULT NULL,
  `SSZAbBis` datetime DEFAULT NULL,
  `SSZZuVon` datetime DEFAULT NULL,
  `SSZZuBis` datetime DEFAULT NULL,
  PRIMARY KEY (`Abrechnungs_Status`,`Orderid`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claresultmasterruns`
--

DROP TABLE IF EXISTS `claresultmasterruns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claresultmasterruns` (
  `Id` int(11) NOT NULL DEFAULT '0',
  `text` varchar(255) DEFAULT NULL,
  `CaptionText` varchar(50) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `von` datetime DEFAULT NULL,
  `bis` datetime DEFAULT NULL,
  `nurDepot` int(11) DEFAULT NULL,
  `scharferLauf` int(11) DEFAULT NULL,
  `SAPHDtext` varchar(45) DEFAULT NULL,
  `BelegTyp` varchar(10) DEFAULT NULL,
  `ScRunID` int(10) unsigned DEFAULT NULL,
  `ReklaRunId` int(10) unsigned DEFAULT NULL,
  `TomtomRunId` int(10) unsigned DEFAULT NULL,
  `BuDat` datetime DEFAULT NULL,
  `depotfilter` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claresultruns`
--

DROP TABLE IF EXISTS `claresultruns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claresultruns` (
  `id` int(11) NOT NULL DEFAULT '0',
  `MasterID` int(11) DEFAULT NULL,
  `OrderID` double DEFAULT NULL,
  `text` mediumtext,
  `betraege` mediumtext,
  `Belegnummer` double DEFAULT NULL,
  `Errors` varchar(200) DEFAULT NULL,
  `Belegdaten` mediumtext,
  `DepotNrAD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `DepotNrAbD` int(11) DEFAULT NULL,
  `BetragAD` double DEFAULT NULL,
  `BetragLD` double DEFAULT NULL,
  `BetragAbD` double DEFAULT NULL,
  `Bem` mediumtext,
  `BetraegeOK` int(11) DEFAULT NULL,
  `ASDStatus` varchar(15) DEFAULT NULL,
  `Zustellfenster` varchar(15) DEFAULT NULL,
  `Zustelldatum` varchar(15) DEFAULT NULL,
  `lands` varchar(50) DEFAULT NULL,
  `landd` varchar(50) DEFAULT NULL,
  `rlkz` varchar(50) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `GewEff` double DEFAULT NULL,
  `GewBer` double DEFAULT NULL,
  `Verladedatum` datetime DEFAULT NULL,
  `ZoneS` char(1) DEFAULT NULL,
  `ZoneD` char(1) DEFAULT NULL,
  `PLZS` varchar(6) DEFAULT NULL,
  `PLZD` varchar(6) DEFAULT NULL,
  `ColliesGesamt` int(11) DEFAULT NULL,
  `ServicesAD` varchar(250) DEFAULT NULL,
  `ServicesAbD` varchar(250) DEFAULT NULL,
  `ServicesLD` varchar(250) DEFAULT NULL,
  `rgnrAD` int(10) unsigned DEFAULT NULL,
  `rgnrAbD` int(10) unsigned DEFAULT NULL,
  `rgnrLD` int(10) unsigned DEFAULT NULL,
  `istInternational` int(10) unsigned DEFAULT NULL,
  `BetragADMwst` double DEFAULT NULL,
  `BetragAbDMwst` double DEFAULT NULL,
  `BetragLDMwst` double DEFAULT NULL,
  `PCtr` varchar(10) DEFAULT NULL,
  `rgnrADP` int(10) unsigned DEFAULT NULL,
  `rgnrAbDP` int(10) unsigned DEFAULT NULL,
  `rgnrLDP` int(10) unsigned DEFAULT NULL,
  `BetragTAD` double DEFAULT NULL,
  `BetragTAbD` double DEFAULT NULL,
  `BetragTLD` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `masterid` (`MasterID`),
  KEY `belegnummer` (`Belegnummer`),
  KEY `rgnrAD` (`rgnrAD`),
  KEY `rgnrAbD` (`rgnrAbD`),
  KEY `rgnrLD` (`rgnrLD`),
  KEY `istInternational` (`istInternational`),
  KEY `DepotNrAD` (`DepotNrAD`),
  KEY `DepotNrLD` (`DepotNrLD`),
  KEY `DepotNrAbD` (`DepotNrAbD`),
  KEY `OrderID` (`OrderID`),
  KEY `rgnrADP` (`rgnrADP`),
  KEY `rgnrAbDP` (`rgnrAbDP`),
  KEY `rgnrLDP` (`rgnrLDP`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claresults`
--

DROP TABLE IF EXISTS `claresults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claresults` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `LaufID` int(11) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `OderID` double DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `PLAID` int(11) DEFAULT NULL,
  `ProzessID` int(11) DEFAULT NULL,
  `DepotNr` int(11) DEFAULT NULL,
  `Betrag` double DEFAULT NULL,
  `MwStSatz` double DEFAULT NULL,
  `bIntern` smallint(6) DEFAULT NULL,
  `Text` mediumtext,
  `PLAName` varchar(200) DEFAULT NULL,
  `BerechnungAn` varchar(20) DEFAULT NULL,
  `LeistungsGRP` int(11) DEFAULT NULL,
  `UIDDepot` varchar(10) DEFAULT NULL,
  `RGNr` double DEFAULT NULL,
  `Berechnungandepot` int(11) DEFAULT NULL,
  `RGGutschrift` char(1) DEFAULT NULL,
  `LeistungsGRPSAP` varchar(5) DEFAULT NULL,
  `TaxCodeSAP` varchar(5) DEFAULT NULL,
  `TaxCodeConverter` varchar(5) DEFAULT NULL,
  `RGGutschriftM` int(11) DEFAULT NULL,
  `Sendungslauf` varchar(15) DEFAULT NULL,
  `Leistungsmonat` datetime DEFAULT NULL,
  `MwstBetrag` double DEFAULT NULL,
  `tmpCheck` int(11) DEFAULT NULL,
  `TRZ` double DEFAULT NULL,
  `MasterRun` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `LaufID` (`LaufID`),
  KEY `LeistungsGRP` (`LeistungsGRP`),
  KEY `Betrag` (`Betrag`),
  KEY `RGGutschriftM` (`RGGutschriftM`),
  KEY `RGGutschrift` (`RGGutschrift`),
  KEY `Berechnungandepot` (`Berechnungandepot`),
  KEY `BerechnungAn` (`BerechnungAn`),
  KEY `MasterRun` (`MasterRun`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `claresummen`
--

DROP TABLE IF EXISTS `claresummen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claresummen` (
  `ReNr` int(11) NOT NULL DEFAULT '0',
  `DepotNr` int(11) DEFAULT NULL,
  `WKZ` char(3) DEFAULT NULL,
  `nPos` int(11) DEFAULT NULL,
  `RName1` varchar(255) DEFAULT NULL,
  `RName2` varchar(255) DEFAULT NULL,
  `RStrasse` varchar(50) DEFAULT NULL,
  `RStrNr` varchar(10) DEFAULT NULL,
  `ROrt` varchar(50) DEFAULT NULL,
  `RPLZ` varchar(10) DEFAULT NULL,
  `RLKZ` varchar(10) DEFAULT NULL,
  `SAPDebitorennummer` varchar(10) DEFAULT NULL,
  `SAPKreditoennummer` varchar(10) DEFAULT NULL,
  `Belegdatum` datetime DEFAULT NULL,
  `Leistungsmonat` varchar(7) DEFAULT NULL,
  `Art` varchar(10) DEFAULT NULL,
  `RunID` int(10) unsigned DEFAULT NULL,
  `FremtUID` varchar(45) DEFAULT NULL,
  `Anrede` varchar(100) DEFAULT NULL,
  `Berechnungvon` datetime DEFAULT NULL,
  `BerechnungBis` datetime DEFAULT NULL,
  `nettoG` double DEFAULT NULL,
  `nettoGFrei` double DEFAULT NULL,
  `nettoR` double DEFAULT NULL,
  `nettoRFrei` double DEFAULT NULL,
  `brutto` double DEFAULT NULL,
  `mwst` double DEFAULT NULL,
  `mwstG` double DEFAULT NULL,
  `mwstR` double DEFAULT NULL,
  `kontokorrentnr` double DEFAULT NULL,
  `Gedruckt` int(11) DEFAULT NULL,
  `zahlungsbedingungen` varchar(200) DEFAULT NULL,
  `isStorno` int(11) NOT NULL DEFAULT '0',
  `Bemerkung` varchar(200) DEFAULT NULL,
  `Gebucht` int(11) DEFAULT NULL,
  `isRechnung` int(11) DEFAULT NULL,
  `ZahlungsbedingungenShl` varchar(10) DEFAULT NULL,
  `Druckdatum` datetime DEFAULT NULL,
  `SAPHDText` varchar(50) DEFAULT NULL,
  `PosType` varchar(5) DEFAULT NULL,
  `RGTyp` varchar(5) DEFAULT NULL,
  `ScRunID` int(10) unsigned DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `VofiFak` double DEFAULT NULL,
  `TSZProz` double DEFAULT NULL,
  `Centdiffs` double DEFAULT NULL,
  `newum` varchar(5) DEFAULT NULL,
  `CODBank` varchar(20) DEFAULT NULL,
  `BuDat` datetime DEFAULT NULL,
  `BelPrefix` varchar(10) DEFAULT NULL,
  `isSAPStorno` int(11) DEFAULT NULL,
  PRIMARY KEY (`ReNr`) USING BTREE,
  KEY `Gedruckt` (`Gedruckt`),
  KEY `RunID` (`RunID`),
  KEY `DepotNr` (`DepotNr`),
  KEY `Gebucht` (`Gebucht`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clasapaccounts`
--

DROP TABLE IF EXISTS `clasapaccounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clasapaccounts` (
  `Nr` int(11) NOT NULL DEFAULT '0',
  `Prozess` varchar(255) DEFAULT NULL,
  `Konto` int(11) DEFAULT NULL,
  `ProfitCenter` int(11) DEFAULT NULL,
  `KST` int(11) DEFAULT NULL,
  `Erkennung` varchar(255) DEFAULT NULL,
  `BText` varchar(255) DEFAULT NULL,
  `Mengenkz` varchar(255) DEFAULT NULL,
  `F9` varchar(255) DEFAULT NULL,
  `F10` varchar(255) DEFAULT NULL,
  `F11` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Nr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clasapprofitcenter`
--

DROP TABLE IF EXISTS `clasapprofitcenter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clasapprofitcenter` (
  `id` int(11) NOT NULL DEFAULT '0',
  `Company` varchar(255) DEFAULT NULL,
  `Cost_Center` int(11) DEFAULT NULL,
  `CCDescription` varchar(255) DEFAULT NULL,
  `CCDescriptioLongtext` varchar(255) DEFAULT NULL,
  `Valid` datetime DEFAULT NULL,
  `ValidUntil` datetime DEFAULT NULL,
  `Currency` char(3) DEFAULT NULL,
  `CompanyCode` varchar(4) DEFAULT NULL,
  `PrCtr` int(11) DEFAULT NULL,
  `PCDescription` varchar(255) DEFAULT NULL,
  `PCDescriptionLongtext` varchar(255) DEFAULT NULL,
  `Responsibility` varchar(255) DEFAULT NULL,
  `Region` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clasaptaxcodes`
--

DROP TABLE IF EXISTS `clasaptaxcodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clasaptaxcodes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `taxcode` varchar(5) NOT NULL DEFAULT '',
  `deb_ced` varchar(15) NOT NULL DEFAULT '',
  `leistung` varchar(15) DEFAULT '',
  `Art` varchar(15) DEFAULT '',
  `startlkz` int(11) NOT NULL DEFAULT '0',
  `ziellkz` int(11) NOT NULL DEFAULT '0',
  `istunternehmer` int(11) NOT NULL DEFAULT '0',
  `unternehmersitz` int(11) NOT NULL DEFAULT '0',
  `istmwst` int(11) NOT NULL DEFAULT '0',
  `istverbunden` int(11) NOT NULL DEFAULT '0',
  `taxcodeconverter` varchar(5) NOT NULL DEFAULT '',
  `Bemerkung` varchar(250) DEFAULT NULL,
  `StartZielLkzGleich` int(11) NOT NULL DEFAULT '0',
  `TaxCodeTextID` int(11) DEFAULT NULL,
  `StartUntGleich` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=159 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `erkontrolle`
--

DROP TABLE IF EXISTS `erkontrolle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erkontrolle` (
  `ID` double NOT NULL DEFAULT '0',
  `tTimestamp` datetime DEFAULT NULL,
  `Benutzer` varchar(45) DEFAULT NULL,
  `RBetragNetto` double DEFAULT NULL,
  `Auftragnehmer` varchar(20) DEFAULT NULL,
  `ReNr` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hubdepotliste`
--

DROP TABLE IF EXISTS `hubdepotliste`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hubdepotliste` (
  `DepotNr` int(11) NOT NULL DEFAULT '0',
  `Firma1` varchar(50) DEFAULT NULL,
  `LKZ` char(2) NOT NULL DEFAULT 'DE',
  `PLZ` varchar(8) DEFAULT NULL,
  `Ort` varchar(50) DEFAULT NULL,
  `Strasse` varchar(50) DEFAULT NULL,
  `StrNr` varchar(10) DEFAULT NULL,
  `UmschlagLong` int(11) DEFAULT NULL,
  `UmschlagLat` int(11) DEFAULT NULL,
  `GeoMode` varchar(5) DEFAULT NULL,
  `AdrPos` varchar(200) DEFAULT NULL,
  `ParkLong` int(11) DEFAULT NULL,
  `ParkLat` int(11) DEFAULT NULL,
  `Dist` double DEFAULT NULL,
  `LKZLiefer` varchar(2) DEFAULT NULL,
  `PLZLiefer` varchar(8) DEFAULT NULL,
  `OrtLiefer` varchar(50) DEFAULT NULL,
  `StrasseLiefer` varchar(50) DEFAULT NULL,
  `StrNrLiefer` varchar(5) DEFAULT NULL,
  `LieferLong` int(11) DEFAULT NULL,
  `LieferLat` int(11) DEFAULT NULL,
  `GeoStatus` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`DepotNr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hubdistances`
--

DROP TABLE IF EXISTS `hubdistances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hubdistances` (
  `idhubdistances` int(11) NOT NULL AUTO_INCREMENT,
  `depotnra` int(11) DEFAULT NULL,
  `depotnrb` int(11) DEFAULT NULL,
  `distance` double NOT NULL DEFAULT '0',
  `ntime` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idhubdistances`)
) ENGINE=MyISAM AUTO_INCREMENT=39844 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hublinienruu`
--

DROP TABLE IF EXISTS `hublinienruu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hublinienruu` (
  `ID` double NOT NULL DEFAULT '0',
  `Bemerkungen` longtext,
  `tTimestamp` datetime DEFAULT NULL,
  `abfDatum` datetime DEFAULT NULL,
  `Benutzer` varchar(45) DEFAULT NULL,
  `LinienNr` double DEFAULT NULL,
  `vKosten` double DEFAULT NULL,
  `Auftragnehmer` varchar(200) DEFAULT NULL,
  `KW` double DEFAULT NULL,
  `ReNr` varchar(45) DEFAULT NULL,
  `Kreuzreferenz` double DEFAULT NULL,
  `KFZArt` varchar(45) DEFAULT NULL,
  `zulGesGew` double DEFAULT NULL,
  `MasseLBH` varchar(45) DEFAULT NULL,
  `KFZHubEingang` datetime DEFAULT NULL,
  `KFZHubAusgang` datetime DEFAULT NULL,
  `ScannerNr` double DEFAULT NULL,
  `ScannerHubEingang` datetime DEFAULT NULL,
  `ScannerHubAusgang` datetime DEFAULT NULL,
  `KFZKennzeichen` varchar(45) DEFAULT NULL,
  `dstart` varchar(10) DEFAULT NULL,
  `dstat1` varchar(10) DEFAULT NULL,
  `dstat2` varchar(10) DEFAULT NULL,
  `dstat3` varchar(10) DEFAULT NULL,
  `dstat4` varchar(10) DEFAULT NULL,
  `dstat5` varchar(10) DEFAULT NULL,
  `dstat6` varchar(10) DEFAULT NULL,
  `dstat7` varchar(10) DEFAULT NULL,
  `dstat8` varchar(10) DEFAULT NULL,
  `dstat9` varchar(10) DEFAULT NULL,
  `dstat10` varchar(10) DEFAULT NULL,
  `dstat11` varchar(10) DEFAULT NULL,
  `dstat12` varchar(10) DEFAULT NULL,
  `dstat13` varchar(10) DEFAULT NULL,
  `dstat14` varchar(10) DEFAULT NULL,
  `dstat15` varchar(10) DEFAULT NULL,
  `dende` varchar(10) DEFAULT NULL,
  `dstorno` smallint(6) NOT NULL DEFAULT '0',
  `LinienArt` smallint(6) DEFAULT NULL,
  `dAktiv` smallint(6) DEFAULT NULL,
  `gueltigAb` datetime DEFAULT NULL,
  `dRundlauf` smallint(6) DEFAULT NULL,
  `KFZKennIn` varchar(45) DEFAULT NULL,
  `KFZKennOut` varchar(45) DEFAULT NULL,
  `KarteNr` double DEFAULT NULL,
  `zh` varchar(1) DEFAULT NULL,
  `ScannerNrOut` double DEFAULT NULL,
  `AuftragnehmerID` int(11) DEFAULT NULL,
  `dKM` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hublinienvertrag`
--

DROP TABLE IF EXISTS `hublinienvertrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hublinienvertrag` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `LinienNr` int(11) DEFAULT NULL,
  `txt` text,
  `Vol` double DEFAULT NULL,
  `Nutzlast` double DEFAULT NULL,
  `Betrag` double DEFAULT NULL,
  `GueltigAb` datetime DEFAULT NULL,
  `AuftragnehmerID` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `GueltigBis` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=845 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hublinienvertragdepots`
--

DROP TABLE IF EXISTS `hublinienvertragdepots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hublinienvertragdepots` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Linienvertragid` int(10) unsigned NOT NULL,
  `Depot` varchar(10) NOT NULL,
  `Abzulauf` int(11) NOT NULL,
  `Ankunft` datetime NOT NULL,
  `Abfahrt` datetime NOT NULL,
  `LinienNR` int(10) unsigned NOT NULL,
  `Adresse` varchar(200) NOT NULL,
  `Vertrag` varchar(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hublinienvertragzusatz`
--

DROP TABLE IF EXISTS `hublinienvertragzusatz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hublinienvertragzusatz` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `LinienvertragID` int(10) unsigned NOT NULL,
  `Typ` varchar(250) NOT NULL,
  `Abzu` int(11) NOT NULL,
  `Betrag` double NOT NULL,
  `Depot` varchar(10) NOT NULL,
  `Ankunft` datetime DEFAULT NULL,
  `Abfahrt` datetime DEFAULT NULL,
  `Adresse` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=7952 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hubmsg`
--

DROP TABLE IF EXISTS `hubmsg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hubmsg` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `linie` int(11) DEFAULT NULL,
  `adr` int(11) DEFAULT NULL,
  `lat` int(11) DEFAULT NULL,
  `long` int(11) DEFAULT NULL,
  `msg` text,
  `msgcode` varchar(10) DEFAULT NULL,
  `msgtime` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `verladedatum` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=26615 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hubposdata`
--

DROP TABLE IF EXISTS `hubposdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hubposdata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `msgid` int(10) unsigned DEFAULT NULL,
  `msgtime` datetime DEFAULT NULL,
  `objnr` varchar(45) DEFAULT NULL,
  `postext` varchar(250) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `poslat` int(11) DEFAULT NULL,
  `poslong` int(11) DEFAULT NULL,
  `msgtext` varchar(250) DEFAULT NULL,
  `adr` int(11) DEFAULT NULL,
  `Speed` int(11) DEFAULT NULL,
  `DepotAdrNr` int(11) DEFAULT NULL,
  `NavTargetAdrNr` varchar(10) DEFAULT NULL,
  `NavDest` varchar(200) DEFAULT NULL,
  `NavDestKm` int(11) DEFAULT NULL,
  `NavDestETA` datetime DEFAULT NULL,
  `Evttype` varchar(1) DEFAULT NULL,
  `liniennr` int(11) DEFAULT NULL,
  `abzulauf` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `timestamp` (`timestamp`)
) ENGINE=MyISAM AUTO_INCREMENT=70953688 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `hubsendungen`
--

DROP TABLE IF EXISTS `hubsendungen`;
/*!50001 DROP VIEW IF EXISTS `hubsendungen`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `hubsendungen` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `Ladelisten_Nummer` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `OrderIDy` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `ColliesGesamty` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `tblauftrag_Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `frei3` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `verladelinie` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `BagIDNrC` tinyint NOT NULL,
  `BagBelegNrC` tinyint NOT NULL,
  `BagBelegNrAbC` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `hubsendungen20`
--

DROP TABLE IF EXISTS `hubsendungen20`;
/*!50001 DROP VIEW IF EXISTS `hubsendungen20`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `hubsendungen20` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `Ladelisten_Nummer` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `OrderIDy` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `ColliesGesamty` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `tblauftrag_Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `frei3` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `verladelinie` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `hubsendungenakt`
--

DROP TABLE IF EXISTS `hubsendungenakt`;
/*!50001 DROP VIEW IF EXISTS `hubsendungenakt`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `hubsendungenakt` (
  `kz_transportart` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `BagIDNrA` tinyint NOT NULL,
  `ClearingArtMaster` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `LadelistennummerD` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `verladelinie` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `BagIDNrC` tinyint NOT NULL,
  `BagBelegNrC` tinyint NOT NULL,
  `BagBelegNrAbC` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `hubsscount`
--

DROP TABLE IF EXISTS `hubsscount`;
/*!50001 DROP VIEW IF EXISTS `hubsscount`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `hubsscount` (
  `ES` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `verladelinie` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `hubstatus`
--

DROP TABLE IF EXISTS `hubstatus`;
/*!50001 DROP VIEW IF EXISTS `hubstatus`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `hubstatus` (
  `KZ_Statuserzeuger` tinyint NOT NULL,
  `Packstuecknummer` tinyint NOT NULL,
  `Datum` tinyint NOT NULL,
  `Zeit` tinyint NOT NULL,
  `KZ_Status` tinyint NOT NULL,
  `Fehlercode` tinyint NOT NULL,
  `Erzeugerstation` tinyint NOT NULL,
  `Text` tinyint NOT NULL,
  `Wartezeit` tinyint NOT NULL,
  `Exportstation` tinyint NOT NULL,
  `Frei1` tinyint NOT NULL,
  `Infotext` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `uebertragen` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `Timestamp2` tinyint NOT NULL,
  `Zaehler` tinyint NOT NULL,
  `UploadStatus` tinyint NOT NULL,
  `SendStatus` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `hubtmp`
--

DROP TABLE IF EXISTS `hubtmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hubtmp` (
  `ClientID` int(11) NOT NULL DEFAULT '0',
  `LinienNr` int(11) NOT NULL DEFAULT '0',
  `DiffLinie` varchar(100) DEFAULT NULL,
  `DiffHub` varchar(100) DEFAULT NULL,
  `DiffLinieAnzahl` int(11) DEFAULT NULL,
  `DiffHubAnzahl` int(11) DEFAULT NULL,
  `Scanner_Status` int(11) DEFAULT NULL,
  `Scanner_Abfahrtsanforderung` int(11) DEFAULT NULL,
  `LinienID` int(11) DEFAULT NULL,
  `Scanner_Abfahrtsbemerkung` varchar(100) DEFAULT NULL,
  `Scanner_LadelisteUnterschrift` varchar(200) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ClientID`,`LinienNr`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `lag_view_ac`
--

DROP TABLE IF EXISTS `lag_view_ac`;
/*!50001 DROP VIEW IF EXISTS `lag_view_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `lag_view_ac` (
  `OrderID` tinyint NOT NULL,
  `EBOrderID` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `EXAuftragsIDRef` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `frei2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtLagereingang` tinyint NOT NULL,
  `dtLagerausgang` tinyint NOT NULL,
  `Verladelinie` tinyint NOT NULL,
  `Auslieferdatum2` tinyint NOT NULL,
  `Lagerplatz` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `lag_view_ac_total`
--

DROP TABLE IF EXISTS `lag_view_ac_total`;
/*!50001 DROP VIEW IF EXISTS `lag_view_ac_total`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `lag_view_ac_total` (
  `OrderID` tinyint NOT NULL,
  `EBOrderID` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `EXAuftragsIDRef` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `frei2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtLagereingang` tinyint NOT NULL,
  `dtLagerausgang` tinyint NOT NULL,
  `Verladelinie` tinyint NOT NULL,
  `Auslieferdatum2` tinyint NOT NULL,
  `Lagerplatz` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `lag_view_c`
--

DROP TABLE IF EXISTS `lag_view_c`;
/*!50001 DROP VIEW IF EXISTS `lag_view_c`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `lag_view_c` (
  `Verladedatum` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Lagerplatz` tinyint NOT NULL,
  `dtLagereingang` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `Empfaenger2` tinyint NOT NULL,
  `Verpackungsart` tinyint NOT NULL,
  `Verladelinie` tinyint NOT NULL,
  `Auslieferdatum2` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `lagkontrollscan`
--

DROP TABLE IF EXISTS `lagkontrollscan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lagkontrollscan` (
  `LagID` double NOT NULL DEFAULT '0',
  `BelegNr` double DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `dtLagereingang` datetime DEFAULT NULL,
  `dtLagerausgang` datetime DEFAULT NULL,
  `KontrollscanL` double DEFAULT NULL,
  `KontrollscanD` datetime DEFAULT NULL,
  `ClientID` double DEFAULT NULL,
  PRIMARY KEY (`LagID`),
  KEY `Lagerplatz` (`Lagerplatz`),
  KEY `KontrollscanL` (`KontrollscanL`),
  KEY `KontrollscanD` (`KontrollscanD`),
  KEY `BelegNr` (`BelegNr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `leo_clearingstatus_a`
--

DROP TABLE IF EXISTS `leo_clearingstatus_a`;
/*!50001 DROP VIEW IF EXISTS `leo_clearingstatus_a`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_clearingstatus_a` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_clearingstatus_ac`
--

DROP TABLE IF EXISTS `leo_clearingstatus_ac`;
/*!50001 DROP VIEW IF EXISTS `leo_clearingstatus_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_clearingstatus_ac` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_clearingstatus_lj_ac`
--

DROP TABLE IF EXISTS `leo_clearingstatus_lj_ac`;
/*!50001 DROP VIEW IF EXISTS `leo_clearingstatus_lj_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_clearingstatus_lj_ac` (
  `FirmaD` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `referenz` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_dispo_a`
--

DROP TABLE IF EXISTS `leo_dispo_a`;
/*!50001 DROP VIEW IF EXISTS `leo_dispo_a`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_dispo_a` (
  `OrderID` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrbev` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `DatumNN` tinyint NOT NULL,
  `erhaltenNN` tinyint NOT NULL,
  `Feiertag_1` tinyint NOT NULL,
  `FeiertagShlD` tinyint NOT NULL,
  `FeiertagShlS` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Satzart` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `Referenz2` tinyint NOT NULL,
  `Frei` tinyint NOT NULL,
  `Adr_Nr_Absender` tinyint NOT NULL,
  `Adr_Nr_Empfaenger` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Lieferdatum` tinyint NOT NULL,
  `Lieferzeit_von` tinyint NOT NULL,
  `Lieferzeit_bis` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `KZServiceLeo` tinyint NOT NULL,
  `Sendungsstatus` tinyint NOT NULL,
  `Ausliefertour` tinyint NOT NULL,
  `KZ_erweitert` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Frei5` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `Besteller_Name` tinyint NOT NULL,
  `Ladelisten_Nummer` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `KZ_Zusatz` tinyint NOT NULL,
  `CR` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `d` tinyint NOT NULL,
  `a` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `RechnungsNr` tinyint NOT NULL,
  `Satzart_ava` tinyint NOT NULL,
  `FirmaD3X` tinyint NOT NULL,
  `LandDX` tinyint NOT NULL,
  `PLZDX` tinyint NOT NULL,
  `OrtDX` tinyint NOT NULL,
  `StrasseDX` tinyint NOT NULL,
  `StrNrDX` tinyint NOT NULL,
  `frueheste_zustellzeit` tinyint NOT NULL,
  `Satzartp` tinyint NOT NULL,
  `PZA_bag` tinyint NOT NULL,
  `Belegnummer_akt` tinyint NOT NULL,
  `UploadStatus` tinyint NOT NULL,
  `SendStatus` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `OrderID_X` tinyint NOT NULL,
  `PZAImageZIP` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `RueckDate` tinyint NOT NULL,
  `ClearingArtMaster` tinyint NOT NULL,
  `ZoneS` tinyint NOT NULL,
  `Locking` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `ROrderID` tinyint NOT NULL,
  `EBOrderID` tinyint NOT NULL,
  `EXAuftragsIDRef` tinyint NOT NULL,
  `EBRueckGrund` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_dispo_ac`
--

DROP TABLE IF EXISTS `leo_dispo_ac`;
/*!50001 DROP VIEW IF EXISTS `leo_dispo_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_dispo_ac` (
  `OrderID` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrbev` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `DatumNN` tinyint NOT NULL,
  `erhaltenNN` tinyint NOT NULL,
  `Feiertag_1` tinyint NOT NULL,
  `FeiertagShlD` tinyint NOT NULL,
  `FeiertagShlS` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Satzart` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `Referenz2` tinyint NOT NULL,
  `Frei` tinyint NOT NULL,
  `Adr_Nr_Absender` tinyint NOT NULL,
  `Adr_Nr_Empfaenger` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Lieferdatum` tinyint NOT NULL,
  `Lieferzeit_von` tinyint NOT NULL,
  `Lieferzeit_bis` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `KZServiceLeo` tinyint NOT NULL,
  `Sendungsstatus` tinyint NOT NULL,
  `Ausliefertour` tinyint NOT NULL,
  `KZ_erweitert` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Frei5` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `Besteller_Name` tinyint NOT NULL,
  `Ladelisten_Nummer` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `KZ_Zusatz` tinyint NOT NULL,
  `CR` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `d` tinyint NOT NULL,
  `a` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `RechnungsNr` tinyint NOT NULL,
  `Satzart_ava` tinyint NOT NULL,
  `FirmaD3X` tinyint NOT NULL,
  `LandDX` tinyint NOT NULL,
  `PLZDX` tinyint NOT NULL,
  `OrtDX` tinyint NOT NULL,
  `StrasseDX` tinyint NOT NULL,
  `StrNrDX` tinyint NOT NULL,
  `frueheste_zustellzeit` tinyint NOT NULL,
  `Satzartp` tinyint NOT NULL,
  `PZA_bag` tinyint NOT NULL,
  `Belegnummer_akt` tinyint NOT NULL,
  `UploadStatus` tinyint NOT NULL,
  `SendStatus` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `OrderID_X` tinyint NOT NULL,
  `PZAImageZIP` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `RueckDate` tinyint NOT NULL,
  `ClearingArtMaster` tinyint NOT NULL,
  `ZoneS` tinyint NOT NULL,
  `Locking` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `ROrderID` tinyint NOT NULL,
  `EBOrderID` tinyint NOT NULL,
  `EXAuftragsIDRef` tinyint NOT NULL,
  `EBRueckGrund` tinyint NOT NULL,
  `YOrderid` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `i_scan` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_erfassung_a`
--

DROP TABLE IF EXISTS `leo_erfassung_a`;
/*!50001 DROP VIEW IF EXISTS `leo_erfassung_a`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_erfassung_a` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `OrderIDy` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `ColliesGesamty` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_erfassung_ac`
--

DROP TABLE IF EXISTS `leo_erfassung_ac`;
/*!50001 DROP VIEW IF EXISTS `leo_erfassung_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_erfassung_ac` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `OrderIDy` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `ColliesGesamty` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_fr_repl_a`
--

DROP TABLE IF EXISTS `leo_fr_repl_a`;
/*!50001 DROP VIEW IF EXISTS `leo_fr_repl_a`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_fr_repl_a` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `Referenz` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_fr_repl_ac`
--

DROP TABLE IF EXISTS `leo_fr_repl_ac`;
/*!50001 DROP VIEW IF EXISTS `leo_fr_repl_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_fr_repl_ac` (
  `FirmaD` tinyint NOT NULL,
  `depotnred` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `Ausliefertour` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingang2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Timestamp22` tinyint NOT NULL,
  `i_scan` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `product_spec` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_fr_repl_lj_ac`
--

DROP TABLE IF EXISTS `leo_fr_repl_lj_ac`;
/*!50001 DROP VIEW IF EXISTS `leo_fr_repl_lj_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_fr_repl_lj_ac` (
  `FirmaD` tinyint NOT NULL,
  `depotnred` tinyint NOT NULL,
  `referenz` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `Ausliefertour` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingang2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Timestamp22` tinyint NOT NULL,
  `i_scan` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_ladelistenscan_ac`
--

DROP TABLE IF EXISTS `leo_ladelistenscan_ac`;
/*!50001 DROP VIEW IF EXISTS `leo_ladelistenscan_ac`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_ladelistenscan_ac` (
  `FirmaD` tinyint NOT NULL,
  `depotnred` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `Ausliefertour` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingang2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Timestamp22` tinyint NOT NULL,
  `i_scan` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `LadelistennummerD` tinyint NOT NULL,
  `Beladelinie` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_ladelistenscan_ac_20120626`
--

DROP TABLE IF EXISTS `leo_ladelistenscan_ac_20120626`;
/*!50001 DROP VIEW IF EXISTS `leo_ladelistenscan_ac_20120626`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_ladelistenscan_ac_20120626` (
  `FirmaD` tinyint NOT NULL,
  `depotnred` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaS2` tinyint NOT NULL,
  `FirmaS3` tinyint NOT NULL,
  `Ausliefertour` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingang2` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `Timestamp22` tinyint NOT NULL,
  `i_scan` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `LadelistennummerD` tinyint NOT NULL,
  `Beladelinie` tinyint NOT NULL,
  `RUP` tinyint NOT NULL,
  `RUP_org` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_view_rollkartenscan`
--

DROP TABLE IF EXISTS `leo_view_rollkartenscan`;
/*!50001 DROP VIEW IF EXISTS `leo_view_rollkartenscan`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_view_rollkartenscan` (
  `OrderID` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `Timestamp22` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `frei4_` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `rKM` tinyint NOT NULL,
  `rkLR` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_view_rpt_rollkarte`
--

DROP TABLE IF EXISTS `leo_view_rpt_rollkarte`;
/*!50001 DROP VIEW IF EXISTS `leo_view_rpt_rollkarte`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_view_rpt_rollkarte` (
  `OrderID` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `DepotNrED` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `Timestamp22` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `frei4` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `rKM` tinyint NOT NULL,
  `rkLR` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `leo_view_rueckerfassung`
--

DROP TABLE IF EXISTS `leo_view_rueckerfassung`;
/*!50001 DROP VIEW IF EXISTS `leo_view_rueckerfassung`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `leo_view_rueckerfassung` (
  `OrderID` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `Frei4_` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `FirmaD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Frei1` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei3` tinyint NOT NULL,
  `frei4` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `AuslieferDatum2` tinyint NOT NULL,
  `AuslieferZeit2` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `Empfaenger2` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `linienverspaetung`
--

DROP TABLE IF EXISTS `linienverspaetung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linienverspaetung` (
  `id` double NOT NULL AUTO_INCREMENT,
  `direction` varchar(1) DEFAULT NULL,
  `linie` double DEFAULT NULL,
  `t1` datetime DEFAULT NULL,
  `t2` datetime DEFAULT NULL,
  `nok` varchar(3) DEFAULT NULL,
  `tdiff` double DEFAULT NULL,
  `diffid` double DEFAULT NULL,
  `hub` double DEFAULT NULL,
  `mtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mtime` (`mtime`),
  KEY `direction` (`direction`),
  KEY `tdiff` (`tdiff`)
) ENGINE=MyISAM AUTO_INCREMENT=1751977 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scaerror`
--

DROP TABLE IF EXISTS `scaerror`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scaerror` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `errorNr` int(10) unsigned DEFAULT NULL,
  `errorText` text,
  `errorTs` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sendstatus` int(10) unsigned DEFAULT NULL,
  `uidLogin` int(10) DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sendstatus` (`sendstatus`),
  KEY `uidLogin` (`uidLogin`),
  KEY `errorts` (`errorTs`)
) ENGINE=MyISAM AUTO_INCREMENT=124655751 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scaimages`
--

DROP TABLE IF EXISTS `scaimages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scaimages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `imgPath` varchar(100) DEFAULT NULL,
  `linie` int(11) DEFAULT NULL,
  `colliebelegnummer` double DEFAULT NULL,
  `imgtyp` int(11) DEFAULT NULL,
  `bem` varchar(255) DEFAULT NULL,
  `poslat` double DEFAULT NULL,
  `poslong` double DEFAULT NULL,
  `imageTs` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `uidLogin` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=39795 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scalog`
--

DROP TABLE IF EXISTS `scalog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scalog` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sca_Id` varchar(45) DEFAULT NULL,
  `msg` varchar(250) DEFAULT NULL,
  `msgLoc` varchar(100) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sca_Id` (`sca_Id`)
) ENGINE=MyISAM AUTO_INCREMENT=2472 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scalogins`
--

DROP TABLE IF EXISTS `scalogins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scalogins` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `LastLoginId` int(10) unsigned NOT NULL,
  `LastLoginDate` datetime NOT NULL,
  `LinienNr` int(10) unsigned NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sca_id` varchar(45) DEFAULT NULL,
  `LastPwd` varchar(100) DEFAULT NULL,
  `gueltig` smallint(6) DEFAULT NULL,
  `sca_version` varchar(45) DEFAULT NULL,
  `checkUpdate` smallint(6) DEFAULT NULL,
  `updateRequired` smallint(6) DEFAULT NULL,
  `AuftragnehmerId` int(11) DEFAULT NULL,
  `sca_name` varchar(45) DEFAULT NULL,
  `sca_typ` int(11) DEFAULT NULL,
  `checkPackIn` int(11) DEFAULT NULL,
  `checkMain` int(11) DEFAULT NULL,
  `checkPackOut` int(11) DEFAULT NULL,
  `LinienNrOut` int(10) DEFAULT NULL,
  `tourinout` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sca_id_UNIQUE` (`sca_id`),
  KEY `loginId` (`LastLoginId`),
  KEY `gueltig` (`gueltig`),
  KEY `liniennr` (`LinienNr`),
  KEY `checkUpdate` (`checkUpdate`),
  KEY `updateRequired` (`updateRequired`)
) ENGINE=MyISAM AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scamsg`
--

DROP TABLE IF EXISTS `scamsg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scamsg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uidLogin` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `msg` varchar(255) DEFAULT NULL,
  `sendstatus` int(11) DEFAULT NULL,
  `msgid` int(11) DEFAULT NULL,
  `poslong` double DEFAULT NULL,
  `poslat` double DEFAULT NULL,
  `dtmsg` datetime DEFAULT NULL,
  `dtpos` datetime DEFAULT NULL,
  `linie` int(11) DEFAULT NULL,
  `tourinout` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scapackets`
--

DROP TABLE IF EXISTS `scapackets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scapackets` (
  `colliebelegnr` double NOT NULL DEFAULT '0',
  `depotnrabd` int(11) DEFAULT NULL,
  `depotnrld` int(11) DEFAULT NULL,
  `gewichtreal` double DEFAULT NULL,
  `gewichtlbh` double DEFAULT NULL,
  `beladelinie` int(11) DEFAULT NULL,
  `FirmaD` varchar(50) DEFAULT NULL,
  `LandD` char(3) DEFAULT NULL,
  `PLZD` varchar(10) DEFAULT NULL,
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `tourinout` int(11) NOT NULL DEFAULT '0',
  `Verladedatum` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dtausgangDepot2` datetime DEFAULT NULL,
  `LadelistenrAbD` double DEFAULT NULL,
  `Orderid` double NOT NULL,
  `lockflag` int(11) DEFAULT NULL,
  `isInBag` int(11) DEFAULT NULL,
  `ScaIdAct` varchar(250) DEFAULT NULL,
  `scDepot` int(11) DEFAULT NULL,
  `scScanner` int(11) DEFAULT NULL,
  PRIMARY KEY (`colliebelegnr`,`tourinout`) USING BTREE,
  KEY `verladelinie` (`Verladelinie`),
  KEY `beladelinie` (`beladelinie`),
  KEY `ScaIdAct` (`ScaIdAct`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scaposdata`
--

DROP TABLE IF EXISTS `scaposdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scaposdata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `msgid` int(10) unsigned DEFAULT NULL,
  `msgtime` datetime DEFAULT NULL,
  `objnr` varchar(45) DEFAULT NULL,
  `postext` varchar(250) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `poslat` double DEFAULT NULL,
  `poslong` double DEFAULT NULL,
  `msgtext` varchar(250) DEFAULT NULL,
  `adr` int(11) DEFAULT NULL,
  `Speed` double DEFAULT NULL,
  `DepotAdrNr` int(11) DEFAULT NULL,
  `NavTargetAdrNr` varchar(10) DEFAULT NULL,
  `NavDest` varchar(200) DEFAULT NULL,
  `NavDestKm` int(11) DEFAULT NULL,
  `NavDestETA` datetime DEFAULT NULL,
  `Evttype` varchar(1) DEFAULT NULL,
  `liniennr` int(11) DEFAULT NULL,
  `abzulauf` varchar(5) DEFAULT NULL,
  `posDt` datetime DEFAULT NULL,
  `scaName` varchar(10) DEFAULT NULL,
  `uidLogin` int(11) DEFAULT NULL,
  `sendstatus` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `uidlogin` (`uidLogin`),
  KEY `sendstatus` (`status`),
  KEY `sendstatus_` (`sendstatus`),
  KEY `timestamp` (`timestamp`),
  KEY `tsPos` (`posDt`),
  KEY `liniennr` (`liniennr`)
) ENGINE=MyISAM AUTO_INCREMENT=17629048 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scaqueue`
--

DROP TABLE IF EXISTS `scaqueue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scaqueue` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uidLogin` int(10) unsigned NOT NULL,
  `functionId` int(10) unsigned NOT NULL,
  `txtP1` varchar(100) DEFAULT NULL,
  `tsApplied` datetime DEFAULT NULL,
  `tsTransmitted` datetime DEFAULT NULL,
  `tsExecuted` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sort` int(10) unsigned NOT NULL,
  `txtRet` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `functionId` (`functionId`),
  KEY `sort` (`sort`),
  KEY `tsApplied` (`tsApplied`),
  KEY `tsTransmitted` (`tsTransmitted`),
  KEY `tsExecuted` (`tsExecuted`),
  KEY `LastLoginId` (`uidLogin`)
) ENGINE=MyISAM AUTO_INCREMENT=2122 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scascans`
--

DROP TABLE IF EXISTS `scascans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scascans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uidLogin` int(10) unsigned NOT NULL,
  `CollieNr` double unsigned NOT NULL,
  `DepotNr` int(11) DEFAULT NULL,
  `poslat` double DEFAULT NULL,
  `poslong` double DEFAULT NULL,
  `Bemerkung` varchar(200) DEFAULT NULL,
  `tsScanned` datetime DEFAULT NULL,
  `in_Out` int(11) DEFAULT NULL,
  `sendstatus` int(11) NOT NULL DEFAULT '0',
  `tsCreated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `tsUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `linieIn` int(11) DEFAULT NULL,
  `statuscode` int(11) DEFAULT NULL,
  `sca_name` varchar(10) DEFAULT NULL,
  `linieOut` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `uidLogin` (`uidLogin`),
  KEY `CollieNr` (`CollieNr`),
  KEY `sendstatus` (`sendstatus`),
  KEY `inOut` (`in_Out`),
  KEY `scaName` (`sca_name`),
  KEY `linieIn` (`linieIn`),
  KEY `linieOut` (`linieOut`),
  KEY `tsscanned` (`tsScanned`)
) ENGINE=MyISAM AUTO_INCREMENT=2693247 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `scerr_dt`
--

DROP TABLE IF EXISTS `scerr_dt`;
/*!50001 DROP VIEW IF EXISTS `scerr_dt`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `scerr_dt` (
  `id` tinyint NOT NULL,
  `errorNr` tinyint NOT NULL,
  `errorText` tinyint NOT NULL,
  `errorTs` tinyint NOT NULL,
  `timestamp` tinyint NOT NULL,
  `sendstatus` tinyint NOT NULL,
  `uidLogin` tinyint NOT NULL,
  `sender` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `scnclearfinal`
--

DROP TABLE IF EXISTS `scnclearfinal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scnclearfinal` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `vonDatum` datetime DEFAULT NULL,
  `bisDatum` datetime DEFAULT NULL,
  `DepotNr` int(11) DEFAULT NULL,
  `ReNr` int(10) unsigned DEFAULT NULL,
  `FinalisierErrs` varchar(200) DEFAULT NULL,
  `TaxCode` varchar(5) DEFAULT NULL,
  `MwstShl` int(11) DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `PID` int(11) DEFAULT NULL,
  `UID` varchar(50) DEFAULT NULL,
  `dBetrag` double DEFAULT NULL,
  `MasterID` int(11) DEFAULT NULL,
  `BtrMwst` double DEFAULT NULL,
  `SAPLa` varchar(45) DEFAULT NULL,
  `KstPctr` varchar(45) DEFAULT NULL,
  `Leistungsmonat` varchar(45) DEFAULT NULL,
  `LA` varchar(45) DEFAULT NULL,
  `Belegtext` varchar(45) DEFAULT NULL,
  `ReNrP` int(10) unsigned DEFAULT NULL,
  `MasterIDP` int(11) DEFAULT NULL,
  `nTage` int(11) DEFAULT NULL,
  `SetNr` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ReNrP` (`ReNrP`),
  KEY `ReNr` (`ReNr`),
  KEY `LA` (`LA`),
  KEY `Leistungsmonat` (`Leistungsmonat`),
  KEY `MwstShl` (`MwstShl`),
  KEY `MasterID` (`MasterID`),
  KEY `DepotNr` (`DepotNr`)
) ENGINE=MyISAM AUTO_INCREMENT=1281786 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scnhistorie`
--

DROP TABLE IF EXISTS `scnhistorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scnhistorie` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `setid` int(10) unsigned DEFAULT NULL,
  `mcid` int(10) unsigned DEFAULT NULL,
  `mobileid` int(10) unsigned DEFAULT NULL,
  `event` varchar(45) NOT NULL,
  `txt` text,
  `depot` int(10) unsigned DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `setid` (`setid`),
  KEY `timestamp` (`timestamp`)
) ENGINE=MyISAM AUTO_INCREMENT=10738 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scnmc`
--

DROP TABLE IF EXISTS `scnmc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scnmc` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mctyp` varchar(45) NOT NULL,
  `snr` varchar(45) NOT NULL,
  `assetnr` varchar(45) NOT NULL,
  `statusid` int(10) unsigned NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdat` datetime NOT NULL,
  `anmeldeid` varchar(20) DEFAULT NULL,
  `LastLoginId` int(10) unsigned NOT NULL,
  `LastLoginDate` datetime NOT NULL,
  `LinienNr` int(10) unsigned NOT NULL,
  `LinienNrOut` int(10) DEFAULT NULL,
  `LastPwd` varchar(100) DEFAULT NULL,
  `gueltig` smallint(6) DEFAULT NULL,
  `sca_version` varchar(45) DEFAULT NULL,
  `AuftragnehmerId` int(11) DEFAULT NULL,
  `sca_name` varchar(45) DEFAULT NULL,
  `sca_typ` int(11) DEFAULT NULL,
  `checkPackIn` int(11) DEFAULT NULL,
  `checkMain` int(11) DEFAULT NULL,
  `checkPackOut` int(11) DEFAULT NULL,
  `tourinout` int(11) DEFAULT NULL,
  `tsLoginWithRoute` datetime DEFAULT NULL,
  `tsLoginWithRouteOut` datetime DEFAULT NULL,
  `Bem` varchar(250) DEFAULT NULL,
  `tsLastHello` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `snr` (`snr`),
  KEY `lastloginid` (`LastLoginId`),
  KEY `gueltig` (`gueltig`),
  KEY `tourinout` (`tourinout`),
  KEY `pwd` (`LastPwd`),
  KEY `stLastHello` (`tsLastHello`)
) ENGINE=MyISAM AUTO_INCREMENT=1710 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scnmobiles`
--

DROP TABLE IF EXISTS `scnmobiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scnmobiles` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mobiletyp` varchar(45) NOT NULL,
  `emai` varchar(45) NOT NULL,
  `rufnummer` varchar(45) NOT NULL,
  `pin` varchar(45) NOT NULL,
  `puk` varchar(45) NOT NULL,
  `statusid` int(10) unsigned NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createad` datetime NOT NULL,
  `kartennr` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=863 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scnsetcount`
--

DROP TABLE IF EXISTS `scnsetcount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scnsetcount` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `setid` int(10) unsigned NOT NULL,
  `activvon` datetime DEFAULT NULL,
  `activebis` datetime DEFAULT NULL,
  `abgerechnetbis` datetime DEFAULT NULL,
  `statusid` int(10) unsigned NOT NULL,
  `depot` int(10) unsigned NOT NULL,
  `Bemerkung` varchar(150) DEFAULT NULL,
  `isAbgerechnet` int(11) NOT NULL DEFAULT '0',
  `abgerechnetbisSave` datetime DEFAULT NULL,
  `ohneBerechnung` int(11) NOT NULL,
  `BelegOK` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=11113 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scnsets`
--

DROP TABLE IF EXISTS `scnsets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scnsets` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MCID` int(10) unsigned NOT NULL,
  `MoID` int(10) unsigned NOT NULL,
  `txt` text,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=627 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_check`
--

DROP TABLE IF EXISTS `sso_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_check` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `CollieBelegNr` varchar(15) DEFAULT NULL,
  `Strang` int(11) DEFAULT NULL,
  `Bemerkung` varchar(200) DEFAULT NULL,
  `EntladeDeltaMin` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `run` int(11) DEFAULT NULL,
  `ld` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `run` (`run`),
  KEY `strang` (`Strang`),
  KEY `timestamp` (`timestamp`)
) ENGINE=MyISAM AUTO_INCREMENT=1845853 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_global`
--

DROP TABLE IF EXISTS `sso_global`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_global` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `dID` double DEFAULT NULL,
  `dValue` double DEFAULT NULL,
  `sValue` varchar(255) DEFAULT NULL,
  `Bemerkung` varchar(100) DEFAULT NULL,
  `statuszeit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=39 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_p_mov`
--

DROP TABLE IF EXISTS `sso_p_mov`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_p_mov` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `plombennummer` double DEFAULT NULL,
  `status` double DEFAULT NULL,
  `statuszeit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastdepot` double DEFAULT NULL,
  `farbe` varchar(10) DEFAULT NULL,
  `bemerkung` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=192245 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_p_pool`
--

DROP TABLE IF EXISTS `sso_p_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_p_pool` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `plombennummer` double DEFAULT NULL,
  `status` double DEFAULT NULL,
  `statuszeit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastdepot` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=3393 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_relationen`
--

DROP TABLE IF EXISTS `sso_relationen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_relationen` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `StrangNr` int(10) unsigned DEFAULT NULL,
  `StrangOrder` double DEFAULT NULL,
  `StrangDepot` int(10) unsigned DEFAULT NULL,
  `StrangPos` int(10) unsigned DEFAULT NULL,
  `StrangDatum` datetime DEFAULT NULL,
  `StrangZ` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `StrangNr` (`StrangNr`),
  KEY `StrangPos` (`StrangPos`),
  KEY `StrangDatum` (`StrangDatum`),
  KEY `StrangZ` (`StrangZ`)
) ENGINE=MyISAM AUTO_INCREMENT=170 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_s_mov`
--

DROP TABLE IF EXISTS `sso_s_mov`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_s_mov` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `sacknummer` double DEFAULT NULL,
  `plombennummer_gruen` double DEFAULT NULL,
  `status` double DEFAULT NULL,
  `statuszeit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastdepot` double DEFAULT NULL,
  `plombennummer_gelb` double DEFAULT NULL,
  `plombennummer_rot` double DEFAULT NULL,
  `orderhub2depot` double DEFAULT NULL,
  `orderdepot2hub` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `sacknummer` (`sacknummer`),
  KEY `orderhub2depot` (`orderhub2depot`),
  KEY `orderdepot2hub` (`orderdepot2hub`),
  KEY `status` (`status`),
  KEY `statuszeit` (`statuszeit`),
  KEY `lastdepot` (`lastdepot`)
) ENGINE=MyISAM AUTO_INCREMENT=95218 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_s_pool`
--

DROP TABLE IF EXISTS `sso_s_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_s_pool` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `sacknummer` double DEFAULT NULL,
  `status` double DEFAULT NULL,
  `statuszeit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gedruckt` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `sacknummer` (`sacknummer`)
) ENGINE=MyISAM AUTO_INCREMENT=70011308209 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_auftrag_info`
--

DROP TABLE IF EXISTS `tbl_auftrag_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_auftrag_info` (
  `id` double NOT NULL AUTO_INCREMENT,
  `sMeldung` varchar(255) DEFAULT NULL,
  `dInfoDepot` double DEFAULT NULL,
  `dAbholDepot` double DEFAULT NULL,
  `dLieferDepot` double DEFAULT NULL,
  `dAllDepot` double DEFAULT NULL,
  `gueltigAb` datetime DEFAULT NULL,
  `gueltigBis` datetime DEFAULT NULL,
  `dIstGueltig` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `dAbholDepot` (`dAbholDepot`),
  KEY `dLieferDepot` (`dLieferDepot`),
  KEY `dAllDepot` (`dAllDepot`),
  KEY `gueltigAb` (`gueltigAb`),
  KEY `gueltigBis` (`gueltigBis`),
  KEY `dIstGueltig` (`dIstGueltig`)
) ENGINE=MyISAM AUTO_INCREMENT=833 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_avise_auftrag`
--

DROP TABLE IF EXISTS `tbl_avise_auftrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_avise_auftrag` (
  `OrderID` double NOT NULL DEFAULT '0',
  `AuftragsID` varchar(25) DEFAULT NULL,
  `GKNr` int(11) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT NULL,
  `DepotNrAbD` int(11) DEFAULT NULL,
  `DepotNrbev` int(11) DEFAULT NULL,
  `DepotNrAD` int(11) DEFAULT NULL,
  `DepotNrZD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `lockflag` smallint(6) NOT NULL DEFAULT '1',
  `dtCreateAD` datetime DEFAULT NULL,
  `dtSendAD2Z` datetime DEFAULT NULL,
  `dtReceiveAD2Z` datetime DEFAULT NULL,
  `dtSendZ2H` datetime DEFAULT NULL,
  `dtReceiveZ2H` datetime DEFAULT NULL,
  `dtModifyH` datetime DEFAULT NULL,
  `dtSendH2Z` datetime DEFAULT NULL,
  `dtReceiveH2Z` datetime DEFAULT NULL,
  `dtSendZ2ZD` datetime DEFAULT NULL,
  `dtReceiveZ2ZD` datetime DEFAULT NULL,
  `dtModifyZD` datetime DEFAULT NULL,
  `dtTermin_von` datetime DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `dtAuslieferDatum` datetime DEFAULT NULL,
  `dtAuslieferZeit` datetime DEFAULT NULL,
  `Empfaenger` varchar(35) DEFAULT NULL,
  `dtSendZD2Z` datetime DEFAULT NULL,
  `dtReceiveZD2Z` datetime DEFAULT NULL,
  `dtSendZ2AD` datetime DEFAULT NULL,
  `dtReceiveZ2AD` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `KDNR` int(9) DEFAULT NULL,
  `FirmaS` varchar(50) DEFAULT NULL,
  `FirmaS2` varchar(50) DEFAULT NULL,
  `LandS` char(3) DEFAULT NULL,
  `PLZS` varchar(10) DEFAULT NULL,
  `OrtS` varchar(50) DEFAULT NULL,
  `StrasseS` varchar(50) DEFAULT NULL,
  `StrNrS` varchar(10) DEFAULT NULL,
  `TelefonVWS` varchar(20) DEFAULT NULL,
  `TelefonNrS` varchar(20) DEFAULT NULL,
  `TelefaxNrS` varchar(20) DEFAULT NULL,
  `FirmaD` varchar(50) DEFAULT NULL,
  `FirmaD2` varchar(50) DEFAULT NULL,
  `LandD` char(3) DEFAULT NULL,
  `PLZD` varchar(10) DEFAULT NULL,
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `TelefonVWD` varchar(20) DEFAULT NULL,
  `TelefonNrD` varchar(20) DEFAULT NULL,
  `TelefaxNrD` varchar(20) DEFAULT NULL,
  `ClearingArt` smallint(6) DEFAULT NULL,
  `ZuschlagsArt` smallint(6) DEFAULT NULL,
  `GewichtGesamt` double DEFAULT NULL,
  `PreisEK` double(20,2) DEFAULT NULL,
  `PreisEG` double(20,2) DEFAULT NULL,
  `PreisEP` double(20,2) DEFAULT NULL,
  `PreiseZS` double(20,2) DEFAULT NULL,
  `ColliesGesamt` smallint(6) DEFAULT NULL,
  `PreisNN` double(20,2) NOT NULL DEFAULT '0.00',
  `DatumNN` date DEFAULT NULL,
  `erhaltenNN` int(11) DEFAULT NULL,
  `sendD2Z` int(1) DEFAULT NULL,
  `Feiertag_1` varchar(15) DEFAULT NULL,
  `FeiertagShlD` int(11) DEFAULT NULL,
  `FeiertagShlS` int(11) DEFAULT NULL,
  `Feiertag_2` varchar(15) DEFAULT NULL,
  `ClearingDate` date DEFAULT NULL,
  `ClearingDateMaster` datetime DEFAULT NULL,
  `Satzart` char(1) DEFAULT NULL,
  `Referenz` varchar(30) DEFAULT NULL,
  `Referenz2` varchar(15) DEFAULT NULL,
  `Frei` varchar(6) DEFAULT NULL,
  `Adr_Nr_Absender` text,
  `Adr_Nr_Empfaenger` text,
  `Wert` double(8,2) unsigned zerofill DEFAULT NULL,
  `Nachnahmebetrag` double DEFAULT NULL,
  `Versicherungswert` double(10,2) unsigned zerofill DEFAULT NULL,
  `Frei2` double(8,2) DEFAULT NULL,
  `Verladedatum` datetime DEFAULT NULL,
  `Lieferdatum` datetime DEFAULT NULL,
  `Lieferzeit_von` datetime DEFAULT NULL,
  `Lieferzeit_bis` datetime DEFAULT NULL,
  `KZ_Fahrzeug` tinyint(2) unsigned zerofill DEFAULT NULL,
  `KZ_Transportart` tinyint(2) unsigned zerofill DEFAULT NULL,
  `Frei3` char(1) DEFAULT NULL,
  `Service` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `KZServiceLeo` int(10) unsigned NOT NULL DEFAULT '0',
  `Sendungsstatus` tinyint(4) unsigned zerofill DEFAULT NULL,
  `Ausliefertour` int(4) unsigned zerofill DEFAULT NULL,
  `Routung_Hilfsspalte` varchar(10) DEFAULT NULL,
  `KZ_erweitert` int(10) unsigned DEFAULT NULL,
  `Information1` varchar(40) DEFAULT NULL,
  `Information2` varchar(40) DEFAULT NULL,
  `Inhalt` varchar(50) DEFAULT NULL,
  `Frei4` varchar(20) DEFAULT NULL,
  `Frei5` varchar(40) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `Frei6` varchar(69) DEFAULT NULL,
  `PU_GK_Auftrags_Nr` int(8) DEFAULT NULL,
  `Besteller_Name` varchar(25) DEFAULT NULL,
  `Ladelisten_Nummer` int(8) unsigned zerofill DEFAULT NULL,
  `Frei7` char(2) DEFAULT NULL,
  `Kennzeichen_Info` char(1) DEFAULT NULL,
  `Frei8` char(2) DEFAULT NULL,
  `Frei9` varchar(11) DEFAULT NULL,
  `Frei10` varchar(10) DEFAULT NULL,
  `Anzahl_Teilretouren` tinyint(4) unsigned zerofill DEFAULT NULL,
  `KZ_Zusatz` tinyint(2) unsigned zerofill DEFAULT NULL,
  `CR` char(2) DEFAULT NULL,
  `FahrerNr` int(11) DEFAULT NULL,
  `d` int(1) DEFAULT NULL,
  `a` int(1) DEFAULT NULL,
  `Info_Rollkarte` longtext,
  `Info_Intern` longtext,
  `termin_i` int(1) unsigned zerofill DEFAULT NULL,
  `Zone` char(1) DEFAULT NULL,
  `Insel` char(1) DEFAULT NULL,
  `Zonea` char(1) DEFAULT NULL,
  `Insela` char(1) DEFAULT NULL,
  `69_WZ_Abholung` char(2) DEFAULT NULL,
  `69_Leer1` varchar(10) DEFAULT NULL,
  `69_Betrag_Abrechnung_Kunde` double(8,2) unsigned zerofill DEFAULT NULL,
  `69_Leer2` varchar(16) DEFAULT NULL,
  `69_Zerofill1` int(3) unsigned zerofill DEFAULT NULL,
  `69_KostenBahnFlug` double(5,2) unsigned zerofill DEFAULT NULL,
  `69_Zerofill2` int(8) unsigned zerofill DEFAULT NULL,
  `69_KmDirekt` int(4) unsigned zerofill DEFAULT NULL,
  `69_KZ_SameDay` tinyint(1) unsigned zerofill DEFAULT NULL,
  `69_KZ_Serviceart` tinyint(2) unsigned zerofill DEFAULT NULL,
  `69_Undefined` tinyint(1) unsigned zerofill DEFAULT NULL,
  `69_Servicezeit_XChange` char(2) DEFAULT NULL,
  `69_Avis` char(1) DEFAULT NULL,
  `69_N2` char(1) DEFAULT NULL,
  `69_Leer3` varchar(4) DEFAULT NULL,
  `69_Fremdavis` char(1) DEFAULT NULL,
  `Betrag_Importkosten` double(8,2) DEFAULT NULL,
  `Betrag_Importkosten_best` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten_best` double(8,2) DEFAULT NULL,
  `Leer4` char(1) DEFAULT NULL,
  `Sondervereinbarung` varchar(20) DEFAULT NULL,
  `Importkosten` varchar(20) DEFAULT NULL,
  `ExportkostenPU` varchar(20) DEFAULT NULL,
  `RechnungsNr` varchar(20) DEFAULT NULL,
  `Satzart_ava` char(3) DEFAULT NULL,
  `FirmaDX` varchar(50) DEFAULT NULL,
  `FirmaD2X` varchar(50) DEFAULT NULL,
  `LandDX` char(3) DEFAULT NULL,
  `PLZDX` varchar(10) DEFAULT NULL,
  `OrtDX` varchar(50) DEFAULT NULL,
  `StrasseDX` varchar(50) DEFAULT NULL,
  `StrNrDX` varchar(10) DEFAULT NULL,
  `dtAuslieferungX` datetime DEFAULT NULL,
  `dtTermin_vonX` datetime DEFAULT NULL,
  `dtTerminX` datetime DEFAULT NULL,
  `frueheste_zustellzeit` varchar(4) DEFAULT NULL,
  `Satzartp` char(1) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `UploadStatus` tinyint(4) NOT NULL DEFAULT '0',
  `SendStatus` tinyint(4) NOT NULL DEFAULT '0',
  `clearingpreis` double(8,2) DEFAULT NULL,
  `SdgArt` varchar(4) DEFAULT NULL,
  `OrderID_X` varchar(15) DEFAULT NULL,
  `PZAImageZIP` varchar(60) DEFAULT NULL,
  `IDSdgArt` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `RueckDate` datetime DEFAULT NULL,
  `ClearingArtMaster` int(10) unsigned NOT NULL DEFAULT '0',
  `ZoneS` char(1) DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `SdgType` char(1) DEFAULT NULL,
  `SdgStatus` char(1) DEFAULT NULL,
  `ROrderID` double DEFAULT NULL,
  `EBOrderID` double DEFAULT NULL,
  `EXAuftragsIDRef` varchar(25) DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `EBRueckGrund` varchar(25) DEFAULT NULL,
  `wt_avis` double DEFAULT NULL,
  `FirmaS3` varchar(50) DEFAULT NULL,
  `FirmaD3` varchar(50) DEFAULT NULL,
  `product_spec` varchar(5) DEFAULT NULL,
  `FirmaD3X` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  UNIQUE KEY `DepotNrED` (`DepotNrED`,`DepotNrAD`,`DepotNrZD`,`DepotNrLD`,`OrderID`),
  UNIQUE KEY `DepotNrLD` (`DepotNrLD`,`DepotNrZD`,`DepotNrAD`,`DepotNrED`,`OrderID`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `AuftragsID` (`AuftragsID`),
  KEY `dtAuslieferung` (`dtAuslieferung`),
  KEY `DepotNrZD` (`DepotNrZD`),
  KEY `dtSendZ2ZD` (`dtSendZ2ZD`),
  KEY `Empfaenger` (`Empfaenger`),
  KEY `DepotNrLDx` (`DepotNrLD`),
  KEY `DepotNrAD` (`DepotNrAD`),
  KEY `DepotNrAbD` (`DepotNrAbD`),
  KEY `plzd` (`PLZD`),
  KEY `OrtD` (`OrtD`),
  KEY `DepotNrEDInd` (`DepotNrED`),
  KEY `dtCreateAD` (`dtCreateAD`),
  KEY `FirmaS` (`FirmaS`),
  KEY `SendStatus` (`SendStatus`),
  KEY `Referenz` (`Referenz`),
  KEY `lockflag` (`lockflag`),
  KEY `Verladedatum` (`Verladedatum`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `ROrderID` (`ROrderID`),
  KEY `EBOrderID` (`EBOrderID`),
  KEY `EXAuftragsIDRef` (`EXAuftragsIDRef`),
  KEY `Referenz2` (`Referenz2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_avise_ke`
--

DROP TABLE IF EXISTS `tbl_avise_ke`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_avise_ke` (
  `OrderID` double unsigned NOT NULL DEFAULT '0',
  `Wert` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `Transportart` char(55) DEFAULT NULL,
  `Preis` tinyint(6) unsigned zerofill DEFAULT '000000',
  `setzen` int(1) DEFAULT '0',
  `summe_wert` int(10) unsigned zerofill DEFAULT '0000000000',
  `summe_preis` tinyint(6) unsigned zerofill DEFAULT '000000',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`OrderID`,`Wert`),
  KEY `OrderID` (`OrderID`),
  KEY `Wert` (`Wert`),
  KEY `setzen` (`setzen`),
  KEY `Transportart` (`Transportart`),
  KEY `Timestamp` (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_avise_servicekennzeichen`
--

DROP TABLE IF EXISTS `tbl_avise_servicekennzeichen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_avise_servicekennzeichen` (
  `OrderID` double unsigned zerofill NOT NULL DEFAULT '0000000000000000000000',
  `Wert` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `Transportart` varchar(55) DEFAULT NULL,
  `Preis` int(10) unsigned DEFAULT '0',
  `setzen` int(1) DEFAULT '0',
  `summe_wert` int(10) unsigned zerofill DEFAULT '0000000000',
  `summe_preis` tinyint(6) unsigned zerofill DEFAULT '000000',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `OrderID_X` varchar(20) DEFAULT NULL,
  `Timestamp2` datetime DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`Wert`),
  KEY `OrderID` (`OrderID`),
  KEY `Orderid_X` (`OrderID_X`),
  KEY `wert` (`Wert`),
  KEY `setzen` (`setzen`),
  KEY `Timestamp2` (`Timestamp2`),
  KEY `Timestamp` (`Timestamp`),
  KEY `Transportart` (`Transportart`),
  KEY `Preis` (`Preis`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_grid_val_all`
--

DROP TABLE IF EXISTS `tbl_grid_val_all`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_grid_val_all` (
  `id` double NOT NULL AUTO_INCREMENT,
  `gOrderID` double DEFAULT NULL,
  `gCollieBelegNr` double DEFAULT NULL,
  `gErzeuger` int(10) unsigned DEFAULT NULL,
  `gFC` int(10) unsigned NOT NULL DEFAULT '0',
  `gVerladedatum` datetime DEFAULT NULL,
  `gVerpackungsart` int(10) unsigned NOT NULL DEFAULT '0',
  `gStatuszeit` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gDepotNrAD` int(10) unsigned DEFAULT NULL,
  `gDepotNrAbD` int(10) unsigned DEFAULT NULL,
  `gDepotNrLD` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `gVerladedatum` (`gVerladedatum`),
  KEY `gVerpackungsart` (`gVerpackungsart`),
  KEY `gCollieBelegNr` (`gCollieBelegNr`),
  KEY `gStatuszeit` (`gStatuszeit`),
  KEY `gOrderID` (`gOrderID`)
) ENGINE=MyISAM AUTO_INCREMENT=3567010 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_grid_val_depot`
--

DROP TABLE IF EXISTS `tbl_grid_val_depot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_grid_val_depot` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `gVerladedatum` datetime DEFAULT NULL,
  `gDepotNr` int(10) unsigned DEFAULT NULL,
  `gScanbeginn` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `gVerladedatum` (`gVerladedatum`),
  KEY `gScanbeginn` (`gScanbeginn`)
) ENGINE=MyISAM AUTO_INCREMENT=74065 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_zustelldispo`
--

DROP TABLE IF EXISTS `tbl_zustelldispo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_zustelldispo` (
  `lfdnr` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `OrderID` double DEFAULT NULL,
  `OrderPos` double DEFAULT NULL,
  `CollieBelegNr` double DEFAULT NULL,
  `GewichtReal` double DEFAULT NULL,
  `GewichtLBH` double DEFAULT NULL,
  `GewichtEffektiv` double DEFAULT NULL,
  `Frei2` varchar(6) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `Frei3` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `Frei4_` double DEFAULT NULL,
  `RollkartennummerD` double DEFAULT NULL,
  `TourNr2` double DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `mydepotid2` double DEFAULT NULL,
  `Timestamp22` datetime DEFAULT NULL,
  `rkM` int(11) DEFAULT NULL,
  `rkLR` int(11) DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT NULL,
  `DepotNrAbD` int(11) DEFAULT NULL,
  `DepotNrAD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `lockflag` int(11) DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `IDSdgArt` int(11) DEFAULT NULL,
  `PLZD` varchar(10) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `OrtD` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `termin_i` int(11) DEFAULT NULL,
  PRIMARY KEY (`lfdnr`),
  KEY `mydepotid2` (`mydepotid2`),
  KEY `Orderid` (`OrderID`),
  KEY `Orderpos` (`OrderPos`),
  KEY `RollkartennummerD` (`RollkartennummerD`),
  KEY `Timestamp22` (`Timestamp22`),
  KEY `termin_i` (`termin_i`),
  KEY `TourNr2` (`TourNr2`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rkLR` (`rkLR`)
) ENGINE=MyISAM AUTO_INCREMENT=55380190 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_zustelldispo_collies_neu`
--

DROP TABLE IF EXISTS `tbl_zustelldispo_collies_neu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_zustelldispo_collies_neu` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL AUTO_INCREMENT,
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `Laenge` smallint(6) NOT NULL DEFAULT '0',
  `Breite` smallint(6) NOT NULL DEFAULT '0',
  `Hoehe` smallint(6) NOT NULL DEFAULT '0',
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double NOT NULL DEFAULT '0',
  `VerpackungsArt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bemerkung` varchar(255) DEFAULT '',
  `Satzart` tinyint(1) unsigned zerofill NOT NULL DEFAULT '2',
  `Sendungsnummer` varchar(11) DEFAULT NULL,
  `Laufende_Nummer` tinyint(2) NOT NULL DEFAULT '0',
  `Frei1` varchar(4) DEFAULT NULL,
  `Frei2` varchar(6) DEFAULT NULL,
  `Packstueckart` tinyint(2) unsigned zerofill NOT NULL DEFAULT '00',
  `Frei3` varchar(8) DEFAULT NULL,
  `Frei4` double DEFAULT NULL,
  `Referenz_Teilretoure` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `Frei5` varchar(4) DEFAULT NULL,
  `i_scan` int(1) NOT NULL DEFAULT '0',
  `Rollkartennummer` varchar(20) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `TourNr2` int(11) NOT NULL DEFAULT '0',
  `dtEingang2` datetime DEFAULT NULL,
  `dtAusgang2` datetime DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `dtAusgangDepot2` datetime DEFAULT NULL,
  `dtEingangHup2` datetime DEFAULT NULL,
  `dtAusgangHup2` datetime DEFAULT NULL,
  `ueD2H2` int(1) NOT NULL DEFAULT '-1',
  `nueD2H2` int(1) NOT NULL DEFAULT '0',
  `ueH2D2` int(1) NOT NULL DEFAULT '-1',
  `nueH2D2` int(1) NOT NULL DEFAULT '0',
  `mydepotid2` int(11) NOT NULL DEFAULT '0',
  `Timestamp22` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `AuslieferDatum2` datetime DEFAULT NULL,
  `AuslieferZeit2` datetime DEFAULT NULL,
  `Empfaenger2` varchar(35) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `dtEingangHup3` datetime DEFAULT NULL,
  `dtAusgangHup3` datetime DEFAULT NULL,
  `rKM` double DEFAULT NULL,
  `rkLR` double DEFAULT NULL,
  `RollkartennummerD` double DEFAULT NULL,
  `dtLagereingang` datetime DEFAULT NULL,
  `dtLagerausgang` datetime DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `Beladelinie` int(11) DEFAULT NULL,
  `LadelistennummerD` double DEFAULT NULL,
  `bmpFileName` varchar(100) DEFAULT NULL,
  `mydepotabd` int(11) DEFAULT NULL,
  `cReferenz` varchar(30) DEFAULT NULL,
  `RUP` varchar(3) DEFAULT NULL,
  `RUP_org` varchar(3) DEFAULT NULL,
  `BagBelegNrC` double DEFAULT NULL,
  `BagIDNrC` double DEFAULT NULL,
  `PlombennummerC` double DEFAULT NULL,
  `PlomberNrC` double DEFAULT NULL,
  `PlombenNrC` double DEFAULT NULL,
  `BagBelegNrAbC` double DEFAULT NULL,
  `val_adr` varchar(35) DEFAULT NULL,
  `lieferstatus` smallint(6) NOT NULL DEFAULT '0',
  `lieferfehler` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`OrderID`,`OrderPos`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rollkartennummer` (`Rollkartennummer`),
  KEY `iscan` (`i_scan`),
  KEY `OrderID` (`OrderID`),
  KEY `OrderPos` (`OrderPos`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `dtEingang` (`dtEingang2`),
  KEY `TourNr` (`TourNr2`),
  KEY `dtAusgang` (`dtAusgang2`),
  KEY `dtEingangDepot` (`dtEingangDepot2`),
  KEY `dtAusgangDepot` (`dtAusgangDepot2`),
  KEY `mydepotid` (`mydepotid2`),
  KEY `Timestamp2` (`Timestamp22`),
  KEY `Verladelinie` (`Verladelinie`),
  KEY `dtEingangHup3` (`dtEingangHup3`),
  KEY `dtAusgangHup3` (`dtAusgangHup3`),
  KEY `Frei2` (`Frei2`),
  KEY `Frei3` (`Frei3`),
  KEY `Frei4` (`Frei4`),
  KEY `RollkartennummerD` (`RollkartennummerD`),
  KEY `rkM` (`rKM`),
  KEY `rkLR` (`rkLR`),
  KEY `dtLagereingang` (`dtLagereingang`),
  KEY `dtLagerausgang` (`dtLagerausgang`),
  KEY `Lagerplatz` (`Lagerplatz`),
  KEY `Beladelinie` (`Beladelinie`),
  KEY `LadelistennummerD` (`LadelistennummerD`),
  KEY `mydepotabd` (`mydepotabd`),
  KEY `VerpackungsArt` (`VerpackungsArt`)
) ENGINE=MyISAM AUTO_INCREMENT=32768 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_zustelldispo_neu`
--

DROP TABLE IF EXISTS `tbl_zustelldispo_neu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_zustelldispo_neu` (
  `lfdnr` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `OrderID` double DEFAULT NULL,
  `OrderPos` double NOT NULL DEFAULT '0',
  `CollieBelegNr` double DEFAULT NULL,
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double DEFAULT NULL,
  `Frei2` varchar(6) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `Frei3` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `Frei4_` double DEFAULT NULL,
  `RollkartennummerD` double DEFAULT NULL,
  `TourNr2` double DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `mydepotid2` double NOT NULL DEFAULT '0',
  `Timestamp22` datetime DEFAULT NULL,
  `rkM` int(11) DEFAULT NULL,
  `rkLR` int(11) DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT NULL,
  `DepotNrAbD` int(11) DEFAULT NULL,
  `DepotNrAD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `lockflag` int(11) DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `IDSdgArt` int(11) DEFAULT NULL,
  `PLZD` varchar(10) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `OrtD` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `termin_i` int(11) DEFAULT NULL,
  `tour_neu` double NOT NULL DEFAULT '0',
  `check_neu` int(1) NOT NULL DEFAULT '0',
  `Empfaengername` varchar(50) DEFAULT NULL,
  `Empfaengerstr` varchar(50) DEFAULT NULL,
  `dtTermin_von` datetime DEFAULT NULL,
  `anzPK` double NOT NULL DEFAULT '0',
  `Empfaengername2` varchar(50) DEFAULT NULL,
  `Hausnummer` varchar(15) DEFAULT NULL,
  `Land` varchar(3) DEFAULT NULL,
  `Telefonnummer` varchar(30) DEFAULT NULL,
  `PreisNN` double NOT NULL DEFAULT '0',
  `Info_Rollkarte` varchar(100) DEFAULT NULL,
  `Timestamp` datetime DEFAULT NULL,
  `Auslieferdatum` datetime DEFAULT NULL,
  `Auslieferuhrzeit` datetime DEFAULT NULL,
  `Ausliefername` varchar(45) DEFAULT NULL,
  `Information2` varchar(100) DEFAULT NULL,
  `SdgType` varchar(3) DEFAULT NULL,
  `SdgStatus` varchar(3) DEFAULT NULL,
  `Bemerkung` varchar(255) DEFAULT NULL,
  `EBOrderID` double DEFAULT NULL,
  `markSort` int(1) NOT NULL DEFAULT '0',
  `Service` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`lfdnr`),
  KEY `mydepotid2` (`mydepotid2`),
  KEY `Orderid` (`OrderID`),
  KEY `Orderpos` (`OrderPos`),
  KEY `RollkartennummerD` (`RollkartennummerD`),
  KEY `Timestamp22` (`Timestamp22`),
  KEY `termin_i` (`termin_i`),
  KEY `tour_neu` (`tour_neu`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rkLR` (`rkLR`),
  KEY `check_neu` (`check_neu`),
  KEY `PLZD` (`PLZD`),
  KEY `markSort` (`markSort`)
) ENGINE=MyISAM AUTO_INCREMENT=29986084 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftrag`
--

DROP TABLE IF EXISTS `tblauftrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftrag` (
  `OrderID` double NOT NULL DEFAULT '0',
  `AuftragsID` varchar(25) DEFAULT NULL,
  `GKNr` int(11) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT NULL,
  `DepotNrAbD` int(11) DEFAULT NULL,
  `DepotNrbev` int(11) DEFAULT NULL,
  `DepotNrAD` int(11) DEFAULT NULL,
  `DepotNrZD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `lockflag` smallint(6) NOT NULL DEFAULT '1',
  `dtCreateAD` datetime DEFAULT NULL,
  `dtSendAD2Z` datetime DEFAULT NULL,
  `dtReceiveAD2Z` datetime DEFAULT NULL,
  `dtSendZ2H` datetime DEFAULT NULL,
  `dtReceiveZ2H` datetime DEFAULT NULL,
  `dtModifyH` datetime DEFAULT NULL,
  `dtSendH2Z` datetime DEFAULT NULL,
  `dtReceiveH2Z` datetime DEFAULT NULL,
  `dtSendZ2ZD` datetime DEFAULT NULL,
  `dtReceiveZ2ZD` datetime DEFAULT NULL,
  `dtModifyZD` datetime DEFAULT NULL,
  `dtTermin_von` datetime DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `dtAuslieferDatum` datetime DEFAULT NULL,
  `dtAuslieferZeit` datetime DEFAULT NULL,
  `Empfaenger` varchar(35) DEFAULT NULL,
  `dtSendZD2Z` datetime DEFAULT NULL,
  `dtReceiveZD2Z` datetime DEFAULT NULL,
  `dtSendZ2AD` datetime DEFAULT NULL,
  `dtReceiveZ2AD` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `KDNR` int(9) DEFAULT NULL,
  `FirmaS` varchar(50) DEFAULT NULL,
  `FirmaS2` varchar(50) DEFAULT NULL,
  `LandS` char(3) DEFAULT NULL,
  `PLZS` varchar(10) DEFAULT NULL,
  `OrtS` varchar(50) DEFAULT NULL,
  `StrasseS` varchar(50) DEFAULT NULL,
  `StrNrS` varchar(10) DEFAULT NULL,
  `TelefonVWS` varchar(20) DEFAULT NULL,
  `TelefonNrS` varchar(20) DEFAULT NULL,
  `TelefaxNrS` varchar(20) DEFAULT NULL,
  `FirmaD` varchar(50) DEFAULT NULL,
  `FirmaD2` varchar(50) DEFAULT NULL,
  `LandD` char(3) DEFAULT NULL,
  `PLZD` varchar(10) DEFAULT NULL,
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `TelefonVWD` varchar(20) DEFAULT NULL,
  `TelefonNrD` varchar(20) DEFAULT NULL,
  `TelefaxNrD` varchar(20) DEFAULT NULL,
  `ClearingArt` smallint(6) DEFAULT NULL,
  `ZuschlagsArt` smallint(6) DEFAULT NULL,
  `GewichtGesamt` double DEFAULT NULL,
  `PreisEK` double(20,2) DEFAULT NULL,
  `PreisEG` double(20,2) DEFAULT NULL,
  `PreisEP` double(20,2) DEFAULT NULL,
  `PreiseZS` double(20,2) DEFAULT NULL,
  `ColliesGesamt` smallint(6) DEFAULT NULL,
  `PreisNN` double(20,2) NOT NULL DEFAULT '0.00',
  `DatumNN` date DEFAULT NULL,
  `erhaltenNN` int(11) DEFAULT NULL,
  `sendD2Z` int(1) DEFAULT NULL,
  `Feiertag_1` varchar(15) DEFAULT NULL,
  `FeiertagShlD` int(11) DEFAULT NULL,
  `FeiertagShlS` int(11) DEFAULT NULL,
  `Feiertag_2` varchar(15) DEFAULT NULL,
  `ClearingDate` date DEFAULT NULL,
  `ClearingDateMaster` datetime DEFAULT NULL,
  `Satzart` char(1) DEFAULT NULL,
  `Referenz` varchar(30) DEFAULT NULL,
  `Referenz2` varchar(15) DEFAULT NULL,
  `Frei` varchar(6) DEFAULT NULL,
  `Adr_Nr_Absender` text,
  `Adr_Nr_Empfaenger` text,
  `Wert` double(8,2) unsigned zerofill DEFAULT NULL,
  `Nachnahmebetrag` double DEFAULT NULL,
  `Versicherungswert` double(10,2) unsigned zerofill DEFAULT NULL,
  `Frei2` double(8,2) DEFAULT NULL,
  `Verladedatum` datetime DEFAULT NULL,
  `Lieferdatum` datetime DEFAULT NULL,
  `Lieferzeit_von` datetime DEFAULT NULL,
  `Lieferzeit_bis` datetime DEFAULT NULL,
  `KZ_Fahrzeug` tinyint(2) unsigned zerofill DEFAULT NULL,
  `KZ_Transportart` tinyint(2) unsigned zerofill DEFAULT NULL,
  `Frei3` char(1) DEFAULT NULL,
  `Service` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `KZServiceLeo` int(10) unsigned NOT NULL DEFAULT '0',
  `Sendungsstatus` tinyint(4) unsigned zerofill DEFAULT NULL,
  `Ausliefertour` int(4) unsigned zerofill DEFAULT NULL,
  `Routung_Hilfsspalte` varchar(10) DEFAULT NULL,
  `KZ_erweitert` int(10) unsigned DEFAULT NULL,
  `Information1` varchar(80) DEFAULT NULL,
  `Information2` varchar(40) DEFAULT NULL,
  `Inhalt` varchar(50) DEFAULT NULL,
  `Frei4` varchar(20) DEFAULT NULL,
  `Frei5` varchar(40) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `Frei6` varchar(69) DEFAULT NULL,
  `PU_GK_Auftrags_Nr` int(8) DEFAULT NULL,
  `Besteller_Name` varchar(25) DEFAULT NULL,
  `Ladelisten_Nummer` int(8) unsigned zerofill DEFAULT NULL,
  `Frei7` char(2) DEFAULT NULL,
  `Kennzeichen_Info` char(1) DEFAULT NULL,
  `Frei8` char(2) DEFAULT NULL,
  `Frei9` varchar(11) DEFAULT NULL,
  `Frei10` varchar(10) DEFAULT NULL,
  `Anzahl_Teilretouren` tinyint(4) unsigned zerofill DEFAULT NULL,
  `KZ_Zusatz` tinyint(2) unsigned zerofill DEFAULT NULL,
  `CR` char(2) DEFAULT NULL,
  `FahrerNr` int(11) DEFAULT NULL,
  `d` int(1) DEFAULT NULL,
  `a` int(1) DEFAULT NULL,
  `Info_Rollkarte` longtext,
  `Info_Intern` longtext,
  `termin_i` int(1) unsigned zerofill DEFAULT NULL,
  `Zone` char(1) DEFAULT NULL,
  `Insel` char(1) DEFAULT NULL,
  `Zonea` char(1) DEFAULT NULL,
  `Insela` char(1) DEFAULT NULL,
  `69_WZ_Abholung` char(2) DEFAULT NULL,
  `69_Leer1` varchar(10) DEFAULT NULL,
  `69_Betrag_Abrechnung_Kunde` double(8,2) unsigned zerofill DEFAULT NULL,
  `69_Leer2` varchar(16) DEFAULT NULL,
  `69_Zerofill1` int(3) unsigned zerofill DEFAULT NULL,
  `69_KostenBahnFlug` double(5,2) unsigned zerofill DEFAULT NULL,
  `69_Zerofill2` int(8) unsigned zerofill DEFAULT NULL,
  `69_KmDirekt` int(4) unsigned zerofill DEFAULT NULL,
  `69_KZ_SameDay` tinyint(1) unsigned zerofill DEFAULT NULL,
  `69_KZ_Serviceart` tinyint(2) unsigned zerofill DEFAULT NULL,
  `69_Undefined` tinyint(1) unsigned zerofill DEFAULT NULL,
  `69_Servicezeit_XChange` char(2) DEFAULT NULL,
  `69_Avis` char(1) DEFAULT NULL,
  `69_N2` char(1) DEFAULT NULL,
  `69_Leer3` varchar(4) DEFAULT NULL,
  `69_Fremdavis` char(1) DEFAULT NULL,
  `Betrag_Importkosten` double(8,2) DEFAULT NULL,
  `Betrag_Importkosten_best` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten_best` double(8,2) DEFAULT NULL,
  `Leer4` char(1) DEFAULT NULL,
  `Sondervereinbarung` varchar(20) DEFAULT NULL,
  `Importkosten` varchar(20) DEFAULT NULL,
  `ExportkostenPU` varchar(20) DEFAULT NULL,
  `RechnungsNr` varchar(20) DEFAULT NULL,
  `Satzart_ava` char(3) DEFAULT NULL,
  `FirmaDX` varchar(50) DEFAULT NULL,
  `FirmaD2X` varchar(50) DEFAULT NULL,
  `LandDX` char(3) DEFAULT NULL,
  `PLZDX` varchar(10) DEFAULT NULL,
  `OrtDX` varchar(50) DEFAULT NULL,
  `StrasseDX` varchar(50) DEFAULT NULL,
  `StrNrDX` varchar(10) DEFAULT NULL,
  `dtAuslieferungX` datetime DEFAULT NULL,
  `dtTermin_vonX` datetime DEFAULT NULL,
  `dtTerminX` datetime DEFAULT NULL,
  `frueheste_zustellzeit` varchar(4) DEFAULT NULL,
  `Satzartp` char(1) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `UploadStatus` tinyint(4) NOT NULL DEFAULT '0',
  `SendStatus` tinyint(4) NOT NULL DEFAULT '0',
  `clearingpreis` double(8,2) DEFAULT NULL,
  `SdgArt` varchar(4) DEFAULT NULL,
  `OrderID_X` varchar(15) DEFAULT NULL,
  `PZAImageZIP` varchar(60) DEFAULT NULL,
  `IDSdgArt` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `RueckDate` datetime DEFAULT NULL,
  `ClearingArtMaster` int(10) unsigned DEFAULT '0',
  `ZoneS` char(1) DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `SdgType` char(1) DEFAULT NULL,
  `SdgStatus` char(1) DEFAULT NULL,
  `ROrderID` double DEFAULT NULL,
  `EBOrderID` double DEFAULT NULL,
  `EXAuftragsIDRef` varchar(25) DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `EBRueckGrund` varchar(25) DEFAULT NULL,
  `FirmaS3` varchar(50) DEFAULT NULL,
  `FirmaD3` varchar(50) DEFAULT NULL,
  `product_spec` varchar(5) DEFAULT NULL,
  `FirmaD3X` varchar(50) DEFAULT NULL,
  `BagBelegNrA` double DEFAULT NULL,
  `BagIDNrA` double DEFAULT NULL,
  `PlombennummerA` double DEFAULT NULL,
  `PlombenNrA` double DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  UNIQUE KEY `DepotNrED` (`DepotNrED`,`DepotNrAD`,`DepotNrZD`,`DepotNrLD`,`OrderID`),
  UNIQUE KEY `DepotNrLD` (`DepotNrLD`,`DepotNrZD`,`DepotNrAD`,`DepotNrED`,`OrderID`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `AuftragsID` (`AuftragsID`),
  KEY `dtAuslieferung` (`dtAuslieferung`),
  KEY `DepotNrZD` (`DepotNrZD`),
  KEY `dtSendZ2ZD` (`dtSendZ2ZD`),
  KEY `Empfaenger` (`Empfaenger`),
  KEY `DepotNrLDx` (`DepotNrLD`),
  KEY `DepotNrAD` (`DepotNrAD`),
  KEY `DepotNrAbD` (`DepotNrAbD`),
  KEY `plzd` (`PLZD`),
  KEY `OrtD` (`OrtD`),
  KEY `DepotNrEDInd` (`DepotNrED`),
  KEY `dtCreateAD` (`dtCreateAD`),
  KEY `FirmaS` (`FirmaS`),
  KEY `SendStatus` (`SendStatus`),
  KEY `Referenz` (`Referenz`),
  KEY `lockflag` (`lockflag`),
  KEY `Verladedatum` (`Verladedatum`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `ROrderID` (`ROrderID`),
  KEY `EBOrderID` (`EBOrderID`),
  KEY `EXAuftragsIDRef` (`EXAuftragsIDRef`),
  KEY `Referenz2` (`Referenz2`),
  KEY `BagIDNrA` (`BagIDNrA`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragadrabs`
--

DROP TABLE IF EXISTS `tblauftragadrabs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragadrabs` (
  `OrderID` double DEFAULT '0',
  `FirmaS` varchar(50) DEFAULT NULL,
  `FirmaS2` varchar(50) DEFAULT NULL,
  `LandS` char(3) DEFAULT '',
  `PLZS` varchar(10) DEFAULT '',
  `OrtS` varchar(50) DEFAULT '',
  `StrasseS` varchar(50) DEFAULT '',
  `StrNrS` varchar(10) DEFAULT '',
  `TelefonVWS` varchar(20) DEFAULT '',
  `TelefonNrS` varchar(20) DEFAULT '',
  `TelefaxNrS` varchar(20) DEFAULT '',
  `Verladedatum` date DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT '999',
  `ID` double NOT NULL DEFAULT '0',
  `FirmaS3` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FirmaS` (`FirmaS`),
  KEY `FirmaS2` (`FirmaS2`),
  KEY `LandS` (`LandS`),
  KEY `PLZS` (`PLZS`),
  KEY `OrtS` (`OrtS`),
  KEY `StrasseS` (`StrasseS`),
  KEY `Verladedatum` (`Verladedatum`),
  KEY `Locking` (`Locking`),
  KEY `DepotNrED` (`DepotNrED`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragadrempf`
--

DROP TABLE IF EXISTS `tblauftragadrempf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragadrempf` (
  `OrderID` double DEFAULT '0',
  `FirmaD` varchar(50) DEFAULT NULL,
  `FirmaD2` varchar(50) DEFAULT NULL,
  `LandD` char(3) DEFAULT '',
  `PLZD` varchar(10) DEFAULT '',
  `OrtD` varchar(50) DEFAULT '',
  `StrasseD` varchar(50) DEFAULT '',
  `StrNrD` varchar(10) DEFAULT '',
  `TelefonVWD` varchar(20) DEFAULT '',
  `TelefonNrD` varchar(20) DEFAULT '',
  `TelefaxNrD` varchar(20) DEFAULT '',
  `Verladedatum` date DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT '999',
  `ID` double NOT NULL DEFAULT '0',
  `FirmaD3` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FirmaD` (`FirmaD`),
  KEY `LandD` (`LandD`),
  KEY `PLZD` (`PLZD`),
  KEY `OrtD` (`OrtD`),
  KEY `StrasseD` (`StrasseD`),
  KEY `Verladedatum` (`Verladedatum`),
  KEY `Locking` (`Locking`),
  KEY `DepotNrED` (`DepotNrED`),
  KEY `FirmaD2` (`FirmaD3`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragclear`
--

DROP TABLE IF EXISTS `tblauftragclear`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragclear` (
  `orderid` double NOT NULL DEFAULT '0',
  `Belegnummer` double NOT NULL DEFAULT '0',
  `stat` int(10) unsigned NOT NULL DEFAULT '0',
  `Dat` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `statuszaehler` int(11) NOT NULL DEFAULT '0',
  `type` varchar(10) NOT NULL DEFAULT '',
  `ld` int(11) NOT NULL DEFAULT '0',
  `lieferdatum` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ldlkz` char(2) NOT NULL DEFAULT '',
  `KZ_Transportart` int(11) NOT NULL DEFAULT '0',
  `mintolate` int(11) NOT NULL DEFAULT '0',
  `minIODlate` int(11) NOT NULL,
  `LZ` int(11) NOT NULL,
  `AD` int(11) NOT NULL,
  `BadFlag` int(11) DEFAULT NULL,
  `minBMPLate` int(11) DEFAULT NULL,
  `timestamp22` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`orderid`),
  KEY `KZ_Transportart` (`KZ_Transportart`),
  KEY `stat` (`stat`),
  KEY `type` (`type`),
  KEY `lieferdatum` (`lieferdatum`),
  KEY `ldlkz` (`ldlkz`),
  KEY `ld` (`ld`),
  KEY `ad` (`AD`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `BadFlag` (`BadFlag`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragclearfinal`
--

DROP TABLE IF EXISTS `tblauftragclearfinal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragclearfinal` (
  `orderid` double NOT NULL DEFAULT '0',
  `depotnrad` int(11) NOT NULL DEFAULT '0',
  `depotnrabd` int(11) NOT NULL DEFAULT '0',
  `depotnrld` int(11) NOT NULL DEFAULT '0',
  `rlkzad` varchar(5) DEFAULT '',
  `rlkzabd` varchar(5) DEFAULT '',
  `rlkzld` varchar(5) DEFAULT '',
  `verladedatum` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `plzs` varchar(10) DEFAULT '',
  `plzd` varchar(10) DEFAULT '',
  `zones` char(2) DEFAULT '',
  `zoned` char(2) DEFAULT '',
  `ASDOK` int(11) NOT NULL DEFAULT '0',
  `Gelaufen` int(11) NOT NULL DEFAULT '0',
  `dttermin_von` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dttermin` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dtAuslieferung` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ASDDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ASDZeit` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `bestExKost` double NOT NULL DEFAULT '0',
  `bestImpKost` double NOT NULL DEFAULT '0',
  `landd` varchar(5) DEFAULT '',
  `lands` varchar(5) DEFAULT '',
  `belegnummer` double NOT NULL DEFAULT '0',
  `spaetPU` int(11) NOT NULL DEFAULT '0',
  `service` double NOT NULL DEFAULT '0',
  `termin_i` int(11) NOT NULL DEFAULT '0',
  `SameDay` varchar(10) NOT NULL DEFAULT '',
  `fahrzeug` int(11) NOT NULL DEFAULT '0',
  `Feiertagshld` int(11) NOT NULL DEFAULT '0',
  `Feiertagshls` int(11) NOT NULL DEFAULT '0',
  `verpackungsart` int(11) NOT NULL DEFAULT '0',
  `gewichtgesamt` double NOT NULL DEFAULT '0',
  `CLearingArtMaster` int(11) NOT NULL DEFAULT '0',
  `sdgtype` char(1) NOT NULL DEFAULT '',
  `KmDirekt` int(11) NOT NULL DEFAULT '0',
  `BetragAD` double NOT NULL DEFAULT '0',
  `BetragAbD` double NOT NULL DEFAULT '0',
  `BetragLD` double NOT NULL DEFAULT '0',
  `ColliesGesamt` int(11) NOT NULL DEFAULT '0',
  `GewichtBerechnet` double NOT NULL DEFAULT '0',
  `Transportart` int(11) NOT NULL DEFAULT '0',
  `ADUID` varchar(50) DEFAULT NULL,
  `AbDUID` varchar(50) DEFAULT NULL,
  `LDUID` varchar(50) DEFAULT NULL,
  `ADVerbunden` int(11) DEFAULT '0',
  `AbDVerbunden` int(11) DEFAULT '0',
  `LDVerbunden` int(11) DEFAULT '0',
  `Sendungslauf` varchar(45) DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `LeistungsMonat` varchar(7) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `dtCreateAD` datetime DEFAULT NULL,
  `istEinzelPU` int(11) DEFAULT NULL,
  `Importweg` varchar(5) DEFAULT NULL,
  `Hubdurchlaufgewicht` double DEFAULT NULL,
  `SdgisVolGew` int(11) DEFAULT NULL,
  `SdggewKlasse` int(11) DEFAULT NULL,
  `InselZ` int(11) DEFAULT NULL,
  `InselA` int(11) DEFAULT NULL,
  `EmpfaengerName` varchar(50) DEFAULT NULL,
  `ErstPUBelNr` double DEFAULT NULL,
  `FinalisierErrs` varchar(200) DEFAULT NULL,
  `ADSapTaxcode` varchar(5) DEFAULT NULL,
  `AbDSapTaxcode` varchar(5) DEFAULT NULL,
  `LDSapTaxcode` varchar(5) DEFAULT NULL,
  `ADKonvTaxcode` varchar(5) DEFAULT NULL,
  `AbDKonvTaxcode` varchar(5) DEFAULT NULL,
  `LDKonvTaxcode` varchar(5) DEFAULT NULL,
  `ADMwstshl` int(11) DEFAULT NULL,
  `AbDMwstshl` int(11) DEFAULT NULL,
  `LDMwstshl` int(11) DEFAULT NULL,
  `ZoneExS` varchar(5) DEFAULT NULL,
  `ZoneExD` varchar(5) DEFAULT NULL,
  `LandDMwstshl` int(11) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `VerladelinieU` int(11) DEFAULT NULL,
  `VerladelinieZ` int(11) DEFAULT NULL,
  `Zubringerlinie` int(11) DEFAULT NULL,
  `ZubringerlinieU` int(11) DEFAULT NULL,
  `ZubringerlinieZ` int(11) DEFAULT NULL,
  `PCtr` varchar(10) DEFAULT NULL,
  `Versicherungswert` double DEFAULT NULL,
  `FirmaD` varchar(50) DEFAULT NULL,
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `product_spec` varchar(5) DEFAULT NULL,
  `PODDate` datetime DEFAULT NULL,
  `EmpfaengerPOD` varchar(50) DEFAULT NULL,
  `KDNR` int(11) DEFAULT NULL,
  `finalerrsum` varchar(255) DEFAULT NULL,
  `VerladelinieS` varchar(45) DEFAULT NULL,
  `BeladelinieS` varchar(45) DEFAULT NULL,
  `ZeitoptionAb` int(11) DEFAULT NULL,
  `ZeitoptionZu` int(11) DEFAULT NULL,
  `ServiceZeitSvon` datetime DEFAULT NULL,
  `ServiceZeitSbis` datetime DEFAULT NULL,
  `ServiceZeitDvon` datetime DEFAULT NULL,
  `ServiceZeitDbis` datetime DEFAULT NULL,
  `AbZuFlags` int(11) DEFAULT NULL,
  `istEinzelZU` int(11) DEFAULT NULL,
  `ErstZUBelNr` double DEFAULT NULL,
  PRIMARY KEY (`orderid`),
  KEY `belegnummer` (`belegnummer`),
  KEY `verladedatum` (`verladedatum`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragclearfinalcod`
--

DROP TABLE IF EXISTS `tblauftragclearfinalcod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragclearfinalcod` (
  `orderid` double NOT NULL,
  `verladedatum` datetime DEFAULT NULL,
  `auslieferdatum` datetime DEFAULT NULL,
  `PreisNN` double DEFAULT NULL,
  `MasterRunAD` int(11) DEFAULT NULL,
  `MasterRunLD` int(11) DEFAULT NULL,
  `RGNrAD` int(11) DEFAULT NULL,
  `RGNrLD` int(11) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `Belegdatum` datetime DEFAULT NULL,
  `DatAbbuchung` datetime DEFAULT NULL,
  `DatUeberweisung` datetime DEFAULT NULL,
  `ZBAD` int(11) DEFAULT NULL,
  `ZBLD` int(11) DEFAULT NULL,
  `SAPLA` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DepotnrAD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `CODAD` varchar(5) DEFAULT NULL,
  `CODLD` varchar(5) DEFAULT NULL,
  `Empfaenger` varchar(45) DEFAULT NULL,
  `AbrechnungsDatum` datetime DEFAULT NULL,
  `rgnrldP` double DEFAULT NULL,
  `rgnrabdP` double DEFAULT NULL,
  `rgnradP` double DEFAULT NULL,
  PRIMARY KEY (`orderid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragclearfinaltmp`
--

DROP TABLE IF EXISTS `tblauftragclearfinaltmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragclearfinaltmp` (
  `orderid` double NOT NULL DEFAULT '0',
  `depotnrad` int(11) NOT NULL DEFAULT '0',
  `depotnrabd` int(11) NOT NULL DEFAULT '0',
  `depotnrld` int(11) NOT NULL DEFAULT '0',
  `rlkzad` varchar(5) DEFAULT '',
  `rlkzabd` varchar(5) DEFAULT '',
  `rlkzld` varchar(5) DEFAULT '',
  `verladedatum` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `plzs` varchar(10) DEFAULT '',
  `plzd` varchar(10) DEFAULT '',
  `zones` char(2) DEFAULT '',
  `zoned` char(2) DEFAULT '',
  `ASDOK` int(11) NOT NULL DEFAULT '0',
  `Gelaufen` int(11) NOT NULL DEFAULT '0',
  `dttermin_von` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dttermin` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dtAuslieferung` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ASDDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ASDZeit` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `bestExKost` double NOT NULL DEFAULT '0',
  `bestImpKost` double NOT NULL DEFAULT '0',
  `landd` varchar(5) DEFAULT '',
  `lands` varchar(5) DEFAULT '',
  `belegnummer` double NOT NULL DEFAULT '0',
  `spaetPU` int(11) NOT NULL DEFAULT '0',
  `service` double NOT NULL DEFAULT '0',
  `termin_i` int(11) NOT NULL DEFAULT '0',
  `SameDay` varchar(10) NOT NULL DEFAULT '',
  `fahrzeug` int(11) NOT NULL DEFAULT '0',
  `Feiertagshld` int(11) NOT NULL DEFAULT '0',
  `Feiertagshls` int(11) NOT NULL DEFAULT '0',
  `verpackungsart` int(11) NOT NULL DEFAULT '0',
  `gewichtgesamt` double NOT NULL DEFAULT '0',
  `CLearingArtMaster` int(11) NOT NULL DEFAULT '0',
  `sdgtype` char(1) NOT NULL DEFAULT '',
  `KmDirekt` int(11) NOT NULL DEFAULT '0',
  `BetragAD` double NOT NULL DEFAULT '0',
  `BetragAbD` double NOT NULL DEFAULT '0',
  `BetragLD` double NOT NULL DEFAULT '0',
  `ColliesGesamt` int(11) NOT NULL DEFAULT '0',
  `GewichtBerechnet` double NOT NULL DEFAULT '0',
  `Transportart` int(11) NOT NULL DEFAULT '0',
  `ADUID` varchar(50) DEFAULT NULL,
  `AbDUID` varchar(50) DEFAULT NULL,
  `LDUID` varchar(50) DEFAULT NULL,
  `ADVerbunden` int(11) DEFAULT '0',
  `AbDVerbunden` int(11) DEFAULT '0',
  `LDVerbunden` int(11) DEFAULT '0',
  `Sendungslauf` varchar(45) DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `LeistungsMonat` varchar(7) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `dtCreateAD` datetime DEFAULT NULL,
  `istEinzelPU` int(11) DEFAULT NULL,
  `Importweg` varchar(5) DEFAULT NULL,
  `Hubdurchlaufgewicht` double DEFAULT NULL,
  `SdgisVolGew` int(11) DEFAULT NULL,
  `SdggewKlasse` int(11) DEFAULT NULL,
  `InselZ` int(11) DEFAULT NULL,
  `InselA` int(11) DEFAULT NULL,
  `EmpfaengerName` varchar(50) DEFAULT NULL,
  `ErstPUBelNr` double DEFAULT NULL,
  `FinalisierErrs` varchar(200) DEFAULT NULL,
  `ADSapTaxcode` varchar(5) DEFAULT NULL,
  `AbDSapTaxcode` varchar(5) DEFAULT NULL,
  `LDSapTaxcode` varchar(5) DEFAULT NULL,
  `ADKonvTaxcode` varchar(5) DEFAULT NULL,
  `AbDKonvTaxcode` varchar(5) DEFAULT NULL,
  `LDKonvTaxcode` varchar(5) DEFAULT NULL,
  `ADMwstshl` int(11) DEFAULT NULL,
  `AbDMwstshl` int(11) DEFAULT NULL,
  `LDMwstshl` int(11) DEFAULT NULL,
  `ZoneExS` varchar(5) DEFAULT NULL,
  `ZoneExD` varchar(5) DEFAULT NULL,
  `LandDMwstshl` int(11) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `VerladelinieU` int(11) DEFAULT NULL,
  `VerladelinieZ` int(11) DEFAULT NULL,
  `Zubringerlinie` int(11) DEFAULT NULL,
  `ZubringerlinieU` int(11) DEFAULT NULL,
  `ZubringerlinieZ` int(11) DEFAULT NULL,
  `PCtr` varchar(10) DEFAULT NULL,
  `ClientID` int(10) unsigned NOT NULL,
  `Versicherungswert` double DEFAULT NULL,
  `product_spec` varchar(5) DEFAULT NULL,
  `KDNR` int(11) DEFAULT NULL,
  `VerladelinieS` varchar(45) DEFAULT NULL,
  `BeladelinieS` varchar(45) DEFAULT NULL,
  `ZeitoptionAb` int(11) DEFAULT NULL,
  `ZeitoptionZu` int(11) DEFAULT NULL,
  `ServiceZeitSvon` datetime DEFAULT NULL,
  `ServiceZeitSbis` datetime DEFAULT NULL,
  `ServiceZeitDvon` datetime DEFAULT NULL,
  `ServiceZeitDbis` datetime DEFAULT NULL,
  `AbZuFlags` int(11) DEFAULT NULL,
  `istEinzelZU` int(11) DEFAULT NULL,
  `ErstZUBelNr` double DEFAULT NULL,
  `FirmaD` varchar(50) DEFAULT NULL,
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `PODDate` datetime DEFAULT NULL,
  `EmpfaengerPOD` varchar(50) DEFAULT NULL,
  `finalerrsum` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`orderid`,`ClientID`) USING BTREE,
  KEY `belegnummer` (`belegnummer`),
  KEY `verladedatum` (`verladedatum`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcleartmp`
--

DROP TABLE IF EXISTS `tblauftragcleartmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcleartmp` (
  `orderid` double NOT NULL DEFAULT '0',
  `Belegnummer` double NOT NULL DEFAULT '0',
  `stat` int(10) unsigned NOT NULL DEFAULT '0',
  `Dat` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `statuszaehler` int(11) NOT NULL DEFAULT '0',
  `type` varchar(10) NOT NULL DEFAULT '',
  `ld` int(11) NOT NULL DEFAULT '0',
  `lieferdatum` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ldlkz` char(2) NOT NULL DEFAULT '',
  `KZ_Transportart` int(11) NOT NULL DEFAULT '0',
  `mintolate` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`orderid`),
  KEY `KZ_Transportart` (`KZ_Transportart`),
  KEY `stat` (`stat`),
  KEY `type` (`type`),
  KEY `lieferdatum` (`lieferdatum`),
  KEY `ldlkz` (`ldlkz`),
  KEY `ld` (`ld`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcollies`
--

DROP TABLE IF EXISTS `tblauftragcollies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcollies` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL AUTO_INCREMENT,
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `Laenge` smallint(6) NOT NULL DEFAULT '0',
  `Breite` smallint(6) NOT NULL DEFAULT '0',
  `Hoehe` smallint(6) NOT NULL DEFAULT '0',
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double NOT NULL DEFAULT '0',
  `VerpackungsArt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bemerkung` varchar(255) DEFAULT '',
  `Satzart` tinyint(1) unsigned zerofill NOT NULL DEFAULT '2',
  `Sendungsnummer` varchar(11) NOT NULL DEFAULT '',
  `Laufende_Nummer` tinyint(2) DEFAULT NULL,
  `Frei1` varchar(4) DEFAULT NULL,
  `Frei2` varchar(6) NOT NULL DEFAULT '0',
  `Packstueckart` tinyint(2) unsigned zerofill NOT NULL DEFAULT '00',
  `Frei3` varchar(8) DEFAULT NULL,
  `Frei4` double DEFAULT NULL,
  `Referenz_Teilretoure` int(10) unsigned zerofill DEFAULT NULL,
  `Frei5` varchar(4) DEFAULT NULL,
  `i_scan` int(1) DEFAULT NULL,
  `Rollkartennummer` varchar(20) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `TourNr2` int(11) DEFAULT NULL,
  `dtEingang2` datetime DEFAULT NULL,
  `dtAusgang2` datetime DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `dtAusgangDepot2` datetime DEFAULT NULL,
  `dtEingangHup2` datetime DEFAULT NULL,
  `dtAusgangHup2` datetime DEFAULT NULL,
  `ueD2H2` int(1) DEFAULT NULL,
  `nueD2H2` int(1) DEFAULT NULL,
  `ueH2D2` int(1) DEFAULT NULL,
  `nueH2D2` int(1) DEFAULT NULL,
  `mydepotid2` int(11) DEFAULT NULL,
  `Timestamp22` datetime DEFAULT NULL,
  `AuslieferDatum2` datetime DEFAULT NULL,
  `AuslieferZeit2` datetime DEFAULT NULL,
  `Empfaenger2` varchar(35) DEFAULT NULL,
  `Verladelinie` double DEFAULT NULL,
  `dtEingangHup3` datetime DEFAULT NULL,
  `dtAusgangHup3` datetime DEFAULT NULL,
  `rKM` double DEFAULT NULL,
  `rkLR` double DEFAULT NULL,
  `RollkartennummerD` double DEFAULT NULL,
  `dtLagereingang` datetime DEFAULT NULL,
  `dtLagerausgang` datetime DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `Beladelinie` double DEFAULT NULL,
  `LadelistennummerD` double DEFAULT NULL,
  `bmpFileName` varchar(100) DEFAULT NULL,
  `mydepotabd` int(11) DEFAULT NULL,
  `cReferenz` varchar(30) DEFAULT NULL,
  `RUP` varchar(3) DEFAULT NULL,
  `RUP_org` varchar(3) DEFAULT NULL,
  `BagBelegNrC` double DEFAULT NULL,
  `BagIDNrC` double DEFAULT NULL,
  `PlombennummerC` double DEFAULT NULL,
  `PlomberNrC` double DEFAULT NULL,
  `PlombenNrC` double DEFAULT NULL,
  `BagBelegNrAbC` double DEFAULT NULL,
  `val_adr` varchar(35) DEFAULT NULL,
  `lieferstatus` smallint(6) NOT NULL DEFAULT '0',
  `lieferfehler` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`OrderID`,`OrderPos`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rollkartennummer` (`Rollkartennummer`),
  KEY `iscan` (`i_scan`),
  KEY `OrderID` (`OrderID`),
  KEY `OrderPos` (`OrderPos`),
  KEY `Timestamp` (`Timestamp`),
  KEY `dtEingang` (`dtEingang2`),
  KEY `TourNr` (`TourNr2`),
  KEY `dtAusgang` (`dtAusgang2`),
  KEY `dtEingangDepot` (`dtEingangDepot2`),
  KEY `dtAusgangDepot` (`dtAusgangDepot2`),
  KEY `mydepotid` (`mydepotid2`),
  KEY `Timestamp2` (`Timestamp22`),
  KEY `Verladelinie` (`Verladelinie`),
  KEY `dtEingangHup3` (`dtEingangHup3`),
  KEY `dtAusgangHup3` (`dtAusgangHup3`),
  KEY `Frei2` (`Frei2`),
  KEY `Frei3` (`Frei3`),
  KEY `Frei4` (`Frei4`),
  KEY `RollkartennummerD` (`RollkartennummerD`),
  KEY `Beladelinie` (`Beladelinie`),
  KEY `LadelistennummerD` (`LadelistennummerD`),
  KEY `mydepotabd` (`mydepotabd`),
  KEY `cReferenz` (`cReferenz`),
  KEY `RUP` (`RUP`),
  KEY `BagIDNrC` (`BagIDNrC`),
  KEY `BagBelegNrC` (`BagBelegNrC`),
  KEY `BagBelegNrAbC` (`BagBelegNrAbC`),
  KEY `VerpackungsArt` (`VerpackungsArt`)
) ENGINE=MyISAM AUTO_INCREMENT=32768 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcolliesbmp`
--

DROP TABLE IF EXISTS `tblauftragcolliesbmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcolliesbmp` (
  `CollieBelegNr` double NOT NULL,
  `bmp` blob,
  `file_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`CollieBelegNr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcolliesclearfinal`
--

DROP TABLE IF EXISTS `tblauftragcolliesclearfinal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcolliesclearfinal` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL AUTO_INCREMENT,
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `Laenge` smallint(6) NOT NULL DEFAULT '0',
  `Breite` smallint(6) NOT NULL DEFAULT '0',
  `Hoehe` smallint(6) NOT NULL DEFAULT '0',
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double NOT NULL DEFAULT '0',
  `VerpackungsArt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bemerkung` varchar(255) DEFAULT '',
  `Satzart` tinyint(1) unsigned zerofill NOT NULL DEFAULT '2',
  `Sendungsnummer` varchar(11) NOT NULL DEFAULT '',
  `Laufende_Nummer` tinyint(2) DEFAULT '0',
  `Frei1` varchar(4) DEFAULT NULL,
  `Frei2` varchar(6) DEFAULT NULL,
  `Packstueckart` tinyint(2) unsigned zerofill DEFAULT '00',
  `Frei3` varchar(8) DEFAULT NULL,
  `Frei4` double DEFAULT NULL,
  `Referenz_Teilretoure` int(10) unsigned zerofill DEFAULT '0000000000',
  `Frei5` varchar(4) DEFAULT NULL,
  `Frei6` varchar(6) DEFAULT NULL,
  `i_scan` int(1) DEFAULT '0',
  `Rollkartennummer` varchar(20) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `Belegnummer_gut` tinyint(4) DEFAULT NULL,
  `TourNr2` int(11) DEFAULT '0',
  `dtEingang2` datetime DEFAULT NULL,
  `dtAusgang2` datetime DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `dtAusgangDepot2` datetime DEFAULT NULL,
  `dtEingangHup2` datetime DEFAULT NULL,
  `dtAusgangHup2` datetime DEFAULT NULL,
  `ueD2H2` int(1) DEFAULT '-1',
  `nueD2H2` int(1) DEFAULT '0',
  `ueH2D2` int(1) DEFAULT '-1',
  `nueH2D2` int(1) DEFAULT '0',
  `mydepotid2` int(11) DEFAULT '0',
  `Timestamp22` datetime DEFAULT '0000-00-00 00:00:00',
  `AuslieferDatum2` datetime DEFAULT NULL,
  `AuslieferZeit2` datetime DEFAULT NULL,
  `Empfaenger2` varchar(35) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `dtEingangHup3` datetime DEFAULT NULL,
  `dtAusgangHup3` datetime DEFAULT NULL,
  `rKM` double DEFAULT NULL,
  `rkLR` double DEFAULT NULL,
  `RollkartennummerD` double DEFAULT NULL,
  `dtLagereingang` datetime DEFAULT NULL,
  `dtLagerausgang` datetime DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `isVolGew` int(11) DEFAULT NULL,
  `gewKlasse` int(11) DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`OrderPos`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rollkartennummer` (`Rollkartennummer`),
  KEY `iscan` (`i_scan`),
  KEY `OrderID` (`OrderID`),
  KEY `OrderPos` (`OrderPos`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `dtEingang` (`dtEingang2`),
  KEY `TourNr` (`TourNr2`),
  KEY `dtAusgang` (`dtAusgang2`),
  KEY `dtEingangDepot` (`dtEingangDepot2`),
  KEY `dtAusgangDepot` (`dtAusgangDepot2`),
  KEY `ueD2H` (`ueD2H2`),
  KEY `nueD2H` (`nueD2H2`),
  KEY `ueH2D` (`ueH2D2`),
  KEY `nueH2D` (`nueH2D2`),
  KEY `mydepotid` (`mydepotid2`),
  KEY `Timestamp2` (`Timestamp22`),
  KEY `Verladelinie` (`Verladelinie`),
  KEY `dtEingangHup3` (`dtEingangHup3`),
  KEY `dtAusgangHup3` (`dtAusgangHup3`),
  KEY `Frei2` (`Frei2`),
  KEY `Frei3` (`Frei3`),
  KEY `Frei4` (`Frei4`),
  KEY `RollkartennummerD` (`RollkartennummerD`),
  KEY `rkM` (`rKM`),
  KEY `rkLR` (`rkLR`),
  KEY `dtLagereingang` (`dtLagereingang`),
  KEY `dtLagerausgang` (`dtLagerausgang`),
  KEY `Lagerplatz` (`Lagerplatz`)
) ENGINE=MyISAM AUTO_INCREMENT=32768 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcolliesclearfinaltmp`
--

DROP TABLE IF EXISTS `tblauftragcolliesclearfinaltmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcolliesclearfinaltmp` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL AUTO_INCREMENT,
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `Laenge` smallint(6) NOT NULL DEFAULT '0',
  `Breite` smallint(6) NOT NULL DEFAULT '0',
  `Hoehe` smallint(6) NOT NULL DEFAULT '0',
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double NOT NULL DEFAULT '0',
  `VerpackungsArt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bemerkung` varchar(255) DEFAULT '',
  `Satzart` tinyint(1) unsigned zerofill NOT NULL DEFAULT '2',
  `Sendungsnummer` varchar(11) NOT NULL DEFAULT '',
  `Laufende_Nummer` tinyint(2) DEFAULT '0',
  `Frei1` varchar(4) DEFAULT NULL,
  `Frei2` varchar(6) DEFAULT NULL,
  `Packstueckart` tinyint(2) unsigned zerofill DEFAULT '00',
  `Frei3` varchar(8) DEFAULT NULL,
  `Frei4` double DEFAULT NULL,
  `Referenz_Teilretoure` int(10) unsigned zerofill DEFAULT '0000000000',
  `Frei5` varchar(4) DEFAULT NULL,
  `Frei6` varchar(6) DEFAULT NULL,
  `i_scan` int(1) DEFAULT '0',
  `Rollkartennummer` varchar(20) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `Belegnummer_gut` tinyint(4) DEFAULT NULL,
  `TourNr2` int(11) DEFAULT '0',
  `dtEingang2` datetime DEFAULT NULL,
  `dtAusgang2` datetime DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `dtAusgangDepot2` datetime DEFAULT NULL,
  `dtEingangHup2` datetime DEFAULT NULL,
  `dtAusgangHup2` datetime DEFAULT NULL,
  `ueD2H2` int(1) DEFAULT '-1',
  `nueD2H2` int(1) DEFAULT '0',
  `ueH2D2` int(1) DEFAULT '-1',
  `nueH2D2` int(1) DEFAULT '0',
  `mydepotid2` int(11) DEFAULT '0',
  `Timestamp22` datetime DEFAULT '0000-00-00 00:00:00',
  `AuslieferDatum2` datetime DEFAULT NULL,
  `AuslieferZeit2` datetime DEFAULT NULL,
  `Empfaenger2` varchar(35) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `dtEingangHup3` datetime DEFAULT NULL,
  `dtAusgangHup3` datetime DEFAULT NULL,
  `rKM` double DEFAULT NULL,
  `rkLR` double DEFAULT NULL,
  `RollkartennummerD` double DEFAULT NULL,
  `dtLagereingang` datetime DEFAULT NULL,
  `dtLagerausgang` datetime DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `isVolGew` int(11) DEFAULT NULL,
  `gewKlasse` int(11) DEFAULT NULL,
  `clientid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`OrderID`,`OrderPos`,`clientid`) USING BTREE,
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rollkartennummer` (`Rollkartennummer`),
  KEY `iscan` (`i_scan`),
  KEY `OrderID` (`OrderID`),
  KEY `OrderPos` (`OrderPos`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `dtEingang` (`dtEingang2`),
  KEY `TourNr` (`TourNr2`),
  KEY `dtAusgang` (`dtAusgang2`),
  KEY `dtEingangDepot` (`dtEingangDepot2`),
  KEY `dtAusgangDepot` (`dtAusgangDepot2`),
  KEY `ueD2H` (`ueD2H2`),
  KEY `nueD2H` (`nueD2H2`),
  KEY `ueH2D` (`ueH2D2`),
  KEY `nueH2D` (`nueH2D2`),
  KEY `mydepotid` (`mydepotid2`),
  KEY `Timestamp2` (`Timestamp22`),
  KEY `Verladelinie` (`Verladelinie`),
  KEY `dtEingangHup3` (`dtEingangHup3`),
  KEY `dtAusgangHup3` (`dtAusgangHup3`),
  KEY `Frei2` (`Frei2`),
  KEY `Frei3` (`Frei3`),
  KEY `Frei4` (`Frei4`),
  KEY `RollkartennummerD` (`RollkartennummerD`),
  KEY `rkM` (`rKM`),
  KEY `rkLR` (`rkLR`),
  KEY `dtLagereingang` (`dtLagereingang`),
  KEY `dtLagerausgang` (`dtLagerausgang`),
  KEY `Lagerplatz` (`Lagerplatz`)
) ENGINE=MyISAM AUTO_INCREMENT=432 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcolliesnoterfass`
--

DROP TABLE IF EXISTS `tblauftragcolliesnoterfass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcolliesnoterfass` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL AUTO_INCREMENT,
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `Laenge` smallint(6) NOT NULL DEFAULT '0',
  `Breite` smallint(6) NOT NULL DEFAULT '0',
  `Hoehe` smallint(6) NOT NULL DEFAULT '0',
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double NOT NULL DEFAULT '0',
  `VerpackungsArt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bemerkung` varchar(255) DEFAULT '',
  `Satzart` tinyint(1) unsigned zerofill NOT NULL DEFAULT '2',
  `Sendungsnummer` varchar(11) NOT NULL DEFAULT '',
  `Laufende_Nummer` tinyint(2) DEFAULT '0',
  `Frei1` varchar(4) DEFAULT NULL,
  `Frei2` varchar(6) NOT NULL DEFAULT '0',
  `Packstueckart` tinyint(2) unsigned zerofill DEFAULT '00',
  `Frei3` varchar(8) DEFAULT NULL,
  `Frei4` varchar(11) DEFAULT NULL,
  `Referenz_Teilretoure` int(10) unsigned zerofill DEFAULT '0000000000',
  `Frei5` varchar(4) DEFAULT NULL,
  `Frei6` varchar(6) DEFAULT NULL,
  `i_scan` int(1) DEFAULT '0',
  `Rollkartennummer` varchar(20) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `Belegnummer_gut` tinyint(4) DEFAULT NULL,
  `TourNr2` int(11) DEFAULT '0',
  `dtEingang2` datetime DEFAULT NULL,
  `dtAusgang2` datetime DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `dtAusgangDepot2` datetime DEFAULT NULL,
  `dtEingangHup2` datetime DEFAULT NULL,
  `dtAusgangHup2` datetime DEFAULT NULL,
  `ueD2H2` int(1) DEFAULT '-1',
  `nueD2H2` int(1) DEFAULT '0',
  `ueH2D2` int(1) DEFAULT '-1',
  `nueH2D2` int(1) DEFAULT '0',
  `mydepotid2` int(11) DEFAULT '0',
  `Timestamp22` datetime DEFAULT '0000-00-00 00:00:00',
  `AuslieferDatum2` datetime DEFAULT NULL,
  `AuslieferZeit2` datetime DEFAULT NULL,
  `Empfaenger2` varchar(35) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `dtEingangHup3` datetime DEFAULT NULL,
  `dtAusgangHup3` datetime DEFAULT NULL,
  `uebertragen` int(1) DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`OrderPos`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rollkartennummer` (`Rollkartennummer`),
  KEY `iscan` (`i_scan`),
  KEY `OrderID` (`OrderID`),
  KEY `OrderPos` (`OrderPos`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `dtEingang` (`dtEingang2`),
  KEY `TourNr` (`TourNr2`),
  KEY `dtAusgang` (`dtAusgang2`),
  KEY `dtEingangDepot` (`dtEingangDepot2`),
  KEY `dtAusgangDepot` (`dtAusgangDepot2`),
  KEY `dtEingangHup` (`dtEingangHup2`),
  KEY `dtAusgangHup` (`dtAusgangHup2`),
  KEY `ueD2H` (`ueD2H2`),
  KEY `nueD2H` (`nueD2H2`),
  KEY `ueH2D` (`ueH2D2`),
  KEY `nueH2D` (`nueH2D2`),
  KEY `mydepotid` (`mydepotid2`),
  KEY `Timestamp2` (`Timestamp22`),
  KEY `Empfaenger` (`Empfaenger2`),
  KEY `Verladelinie` (`Verladelinie`),
  KEY `dtEingangHup2` (`dtEingangHup2`),
  KEY `dtAusgangHup2` (`dtAusgangHup2`),
  KEY `dtEingangHup3` (`dtEingangHup3`),
  KEY `dtAusgangHup3` (`dtAusgangHup3`),
  KEY `GewichtReal` (`GewichtReal`),
  KEY `uebertragen` (`uebertragen`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcolliestemp`
--

DROP TABLE IF EXISTS `tblauftragcolliestemp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcolliestemp` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL AUTO_INCREMENT,
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `Laenge` smallint(6) NOT NULL DEFAULT '0',
  `Breite` smallint(6) NOT NULL DEFAULT '0',
  `Hoehe` smallint(6) NOT NULL DEFAULT '0',
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double NOT NULL DEFAULT '0',
  `VerpackungsArt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bemerkung` varchar(255) DEFAULT '',
  `Satzart` tinyint(1) unsigned zerofill NOT NULL DEFAULT '2',
  `Sendungsnummer` varchar(11) NOT NULL DEFAULT '',
  `Laufende_Nummer` tinyint(2) DEFAULT '0',
  `Frei1` varchar(4) DEFAULT NULL,
  `Frei2` varchar(6) DEFAULT NULL,
  `Packstueckart` tinyint(2) unsigned zerofill DEFAULT '00',
  `Frei3` varchar(8) DEFAULT NULL,
  `Frei4` varchar(11) DEFAULT NULL,
  `Referenz_Teilretoure` int(10) unsigned zerofill DEFAULT '0000000000',
  `Frei5` varchar(4) DEFAULT NULL,
  `i_scan` int(1) DEFAULT '0',
  `Rollkartennummer` varchar(20) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `Belegnummer_gut` tinyint(4) DEFAULT NULL,
  `TourNr2` int(11) DEFAULT '0',
  `dtEingang2` datetime DEFAULT NULL,
  `dtAusgang2` datetime DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `dtAusgangDepot2` datetime DEFAULT NULL,
  `dtEingangHup2` datetime DEFAULT NULL,
  `dtAusgangHup2` datetime DEFAULT NULL,
  `ueD2H2` int(1) DEFAULT '-1',
  `nueD2H2` int(1) DEFAULT '0',
  `ueH2D2` int(1) DEFAULT '-1',
  `nueH2D2` int(1) DEFAULT '0',
  `mydepotid2` int(11) DEFAULT '0',
  `Timestamp22` datetime DEFAULT '0000-00-00 00:00:00',
  `AuslieferDatum2` datetime DEFAULT NULL,
  `AuslieferZeit2` datetime DEFAULT NULL,
  `Empfaenger2` varchar(35) DEFAULT NULL,
  `Verladelinie` int(11) DEFAULT NULL,
  `dtEingangHup3` datetime DEFAULT NULL,
  `dtAusgangHup3` datetime DEFAULT NULL,
  `uebertragen` int(1) DEFAULT NULL,
  `mydepotabd` int(11) DEFAULT NULL,
  `cReferenz` varchar(30) DEFAULT NULL,
  `RUP` varchar(5) DEFAULT NULL,
  `frei6` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`OrderPos`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `rollkartennummer` (`Rollkartennummer`),
  KEY `iscan` (`i_scan`),
  KEY `OrderID` (`OrderID`),
  KEY `OrderPos` (`OrderPos`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `dtEingang` (`dtEingang2`),
  KEY `TourNr` (`TourNr2`),
  KEY `dtAusgang` (`dtAusgang2`),
  KEY `dtEingangDepot` (`dtEingangDepot2`),
  KEY `dtAusgangDepot` (`dtAusgangDepot2`),
  KEY `dtEingangHup` (`dtEingangHup2`),
  KEY `dtAusgangHup` (`dtAusgangHup2`),
  KEY `ueD2H` (`ueD2H2`),
  KEY `nueD2H` (`nueD2H2`),
  KEY `ueH2D` (`ueH2D2`),
  KEY `nueH2D` (`nueH2D2`),
  KEY `mydepotid` (`mydepotid2`),
  KEY `Timestamp2` (`Timestamp22`),
  KEY `Empfaenger` (`Empfaenger2`),
  KEY `Verladelinie` (`Verladelinie`),
  KEY `dtEingangHup2` (`dtEingangHup2`),
  KEY `dtAusgangHup2` (`dtAusgangHup2`),
  KEY `dtEingangHup3` (`dtEingangHup3`),
  KEY `dtAusgangHup3` (`dtAusgangHup3`),
  KEY `GewichtReal` (`GewichtReal`),
  KEY `uebertragen` (`uebertragen`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcolliestmp`
--

DROP TABLE IF EXISTS `tblauftragcolliestmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcolliestmp` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL DEFAULT '0',
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `Laenge` smallint(6) NOT NULL DEFAULT '0',
  `Breite` smallint(6) NOT NULL DEFAULT '0',
  `Hoehe` smallint(6) NOT NULL DEFAULT '0',
  `GewichtReal` double NOT NULL DEFAULT '0',
  `GewichtLBH` double NOT NULL DEFAULT '0',
  `GewichtEffektiv` double NOT NULL DEFAULT '0',
  `VerpackungsArt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bemerkung` varchar(255) DEFAULT NULL,
  `Satzart` tinyint(1) unsigned zerofill NOT NULL DEFAULT '2',
  `Sendungsnummer` varchar(11) NOT NULL DEFAULT '0',
  `Laufende_Nummer` tinyint(2) DEFAULT NULL,
  `Frei1` varchar(4) DEFAULT NULL,
  `Frei2` varchar(6) NOT NULL DEFAULT '0',
  `Packstueckart` tinyint(2) unsigned zerofill DEFAULT NULL,
  `Frei3` varchar(8) DEFAULT NULL,
  `Frei4` double DEFAULT NULL,
  `Referenz_Teilretoure` int(10) unsigned zerofill DEFAULT NULL,
  `Frei5` varchar(4) DEFAULT NULL,
  `i_scan` int(1) DEFAULT NULL,
  `Rollkartennummer` varchar(20) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `TourNr2` int(11) DEFAULT NULL,
  `dtEingang2` datetime DEFAULT NULL,
  `dtAusgang2` datetime DEFAULT NULL,
  `dtEingangDepot2` datetime DEFAULT NULL,
  `dtAusgangDepot2` datetime DEFAULT NULL,
  `dtEingangHup2` datetime DEFAULT NULL,
  `dtAusgangHup2` datetime DEFAULT NULL,
  `ueD2H2` int(1) DEFAULT NULL,
  `nueD2H2` int(1) DEFAULT NULL,
  `ueH2D2` int(1) DEFAULT NULL,
  `nueH2D2` int(1) DEFAULT NULL,
  `mydepotid2` int(11) DEFAULT NULL,
  `Timestamp22` datetime DEFAULT NULL,
  `AuslieferDatum2` datetime DEFAULT NULL,
  `AuslieferZeit2` datetime DEFAULT NULL,
  `Empfaenger2` varchar(35) DEFAULT NULL,
  `Verladelinie` double DEFAULT NULL,
  `dtEingangHup3` datetime DEFAULT NULL,
  `dtAusgangHup3` datetime DEFAULT NULL,
  `rKM` double DEFAULT NULL,
  `rkLR` double DEFAULT NULL,
  `RollkartennummerD` double DEFAULT NULL,
  `ClientID` int(11) NOT NULL DEFAULT '0',
  `dtLagereingang` datetime DEFAULT NULL,
  `dtLagerausgang` datetime DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `Beladelinie` double DEFAULT NULL,
  `LadelistennummerD` double DEFAULT NULL,
  `bmpFileName` varchar(100) DEFAULT NULL,
  `mydepotabd` int(11) DEFAULT NULL,
  `cReferenz` varchar(30) DEFAULT NULL,
  `RUP` varchar(5) DEFAULT NULL,
  `RUP_org` varchar(5) DEFAULT NULL,
  `BagBelegNrC` double DEFAULT NULL,
  `BagIDNrC` double DEFAULT NULL,
  `PlombennummerC` double DEFAULT NULL,
  `PlomberNrC` double DEFAULT NULL,
  `PlombenNrC` double DEFAULT NULL,
  `BagBelegNrAbC` double DEFAULT NULL,
  `val_adr` varchar(35) DEFAULT NULL,
  `lieferstatus` smallint(6) NOT NULL DEFAULT '0',
  `lieferfehler` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`OrderID`,`OrderPos`,`ClientID`),
  KEY `CollieBelegNr` (`CollieBelegNr`),
  KEY `iscan` (`i_scan`),
  KEY `OrderID` (`OrderID`),
  KEY `OrderPos` (`OrderPos`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `dtEingang` (`dtEingang2`),
  KEY `TourNr` (`TourNr2`),
  KEY `dtAusgang` (`dtAusgang2`),
  KEY `dtEingangDepot` (`dtEingangDepot2`),
  KEY `dtAusgangDepot` (`dtAusgangDepot2`),
  KEY `mydepotid` (`mydepotid2`),
  KEY `Timestamp2` (`Timestamp22`),
  KEY `Verladelinie` (`Verladelinie`),
  KEY `dtEingangHup3` (`dtEingangHup3`),
  KEY `dtAusgangHup3` (`dtAusgangHup3`),
  KEY `Frei2` (`Frei2`),
  KEY `Frei3` (`Frei3`),
  KEY `Frei4` (`Frei4`),
  KEY `RollkartennummerD` (`RollkartennummerD`),
  KEY `ClientID` (`ClientID`),
  KEY `Beladelinie` (`Beladelinie`),
  KEY `LadelistennummerD` (`LadelistennummerD`),
  KEY `BagBelegNrC` (`BagBelegNrC`),
  KEY `BagBelegNrAbC` (`BagBelegNrAbC`),
  KEY `VerpackungsArt` (`VerpackungsArt`)
) ENGINE=MyISAM AUTO_INCREMENT=32768 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragcollieswaage`
--

DROP TABLE IF EXISTS `tblauftragcollieswaage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragcollieswaage` (
  `CollieBelegNr` double NOT NULL DEFAULT '0',
  `waagetext` varchar(250) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Userid` int(11) DEFAULT '0',
  `Karte` int(11) DEFAULT NULL,
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `lfdNr` int(11) DEFAULT '0',
  `timestampDevice` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1620487 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftraginfo`
--

DROP TABLE IF EXISTS `tblauftraginfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftraginfo` (
  `OrderID` double NOT NULL DEFAULT '0',
  `Satzart` char(1) DEFAULT 'I',
  `Info_Kennzeichen` char(1) DEFAULT 'S',
  `Kundennummer` int(9) DEFAULT '0',
  `Sendungsnummer` varchar(11) DEFAULT '00000000000',
  `Laufende_Satznummer` smallint(3) NOT NULL DEFAULT '0',
  `Info_Text` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`Laufende_Satznummer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragnoterfass`
--

DROP TABLE IF EXISTS `tblauftragnoterfass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragnoterfass` (
  `OrderID` double NOT NULL DEFAULT '0',
  `AuftragsID` varchar(25) DEFAULT NULL,
  `GKNr` int(11) DEFAULT '0',
  `Belegnummer` double DEFAULT '0',
  `DepotNrED` int(11) DEFAULT '999',
  `DepotNrAbD` int(11) DEFAULT '0',
  `DepotNrbev` int(11) DEFAULT '0',
  `DepotNrAD` int(11) DEFAULT '999',
  `DepotNrZD` int(11) DEFAULT '0',
  `DepotNrLD` int(11) DEFAULT '0',
  `lockflag` smallint(6) NOT NULL DEFAULT '1',
  `dtCreateAD` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dtSendAD2Z` datetime DEFAULT NULL,
  `dtReceiveAD2Z` datetime DEFAULT NULL,
  `dtSendZ2H` datetime DEFAULT NULL,
  `dtReceiveZ2H` datetime DEFAULT NULL,
  `dtModifyH` datetime DEFAULT NULL,
  `dtSendH2Z` datetime DEFAULT NULL,
  `dtReceiveH2Z` datetime DEFAULT NULL,
  `dtSendZ2ZD` datetime DEFAULT NULL,
  `dtReceiveZ2ZD` datetime DEFAULT NULL,
  `dtModifyZD` datetime DEFAULT NULL,
  `dtTermin_von` datetime DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `dtAuslieferDatum` datetime DEFAULT NULL,
  `dtAuslieferZeit` datetime DEFAULT NULL,
  `Empfaenger` varchar(35) DEFAULT NULL,
  `dtSendZD2Z` datetime DEFAULT NULL,
  `dtReceiveZD2Z` datetime DEFAULT NULL,
  `dtSendZ2AD` datetime DEFAULT NULL,
  `dtReceiveZ2AD` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `KDNR` int(9) DEFAULT '0',
  `FirmaS` varchar(25) DEFAULT NULL,
  `FirmaS2` varchar(25) DEFAULT NULL,
  `LandS` char(3) DEFAULT 'DE',
  `PLZS` varchar(10) DEFAULT '99999',
  `OrtS` varchar(50) DEFAULT NULL,
  `StrasseS` varchar(50) DEFAULT NULL,
  `StrNrS` varchar(10) DEFAULT NULL,
  `TelefonVWS` int(11) DEFAULT '0',
  `TelefonNrS` int(11) DEFAULT '0',
  `TelefaxNrS` int(11) DEFAULT '0',
  `FirmaD` varchar(25) DEFAULT NULL,
  `FirmaD2` varchar(25) DEFAULT NULL,
  `LandD` char(3) DEFAULT 'DE',
  `PLZD` varchar(10) DEFAULT '99999',
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `TelefonVWD` int(11) DEFAULT '0',
  `TelefonNrD` int(11) DEFAULT '0',
  `TelefaxNrD` int(11) DEFAULT '0',
  `ClearingArt` smallint(6) DEFAULT '0',
  `ZuschlagsArt` smallint(6) DEFAULT '0',
  `GewichtGesamt` double DEFAULT '0',
  `PreisEK` double(20,2) DEFAULT '0.00',
  `PreisEG` double(20,2) DEFAULT '0.00',
  `PreisEP` double(20,2) DEFAULT '0.00',
  `PreiseZS` double(20,2) DEFAULT '0.00',
  `ColliesGesamt` smallint(6) DEFAULT '0',
  `PreisNN` double(20,2) DEFAULT '0.00',
  `DatumNN` date DEFAULT '0000-00-00',
  `erhaltenNN` int(11) DEFAULT '0',
  `sendD2Z` int(1) DEFAULT '-1',
  `Feiertag_1` varchar(15) DEFAULT NULL,
  `FeiertagShlD` int(11) DEFAULT NULL,
  `FeiertagShlS` int(11) DEFAULT NULL,
  `SpaetAbholRouting` datetime DEFAULT NULL,
  `SpaetZustellRouting` datetime DEFAULT NULL,
  `Feiertag_2` varchar(15) DEFAULT NULL,
  `ClearingDate` date DEFAULT NULL,
  `ClearingDateMaster` datetime DEFAULT NULL,
  `Satzart` char(1) DEFAULT '1',
  `Referenz` varchar(10) DEFAULT NULL,
  `Referenz2` varchar(15) DEFAULT NULL,
  `Frei` varchar(6) DEFAULT NULL,
  `Adr_Nr_Absender` text,
  `Adr_Nr_Empfaenger` text,
  `Wert` double(8,2) unsigned zerofill DEFAULT '00000.00',
  `Nachnahmebetrag` tinyint(6) unsigned zerofill DEFAULT '000000',
  `Versicherungswert` double(10,2) unsigned zerofill DEFAULT '0000000.00',
  `Frei2` double(8,2) DEFAULT '0.00',
  `Verladedatum` datetime DEFAULT NULL,
  `Lieferdatum` datetime DEFAULT NULL,
  `Lieferzeit_von` datetime DEFAULT NULL,
  `Lieferzeit_bis` datetime DEFAULT NULL,
  `KZ_Fahrzeug` tinyint(2) unsigned zerofill DEFAULT '08',
  `KZ_Transportart` tinyint(2) unsigned zerofill DEFAULT '00',
  `Frei3` char(1) DEFAULT '0',
  `Service` int(10) unsigned zerofill DEFAULT '0000000000',
  `KZServiceLeo` int(10) unsigned DEFAULT NULL,
  `Sendungsstatus` tinyint(4) unsigned zerofill DEFAULT '0001',
  `Ausliefertour` int(4) unsigned zerofill DEFAULT '0000',
  `Routung_Hilfsspalte` varchar(10) DEFAULT NULL,
  `KZ_erweitert` tinyint(2) unsigned DEFAULT NULL,
  `Information1` varchar(40) DEFAULT NULL,
  `Information2` varchar(40) DEFAULT NULL,
  `Inhalt` varchar(30) DEFAULT NULL,
  `Frei4` varchar(20) DEFAULT NULL,
  `Frei5` varchar(40) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `Frei6` varchar(69) DEFAULT NULL,
  `PU_GK_Auftrags_Nr` int(8) DEFAULT NULL,
  `Besteller_Name` varchar(25) DEFAULT NULL,
  `Ladelisten_Nummer` int(8) unsigned zerofill DEFAULT '00000000',
  `Frei7` char(2) DEFAULT NULL,
  `Kennzeichen_Info` char(1) DEFAULT '0',
  `Frei8` char(2) DEFAULT NULL,
  `Frei9` varchar(11) DEFAULT NULL,
  `Frei10` varchar(10) DEFAULT NULL,
  `Anzahl_Teilretouren` tinyint(4) unsigned zerofill DEFAULT '0000',
  `KZ_Zusatz` tinyint(2) unsigned zerofill DEFAULT '00',
  `CR` char(2) DEFAULT 'CR',
  `Preisvereinbarung` double(20,2) DEFAULT '0.00',
  `FahrerNr` int(11) DEFAULT '0',
  `d` int(1) DEFAULT '0',
  `g` int(1) DEFAULT '0',
  `a` int(1) DEFAULT '0',
  `Info_Rollkarte` longtext,
  `Info_Intern` longtext,
  `termin_i` int(1) unsigned zerofill DEFAULT '2',
  `Zone` char(1) DEFAULT NULL,
  `Insel` char(1) DEFAULT NULL,
  `Zonea` char(1) DEFAULT NULL,
  `Insela` char(1) DEFAULT NULL,
  `69_WZ_Abholung` char(2) DEFAULT '00',
  `69_Leer1` varchar(10) DEFAULT NULL,
  `69_Betrag_Abrechnung_Kunde` double(8,2) unsigned zerofill DEFAULT '00000.00',
  `69_Leer2` varchar(16) DEFAULT NULL,
  `69_Zerofill1` int(3) unsigned zerofill DEFAULT '000',
  `69_KostenBahnFlug` double(5,2) unsigned zerofill DEFAULT '00.00',
  `69_Zerofill2` int(8) unsigned zerofill DEFAULT '00000000',
  `69_KmDirekt` int(4) unsigned zerofill DEFAULT '0000',
  `69_KZ_SameDay` tinyint(1) unsigned zerofill DEFAULT '0',
  `69_KZ_Serviceart` tinyint(2) unsigned zerofill DEFAULT '00',
  `69_Undefined` tinyint(1) unsigned zerofill DEFAULT '0',
  `69_Servicezeit_XChange` char(2) DEFAULT '00',
  `69_Avis` char(1) DEFAULT NULL,
  `69_N2` char(1) DEFAULT 'N',
  `69_Leer3` varchar(4) DEFAULT NULL,
  `69_Fremdavis` char(1) DEFAULT 'N',
  `Betrag_Importkosten` double(8,2) DEFAULT '0.00',
  `Betrag_Importkosten_best` double(8,2) DEFAULT '0.00',
  `Betrag_Exportkosten` double(8,2) DEFAULT '0.00',
  `Betrag_Exportkosten_best` double(8,2) DEFAULT '0.00',
  `Leer4` char(1) DEFAULT NULL,
  `Sondervereinbarung` varchar(20) DEFAULT NULL,
  `Importkosten` varchar(20) DEFAULT NULL,
  `ExportkostenPU` varchar(20) DEFAULT NULL,
  `RechnungsNr` varchar(20) DEFAULT NULL,
  `Rechnungsbetrag` double(8,2) DEFAULT NULL,
  `RechnungsDatum` date DEFAULT NULL,
  `Satzart_ava` char(3) DEFAULT '1',
  `FirmaDX` varchar(25) DEFAULT NULL,
  `FirmaD2X` varchar(25) DEFAULT NULL,
  `LandDX` char(3) DEFAULT 'DE',
  `PLZDX` varchar(10) DEFAULT '99999',
  `OrtDX` varchar(50) DEFAULT NULL,
  `StrasseDX` varchar(50) DEFAULT NULL,
  `StrNrDX` varchar(10) DEFAULT NULL,
  `dtAuslieferungX` datetime DEFAULT NULL,
  `dtTermin_vonX` datetime DEFAULT NULL,
  `dtTerminX` datetime DEFAULT NULL,
  `frueheste_zustellzeit` varchar(4) DEFAULT NULL,
  `Satzartp` char(1) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `Belegnummer_gut` tinyint(4) DEFAULT NULL,
  `UploadStatus` tinyint(4) NOT NULL DEFAULT '0',
  `SendStatus` tinyint(4) NOT NULL DEFAULT '0',
  `clearingpreis` double(8,2) DEFAULT NULL,
  `SdgArt` varchar(4) DEFAULT 'sdg',
  `OrderID_X` varchar(15) DEFAULT NULL,
  `PZAImageZIP` varchar(60) DEFAULT NULL,
  `IDSdgArt` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `RueckDate` datetime DEFAULT NULL,
  `ClearingArtMaster` smallint(6) DEFAULT '0',
  `ZoneS` char(1) DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `uebertragen` int(1) DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  UNIQUE KEY `DepotNrED` (`DepotNrED`,`DepotNrAD`,`DepotNrZD`,`DepotNrLD`,`OrderID`),
  UNIQUE KEY `DepotNrLD` (`DepotNrLD`,`DepotNrZD`,`DepotNrAD`,`DepotNrED`,`OrderID`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `AuftragsID` (`AuftragsID`),
  KEY `dtAuslieferung` (`dtAuslieferung`),
  KEY `DepotNrZD` (`DepotNrZD`),
  KEY `dtSendZ2ZD` (`dtSendZ2ZD`),
  KEY `Empfaenger` (`Empfaenger`),
  KEY `DepotNrLDx` (`DepotNrLD`),
  KEY `Satzart` (`Satzart`),
  KEY `DepotNrAD` (`DepotNrAD`),
  KEY `DepotNrAbD` (`DepotNrAbD`),
  KEY `plzd` (`PLZD`),
  KEY `OrtD` (`OrtD`),
  KEY `DepotNrEDInd` (`DepotNrED`),
  KEY `dtCreateAD` (`dtCreateAD`),
  KEY `FirmaS` (`FirmaS`),
  KEY `SendStatus` (`SendStatus`),
  KEY `SatzartAvA` (`Satzart_ava`),
  KEY `Referenz` (`Referenz`),
  KEY `lockflag` (`lockflag`),
  KEY `SdgArt` (`SdgArt`),
  KEY `Orderid_X` (`OrderID_X`),
  KEY `Verladedatum` (`Verladedatum`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `IDSdgArt` (`IDSdgArt`),
  KEY `ClearingArt` (`ClearingArt`),
  KEY `ClearingArtMaster` (`ClearingArtMaster`),
  KEY `dtReceiveZ2H` (`dtReceiveZ2H`),
  KEY `dtReceiveZ2ZD` (`dtReceiveZ2ZD`),
  KEY `Timestamp` (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragstabelle`
--

DROP TABLE IF EXISTS `tblauftragstabelle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragstabelle` (
  `FilterID` int(11) NOT NULL DEFAULT '0',
  `ClientID` int(11) NOT NULL DEFAULT '0',
  `Sendungsnummer` varchar(255) DEFAULT NULL,
  `Packstuecknummer` varchar(255) DEFAULT NULL,
  `Kundennummer` varchar(20) DEFAULT NULL,
  `Absender` varchar(25) DEFAULT NULL,
  `Strasse Abs` varchar(50) DEFAULT NULL,
  `LKZ Abs` char(3) DEFAULT NULL,
  `PLZ Abs` varchar(10) DEFAULT NULL,
  `Ort Abs` varchar(50) DEFAULT NULL,
  `Strasse Empf` varchar(50) DEFAULT NULL,
  `LKZ Empf` char(3) DEFAULT NULL,
  `PLZ Empf` varchar(10) DEFAULT NULL,
  `Ort Empf` varchar(50) DEFAULT NULL,
  `Verladedatum` datetime DEFAULT NULL,
  `Auslieferdatum` datetime DEFAULT NULL,
  `Fahrer Nr` varchar(50) DEFAULT NULL,
  `Referenz` varchar(15) DEFAULT NULL,
  `OrderID` double DEFAULT NULL,
  `Ausliefername` varchar(255) DEFAULT NULL,
  `Auslieferzeit` datetime DEFAULT NULL,
  `Lieferzeit_von` datetime DEFAULT NULL,
  `Lieferzeit_bis` datetime DEFAULT NULL,
  `Abholendes Depot` varchar(6) DEFAULT NULL,
  `Auftraggeber` varchar(6) DEFAULT NULL,
  `Lieferdepot` varchar(6) DEFAULT NULL,
  `Empfaenger` varchar(25) DEFAULT NULL,
  `Leo` varchar(10) DEFAULT NULL,
  `Sendungsstatus` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`ClientID`,`FilterID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragtemp`
--

DROP TABLE IF EXISTS `tblauftragtemp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragtemp` (
  `OrderID` double NOT NULL DEFAULT '0',
  `AuftragsID` varchar(25) DEFAULT NULL,
  `GKNr` int(11) DEFAULT '0',
  `Belegnummer` double DEFAULT '0',
  `DepotNrED` int(11) DEFAULT '999',
  `DepotNrAbD` int(11) DEFAULT '0',
  `DepotNrbev` int(11) DEFAULT '0',
  `DepotNrAD` int(11) DEFAULT '999',
  `DepotNrZD` int(11) DEFAULT '0',
  `DepotNrLD` int(11) DEFAULT '0',
  `lockflag` smallint(6) NOT NULL DEFAULT '1',
  `dtCreateAD` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dtSendAD2Z` datetime DEFAULT NULL,
  `dtReceiveAD2Z` datetime DEFAULT NULL,
  `dtSendZ2H` datetime DEFAULT NULL,
  `dtReceiveZ2H` datetime DEFAULT NULL,
  `dtModifyH` datetime DEFAULT NULL,
  `dtSendH2Z` datetime DEFAULT NULL,
  `dtReceiveH2Z` datetime DEFAULT NULL,
  `dtSendZ2ZD` datetime DEFAULT NULL,
  `dtReceiveZ2ZD` datetime DEFAULT NULL,
  `dtModifyZD` datetime DEFAULT NULL,
  `dtTermin_von` datetime DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `dtAuslieferDatum` datetime DEFAULT NULL,
  `dtAuslieferZeit` datetime DEFAULT NULL,
  `Empfaenger` varchar(35) DEFAULT NULL,
  `dtSendZD2Z` datetime DEFAULT NULL,
  `dtReceiveZD2Z` datetime DEFAULT NULL,
  `dtSendZ2AD` datetime DEFAULT NULL,
  `dtReceiveZ2AD` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `KDNR` int(9) DEFAULT '0',
  `FirmaS` varchar(40) DEFAULT NULL,
  `FirmaS2` varchar(40) DEFAULT NULL,
  `LandS` char(3) DEFAULT 'DE',
  `PLZS` varchar(10) DEFAULT '99999',
  `OrtS` varchar(50) DEFAULT NULL,
  `StrasseS` varchar(50) DEFAULT NULL,
  `StrNrS` varchar(10) DEFAULT NULL,
  `TelefonVWS` int(11) DEFAULT '0',
  `TelefonNrS` int(11) DEFAULT '0',
  `TelefaxNrS` int(11) DEFAULT '0',
  `FirmaD` varchar(40) DEFAULT NULL,
  `FirmaD2` varchar(40) DEFAULT NULL,
  `LandD` char(3) DEFAULT 'DE',
  `PLZD` varchar(10) DEFAULT '99999',
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `TelefonVWD` int(11) DEFAULT '0',
  `TelefonNrD` int(11) DEFAULT '0',
  `TelefaxNrD` int(11) DEFAULT '0',
  `ClearingArt` smallint(6) DEFAULT '0',
  `ZuschlagsArt` smallint(6) DEFAULT '0',
  `GewichtGesamt` double DEFAULT '0',
  `PreisEK` double(20,2) DEFAULT '0.00',
  `PreisEG` double(20,2) DEFAULT '0.00',
  `PreisEP` double(20,2) DEFAULT '0.00',
  `PreiseZS` double(20,2) DEFAULT '0.00',
  `ColliesGesamt` smallint(6) DEFAULT '0',
  `PreisNN` double(20,2) DEFAULT '0.00',
  `DatumNN` date DEFAULT '0000-00-00',
  `erhaltenNN` int(11) DEFAULT '0',
  `sendD2Z` int(1) DEFAULT '-1',
  `Feiertag_1` varchar(15) DEFAULT NULL,
  `FeiertagShlD` int(11) DEFAULT NULL,
  `FeiertagShlS` int(11) DEFAULT NULL,
  `SpaetAbholRouting` datetime DEFAULT NULL,
  `SpaetZustellRouting` datetime DEFAULT NULL,
  `Feiertag_2` varchar(15) DEFAULT NULL,
  `ClearingDate` date DEFAULT NULL,
  `ClearingDateMaster` datetime DEFAULT NULL,
  `Satzart` char(1) DEFAULT '1',
  `Referenz` varchar(10) DEFAULT NULL,
  `Referenz2` varchar(15) DEFAULT NULL,
  `Frei` varchar(6) DEFAULT NULL,
  `Adr_Nr_Absender` text,
  `Adr_Nr_Empfaenger` text,
  `Wert` double(8,2) unsigned zerofill DEFAULT '00000.00',
  `Nachnahmebetrag` double DEFAULT NULL,
  `Versicherungswert` double(10,2) unsigned zerofill DEFAULT '0000000.00',
  `Frei2` double(8,2) DEFAULT '0.00',
  `Verladedatum` datetime DEFAULT NULL,
  `Lieferdatum` datetime DEFAULT NULL,
  `Lieferzeit_von` datetime DEFAULT NULL,
  `Lieferzeit_bis` datetime DEFAULT NULL,
  `KZ_Fahrzeug` tinyint(2) unsigned zerofill DEFAULT '08',
  `KZ_Transportart` tinyint(2) unsigned zerofill DEFAULT '00',
  `Frei3` char(1) DEFAULT '0',
  `Service` int(10) unsigned zerofill DEFAULT '0000000000',
  `KZServiceLeo` int(10) unsigned DEFAULT NULL,
  `Sendungsstatus` tinyint(4) unsigned zerofill DEFAULT '0001',
  `Ausliefertour` int(4) unsigned zerofill DEFAULT '0000',
  `Routung_Hilfsspalte` varchar(10) DEFAULT NULL,
  `KZ_erweitert` tinyint(2) unsigned DEFAULT NULL,
  `Information1` varchar(80) DEFAULT NULL,
  `Information2` varchar(40) DEFAULT NULL,
  `Inhalt` varchar(30) DEFAULT NULL,
  `Frei4` varchar(20) DEFAULT NULL,
  `Frei5` varchar(40) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `Frei6` varchar(69) DEFAULT NULL,
  `PU_GK_Auftrags_Nr` int(8) DEFAULT NULL,
  `Besteller_Name` varchar(25) DEFAULT NULL,
  `Ladelisten_Nummer` int(8) unsigned zerofill DEFAULT '00000000',
  `Frei7` char(2) DEFAULT NULL,
  `Kennzeichen_Info` char(1) DEFAULT '0',
  `Frei8` char(2) DEFAULT NULL,
  `Frei9` varchar(11) DEFAULT NULL,
  `Frei10` varchar(10) DEFAULT NULL,
  `Anzahl_Teilretouren` tinyint(4) unsigned zerofill DEFAULT '0000',
  `KZ_Zusatz` tinyint(2) unsigned zerofill DEFAULT '00',
  `CR` char(2) DEFAULT 'CR',
  `Preisvereinbarung` double(20,2) DEFAULT '0.00',
  `FahrerNr` int(11) DEFAULT '0',
  `d` int(1) DEFAULT '0',
  `g` int(1) DEFAULT '0',
  `a` int(1) DEFAULT '0',
  `Info_Rollkarte` longtext,
  `Info_Intern` longtext,
  `termin_i` int(1) unsigned zerofill DEFAULT '2',
  `Zone` char(1) DEFAULT NULL,
  `Insel` char(1) DEFAULT NULL,
  `Zonea` char(1) DEFAULT NULL,
  `Insela` char(1) DEFAULT NULL,
  `69_WZ_Abholung` char(2) DEFAULT '00',
  `69_Leer1` varchar(10) DEFAULT NULL,
  `69_Betrag_Abrechnung_Kunde` double(8,2) unsigned zerofill DEFAULT '00000.00',
  `69_Leer2` varchar(16) DEFAULT NULL,
  `69_Zerofill1` int(3) unsigned zerofill DEFAULT '000',
  `69_KostenBahnFlug` double(5,2) unsigned zerofill DEFAULT '00.00',
  `69_Zerofill2` int(8) unsigned zerofill DEFAULT '00000000',
  `69_KmDirekt` int(4) unsigned zerofill DEFAULT '0000',
  `69_KZ_SameDay` tinyint(1) unsigned zerofill DEFAULT '0',
  `69_KZ_Serviceart` tinyint(2) unsigned zerofill DEFAULT '00',
  `69_Undefined` tinyint(1) unsigned zerofill DEFAULT '0',
  `69_Servicezeit_XChange` char(2) DEFAULT '00',
  `69_Avis` char(1) DEFAULT NULL,
  `69_N2` char(1) DEFAULT 'N',
  `69_Leer3` varchar(4) DEFAULT NULL,
  `69_Fremdavis` char(1) DEFAULT 'N',
  `Betrag_Importkosten` double(8,2) DEFAULT '0.00',
  `Betrag_Importkosten_best` double(8,2) DEFAULT '0.00',
  `Betrag_Exportkosten` double(8,2) DEFAULT '0.00',
  `Betrag_Exportkosten_best` double(8,2) DEFAULT '0.00',
  `Leer4` char(1) DEFAULT NULL,
  `Sondervereinbarung` varchar(20) DEFAULT NULL,
  `Importkosten` varchar(20) DEFAULT NULL,
  `ExportkostenPU` varchar(20) DEFAULT NULL,
  `RechnungsNr` varchar(20) DEFAULT NULL,
  `Rechnungsbetrag` double(8,2) DEFAULT NULL,
  `RechnungsDatum` date DEFAULT NULL,
  `Satzart_ava` char(3) DEFAULT '1',
  `FirmaDX` varchar(40) DEFAULT NULL,
  `FirmaD2X` varchar(40) DEFAULT NULL,
  `LandDX` char(3) DEFAULT 'DE',
  `PLZDX` varchar(10) DEFAULT '99999',
  `OrtDX` varchar(50) DEFAULT NULL,
  `StrasseDX` varchar(50) DEFAULT NULL,
  `StrNrDX` varchar(10) DEFAULT NULL,
  `dtAuslieferungX` datetime DEFAULT NULL,
  `dtTermin_vonX` datetime DEFAULT NULL,
  `dtTerminX` datetime DEFAULT NULL,
  `frueheste_zustellzeit` varchar(4) DEFAULT NULL,
  `Satzartp` char(1) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `Belegnummer_gut` tinyint(4) DEFAULT NULL,
  `UploadStatus` tinyint(4) NOT NULL DEFAULT '0',
  `SendStatus` tinyint(4) NOT NULL DEFAULT '0',
  `clearingpreis` double(8,2) DEFAULT NULL,
  `SdgArt` varchar(4) DEFAULT 'sdg',
  `OrderID_X` varchar(15) DEFAULT NULL,
  `PZAImageZIP` varchar(60) DEFAULT NULL,
  `IDSdgArt` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `RueckDate` datetime DEFAULT NULL,
  `ClearingArtMaster` smallint(6) DEFAULT '0',
  `ZoneS` char(1) DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `ROrderID` double DEFAULT NULL,
  `EBOrderID` double DEFAULT NULL,
  `EXAuftragsIDRef` varchar(25) DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `FirmaS3` varchar(40) DEFAULT NULL,
  `FirmaD3` varchar(40) DEFAULT NULL,
  `product_spec` varchar(5) DEFAULT NULL,
  `FirmaD3X` varchar(40) DEFAULT NULL,
  `gg` int(11) DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  UNIQUE KEY `DepotNrED` (`DepotNrED`,`DepotNrAD`,`DepotNrZD`,`DepotNrLD`,`OrderID`),
  UNIQUE KEY `DepotNrLD` (`DepotNrLD`,`DepotNrZD`,`DepotNrAD`,`DepotNrED`,`OrderID`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `AuftragsID` (`AuftragsID`),
  KEY `dtAuslieferung` (`dtAuslieferung`),
  KEY `DepotNrZD` (`DepotNrZD`),
  KEY `dtSendZ2ZD` (`dtSendZ2ZD`),
  KEY `Empfaenger` (`Empfaenger`),
  KEY `DepotNrLDx` (`DepotNrLD`),
  KEY `Satzart` (`Satzart`),
  KEY `DepotNrAD` (`DepotNrAD`),
  KEY `DepotNrAbD` (`DepotNrAbD`),
  KEY `plzd` (`PLZD`),
  KEY `OrtD` (`OrtD`),
  KEY `DepotNrEDInd` (`DepotNrED`),
  KEY `dtCreateAD` (`dtCreateAD`),
  KEY `FirmaS` (`FirmaS`),
  KEY `SendStatus` (`SendStatus`),
  KEY `SatzartAvA` (`Satzart_ava`),
  KEY `Referenz` (`Referenz`),
  KEY `lockflag` (`lockflag`),
  KEY `SdgArt` (`SdgArt`),
  KEY `Orderid_X` (`OrderID_X`),
  KEY `Verladedatum` (`Verladedatum`),
  KEY `PZA_bag` (`PZA_bag`),
  KEY `IDSdgArt` (`IDSdgArt`),
  KEY `dtReceiveZ2H` (`dtReceiveZ2H`),
  KEY `dtReceiveZ2ZD` (`dtReceiveZ2ZD`),
  KEY `Timestamp` (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragtext`
--

DROP TABLE IF EXISTS `tblauftragtext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragtext` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `orderid` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Bemerkung` text,
  `usr` varchar(10) DEFAULT NULL,
  `shl` varchar(5) DEFAULT NULL,
  `aktionsdatum` datetime DEFAULT NULL,
  `aktionszeit` datetime DEFAULT NULL,
  `dAktion` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `orderid` (`orderid`),
  KEY `shl` (`shl`),
  KEY `aktionsdatum` (`aktionsdatum`),
  KEY `aktionszeit` (`aktionszeit`),
  KEY `dAktion` (`dAktion`)
) ENGINE=MyISAM AUTO_INCREMENT=39187 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragtmp`
--

DROP TABLE IF EXISTS `tblauftragtmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragtmp` (
  `OrderID` double NOT NULL DEFAULT '0',
  `AuftragsID` varchar(25) DEFAULT NULL,
  `GKNr` int(11) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT NULL,
  `DepotNrAbD` int(11) DEFAULT NULL,
  `DepotNrbev` int(11) DEFAULT NULL,
  `DepotNrAD` int(11) DEFAULT NULL,
  `DepotNrZD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `lockflag` smallint(6) DEFAULT NULL,
  `dtSendAD2Z` datetime DEFAULT NULL,
  `dtReceiveAD2Z` datetime DEFAULT NULL,
  `dtSendZ2H` datetime DEFAULT NULL,
  `dtReceiveZ2H` datetime DEFAULT NULL,
  `dtModifyH` datetime DEFAULT NULL,
  `dtSendH2Z` datetime DEFAULT NULL,
  `dtReceiveH2Z` datetime DEFAULT NULL,
  `dtSendZ2ZD` datetime DEFAULT NULL,
  `dtReceiveZ2ZD` datetime DEFAULT NULL,
  `dtModifyZD` datetime DEFAULT NULL,
  `dtTermin_von` datetime DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `dtAuslieferDatum` datetime DEFAULT NULL,
  `dtAuslieferZeit` datetime DEFAULT NULL,
  `Empfaenger` varchar(35) DEFAULT NULL,
  `dtSendZD2Z` datetime DEFAULT NULL,
  `dtReceiveZD2Z` datetime DEFAULT NULL,
  `dtSendZ2AD` datetime DEFAULT NULL,
  `dtReceiveZ2AD` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `KDNR` int(9) DEFAULT NULL,
  `FirmaS` varchar(50) DEFAULT NULL,
  `FirmaS2` varchar(50) DEFAULT NULL,
  `LandS` char(3) DEFAULT NULL,
  `PLZS` varchar(10) DEFAULT NULL,
  `OrtS` varchar(50) DEFAULT NULL,
  `StrasseS` varchar(50) DEFAULT NULL,
  `StrNrS` varchar(10) DEFAULT NULL,
  `TelefonVWS` varchar(20) DEFAULT NULL,
  `TelefonNrS` varchar(20) DEFAULT NULL,
  `TelefaxNrS` varchar(20) DEFAULT NULL,
  `FirmaD` varchar(50) DEFAULT NULL,
  `FirmaD2` varchar(50) DEFAULT NULL,
  `LandD` char(3) DEFAULT NULL,
  `PLZD` varchar(10) DEFAULT NULL,
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `TelefonVWD` varchar(20) DEFAULT NULL,
  `TelefonNrD` varchar(20) DEFAULT NULL,
  `TelefaxNrD` varchar(20) DEFAULT NULL,
  `ClearingArt` smallint(6) DEFAULT NULL,
  `ZuschlagsArt` smallint(6) DEFAULT NULL,
  `GewichtGesamt` double DEFAULT NULL,
  `PreisEK` double(20,2) DEFAULT NULL,
  `PreisEG` double(20,2) DEFAULT NULL,
  `PreisEP` double(20,2) DEFAULT NULL,
  `PreiseZS` double(20,2) DEFAULT NULL,
  `ColliesGesamt` smallint(6) DEFAULT NULL,
  `PreisNN` double(20,2) DEFAULT NULL,
  `DatumNN` date DEFAULT NULL,
  `erhaltenNN` int(11) DEFAULT NULL,
  `sendD2Z` int(1) DEFAULT NULL,
  `Feiertag_1` varchar(15) DEFAULT NULL,
  `FeiertagShlD` int(11) DEFAULT NULL,
  `FeiertagShlS` int(11) DEFAULT NULL,
  `Feiertag_2` varchar(15) DEFAULT NULL,
  `ClearingDate` date DEFAULT NULL,
  `ClearingDateMaster` datetime DEFAULT NULL,
  `Satzart` char(1) DEFAULT NULL,
  `Referenz` varchar(30) DEFAULT NULL,
  `Referenz2` varchar(15) DEFAULT NULL,
  `Frei` varchar(6) DEFAULT NULL,
  `Adr_Nr_Absender` text,
  `Adr_Nr_Empfaenger` text,
  `Wert` double(8,2) unsigned DEFAULT NULL,
  `Nachnahmebetrag` double DEFAULT NULL,
  `Versicherungswert` double(10,2) unsigned DEFAULT NULL,
  `Frei2` double(8,2) DEFAULT NULL,
  `Verladedatum` datetime DEFAULT NULL,
  `Lieferdatum` datetime DEFAULT NULL,
  `Lieferzeit_von` datetime DEFAULT NULL,
  `Lieferzeit_bis` datetime DEFAULT NULL,
  `KZ_Fahrzeug` tinyint(2) unsigned DEFAULT NULL,
  `KZ_Transportart` tinyint(2) unsigned DEFAULT NULL,
  `Frei3` char(1) DEFAULT NULL,
  `Service` int(10) unsigned DEFAULT NULL,
  `KZServiceLeo` int(10) unsigned DEFAULT NULL,
  `Sendungsstatus` tinyint(4) unsigned DEFAULT NULL,
  `Ausliefertour` int(4) unsigned DEFAULT NULL,
  `Routung_Hilfsspalte` varchar(10) DEFAULT NULL,
  `KZ_erweitert` int(10) unsigned DEFAULT NULL,
  `Information1` varchar(80) DEFAULT NULL,
  `Information2` varchar(40) DEFAULT NULL,
  `Inhalt` varchar(50) DEFAULT NULL,
  `Frei4` varchar(20) DEFAULT NULL,
  `Frei5` varchar(40) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `Frei6` varchar(69) DEFAULT NULL,
  `PU_GK_Auftrags_Nr` int(8) DEFAULT NULL,
  `Besteller_Name` varchar(25) DEFAULT NULL,
  `Ladelisten_Nummer` int(8) unsigned DEFAULT NULL,
  `Frei7` char(2) DEFAULT NULL,
  `Kennzeichen_Info` char(1) DEFAULT NULL,
  `Frei8` char(2) DEFAULT NULL,
  `Frei9` varchar(11) DEFAULT NULL,
  `Frei10` varchar(10) DEFAULT NULL,
  `Anzahl_Teilretouren` tinyint(4) unsigned DEFAULT NULL,
  `KZ_Zusatz` tinyint(2) unsigned DEFAULT NULL,
  `CR` char(2) DEFAULT NULL,
  `FahrerNr` int(11) DEFAULT NULL,
  `d` int(1) DEFAULT NULL,
  `a` int(1) DEFAULT NULL,
  `Info_Rollkarte` varchar(100) DEFAULT NULL,
  `Info_Intern` varchar(100) DEFAULT NULL,
  `termin_i` int(1) unsigned DEFAULT NULL,
  `Zone` char(1) DEFAULT NULL,
  `Insel` char(1) DEFAULT NULL,
  `Zonea` char(1) DEFAULT NULL,
  `Insela` char(1) DEFAULT NULL,
  `69_WZ_Abholung` char(2) DEFAULT NULL,
  `69_Leer1` varchar(10) DEFAULT NULL,
  `69_Betrag_Abrechnung_Kunde` double(8,2) unsigned DEFAULT NULL,
  `69_Leer2` varchar(16) DEFAULT NULL,
  `69_Zerofill1` int(3) unsigned DEFAULT NULL,
  `69_KostenBahnFlug` double(5,2) unsigned DEFAULT NULL,
  `69_Zerofill2` int(8) unsigned DEFAULT NULL,
  `69_KmDirekt` int(4) unsigned DEFAULT NULL,
  `69_KZ_SameDay` tinyint(1) unsigned DEFAULT NULL,
  `69_KZ_Serviceart` tinyint(2) unsigned DEFAULT NULL,
  `69_Undefined` tinyint(1) unsigned DEFAULT NULL,
  `69_Servicezeit_XChange` char(2) DEFAULT NULL,
  `69_Avis` char(1) DEFAULT NULL,
  `69_N2` char(1) DEFAULT NULL,
  `69_Leer3` varchar(4) DEFAULT NULL,
  `69_Fremdavis` char(1) DEFAULT NULL,
  `Betrag_Importkosten` double(8,2) DEFAULT NULL,
  `Betrag_Importkosten_best` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten_best` double(8,2) DEFAULT NULL,
  `Leer4` char(1) DEFAULT NULL,
  `Sondervereinbarung` varchar(20) DEFAULT NULL,
  `Importkosten` varchar(20) DEFAULT NULL,
  `ExportkostenPU` varchar(20) DEFAULT NULL,
  `RechnungsNr` varchar(20) DEFAULT NULL,
  `Satzart_ava` char(3) DEFAULT NULL,
  `FirmaDX` varchar(50) DEFAULT NULL,
  `FirmaD2X` varchar(50) DEFAULT NULL,
  `LandDX` char(3) DEFAULT NULL,
  `PLZDX` varchar(10) DEFAULT NULL,
  `OrtDX` varchar(50) DEFAULT NULL,
  `StrasseDX` varchar(50) DEFAULT NULL,
  `StrNrDX` varchar(10) DEFAULT NULL,
  `dtAuslieferungX` datetime DEFAULT NULL,
  `dtTermin_vonX` datetime DEFAULT NULL,
  `dtTerminX` datetime DEFAULT NULL,
  `frueheste_zustellzeit` varchar(4) DEFAULT NULL,
  `Satzartp` char(1) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `UploadStatus` tinyint(4) DEFAULT NULL,
  `SendStatus` tinyint(4) DEFAULT NULL,
  `clearingpreis` double(8,2) DEFAULT NULL,
  `SdgArt` varchar(4) DEFAULT NULL,
  `OrderID_X` varchar(15) DEFAULT NULL,
  `PZAImageZIP` varchar(60) DEFAULT NULL,
  `IDSdgArt` tinyint(3) unsigned DEFAULT NULL,
  `RueckDate` datetime DEFAULT NULL,
  `ClearingArtMaster` int(10) unsigned DEFAULT NULL,
  `ZoneS` char(1) DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `SdgType` char(1) DEFAULT NULL,
  `SdgStatus` char(1) DEFAULT NULL,
  `ClientID` int(11) NOT NULL DEFAULT '0',
  `ROrderID` double DEFAULT NULL,
  `EBOrderID` double DEFAULT NULL,
  `EXAuftragsIDRef` varchar(25) DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `EBRueckGrund` varchar(25) DEFAULT NULL,
  `FirmaS3` varchar(50) DEFAULT NULL,
  `FirmaD3` varchar(50) DEFAULT NULL,
  `product_spec` varchar(5) DEFAULT NULL,
  `FirmaD3X` varchar(50) DEFAULT NULL,
  `dtCreateAD` datetime DEFAULT NULL,
  `BagBelegNrA` double DEFAULT NULL,
  `BagIDNrA` double DEFAULT NULL,
  `PlombennummerA` double DEFAULT NULL,
  `PlombenNrA` double DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`ClientID`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `DepotNrZD` (`DepotNrZD`),
  KEY `DepotNrLDx` (`DepotNrLD`),
  KEY `DepotNrAD` (`DepotNrAD`),
  KEY `DepotNrAbD` (`DepotNrAbD`),
  KEY `DepotNrEDInd` (`DepotNrED`),
  KEY `SendStatus` (`SendStatus`),
  KEY `SatzartAvA` (`Satzart_ava`),
  KEY `lockflag` (`lockflag`),
  KEY `Timestamp` (`Timestamp`),
  KEY `OrderID` (`OrderID`),
  KEY `ClientID` (`ClientID`),
  KEY `Referenz` (`Referenz`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragtmpb`
--

DROP TABLE IF EXISTS `tblauftragtmpb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragtmpb` (
  `OrderID` double NOT NULL DEFAULT '0',
  `AuftragsID` varchar(25) DEFAULT NULL,
  `GKNr` int(11) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `DepotNrED` int(11) DEFAULT NULL,
  `DepotNrAbD` int(11) DEFAULT NULL,
  `DepotNrbev` int(11) DEFAULT NULL,
  `DepotNrAD` int(11) DEFAULT NULL,
  `DepotNrZD` int(11) DEFAULT NULL,
  `DepotNrLD` int(11) DEFAULT NULL,
  `lockflag` smallint(6) DEFAULT NULL,
  `dtSendAD2Z` datetime DEFAULT NULL,
  `dtReceiveAD2Z` datetime DEFAULT NULL,
  `dtSendZ2H` datetime DEFAULT NULL,
  `dtReceiveZ2H` datetime DEFAULT NULL,
  `dtModifyH` datetime DEFAULT NULL,
  `dtSendH2Z` datetime DEFAULT NULL,
  `dtReceiveH2Z` datetime DEFAULT NULL,
  `dtSendZ2ZD` datetime DEFAULT NULL,
  `dtReceiveZ2ZD` datetime DEFAULT NULL,
  `dtModifyZD` datetime DEFAULT NULL,
  `dtTermin_von` datetime DEFAULT NULL,
  `dtTermin` datetime DEFAULT NULL,
  `dtAuslieferung` datetime DEFAULT NULL,
  `dtAuslieferDatum` datetime DEFAULT NULL,
  `dtAuslieferZeit` datetime DEFAULT NULL,
  `Empfaenger` varchar(35) DEFAULT NULL,
  `dtSendZD2Z` datetime DEFAULT NULL,
  `dtReceiveZD2Z` datetime DEFAULT NULL,
  `dtSendZ2AD` datetime DEFAULT NULL,
  `dtReceiveZ2AD` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `KDNR` int(9) DEFAULT NULL,
  `FirmaS` varchar(50) DEFAULT NULL,
  `FirmaS2` varchar(50) DEFAULT NULL,
  `LandS` char(3) DEFAULT NULL,
  `PLZS` varchar(10) DEFAULT NULL,
  `OrtS` varchar(50) DEFAULT NULL,
  `StrasseS` varchar(50) DEFAULT NULL,
  `StrNrS` varchar(10) DEFAULT NULL,
  `TelefonVWS` varchar(20) DEFAULT NULL,
  `TelefonNrS` varchar(20) DEFAULT NULL,
  `TelefaxNrS` varchar(20) DEFAULT NULL,
  `FirmaD` varchar(50) DEFAULT NULL,
  `FirmaD2` varchar(50) DEFAULT NULL,
  `LandD` char(3) DEFAULT NULL,
  `PLZD` varchar(10) DEFAULT NULL,
  `OrtD` varchar(50) DEFAULT NULL,
  `StrasseD` varchar(50) DEFAULT NULL,
  `StrNrD` varchar(10) DEFAULT NULL,
  `TelefonVWD` varchar(20) DEFAULT NULL,
  `TelefonNrD` varchar(20) DEFAULT NULL,
  `TelefaxNrD` varchar(20) DEFAULT NULL,
  `ClearingArt` smallint(6) DEFAULT NULL,
  `ZuschlagsArt` smallint(6) DEFAULT NULL,
  `GewichtGesamt` double DEFAULT NULL,
  `PreisEK` double(20,2) DEFAULT NULL,
  `PreisEG` double(20,2) DEFAULT NULL,
  `PreisEP` double(20,2) DEFAULT NULL,
  `PreiseZS` double(20,2) DEFAULT NULL,
  `ColliesGesamt` smallint(6) DEFAULT NULL,
  `PreisNN` double(20,2) DEFAULT NULL,
  `DatumNN` date DEFAULT NULL,
  `erhaltenNN` int(11) DEFAULT NULL,
  `sendD2Z` int(1) DEFAULT NULL,
  `Feiertag_1` varchar(15) DEFAULT NULL,
  `FeiertagShlD` int(11) DEFAULT NULL,
  `FeiertagShlS` int(11) DEFAULT NULL,
  `SpaetAbholRouting` datetime DEFAULT NULL,
  `SpaetZustellRouting` datetime DEFAULT NULL,
  `Feiertag_2` varchar(15) DEFAULT NULL,
  `ClearingDate` date DEFAULT NULL,
  `ClearingDateMaster` datetime DEFAULT NULL,
  `Satzart` char(1) DEFAULT NULL,
  `Referenz` varchar(30) DEFAULT NULL,
  `Referenz2` varchar(15) DEFAULT NULL,
  `Frei` varchar(6) DEFAULT NULL,
  `Adr_Nr_Absender` text,
  `Adr_Nr_Empfaenger` text,
  `Wert` double(8,2) unsigned DEFAULT NULL,
  `Nachnahmebetrag` double DEFAULT NULL,
  `Versicherungswert` double(10,2) unsigned DEFAULT NULL,
  `Frei2` double(8,2) DEFAULT NULL,
  `Verladedatum` datetime DEFAULT NULL,
  `Lieferdatum` datetime DEFAULT NULL,
  `Lieferzeit_von` datetime DEFAULT NULL,
  `Lieferzeit_bis` datetime DEFAULT NULL,
  `KZ_Fahrzeug` tinyint(2) unsigned DEFAULT NULL,
  `KZ_Transportart` tinyint(2) unsigned DEFAULT NULL,
  `Frei3` char(1) DEFAULT NULL,
  `Service` int(10) unsigned DEFAULT NULL,
  `KZServiceLeo` int(10) unsigned DEFAULT NULL,
  `Sendungsstatus` tinyint(4) unsigned DEFAULT NULL,
  `Ausliefertour` int(4) unsigned DEFAULT NULL,
  `Routung_Hilfsspalte` varchar(10) DEFAULT NULL,
  `KZ_erweitert` tinyint(2) unsigned DEFAULT NULL,
  `Information1` varchar(40) DEFAULT NULL,
  `Information2` varchar(40) DEFAULT NULL,
  `Inhalt` varchar(50) DEFAULT NULL,
  `Frei4` varchar(20) DEFAULT NULL,
  `Frei5` varchar(40) DEFAULT NULL,
  `Verladezeit_von` datetime DEFAULT NULL,
  `Verladezeit_bis` datetime DEFAULT NULL,
  `Frei6` varchar(69) DEFAULT NULL,
  `PU_GK_Auftrags_Nr` int(8) DEFAULT NULL,
  `Besteller_Name` varchar(25) DEFAULT NULL,
  `Ladelisten_Nummer` int(8) unsigned DEFAULT NULL,
  `Frei7` char(2) DEFAULT NULL,
  `Kennzeichen_Info` char(1) DEFAULT NULL,
  `Frei8` char(2) DEFAULT NULL,
  `Frei9` varchar(11) DEFAULT NULL,
  `Frei10` varchar(10) DEFAULT NULL,
  `Anzahl_Teilretouren` tinyint(4) unsigned DEFAULT NULL,
  `KZ_Zusatz` tinyint(2) unsigned DEFAULT NULL,
  `CR` char(2) DEFAULT NULL,
  `Preisvereinbarung` double(20,2) DEFAULT NULL,
  `FahrerNr` int(11) DEFAULT NULL,
  `d` int(1) DEFAULT NULL,
  `g` int(1) DEFAULT NULL,
  `a` int(1) DEFAULT NULL,
  `Info_Rollkarte` varchar(100) DEFAULT NULL,
  `Info_Intern` varchar(100) DEFAULT NULL,
  `termin_i` int(1) unsigned DEFAULT NULL,
  `Zone` char(1) DEFAULT NULL,
  `Insel` char(1) DEFAULT NULL,
  `Zonea` char(1) DEFAULT NULL,
  `Insela` char(1) DEFAULT NULL,
  `69_WZ_Abholung` char(2) DEFAULT NULL,
  `69_Leer1` varchar(10) DEFAULT NULL,
  `69_Betrag_Abrechnung_Kunde` double(8,2) unsigned DEFAULT NULL,
  `69_Leer2` varchar(16) DEFAULT NULL,
  `69_Zerofill1` int(3) unsigned DEFAULT NULL,
  `69_KostenBahnFlug` double(5,2) unsigned DEFAULT NULL,
  `69_Zerofill2` int(8) unsigned DEFAULT NULL,
  `69_KmDirekt` int(4) unsigned DEFAULT NULL,
  `69_KZ_SameDay` tinyint(1) unsigned DEFAULT NULL,
  `69_KZ_Serviceart` tinyint(2) unsigned DEFAULT NULL,
  `69_Undefined` tinyint(1) unsigned DEFAULT NULL,
  `69_Servicezeit_XChange` char(2) DEFAULT NULL,
  `69_Avis` char(1) DEFAULT NULL,
  `69_N2` char(1) DEFAULT NULL,
  `69_Leer3` varchar(4) DEFAULT NULL,
  `69_Fremdavis` char(1) DEFAULT NULL,
  `Betrag_Importkosten` double(8,2) DEFAULT NULL,
  `Betrag_Importkosten_best` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten` double(8,2) DEFAULT NULL,
  `Betrag_Exportkosten_best` double(8,2) DEFAULT NULL,
  `Leer4` char(1) DEFAULT NULL,
  `Sondervereinbarung` varchar(20) DEFAULT NULL,
  `Importkosten` varchar(20) DEFAULT NULL,
  `ExportkostenPU` varchar(20) DEFAULT NULL,
  `RechnungsNr` varchar(20) DEFAULT NULL,
  `Rechnungsbetrag` double(8,2) DEFAULT NULL,
  `RechnungsDatum` date DEFAULT NULL,
  `Satzart_ava` char(3) DEFAULT NULL,
  `FirmaDX` varchar(50) DEFAULT NULL,
  `FirmaD2X` varchar(50) DEFAULT NULL,
  `LandDX` char(3) DEFAULT NULL,
  `PLZDX` varchar(10) DEFAULT NULL,
  `OrtDX` varchar(50) DEFAULT NULL,
  `StrasseDX` varchar(50) DEFAULT NULL,
  `StrNrDX` varchar(10) DEFAULT NULL,
  `dtAuslieferungX` datetime DEFAULT NULL,
  `dtTermin_vonX` datetime DEFAULT NULL,
  `dtTerminX` datetime DEFAULT NULL,
  `frueheste_zustellzeit` varchar(4) DEFAULT NULL,
  `Satzartp` char(1) DEFAULT NULL,
  `PZA_bag` double DEFAULT NULL,
  `Belegnummer_akt` double DEFAULT NULL,
  `Belegnummer_gut` tinyint(4) DEFAULT NULL,
  `UploadStatus` tinyint(4) DEFAULT NULL,
  `SendStatus` tinyint(4) DEFAULT NULL,
  `clearingpreis` double(8,2) DEFAULT NULL,
  `SdgArt` varchar(4) DEFAULT NULL,
  `OrderID_X` varchar(15) DEFAULT NULL,
  `PZAImageZIP` varchar(60) DEFAULT NULL,
  `IDSdgArt` tinyint(3) unsigned DEFAULT NULL,
  `RueckDate` datetime DEFAULT NULL,
  `ClearingArtMaster` smallint(6) DEFAULT NULL,
  `ZoneS` char(1) DEFAULT NULL,
  `Locking` int(11) DEFAULT NULL,
  `SdgType` char(1) DEFAULT NULL,
  `SdgStatus` char(1) DEFAULT NULL,
  `ClientID` int(11) NOT NULL DEFAULT '0',
  `SdgTransfer` int(11) DEFAULT NULL,
  `ROrderID` double DEFAULT NULL,
  `EBOrderID` double DEFAULT NULL,
  `EXAuftragsIDRef` varchar(25) DEFAULT NULL,
  `Lagerplatz` double DEFAULT NULL,
  `EBRueckGrund` varchar(25) DEFAULT NULL,
  `TelefonLVWS` varchar(20) DEFAULT NULL,
  `TelefonLVWD` varchar(20) DEFAULT NULL,
  `FirmaS3` varchar(50) DEFAULT NULL,
  `FirmaD3` varchar(50) DEFAULT NULL,
  `product_spec` varchar(5) DEFAULT NULL,
  `FirmaD3X` varchar(50) DEFAULT NULL,
  `gg` int(11) DEFAULT NULL,
  `dtCreateAD` datetime DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`ClientID`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `DepotNrZD` (`DepotNrZD`),
  KEY `DepotNrLDx` (`DepotNrLD`),
  KEY `Satzart` (`Satzart`),
  KEY `DepotNrAD` (`DepotNrAD`),
  KEY `DepotNrAbD` (`DepotNrAbD`),
  KEY `DepotNrEDInd` (`DepotNrED`),
  KEY `SendStatus` (`SendStatus`),
  KEY `SatzartAvA` (`Satzart_ava`),
  KEY `lockflag` (`lockflag`),
  KEY `SdgArt` (`SdgArt`),
  KEY `IDSdgArt` (`IDSdgArt`),
  KEY `Timestamp` (`Timestamp`),
  KEY `OrderID` (`OrderID`),
  KEY `ClientID` (`ClientID`),
  KEY `Referenz` (`Referenz`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblauftragzuschlag`
--

DROP TABLE IF EXISTS `tblauftragzuschlag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblauftragzuschlag` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` smallint(6) NOT NULL AUTO_INCREMENT,
  `Beschreibung` int(11) DEFAULT NULL,
  `BetragEK` double(20,2) NOT NULL DEFAULT '0.00',
  `BetragEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`OrderID`,`OrderPos`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblbag`
--

DROP TABLE IF EXISTS `tblbag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblbag` (
  `idtblbag` int(11) NOT NULL AUTO_INCREMENT,
  `DepotNr` int(11) NOT NULL DEFAULT '999',
  `Belegnummer` double NOT NULL DEFAULT '0',
  `Via` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`idtblbag`)
) ENGINE=MyISAM AUTO_INCREMENT=481 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblbank`
--

DROP TABLE IF EXISTS `tblbank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblbank` (
  `Blz` char(8) NOT NULL DEFAULT '',
  `BName` char(50) DEFAULT NULL,
  `PLZ` char(5) DEFAULT NULL,
  `Ort` char(29) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Blz`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblbelegnummern_pza`
--

DROP TABLE IF EXISTS `tblbelegnummern_pza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblbelegnummern_pza` (
  `Id` int(6) unsigned NOT NULL AUTO_INCREMENT,
  `Belegnummer` bigint(12) DEFAULT NULL,
  `von_Nr` int(6) DEFAULT NULL,
  `bis_Nr` int(8) DEFAULT NULL,
  `KdNr` int(9) DEFAULT NULL,
  `verbraucht` int(1) unsigned zerofill DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `KdNr` (`KdNr`)
) ENGINE=MyISAM AUTO_INCREMENT=206009 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblbil`
--

DROP TABLE IF EXISTS `tblbil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblbil` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DepotBil` int(11) DEFAULT NULL,
  `DepotZu` int(11) DEFAULT NULL,
  `Scan` int(11) DEFAULT NULL,
  `Beschreibung` char(45) DEFAULT NULL,
  `Aktiv` int(11) DEFAULT NULL,
  `GLOBil` int(11) DEFAULT NULL,
  `BelNrVon` double DEFAULT NULL,
  `BelNrBis` double DEFAULT NULL,
  `Ladestelle` int(11) DEFAULT NULL,
  `NoterfassungPrio` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `DepotBil` (`DepotBil`),
  KEY `DepotZu` (`DepotZu`)
) ENGINE=MyISAM AUTO_INCREMENT=56 DEFAULT CHARSET=latin1 ROW_FORMAT=FIXED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldaten`
--

DROP TABLE IF EXISTS `tbldaten`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldaten` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `Art` char(1) NOT NULL DEFAULT 'U',
  `Empfaenger` char(27) DEFAULT NULL,
  `Konto` char(10) DEFAULT NULL,
  `BLZ` char(8) DEFAULT NULL,
  `Bank` char(50) DEFAULT NULL,
  `Betrag` double(20,2) NOT NULL DEFAULT '0.00',
  `Verw1` char(27) DEFAULT NULL,
  `Verw2` char(27) DEFAULT NULL,
  `Erw1` char(27) DEFAULT NULL,
  `Erw2` char(27) DEFAULT NULL,
  `Erw3` char(27) DEFAULT NULL,
  `Erw4` char(27) DEFAULT NULL,
  `Erw5` char(27) DEFAULT NULL,
  `Erw6` char(27) DEFAULT NULL,
  `Erw7` char(27) DEFAULT NULL,
  `Erw8` char(27) DEFAULT NULL,
  `Erw9` char(27) DEFAULT NULL,
  `Erw10` char(27) DEFAULT NULL,
  `Erw11` char(27) DEFAULT NULL,
  `Erw12` char(27) DEFAULT NULL,
  `Erw13` char(27) DEFAULT NULL,
  `ADatum` datetime DEFAULT NULL,
  `aktiv` tinyint(4) NOT NULL DEFAULT '1',
  `EDatum` datetime DEFAULT NULL,
  `erledigt` tinyint(4) NOT NULL DEFAULT '0',
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldepotabrechnung`
--

DROP TABLE IF EXISTS `tbldepotabrechnung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldepotabrechnung` (
  `AbrechnungsMonat` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Abrechnungslauf` int(11) NOT NULL AUTO_INCREMENT,
  `DepotNr` int(11) NOT NULL DEFAULT '0',
  `BetragNetto` double(20,2) NOT NULL DEFAULT '0.00',
  `BetragBrutto` double(20,2) NOT NULL DEFAULT '0.00',
  `MehrwertSteuer` double(20,2) NOT NULL DEFAULT '0.00',
  `Zahlungsart` int(11) NOT NULL DEFAULT '0',
  `Abgrechnet` tinyint(4) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`AbrechnungsMonat`,`DepotNr`,`Abrechnungslauf`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldepotliste`
--

DROP TABLE IF EXISTS `tbldepotliste`;
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
  `Email` varchar(75) DEFAULT NULL,
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
  `eMail_pas` varchar(100) DEFAULT NULL,
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
-- Table structure for table `tbldepotvertrag`
--

DROP TABLE IF EXISTS `tbldepotvertrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldepotvertrag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DepotID` int(10) unsigned DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gueltigab` date DEFAULT NULL,
  `gueltigbis` date DEFAULT NULL,
  `bem` varchar(200) DEFAULT NULL,
  `ma` varchar(20) DEFAULT NULL,
  `Titel` varchar(100) DEFAULT NULL,
  `EigStat` int(11) DEFAULT NULL,
  `GesStat` int(11) DEFAULT NULL,
  `LetzteAkt` varchar(100) DEFAULT NULL,
  `AnLink` varchar(250) DEFAULT NULL,
  `Loeschen` int(11) DEFAULT NULL,
  `LetzteAktDatum` date DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldepotvertraghist`
--

DROP TABLE IF EXISTS `tbldepotvertraghist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldepotvertraghist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `depotid` int(11) DEFAULT NULL,
  `depotvertragid` int(11) DEFAULT NULL,
  `depotvertragzusatzid` int(11) DEFAULT NULL,
  `ma` varchar(45) DEFAULT NULL,
  `msg` varchar(200) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=83 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldepotvertragzusatz`
--

DROP TABLE IF EXISTS `tbldepotvertragzusatz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldepotvertragzusatz` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `depotid` int(11) DEFAULT NULL,
  `depotvertragid` int(11) DEFAULT NULL,
  `ma` varchar(45) DEFAULT NULL,
  `titel` varchar(100) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `gueltigab` date DEFAULT NULL,
  `gueltigbis` date DEFAULT NULL,
  `msg` varchar(100) DEFAULT NULL,
  `EigStat` int(11) DEFAULT NULL,
  `AnLink` varchar(250) DEFAULT NULL,
  `Loeschen` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldirektinfo`
--

DROP TABLE IF EXISTS `tbldirektinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldirektinfo` (
  `DepotNrED` int(11) DEFAULT '0',
  `DepotNrZD` int(11) DEFAULT '0',
  `Info1` char(50) DEFAULT NULL,
  `Info2` char(50) DEFAULT NULL,
  `Info3` char(50) DEFAULT NULL,
  `Info4` char(50) DEFAULT NULL,
  `Info5` char(50) DEFAULT NULL,
  `Info` char(250) DEFAULT 'Text',
  `Datum1` datetime DEFAULT '0000-00-00 00:00:00',
  `Datum2` datetime DEFAULT '0000-00-00 00:00:00',
  `Datum3` datetime DEFAULT '0000-00-00 00:00:00',
  `jn` int(1) DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DirektID` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`DirektID`),
  KEY `jn` (`jn`),
  KEY `DepotNrED` (`DepotNrED`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbldirektinfo_aw`
--

DROP TABLE IF EXISTS `tbldirektinfo_aw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldirektinfo_aw` (
  `DepotNrED` int(11) NOT NULL DEFAULT '0',
  `DepotNrZD` int(11) NOT NULL DEFAULT '0',
  `Info1` char(50) DEFAULT NULL,
  `Info2` char(50) DEFAULT NULL,
  `Info3` char(50) DEFAULT NULL,
  `Info4` char(50) DEFAULT NULL,
  `Info5` char(50) DEFAULT NULL,
  `Info` char(250) NOT NULL DEFAULT 'Text',
  `Datum1` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Datum2` datetime DEFAULT '0000-00-00 00:00:00',
  `Datum3` datetime DEFAULT '0000-00-00 00:00:00',
  `jn` int(1) DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DirektID` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`DepotNrZD`,`Info`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblempf`
--

DROP TABLE IF EXISTS `tblempf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblempf` (
  `EmpfID` double NOT NULL DEFAULT '0',
  `DepotID` double DEFAULT NULL,
  `sVisible` double DEFAULT NULL,
  `sField` varchar(45) COLLATE latin1_general_ci DEFAULT NULL,
  `sStandard` text COLLATE latin1_general_ci,
  `sSpeziell` text COLLATE latin1_general_ci,
  PRIMARY KEY (`EmpfID`),
  KEY `DepotID` (`DepotID`),
  KEY `sVisible` (`sVisible`),
  KEY `sField` (`sField`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblexportdefdepotind`
--

DROP TABLE IF EXISTS `tblexportdefdepotind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblexportdefdepotind` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pOrder` int(11) DEFAULT '0',
  `XMLdefID` int(11) DEFAULT '0',
  `PFunction` varchar(50) DEFAULT NULL,
  `tabellenname` varchar(50) DEFAULT NULL,
  `feldname` varchar(50) DEFAULT NULL,
  `xmlname` varchar(50) DEFAULT NULL,
  `LeoFunction` varchar(50) DEFAULT NULL,
  `export` int(11) DEFAULT '0',
  `defValue` varchar(50) DEFAULT NULL,
  `DatenTyp` int(11) DEFAULT '0',
  `laenge` int(11) DEFAULT '0',
  `sichtbar` int(11) DEFAULT '0',
  `LangID` int(11) DEFAULT '0',
  `ExtFeldName` varchar(45) DEFAULT NULL,
  `optional` int(11) DEFAULT NULL,
  `CSVFieldLen` int(11) DEFAULT NULL,
  `LevelAD` int(11) DEFAULT NULL,
  `LevelAbD` int(11) DEFAULT NULL,
  `LevelLD` int(11) DEFAULT NULL,
  `LevelCreate` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `LangID` (`LangID`),
  KEY `porder` (`pOrder`) USING BTREE,
  KEY `XMLdefID` (`XMLdefID`),
  KEY `tabellenname` (`tabellenname`),
  KEY `feldname` (`feldname`)
) ENGINE=MyISAM AUTO_INCREMENT=35012 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblexportdefs`
--

DROP TABLE IF EXISTS `tblexportdefs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblexportdefs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Depotid` int(10) unsigned DEFAULT '0',
  `DefName` varchar(50) DEFAULT 'null',
  `Typ` int(11) DEFAULT '0',
  `Trennzeichen` int(11) DEFAULT '0',
  `TrennzeichenDopplung` int(11) DEFAULT '0',
  `Versandweg` int(11) DEFAULT NULL,
  `Satzart` int(11) DEFAULT NULL,
  `SpaltenUeb` int(11) DEFAULT NULL,
  `NurErstesCollie` int(11) DEFAULT NULL,
  `KeinLF` int(11) DEFAULT NULL,
  `NurASCII` int(11) DEFAULT NULL,
  `ExportSource` varchar(20) DEFAULT NULL,
  `ExportCount` int(11) DEFAULT NULL,
  `sSQL` text,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `nurLogflag` int(11) DEFAULT NULL,
  `exFct` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Depotid` (`Depotid`),
  KEY `DefName` (`DefName`)
) ENGINE=MyISAM AUTO_INCREMENT=768 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblexportfilter`
--

DROP TABLE IF EXISTS `tblexportfilter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblexportfilter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `defid` int(11) DEFAULT NULL,
  `depot` int(11) DEFAULT NULL,
  `WhereDisp` varchar(200) DEFAULT NULL,
  `op1` varchar(50) DEFAULT NULL,
  `op2` varchar(50) DEFAULT NULL,
  `FeldName` varchar(50) DEFAULT NULL,
  `Vergleichop` varchar(50) DEFAULT NULL,
  `Vergleichswert` varchar(50) DEFAULT NULL,
  `WhereSql` varchar(200) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sqlselect` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblexportqueue`
--

DROP TABLE IF EXISTS `tblexportqueue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblexportqueue` (
  `id` int(11) NOT NULL DEFAULT '0',
  `Station` varchar(10) DEFAULT NULL,
  `queue` text,
  `Status` int(11) DEFAULT NULL,
  `defID` int(11) DEFAULT NULL,
  `Versandart` int(11) DEFAULT NULL,
  `prognose` int(11) DEFAULT NULL,
  `sRet` text,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Bem` varchar(200) DEFAULT NULL,
  `postfilter` varchar(200) DEFAULT NULL,
  `eMail` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Station` (`Station`),
  KEY `Status` (`Status`),
  KEY `Versandart` (`Versandart`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblexporttabellen`
--

DROP TABLE IF EXISTS `tblexporttabellen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblexporttabellen` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `pOrder` int(11) DEFAULT NULL,
  `PFunction` varchar(45) DEFAULT NULL,
  `Tabellenname` varchar(45) DEFAULT NULL,
  `Feldname` varchar(45) DEFAULT NULL,
  `DatenTyp` int(11) DEFAULT NULL,
  `laenge` int(11) DEFAULT NULL,
  `ExportEnable` int(11) DEFAULT NULL,
  `InfoText` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pOrder` (`pOrder`),
  KEY `ExportEnable` (`ExportEnable`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblexporttasks`
--

DROP TABLE IF EXISTS `tblexporttasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblexporttasks` (
  `id` int(11) NOT NULL DEFAULT '0',
  `Station` varchar(10) DEFAULT NULL,
  `Aktiv` int(11) DEFAULT NULL,
  `TaskName` varchar(45) DEFAULT NULL,
  `Sel` int(11) DEFAULT NULL,
  `Exportdef` int(11) DEFAULT NULL,
  `SendeZeiten` varchar(45) DEFAULT NULL,
  `Versandweg` int(11) DEFAULT NULL,
  `letzteAusfuerung` datetime DEFAULT NULL,
  `eMail` varchar(200) DEFAULT NULL,
  `SqlSelect` varchar(200) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `filterid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Station` (`Station`),
  KEY `Aktiv` (`Aktiv`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblfahrzeugart`
--

DROP TABLE IF EXISTS `tblfahrzeugart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblfahrzeugart` (
  `Code` int(6) unsigned NOT NULL DEFAULT '0',
  `Fahrzeugart` char(35) DEFAULT NULL,
  PRIMARY KEY (`Code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblfehlercodes`
--

DROP TABLE IF EXISTS `tblfehlercodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblfehlercodes` (
  `Code` int(5) unsigned NOT NULL DEFAULT '0',
  `Fehler` varchar(35) DEFAULT NULL,
  `Flag` char(1) DEFAULT NULL,
  `newFehlercode` int(11) DEFAULT '0',
  `GLSFehlercode` double DEFAULT NULL,
  `ilonexsFehlercode` int(11) DEFAULT NULL,
  `istauslieferung` int(11) DEFAULT NULL,
  `istQuali` int(11) DEFAULT NULL,
  `UQEvtReasonCode` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`Code`),
  KEY `istauslieferung` (`istauslieferung`),
  KEY `GLSFehlercode` (`GLSFehlercode`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblfehlercodes_pza`
--

DROP TABLE IF EXISTS `tblfehlercodes_pza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblfehlercodes_pza` (
  `Code` int(5) unsigned NOT NULL DEFAULT '0',
  `Fehler` varchar(35) DEFAULT NULL,
  `Flag` char(1) DEFAULT NULL,
  `Level` int(11) DEFAULT NULL,
  PRIMARY KEY (`Code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblfeiertagctrl`
--

DROP TABLE IF EXISTS `tblfeiertagctrl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblfeiertagctrl` (
  `FDatum` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `JNPos` int(11) NOT NULL DEFAULT '0',
  `GFLKZ` char(2) NOT NULL DEFAULT '',
  `hoppla` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`FDatum`,`GFLKZ`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblfeldhistorie`
--

DROP TABLE IF EXISTS `tblfeldhistorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblfeldhistorie` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `Belegnummer` double DEFAULT NULL,
  `OrderID` double DEFAULT NULL,
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `FeldName` varchar(50) DEFAULT NULL,
  `OldValue` varchar(255) DEFAULT NULL,
  `NewValue` varchar(255) DEFAULT NULL,
  `Changer` varchar(5) DEFAULT NULL,
  `Point` varchar(45) DEFAULT NULL,
  `Tabelle` varchar(50) DEFAULT NULL,
  `tmp` varchar(50) DEFAULT NULL,
  `tmp2` varchar(50) DEFAULT NULL,
  `checked` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `OrderID` (`OrderID`),
  KEY `timestamp` (`TIMESTAMP`) USING BTREE,
  KEY `FeldName` (`FeldName`),
  KEY `Changer` (`Changer`),
  KEY `OldValue` (`OldValue`),
  KEY `NewValue` (`NewValue`),
  KEY `checked` (`checked`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblformpruefung`
--

DROP TABLE IF EXISTS `tblformpruefung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblformpruefung` (
  `avisID` int(11) NOT NULL AUTO_INCREMENT,
  `feldname` varchar(100) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `levels` double DEFAULT '0',
  `aktion` double DEFAULT '0',
  `pruefen` double DEFAULT '0',
  `depotID` double DEFAULT '0',
  `erlaeuterungen` varchar(255) DEFAULT NULL,
  `multipruefung` varchar(255) DEFAULT NULL,
  `pflicht` double DEFAULT '-1',
  `sortOrder` double DEFAULT '0',
  PRIMARY KEY (`avisID`),
  KEY `depotID` (`depotID`),
  KEY `feldname` (`feldname`),
  KEY `aktion` (`aktion`)
) ENGINE=MyISAM AUTO_INCREMENT=29 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise`
--

DROP TABLE IF EXISTS `tblgewpreise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_at`
--

DROP TABLE IF EXISTS `tblgewpreise_at`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_at` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_be`
--

DROP TABLE IF EXISTS `tblgewpreise_be`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_be` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_ch`
--

DROP TABLE IF EXISTS `tblgewpreise_ch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_ch` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_cz`
--

DROP TABLE IF EXISTS `tblgewpreise_cz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_cz` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_dk`
--

DROP TABLE IF EXISTS `tblgewpreise_dk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_dk` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_dpd`
--

DROP TABLE IF EXISTS `tblgewpreise_dpd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_dpd` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_fr`
--

DROP TABLE IF EXISTS `tblgewpreise_fr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_fr` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_fr2`
--

DROP TABLE IF EXISTS `tblgewpreise_fr2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_fr2` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_it`
--

DROP TABLE IF EXISTS `tblgewpreise_it`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_it` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_lu`
--

DROP TABLE IF EXISTS `tblgewpreise_lu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_lu` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_nl`
--

DROP TABLE IF EXISTS `tblgewpreise_nl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_nl` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_pl`
--

DROP TABLE IF EXISTS `tblgewpreise_pl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_pl` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblgewpreise_si`
--

DROP TABLE IF EXISTS `tblgewpreise_si`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgewpreise_si` (
  `GewKlasse` double NOT NULL DEFAULT '0',
  `GewIntervall` int(11) NOT NULL DEFAULT '0',
  `PreisEK` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEG` double(20,2) NOT NULL DEFAULT '0.00',
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`GewKlasse`,`GewIntervall`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhistorie`
--

DROP TABLE IF EXISTS `tblhistorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhistorie` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DepotID` varchar(10) NOT NULL DEFAULT '',
  `Info` text,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `OrderID` varchar(20) DEFAULT '0',
  `MsgLocation` varchar(45) DEFAULT NULL,
  `dOI` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `DepotID` (`DepotID`),
  KEY `OrderID` (`OrderID`),
  KEY `MsgLocation` (`MsgLocation`),
  KEY `dOI` (`dOI`)
) ENGINE=MyISAM AUTO_INCREMENT=13836430 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubbeladeplatz`
--

DROP TABLE IF EXISTS `tblhubbeladeplatz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubbeladeplatz` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `beladeplatz` int(10) unsigned DEFAULT NULL,
  `linie` int(10) unsigned DEFAULT NULL,
  `AbZulauf` int(11) DEFAULT NULL COMMENT 'zulauf=-1,ablauf=1',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Tmestamp2` datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `beladeplatz` (`beladeplatz`),
  KEY `linie` (`linie`),
  KEY `AbZulauf` (`AbZulauf`)
) ENGINE=MyISAM AUTO_INCREMENT=774 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubcomputer`
--

DROP TABLE IF EXISTS `tblhubcomputer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubcomputer` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Typ` varchar(45) DEFAULT NULL,
  `IPAddr` varchar(45) DEFAULT NULL,
  `MacAddr` varchar(45) DEFAULT NULL,
  `CompID` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IPAddr` (`IPAddr`)
) ENGINE=MyISAM AUTO_INCREMENT=72 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubdepots`
--

DROP TABLE IF EXISTS `tblhubdepots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubdepots` (
  `ID` int(10) unsigned NOT NULL,
  `abfdate` datetime DEFAULT NULL,
  `depotnr` int(11) DEFAULT NULL,
  `col` int(11) DEFAULT NULL,
  `gew` double DEFAULT NULL,
  `abzulauf` int(11) DEFAULT NULL,
  `linie` int(11) DEFAULT NULL,
  `KFZHubScan` datetime DEFAULT NULL,
  `PID` int(11) DEFAULT NULL,
  `ZLinie` int(11) DEFAULT NULL,
  `ZulaufAktiv` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubfahrzeugtyp`
--

DROP TABLE IF EXISTS `tblhubfahrzeugtyp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubfahrzeugtyp` (
  `FahrzeugID` int(11) NOT NULL DEFAULT '0',
  `Hersteller` varchar(45) DEFAULT NULL,
  `MaxGewicht_kg` double DEFAULT NULL,
  `MaxVolumen_m3` double DEFAULT NULL,
  `AvgSpeed_kmh` double DEFAULT NULL,
  `typ` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`FahrzeugID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubhalle`
--

DROP TABLE IF EXISTS `tblhubhalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubhalle` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `halle` int(10) unsigned DEFAULT NULL,
  `beladeplatz` int(10) unsigned DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Timestamp2` datetime DEFAULT '0000-00-00 00:00:00',
  `posx` int(11) DEFAULT NULL,
  `posy` int(11) DEFAULT NULL,
  `bemerkung` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `beladeplatz` (`beladeplatz`)
) ENGINE=MyISAM AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubidpool`
--

DROP TABLE IF EXISTS `tblhubidpool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubidpool` (
  `LoginID` double DEFAULT NULL,
  `KartenNr` int(11) NOT NULL DEFAULT '0',
  `KartenTyp` int(11) DEFAULT NULL,
  `Eigenschaft` varchar(20) DEFAULT NULL,
  `Gruppe` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`KartenNr`),
  KEY `LoginID` (`LoginID`),
  KEY `Eigenschaft` (`Eigenschaft`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhublinie`
--

DROP TABLE IF EXISTS `tblhublinie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhublinie` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `linie` varchar(10) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `fahrzeugID` int(11) DEFAULT NULL,
  `kennzeichen` varchar(45) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Timestamp2` datetime DEFAULT '0000-00-00 00:00:00',
  `TimestampLogin` datetime DEFAULT '0000-00-00 00:00:00',
  `TagesLoginID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=52 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubliniedepots`
--

DROP TABLE IF EXISTS `tblhubliniedepots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubliniedepots` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `linie` int(11) DEFAULT NULL,
  `depot` varchar(10) DEFAULT NULL,
  `AbZulauf` int(11) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Timestamp2` datetime DEFAULT NULL,
  `ZeitAnkunft` datetime DEFAULT NULL,
  `ZeitAbfahrt` datetime DEFAULT NULL,
  `StopTyp` varchar(5) DEFAULT NULL,
  `UeStation` varchar(10) DEFAULT NULL,
  `Version` int(11) NOT NULL DEFAULT '-1',
  `Bemerkung` varchar(100) DEFAULT NULL,
  `Aktiv` int(11) DEFAULT NULL,
  `Rollkarte` int(11) DEFAULT NULL,
  `gewDepot` double NOT NULL DEFAULT '0',
  `colDepot` int(10) unsigned NOT NULL DEFAULT '0',
  `gewDepotEx` double NOT NULL DEFAULT '0',
  `colDepotEx` int(10) unsigned NOT NULL DEFAULT '0',
  `gewHubeingang` double NOT NULL DEFAULT '0',
  `colHubeingang` int(10) unsigned NOT NULL DEFAULT '0',
  `VolDepot` double NOT NULL DEFAULT '0',
  `VolHubentaden` double NOT NULL DEFAULT '0',
  `gewBeladen` double NOT NULL DEFAULT '0',
  `colBeladen` int(10) unsigned NOT NULL DEFAULT '0',
  `PID` int(11) DEFAULT NULL,
  `LID` int(11) DEFAULT NULL,
  `Vertrag` varchar(1) DEFAULT NULL,
  `DepotNr` int(11) DEFAULT NULL,
  `VertragsBem` varchar(50) DEFAULT NULL,
  `KmSatz` double DEFAULT NULL,
  `KM` int(11) DEFAULT NULL,
  `Betrag` double DEFAULT NULL,
  `grund` int(11) DEFAULT NULL,
  `isNullBetragOK` int(11) DEFAULT NULL,
  `dtParkanfang` datetime DEFAULT NULL,
  `dtParkende` datetime DEFAULT NULL,
  `dtETA` datetime DEFAULT NULL,
  `Parkminmin` int(11) DEFAULT NULL,
  `mintonextdepot` int(11) DEFAULT NULL,
  `AktivLive` int(11) DEFAULT NULL,
  `kmtonextdepot` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `linie` (`linie`),
  KEY `depot` (`depot`),
  KEY `AbZulauf` (`AbZulauf`),
  KEY `Version` (`Version`),
  KEY `Aktiv` (`Aktiv`),
  KEY `PID` (`PID`),
  KEY `DepotNr` (`DepotNr`),
  KEY `dtParkende` (`dtParkende`),
  KEY `ZeitAbfahrt` (`ZeitAbfahrt`)
) ENGINE=MyISAM AUTO_INCREMENT=1000185996 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubliniefahrer`
--

DROP TABLE IF EXISTS `tblhubliniefahrer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubliniefahrer` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `linie` varchar(10) DEFAULT NULL,
  `FahrerID` int(11) DEFAULT NULL,
  `fahrzeugID` int(11) DEFAULT NULL,
  `kennzeichen` varchar(45) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Timestamp2` datetime DEFAULT '0000-00-00 00:00:00',
  `TimestampLogin` datetime DEFAULT '0000-00-00 00:00:00',
  `LinienhalterID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `linie` (`linie`),
  KEY `FahrerID` (`FahrerID`)
) ENGINE=MyISAM AUTO_INCREMENT=1425 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhublinien`
--

DROP TABLE IF EXISTS `tblhublinien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhublinien` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `LinienhalterID` int(10) unsigned DEFAULT NULL,
  `LinienTyp` varchar(10) DEFAULT NULL,
  `LinienName` varchar(100) DEFAULT NULL,
  `Nutzlast` int(11) DEFAULT NULL,
  `Strecke` int(11) DEFAULT NULL,
  `Verguetung` double DEFAULT NULL,
  `VerguetungsBem` varchar(100) DEFAULT NULL,
  `KmSatz` double DEFAULT NULL,
  `Linienscanner` int(11) DEFAULT NULL,
  `Bemerkung` varchar(200) DEFAULT NULL,
  `TmpGesGew` text,
  `TmpDepots` text,
  `LadelisteAnforderung` int(11) DEFAULT NULL,
  `DifflisteAnforderung` int(11) DEFAULT NULL,
  `DifflisteDruckAnforderung` int(11) DEFAULT NULL,
  `LadelisteAnforderungPerm` int(11) DEFAULT NULL,
  `LadelisteGedruckt` int(11) DEFAULT '0',
  `Blombennummer` varchar(15) DEFAULT NULL,
  `Bemerkungen` longtext,
  `tTimestamp` datetime DEFAULT NULL,
  `abfDatum` datetime DEFAULT NULL,
  `Benutzer` varchar(45) DEFAULT NULL,
  `LinienNr` int(11) DEFAULT NULL,
  `vKosten` double DEFAULT NULL,
  `Auftragnehmer` varchar(200) DEFAULT NULL,
  `KW` double DEFAULT NULL,
  `ReNr` varchar(45) DEFAULT NULL,
  `Kreuzreferenz` double DEFAULT NULL,
  `KFZArt` varchar(45) DEFAULT NULL,
  `zulGesGew` double DEFAULT NULL,
  `MasseLBH` varchar(45) DEFAULT NULL,
  `KFZHubEingang` datetime DEFAULT NULL,
  `KFZHubAusgang` datetime DEFAULT NULL,
  `ScannerNr` double DEFAULT NULL,
  `ScannerHubEingang` datetime DEFAULT NULL,
  `ScannerHubAusgang` datetime DEFAULT NULL,
  `KFZKennzeichen` varchar(45) DEFAULT NULL,
  `dstart` varchar(10) DEFAULT NULL,
  `dende` varchar(10) DEFAULT NULL,
  `dstorno` smallint(6) DEFAULT '0',
  `LinienArt` smallint(6) DEFAULT NULL,
  `dAktiv` smallint(6) DEFAULT NULL,
  `gueltigAb` datetime DEFAULT NULL,
  `dRundlauf` smallint(6) DEFAULT NULL,
  `KFZKennIn` varchar(45) DEFAULT NULL,
  `KFZKennOut` varchar(45) DEFAULT NULL,
  `KarteNr` double DEFAULT NULL,
  `zh` varchar(1) DEFAULT NULL,
  `ScannerNrOut` double DEFAULT NULL,
  `AuftragnehmerID` int(11) DEFAULT NULL,
  `dKM` double DEFAULT NULL,
  `VorlagenID` int(11) DEFAULT NULL,
  `Beladeplatz` int(11) DEFAULT NULL,
  `Version` int(11) DEFAULT NULL,
  `PID` int(11) DEFAULT NULL,
  `dKMzu` int(11) DEFAULT NULL,
  `dKMab` int(11) DEFAULT NULL,
  `KmSatzab` double DEFAULT NULL,
  `Vertrag` int(11) DEFAULT NULL,
  `dstartab` varchar(10) DEFAULT NULL,
  `dendeab` varchar(10) DEFAULT NULL,
  `VertragsNr` varchar(50) DEFAULT NULL,
  `isUeberhang` int(11) DEFAULT NULL,
  `grund` int(11) DEFAULT NULL,
  `pktFehlend` int(11) DEFAULT NULL,
  `isNullBetragzuOK` int(11) DEFAULT NULL,
  `isNullBetragabOK` int(11) DEFAULT NULL,
  `Verspaetungsgrund` int(11) DEFAULT NULL,
  `SollAnkunft` datetime DEFAULT NULL,
  `BemTextAnkunft` varchar(100) DEFAULT NULL,
  `BemTextAbfahrt` varchar(100) DEFAULT NULL,
  `VerspaetungsgrundAbf` int(11) DEFAULT NULL,
  `navETA` datetime DEFAULT NULL,
  `navonline` int(11) DEFAULT NULL,
  `dtparkseit` datetime DEFAULT NULL,
  `navDepot` varchar(20) DEFAULT NULL,
  `nSpeed` int(11) DEFAULT NULL,
  `navaktiv` int(11) DEFAULT NULL,
  `isReqVolscan` int(11) DEFAULT NULL,
  `nichtAuswertung` int(11) NOT NULL DEFAULT '0',
  `dtladelistegedruckt` datetime DEFAULT NULL,
  `timestamp2` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `scanner_Abfahrtsanforderung` int(11) DEFAULT NULL,
  `scanner_Abfahrtsbemerkung` varchar(100) DEFAULT NULL,
  `scanner_LadelisteUnterschrift` varchar(200) DEFAULT NULL,
  `scanner_Status` int(11) DEFAULT NULL,
  `scanner_DruckerNr` varchar(30) DEFAULT NULL,
  `language` varchar(10) DEFAULT NULL,
  `druckclient` int(11) DEFAULT NULL,
  `MobilNrFahrer` varchar(100) DEFAULT NULL,
  `dtHubAnkunft` datetime DEFAULT NULL,
  `LadelisteNrHUB` double DEFAULT NULL,
  `checkPackIn` int(11) DEFAULT NULL,
  `FahrernameIn` varchar(45) DEFAULT NULL,
  `FahrernameOut` varchar(45) DEFAULT NULL,
  `MobilNrFahrerIn` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `LadelisteAnforderung` (`LadelisteAnforderung`),
  KEY `DifflisteAnforderung` (`DifflisteAnforderung`),
  KEY `DifflisteDruckAnforderung` (`DifflisteDruckAnforderung`),
  KEY `LadelisteAnforderungPerm` (`LadelisteAnforderungPerm`),
  KEY `LadelisteGedruckt` (`LadelisteGedruckt`),
  KEY `Version` (`Version`),
  KEY `PID` (`PID`),
  KEY `LinienNr` (`LinienNr`),
  KEY `dtladelistegedruckt` (`dtladelistegedruckt`),
  KEY `beladeplatz` (`Beladeplatz`),
  KEY `nichtAuswertung` (`nichtAuswertung`),
  KEY `scanner_Abfahrtsanforderung` (`scanner_Abfahrtsanforderung`),
  KEY `scanner_Status` (`scanner_Status`),
  KEY `ladelistenrhub` (`LadelisteNrHUB`),
  KEY `checkPackIn` (`checkPackIn`),
  KEY `AuftragnehmerID` (`AuftragnehmerID`)
) ENGINE=MyISAM AUTO_INCREMENT=20006595 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhublinienfinal`
--

DROP TABLE IF EXISTS `tblhublinienfinal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhublinienfinal` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `abfDatum` datetime DEFAULT NULL,
  `LinienNr` int(11) DEFAULT NULL,
  `abzuLauf` int(11) DEFAULT NULL,
  `ReNr` int(10) unsigned DEFAULT NULL,
  `dstart` varchar(10) DEFAULT NULL,
  `dende` varchar(10) DEFAULT NULL,
  `AuftragnehmerID` int(11) DEFAULT NULL,
  `dKM` int(11) DEFAULT NULL,
  `KmSatz` double DEFAULT NULL,
  `Vertrag` varchar(5) DEFAULT NULL,
  `Vertragsnr` varchar(100) DEFAULT NULL,
  `FinalisierErrs` varchar(200) DEFAULT NULL,
  `TaxCode` varchar(5) DEFAULT NULL,
  `MwstShl` int(11) DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `PID` int(11) DEFAULT NULL,
  `UID` varchar(50) DEFAULT NULL,
  `dBetrag` double DEFAULT NULL,
  `MasterID` int(11) DEFAULT NULL,
  `BtrMwst` double DEFAULT NULL,
  `SAPLa` varchar(45) DEFAULT NULL,
  `KstPctr` varchar(45) DEFAULT NULL,
  `Sendungslauf` varchar(45) DEFAULT NULL,
  `Leistungsmonat` varchar(45) DEFAULT NULL,
  `LA` varchar(45) DEFAULT NULL,
  `Belegtext` varchar(45) DEFAULT NULL,
  `ReNrP` int(10) unsigned DEFAULT NULL,
  `MasterIDP` int(11) DEFAULT NULL,
  `Stationslauf` varchar(200) DEFAULT NULL,
  `ZHTyp` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1280235 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhublinienhalter`
--

DROP TABLE IF EXISTS `tblhublinienhalter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhublinienhalter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `KDNR` int(10) unsigned DEFAULT NULL,
  `FaName` varchar(200) DEFAULT NULL,
  `Strasse` varchar(200) DEFAULT NULL,
  `Plz` varchar(45) DEFAULT NULL,
  `Ort` varchar(200) DEFAULT NULL,
  `DepotNr` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=176 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhublinienplan`
--

DROP TABLE IF EXISTS `tblhublinienplan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhublinienplan` (
  `PID` int(11) NOT NULL AUTO_INCREMENT,
  `Arbeitsdatum` datetime DEFAULT NULL,
  `IstLife` int(11) DEFAULT NULL,
  `Bezeichnung` varchar(50) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`PID`)
) ENGINE=MyISAM AUTO_INCREMENT=1457 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhublinienversionen`
--

DROP TABLE IF EXISTS `tblhublinienversionen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhublinienversionen` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `LinienID` int(11) DEFAULT NULL,
  `Version` int(11) DEFAULT NULL,
  `abDatum` datetime DEFAULT NULL,
  `WochentagRegel` int(11) DEFAULT NULL,
  `Bemerkung` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=81 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubmacaddrs`
--

DROP TABLE IF EXISTS `tblhubmacaddrs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubmacaddrs` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MacAddr` varchar(45) DEFAULT NULL,
  `CompID` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubpersonal`
--

DROP TABLE IF EXISTS `tblhubpersonal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubpersonal` (
  `FahrerID` int(10) unsigned NOT NULL DEFAULT '0',
  `FahrerName` varchar(45) DEFAULT NULL,
  `defFahrzeugID` int(11) DEFAULT NULL,
  `defKennzeichen` varchar(45) DEFAULT NULL,
  `LoginID` int(11) DEFAULT NULL,
  `KartenNr` int(11) DEFAULT NULL,
  `entladeAdmin` int(11) DEFAULT NULL,
  `linienFahrer` int(11) DEFAULT NULL,
  `notlagerist` int(11) DEFAULT NULL,
  `hubMitarbeiter` int(11) DEFAULT NULL,
  `hubLadelisteRecht` int(11) DEFAULT NULL,
  `hubDifflisteRecht` int(11) DEFAULT NULL,
  `hubDifflisteDruckRecht` int(11) DEFAULT NULL,
  `hubLadelistePermRecht` int(11) DEFAULT NULL,
  PRIMARY KEY (`FahrerID`),
  KEY `LoginID` (`LoginID`),
  KEY `FahrerName` (`FahrerName`),
  KEY `entladeAdmin` (`entladeAdmin`),
  KEY `linienFahrer` (`linienFahrer`),
  KEY `notlagerist` (`notlagerist`),
  KEY `hubMitarbeiter` (`hubMitarbeiter`),
  KEY `hubLadelisteRecht` (`hubLadelisteRecht`),
  KEY `hubDifflisteRecht` (`hubDifflisteRecht`),
  KEY `hubDifflisteDruckRecht` (`hubDifflisteDruckRecht`),
  KEY `hubLadelistePermRecht` (`hubLadelistePermRecht`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblhubstation`
--

DROP TABLE IF EXISTS `tblhubstation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhubstation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `linie` varchar(10) DEFAULT NULL,
  `station` varchar(10) DEFAULT NULL,
  `zulauf` int(11) DEFAULT NULL,
  `ablauf` int(11) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Timestamp2` datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblkdstamm`
--

DROP TABLE IF EXISTS `tblkdstamm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblkdstamm` (
  `KDNR` int(9) unsigned zerofill NOT NULL DEFAULT '000000000',
  `FirmaS` varchar(40) DEFAULT NULL,
  `FirmaS2` varchar(40) DEFAULT NULL,
  `LandS` char(3) NOT NULL DEFAULT 'DE',
  `PLZS` varchar(10) DEFAULT '99999',
  `OrtS` varchar(50) DEFAULT NULL,
  `StrasseS` varchar(50) DEFAULT NULL,
  `StrNrS` varchar(10) DEFAULT NULL,
  `TelefonVWS` int(11) DEFAULT '0',
  `TelefonNrS` varchar(40) DEFAULT '0',
  `TelefaxNrS` varchar(40) DEFAULT '0',
  `DepotID` int(11) NOT NULL DEFAULT '0',
  `DepotNr` int(11) NOT NULL,
  `FirmaS3` varchar(40) DEFAULT NULL,
  `DebitorNr` double DEFAULT NULL,
  `KreditorNr` double DEFAULT NULL,
  `Zahlungsbedingungen` int(11) DEFAULT NULL,
  `ZahlungsbedingungenR` int(11) DEFAULT NULL,
  `eMail` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`KDNR`,`DepotNr`) USING BTREE,
  KEY `KdNr` (`KDNR`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblkennzeichenerweitert`
--

DROP TABLE IF EXISTS `tblkennzeichenerweitert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblkennzeichenerweitert` (
  `OrderID` double unsigned NOT NULL DEFAULT '0',
  `Wert` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `Transportart` char(55) DEFAULT NULL,
  `Preis` tinyint(6) unsigned zerofill DEFAULT '000000',
  `setzen` int(1) DEFAULT '0',
  `summe_wert` int(10) unsigned zerofill DEFAULT '0000000000',
  `summe_preis` tinyint(6) unsigned zerofill DEFAULT '000000',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`OrderID`,`Wert`),
  KEY `OrderID` (`OrderID`),
  KEY `Wert` (`Wert`),
  KEY `setzen` (`setzen`),
  KEY `Transportart` (`Transportart`),
  KEY `Timestamp` (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblkennzeichenerweitert_odbc`
--

DROP TABLE IF EXISTS `tblkennzeichenerweitert_odbc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblkennzeichenerweitert_odbc` (
  `Wert` int(5) unsigned NOT NULL DEFAULT '0',
  `Transportart` varchar(35) DEFAULT NULL,
  `Preis` tinyint(6) DEFAULT NULL,
  PRIMARY KEY (`Wert`)
) ENGINE=MyISAM AUTO_INCREMENT=66 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblkurierdaten`
--

DROP TABLE IF EXISTS `tblkurierdaten`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblkurierdaten` (
  `FahrerNr` int(11) NOT NULL DEFAULT '0',
  `Enddatum` datetime DEFAULT NULL,
  `Name1` char(50) DEFAULT NULL,
  `Name2` char(50) DEFAULT NULL,
  `Strasse` char(50) DEFAULT NULL,
  `HausNr` char(50) DEFAULT NULL,
  `Plz` char(20) DEFAULT NULL,
  `Ort` char(50) DEFAULT NULL,
  `Land` char(50) DEFAULT NULL,
  `LVorwahl` char(50) DEFAULT NULL,
  `OVorwahl` char(50) DEFAULT NULL,
  `Telefon` char(25) DEFAULT NULL,
  `Fax` char(25) DEFAULT NULL,
  `TelefonMobil` char(50) DEFAULT NULL,
  `KFZ` char(50) DEFAULT NULL,
  `AmtlKennzeichen` char(50) DEFAULT NULL,
  `AusweisNr` char(50) DEFAULT NULL,
  `Ausweis` tinyint(4) DEFAULT NULL,
  `Fuehrerschein` tinyint(4) DEFAULT NULL,
  `KFZSchein` tinyint(4) DEFAULT NULL,
  `Anfangsdatum` datetime DEFAULT NULL,
  `Auslieferliste` tinyint(4) DEFAULT NULL,
  `DepotNr` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`FahrerNr`,`DepotNr`) USING BTREE,
  KEY `FahrerNr` (`FahrerNr`),
  KEY `KFZ` (`KFZ`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblkzstatus`
--

DROP TABLE IF EXISTS `tblkzstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblkzstatus` (
  `kz_status` int(5) unsigned NOT NULL DEFAULT '0',
  `kz_status_klartext` varchar(35) DEFAULT NULL,
  `kz_erzeuger` char(2) NOT NULL DEFAULT 'A',
  `kz_newStatuscode` int(11) DEFAULT NULL,
  `IsWebStatus` int(11) DEFAULT NULL,
  `IsGLOStatus` int(11) DEFAULT NULL,
  `Systemstatus` int(11) DEFAULT NULL,
  `ilonexs` varchar(20) DEFAULT NULL,
  `UQEvtsname` varchar(6) DEFAULT NULL,
  `Video` int(11) DEFAULT NULL,
  `GLSGepStatus` varchar(50) DEFAULT NULL,
  `UQReasen` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`kz_status`,`kz_erzeuger`),
  KEY `kz_newStatuscode` (`kz_newStatuscode`),
  KEY `IsWebStatus` (`IsWebStatus`),
  KEY `kz_status` (`kz_status`),
  KEY `kz_erzeuger` (`kz_erzeuger`),
  KEY `GLSGepStatus` (`GLSGepStatus`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblkzstatus_pza`
--

DROP TABLE IF EXISTS `tblkzstatus_pza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblkzstatus_pza` (
  `kz_status` int(5) unsigned NOT NULL DEFAULT '0',
  `kz_status_klartext` varchar(35) DEFAULT NULL,
  `kz_erzeuger` char(2) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`kz_status`,`kz_erzeuger`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbllaendercodes`
--

DROP TABLE IF EXISTS `tbllaendercodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbllaendercodes` (
  `LKZ` char(2) NOT NULL DEFAULT '',
  `LNAME` varchar(50) NOT NULL DEFAULT '',
  `LVW` smallint(6) NOT NULL DEFAULT '49',
  `LMwSt` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lkzGrp` varchar(5) DEFAULT NULL,
  `RoutingTyp` int(11) NOT NULL DEFAULT '0',
  `lkzglo` char(3) DEFAULT NULL,
  `LDMwSt` int(11) DEFAULT NULL,
  `AbDMwSt` int(11) DEFAULT NULL,
  `ADMwSt` int(11) DEFAULT NULL,
  `RoutingTyp2` int(10) unsigned NOT NULL DEFAULT '0',
  `PrePaidOK` int(11) NOT NULL DEFAULT '0',
  `PPZonen` varchar(45) DEFAULT NULL,
  `Laendercodes` varchar(10) DEFAULT NULL,
  `Datentyp` varchar(45) DEFAULT NULL,
  `MinLen` int(11) DEFAULT NULL,
  `MaxLen` int(11) DEFAULT NULL,
  `Trennzeichen` varchar(45) DEFAULT NULL,
  `zusammenziehen` int(11) DEFAULT NULL,
  `PCtr` varchar(10) DEFAULT NULL,
  `ZipFormat1` varchar(15) DEFAULT NULL,
  `ZipFormat2` varchar(15) DEFAULT NULL,
  `ZipFormat3` varchar(45) DEFAULT NULL,
  `CountryNum` int(11) DEFAULT NULL,
  `RoutingTypPAS` int(11) NOT NULL DEFAULT '0',
  `DefAbZeitOpt` int(11) DEFAULT NULL,
  `DefZuZeitOpt` int(11) DEFAULT NULL,
  `valOK` double DEFAULT NULL,
  `GLSCode` int(11) DEFAULT NULL,
  PRIMARY KEY (`LKZ`),
  UNIQUE KEY `LNAME` (`LNAME`),
  KEY `RoutingTyp` (`RoutingTyp`),
  KEY `lkzglo` (`lkzglo`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbllinienhalter`
--

DROP TABLE IF EXISTS `tbllinienhalter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbllinienhalter` (
  `DepotNr` int(11) NOT NULL DEFAULT '0',
  `DepotLevel` int(11) DEFAULT '1',
  `DepotParent` int(11) DEFAULT '9999',
  `DepotMatchcode` varchar(50) DEFAULT NULL,
  `LinienNr` smallint(6) DEFAULT '0',
  `Linienankunft` datetime DEFAULT '0000-00-00 00:00:00',
  `Linienabfahrt` datetime DEFAULT '0000-00-00 00:00:00',
  `Aktivierungsdatum` datetime DEFAULT '2002-01-01 00:00:00',
  `Deaktivierungsdatum` datetime DEFAULT '2099-12-31 00:00:00',
  `IstGueltig` smallint(6) DEFAULT '1',
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
  `Email` varchar(75) DEFAULT NULL,
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
  `ExportEmail` varchar(45) DEFAULT NULL,
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
  `Firmenverbund` int(11) DEFAULT NULL,
  `ZahlungsbedingungenR` int(11) DEFAULT NULL,
  `TRZProz` double DEFAULT NULL,
  `eMail_pas` varchar(100) DEFAULT NULL,
  `PA_PDF` int(11) DEFAULT NULL,
  `IDH` int(11) DEFAULT NULL,
  `PA_Druck` int(11) DEFAULT NULL,
  PRIMARY KEY (`DepotNr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbllinienvertrag`
--

DROP TABLE IF EXISTS `tbllinienvertrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbllinienvertrag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DepotID` int(10) unsigned DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gueltigab` date DEFAULT NULL,
  `gueltigbis` date DEFAULT NULL,
  `bem` varchar(200) DEFAULT NULL,
  `ma` varchar(20) DEFAULT NULL,
  `Titel` varchar(100) DEFAULT NULL,
  `EigStat` int(11) DEFAULT NULL,
  `GesStat` int(11) DEFAULT NULL,
  `LetzteAkt` varchar(100) DEFAULT NULL,
  `AnLink` varchar(250) DEFAULT NULL,
  `Loeschen` int(11) DEFAULT NULL,
  `LetzteAktDatum` date DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbllinienvertraghist`
--

DROP TABLE IF EXISTS `tbllinienvertraghist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbllinienvertraghist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `depotid` int(11) DEFAULT NULL,
  `depotvertragid` int(11) DEFAULT NULL,
  `depotvertragzusatzid` int(11) DEFAULT NULL,
  `ma` varchar(45) DEFAULT NULL,
  `msg` varchar(200) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=126 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbllinienvertragzusatz`
--

DROP TABLE IF EXISTS `tbllinienvertragzusatz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbllinienvertragzusatz` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `depotid` int(11) DEFAULT NULL,
  `depotvertragid` int(11) DEFAULT NULL,
  `ma` varchar(45) DEFAULT NULL,
  `titel` varchar(100) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `gueltigab` date DEFAULT NULL,
  `gueltigbis` date DEFAULT NULL,
  `msg` varchar(100) DEFAULT NULL,
  `EigStat` int(11) DEFAULT NULL,
  `AnLink` varchar(250) DEFAULT NULL,
  `Loeschen` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblmailqueue`
--

DROP TABLE IF EXISTS `tblmailqueue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblmailqueue` (
  `id` int(11) NOT NULL DEFAULT '0',
  `Station` int(11) DEFAULT NULL,
  `queue` text,
  `Status` int(11) DEFAULT NULL,
  `defID` int(11) DEFAULT NULL,
  `Versandart` int(11) DEFAULT NULL,
  `prognose` int(11) DEFAULT NULL,
  `sRet` text,
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MailStation` int(11) DEFAULT NULL,
  `DepotMailField` varchar(45) DEFAULT NULL,
  `MailAddress` varchar(250) DEFAULT NULL,
  `Attatchment` varchar(250) DEFAULT NULL,
  `Subject` varchar(250) DEFAULT NULL,
  `DeleteAfeterSend` int(11) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `dtCreate` datetime DEFAULT NULL,
  `dtSend` datetime DEFAULT NULL,
  `MailFrom` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `status` (`Status`),
  KEY `station` (`Station`),
  KEY `Belegnummer` (`Belegnummer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblofflineclients`
--

DROP TABLE IF EXISTS `tblofflineclients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblofflineclients` (
  `Id` int(6) unsigned NOT NULL AUTO_INCREMENT,
  `Station` char(50) DEFAULT '0',
  `DB` char(50) DEFAULT '0',
  `Aktiv` tinyint(4) unsigned zerofill DEFAULT '0000',
  `kennung` char(50) DEFAULT '0',
  `LastUpdate` datetime DEFAULT '2005-01-01 00:00:00',
  `InstallPza` tinyint(3) unsigned zerofill DEFAULT '000',
  `InstallOfflineDB` tinyint(3) unsigned zerofill DEFAULT '000',
  `InstallPzadt` datetime DEFAULT '2005-01-01 00:00:00',
  `InstallOfflineDBdt` datetime DEFAULT '2005-01-01 00:00:00',
  `dtCreate` datetime DEFAULT '2005-01-01 00:00:00',
  `MasterStation` double unsigned zerofill DEFAULT '0000000000000000000000',
  `istMaster` double unsigned zerofill DEFAULT '0000000000000000000000',
  `updPZA` tinyint(4) DEFAULT '0',
  `XLSOk` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `station` (`Station`),
  KEY `DB` (`DB`),
  KEY `kennung` (`kennung`)
) ENGINE=MyISAM AUTO_INCREMENT=12012 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblofflinelog`
--

DROP TABLE IF EXISTS `tblofflinelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblofflinelog` (
  `Id` int(6) unsigned NOT NULL AUTO_INCREMENT,
  `Type` char(5) DEFAULT NULL,
  `Text` char(250) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Depotnr` char(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `Timestamp` (`Timestamp`),
  KEY `Depotnr` (`Depotnr`)
) ENGINE=MyISAM AUTO_INCREMENT=87907541 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblofflinesync`
--

DROP TABLE IF EXISTS `tblofflinesync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblofflinesync` (
  `Id` int(6) unsigned NOT NULL DEFAULT '0',
  `tbl` char(50) NOT NULL DEFAULT '',
  `DB` char(1) NOT NULL DEFAULT '',
  `modeStr` char(1) NOT NULL DEFAULT '',
  `DiffSql` char(250) DEFAULT NULL,
  `JoinStr` char(255) DEFAULT NULL,
  `JoinField` char(50) DEFAULT NULL,
  `OrderStr` char(50) DEFAULT NULL,
  `StartStatus` tinyint(4) unsigned DEFAULT '0',
  `DelRef` char(250) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblofflinesyncex`
--

DROP TABLE IF EXISTS `tblofflinesyncex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblofflinesyncex` (
  `order` char(255) DEFAULT '0',
  `Execute` char(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblonlineclients`
--

DROP TABLE IF EXISTS `tblonlineclients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblonlineclients` (
  `DB` varchar(45) NOT NULL DEFAULT '',
  `Station` varchar(45) NOT NULL DEFAULT '0',
  `Kennung` varchar(45) NOT NULL DEFAULT '',
  `Level` int(10) unsigned DEFAULT NULL,
  `SSOBenutzerID` int(10) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`DB`,`Station`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblopal`
--

DROP TABLE IF EXISTS `tblopal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblopal` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `KdNr` int(11) DEFAULT NULL,
  `DepotID` int(11) DEFAULT NULL,
  `Scan` int(11) DEFAULT NULL,
  `Beschreibung` char(45) DEFAULT NULL,
  `Aktiv` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `KdNr` (`KdNr`),
  KEY `DepotID` (`DepotID`)
) ENGINE=MyISAM AUTO_INCREMENT=25 DEFAULT CHARSET=latin1 ROW_FORMAT=FIXED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbloptionen`
--

DROP TABLE IF EXISTS `tbloptionen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbloptionen` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nr` int(10) unsigned NOT NULL DEFAULT '0',
  `Eigenschaft` varchar(45) NOT NULL DEFAULT '',
  `Wert` varchar(200) NOT NULL,
  `Station` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nr` (`nr`)
) ENGINE=MyISAM AUTO_INCREMENT=3648494 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblorte`
--

DROP TABLE IF EXISTS `tblorte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblorte` (
  `LPID` char(10) NOT NULL DEFAULT 'DE00099999',
  `Ort` char(50) NOT NULL DEFAULT '',
  `OVW` int(11) NOT NULL DEFAULT '0',
  `LKZ` char(2) NOT NULL DEFAULT 'DE',
  `PLZ` char(8) NOT NULL DEFAULT '99999999',
  `IstPostfach` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LPOID` int(11) NOT NULL AUTO_INCREMENT,
  `PLZb` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`LPID`,`Ort`),
  UNIQUE KEY `LKZ` (`LKZ`,`PLZ`,`Ort`),
  UNIQUE KEY `LPOID` (`LPOID`),
  UNIQUE KEY `LKZPostfach` (`LKZ`,`IstPostfach`,`LPOID`),
  KEY `istpostfach` (`IstPostfach`),
  KEY `plz` (`PLZ`),
  KEY `lpid` (`LPID`),
  KEY `iLKZ` (`LKZ`),
  KEY `iOrt` (`Ort`),
  KEY `PLZb` (`PLZb`)
) ENGINE=MyISAM AUTO_INCREMENT=527210 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblpasswort`
--

DROP TABLE IF EXISTS `tblpasswort`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblpasswort` (
  `id2` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_key` char(15) DEFAULT NULL,
  `user_name` char(35) DEFAULT NULL,
  `user_pwd` char(10) DEFAULT NULL,
  `erfassung` int(1) DEFAULT '0',
  `edispo1` int(1) DEFAULT '0',
  `edispo2` int(1) DEFAULT '0',
  `edispo3` int(1) DEFAULT '0',
  `edispo4` int(1) DEFAULT '0',
  `idispo1` int(1) DEFAULT '0',
  `idispo2` int(1) DEFAULT '0',
  `idispo3` int(1) DEFAULT '0',
  `scan1` int(1) DEFAULT '0',
  `scan2` int(1) DEFAULT '0',
  `scan3` int(1) DEFAULT '0',
  `sendung1` int(1) DEFAULT '0',
  `sendung2` int(1) DEFAULT '0',
  `sendung3` int(1) DEFAULT '0',
  `stamm1` int(1) DEFAULT '0',
  `stamm2` int(1) DEFAULT '0',
  `direktinfo` int(1) DEFAULT '0',
  `exportlisten` int(1) DEFAULT '0',
  `importlisten` int(1) DEFAULT '0',
  `rollkarte` int(1) DEFAULT '0',
  `routingliste` int(1) DEFAULT '0',
  `partnerliste` int(1) DEFAULT '0',
  `kundenliste` int(1) DEFAULT '0',
  `transfer1` int(1) DEFAULT '0',
  `transfer2` int(1) DEFAULT '0',
  `DepotNr` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`DepotNr`,`id`) USING BTREE,
  UNIQUE KEY `auto` (`id2`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=55384400 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblpzamaster`
--

DROP TABLE IF EXISTS `tblpzamaster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblpzamaster` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `Master` double NOT NULL DEFAULT '0',
  `Depot` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `master` (`Master`),
  KEY `depot` (`Depot`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblqualidefs`
--

DROP TABLE IF EXISTS `tblqualidefs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblqualidefs` (
  `pOrder` int(11) NOT NULL DEFAULT '0',
  `defID` int(11) NOT NULL DEFAULT '0',
  `Header` char(255) NOT NULL DEFAULT '',
  `Modus` char(45) NOT NULL DEFAULT '',
  `P1` char(255) NOT NULL DEFAULT '',
  `P2` char(255) NOT NULL DEFAULT '',
  `P3` char(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`pOrder`,`defID`),
  KEY `defID` (`defID`),
  KEY `pOrder` (`pOrder`),
  KEY `modus` (`Modus`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=FIXED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblqualihist`
--

DROP TABLE IF EXISTS `tblqualihist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblqualihist` (
  `ID` int(10) unsigned NOT NULL DEFAULT '0',
  `GType` varchar(10) DEFAULT NULL,
  `lieferdatum` datetime DEFAULT NULL,
  `KW` int(11) DEFAULT '0',
  `GRP` varchar(10) DEFAULT '',
  `Sum_soll` double DEFAULT '0',
  `Sum_ist` double DEFAULT '0',
  `T1_soll` double DEFAULT '0',
  `T1_ist` double DEFAULT '0',
  `T2_soll` double DEFAULT '0',
  `T2_ist` double DEFAULT '0',
  `T2_P` double DEFAULT '0',
  `T3_soll` double DEFAULT '0',
  `T3_ist` double DEFAULT '0',
  `T4_soll` double DEFAULT '0',
  `T4_ist` double DEFAULT '0',
  `T5_soll` double DEFAULT '0',
  `T5_ist` double DEFAULT '0',
  `T6_soll` double DEFAULT '0',
  `T6_ist` double DEFAULT '0',
  `T7_soll` double DEFAULT '0',
  `T7_ist` double DEFAULT '0',
  `T8_soll` double DEFAULT '0',
  `T8_ist` double DEFAULT '0',
  `T9_soll` double DEFAULT '0',
  `T9_ist` double DEFAULT '0',
  `T10_soll` double DEFAULT '0',
  `T10_ist` double DEFAULT '0',
  `T1_QBlock` double DEFAULT NULL,
  `T2_QBlock` double DEFAULT NULL,
  `T3_QBlock` double DEFAULT NULL,
  `T4_QBlock` double DEFAULT NULL,
  `T5_QBlock` double DEFAULT NULL,
  `T6_QBlock` double DEFAULT NULL,
  `T7_QBlock` double DEFAULT NULL,
  `T8_QBlock` double DEFAULT NULL,
  `T9_QBlock` double DEFAULT NULL,
  `T10_QBlock` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `kw` (`KW`),
  KEY `grp` (`GRP`),
  KEY `lieferdatum` (`lieferdatum`),
  KEY `Gtype` (`GType`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblqualihisttmp`
--

DROP TABLE IF EXISTS `tblqualihisttmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblqualihisttmp` (
  `ID` int(10) unsigned NOT NULL DEFAULT '0',
  `GType` varchar(10) DEFAULT NULL,
  `lieferdatum` datetime DEFAULT NULL,
  `KW` int(11) DEFAULT '0',
  `GRP` varchar(10) DEFAULT '',
  `Sum_soll` double DEFAULT '0',
  `Sum_ist` double DEFAULT '0',
  `T1_soll` double DEFAULT '0',
  `T1_ist` double DEFAULT '0',
  `T2_soll` double DEFAULT '0',
  `T2_ist` double DEFAULT '0',
  `T2_P` double DEFAULT '0',
  `T3_soll` double DEFAULT '0',
  `T3_ist` double DEFAULT '0',
  `T4_soll` double DEFAULT '0',
  `T4_ist` double DEFAULT '0',
  `T5_soll` double DEFAULT '0',
  `T5_ist` double DEFAULT '0',
  `T6_soll` double DEFAULT '0',
  `T6_ist` double DEFAULT '0',
  `T7_soll` double DEFAULT '0',
  `T7_ist` double DEFAULT '0',
  `T8_soll` double DEFAULT '0',
  `T8_ist` double DEFAULT '0',
  `T9_soll` double DEFAULT '0',
  `T9_ist` double DEFAULT '0',
  `T10_soll` double DEFAULT '0',
  `T10_ist` double DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `kw` (`KW`),
  KEY `grp` (`GRP`),
  KEY `lieferdatum` (`lieferdatum`),
  KEY `Gtype` (`GType`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblqualitasks`
--

DROP TABLE IF EXISTS `tblqualitasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblqualitasks` (
  `id` int(11) NOT NULL DEFAULT '0',
  `Aktiv` int(11) DEFAULT NULL,
  `TaskName` varchar(45) DEFAULT NULL,
  `Sel` int(11) DEFAULT NULL,
  `Exportdef` int(11) DEFAULT NULL,
  `SendeZeiten` varchar(45) DEFAULT NULL,
  `Wochentage` int(11) DEFAULT NULL,
  `letzteAusfuerung` datetime DEFAULT NULL,
  `An` varchar(45) DEFAULT NULL,
  `sSql` text,
  PRIMARY KEY (`id`),
  KEY `Aktiv` (`Aktiv`),
  KEY `Wochentage` (`Wochentage`),
  KEY `SendeZeiten` (`SendeZeiten`),
  KEY `letzteAusfuerung` (`letzteAusfuerung`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblrouten`
--

DROP TABLE IF EXISTS `tblrouten`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblrouten` (
  `LPIDAnfang` varchar(10) NOT NULL DEFAULT 'DE00099999',
  `LPIDEnde` varchar(10) NOT NULL DEFAULT 'DE00099999',
  `Zone` char(1) NOT NULL DEFAULT 'A',
  `DepotNrZD` int(11) NOT NULL DEFAULT '999',
  `DepotNrLD` int(11) NOT NULL DEFAULT '999',
  `LinienNr` smallint(6) NOT NULL DEFAULT '0',
  `AbholungBis` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `TerminBis` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `LKZ` char(2) NOT NULL DEFAULT 'DE',
  `PLZAnfang` varchar(8) NOT NULL DEFAULT '',
  `PLZEnde` varchar(8) NOT NULL DEFAULT '',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Satzart` char(1) NOT NULL DEFAULT 'P',
  `Laenderkennzeichen` char(3) NOT NULL DEFAULT 'D',
  `Postleitzahl` varchar(5) DEFAULT NULL,
  `Frei` varchar(5) DEFAULT NULL,
  `Importstation` char(3) DEFAULT NULL,
  `Ausliefertour` varchar(4) DEFAULT NULL,
  `normale_Zustellzeit` varchar(4) DEFAULT NULL,
  `frueheste_Zustellzeit` varchar(4) DEFAULT NULL,
  `Routung` varchar(9) DEFAULT NULL,
  `Insel_Zustellung` char(1) NOT NULL DEFAULT '0',
  `regionale_Feiertage` varchar(15) DEFAULT 'NNNNNNNNNNNNNNN',
  `Punkte` char(2) DEFAULT NULL,
  `Sektor` char(1) DEFAULT NULL,
  `LPID` varchar(10) NOT NULL DEFAULT 'DE00099999',
  `DepotNrAbD` int(11) NOT NULL DEFAULT '999',
  `GueltigAb` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Aktiv` int(11) NOT NULL DEFAULT '0',
  `area` varchar(5) DEFAULT NULL,
  `Laufzeit` int(11) DEFAULT NULL,
  `PLZ` varchar(10) DEFAULT NULL,
  `sabis` datetime DEFAULT NULL,
  `sobis` datetime DEFAULT NULL,
  PRIMARY KEY (`LKZ`,`PLZEnde`,`GueltigAb`),
  UNIQUE KEY `DepotNrZD` (`DepotNrZD`,`LPIDAnfang`,`LPIDEnde`,`GueltigAb`) USING BTREE,
  UNIQUE KEY `DepotNrLD` (`DepotNrLD`,`LPIDAnfang`,`LPIDEnde`,`GueltigAb`) USING BTREE,
  KEY `lpidende` (`LPIDEnde`),
  KEY `LKZ` (`LKZ`),
  KEY `PLZ_Ende` (`PLZEnde`),
  KEY `GueltigAb` (`GueltigAb`),
  KEY `Aktiv` (`Aktiv`),
  KEY `PLZ` (`PLZ`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblrouten_pza`
--

DROP TABLE IF EXISTS `tblrouten_pza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblrouten_pza` (
  `plz` char(5) NOT NULL DEFAULT '',
  `DepotNrLD` char(3) NOT NULL DEFAULT '',
  PRIMARY KEY (`plz`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblroutendepot`
--

DROP TABLE IF EXISTS `tblroutendepot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblroutendepot` (
  `DepotID` int(11) NOT NULL DEFAULT '0',
  `TourNr` int(11) NOT NULL DEFAULT '0',
  `FahrerNr` int(11) NOT NULL DEFAULT '0',
  `vonPLZ` char(10) NOT NULL DEFAULT '00000',
  `bisPLZ` char(10) NOT NULL DEFAULT '99999',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `TourID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`TourID`),
  KEY `vonplz` (`vonPLZ`),
  KEY `DepotID` (`DepotID`),
  KEY `TourNr` (`TourNr`),
  KEY `FahrerNr` (`FahrerNr`),
  KEY `bisPLZ` (`bisPLZ`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblroutendepot_aw`
--

DROP TABLE IF EXISTS `tblroutendepot_aw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblroutendepot_aw` (
  `DepotID` int(11) NOT NULL DEFAULT '0',
  `TourNr` int(11) NOT NULL DEFAULT '0',
  `FahrerNr` int(11) NOT NULL DEFAULT '0',
  `vonPLZ` char(10) NOT NULL DEFAULT '00000',
  `bisPLZ` char(10) NOT NULL DEFAULT '99999',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `TourID` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`TourNr`,`vonPLZ`,`DepotID`),
  KEY `vonplz` (`vonPLZ`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblroutenexport`
--

DROP TABLE IF EXISTS `tblroutenexport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblroutenexport` (
  `lkz` varchar(10) NOT NULL,
  `plz` varchar(15) NOT NULL,
  `ab` date NOT NULL,
  PRIMARY KEY (`lkz`,`plz`),
  KEY `lkz` (`lkz`),
  KEY `plz` (`plz`),
  KEY `ab` (`ab`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblroutenfahrer`
--

DROP TABLE IF EXISTS `tblroutenfahrer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblroutenfahrer` (
  `DepotID` int(11) DEFAULT '0',
  `TourNr` int(11) DEFAULT '0',
  `FahrerNr` int(11) DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `tabID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`tabID`) USING BTREE,
  KEY `DepotID` (`DepotID`),
  KEY `TourNr` (`TourNr`),
  KEY `FahrerNr` (`FahrerNr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblroutenhist`
--

DROP TABLE IF EXISTS `tblroutenhist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblroutenhist` (
  `LPIDAnfang` char(10) NOT NULL DEFAULT 'DE00099999',
  `LPIDEnde` char(10) NOT NULL DEFAULT 'DE00099999',
  `Zone` char(1) NOT NULL DEFAULT 'A',
  `DepotNrZD` int(11) NOT NULL DEFAULT '999',
  `DepotNrLD` int(11) NOT NULL DEFAULT '999',
  `LinienNr` smallint(6) NOT NULL DEFAULT '0',
  `AbholungBis` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `TerminBis` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `LKZ` char(2) NOT NULL DEFAULT 'DE',
  `PLZAnfang` char(8) NOT NULL DEFAULT '',
  `PLZEnde` char(8) NOT NULL DEFAULT '',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Satzart` char(1) NOT NULL DEFAULT 'P',
  `Laenderkennzeichen` char(3) NOT NULL DEFAULT 'D',
  `Postleitzahl` char(5) DEFAULT NULL,
  `Frei` char(5) DEFAULT NULL,
  `Importstation` char(3) DEFAULT NULL,
  `Ausliefertour` char(4) DEFAULT NULL,
  `normale_Zustellzeit` char(4) DEFAULT NULL,
  `frueheste_Zustellzeit` char(4) DEFAULT NULL,
  `Routung` char(9) DEFAULT NULL,
  `Insel_Zustellung` char(1) NOT NULL DEFAULT '0',
  `regionale_Feiertage` char(15) DEFAULT 'NNNNNNNNNNNNNNN',
  `Punkte` char(2) DEFAULT NULL,
  `Sektor` char(1) DEFAULT NULL,
  `LPID` char(10) NOT NULL DEFAULT 'DE00099999',
  `DepotNrAbD` int(11) NOT NULL DEFAULT '999',
  `deleted` tinyint(6) NOT NULL DEFAULT '0',
  `changed` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`LPIDAnfang`,`LPIDEnde`),
  UNIQUE KEY `DepotNrZD` (`DepotNrZD`,`LPIDAnfang`,`LPIDEnde`),
  UNIQUE KEY `DepotNrLD` (`DepotNrLD`,`LPIDAnfang`,`LPIDEnde`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblrouting`
--

DROP TABLE IF EXISTS `tblrouting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblrouting` (
  `LPID` char(10) NOT NULL DEFAULT 'DE00099999',
  `LKZ` char(2) NOT NULL DEFAULT 'DE',
  `PLZ` char(8) NOT NULL DEFAULT '99999999',
  `DepotNrZD` int(11) NOT NULL DEFAULT '999',
  `DepotNrLD` int(11) NOT NULL DEFAULT '999',
  `DepotNrAbD` int(11) NOT NULL DEFAULT '999',
  `IstPostfach` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Satzart` char(1) DEFAULT 'P',
  `Laenderkennzeichen` char(3) DEFAULT NULL,
  `Postleitzahl` char(5) DEFAULT NULL,
  `Frei` char(5) DEFAULT NULL,
  `Importstation` char(3) DEFAULT NULL,
  `Ausliefertour` char(4) DEFAULT NULL,
  `normale_Zustellzeit` char(4) DEFAULT NULL,
  `frueheste_Zustellzeit` char(4) DEFAULT NULL,
  `Routung` char(9) DEFAULT NULL,
  `Insel_Zustellung` char(1) DEFAULT '0',
  `regionale_Feiertage` char(15) DEFAULT 'NNNNNNNNNNNNNNN',
  `Punkte` char(2) DEFAULT NULL,
  `Zone` char(1) DEFAULT 'A',
  `Sektor` char(1) DEFAULT NULL,
  PRIMARY KEY (`LPID`),
  UNIQUE KEY `PLZ` (`PLZ`,`LKZ`),
  UNIQUE KEY `LKZ` (`LKZ`,`PLZ`),
  UNIQUE KEY `LKZPostfach` (`LKZ`,`IstPostfach`,`LPID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblroutingdepot`
--

DROP TABLE IF EXISTS `tblroutingdepot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblroutingdepot` (
  `OrderID` double NOT NULL DEFAULT '0',
  `OrderPos` int(11) NOT NULL DEFAULT '1',
  `TourNr` int(11) NOT NULL DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dtEingang` datetime DEFAULT NULL,
  `dtAusgang` datetime DEFAULT NULL,
  `dtEingangDepot` datetime DEFAULT NULL,
  `dtAusgangDepot` datetime DEFAULT NULL,
  `dtEingangHup` datetime DEFAULT NULL,
  `dtAusgangHup` datetime DEFAULT NULL,
  `ueD2H` int(1) DEFAULT '-1',
  `nueD2H` int(1) DEFAULT '0',
  `ueH2D` int(1) DEFAULT '-1',
  `nueH2D` int(1) DEFAULT '0',
  `mydepotid` int(11) DEFAULT '0',
  `OrderID_X` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`OrderPos`,`TourNr`),
  KEY `OrderID` (`OrderID`),
  KEY `dtEingangDepot` (`dtEingangDepot`),
  KEY `dtAusgangHup` (`dtAusgangHup`),
  KEY `tournr` (`TourNr`),
  KEY `ueD2H` (`ueD2H`),
  KEY `OrderPos` (`OrderPos`),
  KEY `Orderid_X` (`OrderID_X`),
  KEY `dtAusgang` (`dtAusgang`),
  KEY `dtAusgangDepot` (`dtAusgangDepot`),
  KEY `ueH2D` (`ueH2D`),
  KEY `nueD2H` (`nueD2H`),
  KEY `nueH2D` (`nueH2D`),
  KEY `dtEingang` (`dtEingang`),
  KEY `dtEingangHup` (`dtEingangHup`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblroutingext`
--

DROP TABLE IF EXISTS `tblroutingext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblroutingext` (
  `LKZ` varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `ISO` int(11) DEFAULT NULL,
  `produkt` int(11) DEFAULT NULL,
  `PLZ` varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `zust_norm` int(11) DEFAULT NULL,
  `frueheste_zustellzeit` int(11) DEFAULT NULL,
  `DepotNrZD` int(11) DEFAULT NULL,
  `Zone` varchar(5) CHARACTER SET utf8 DEFAULT NULL,
  `GLOPLZ` varchar(5) CHARACTER SET utf8 DEFAULT NULL,
  `Insel_Zustellung` int(11) DEFAULT NULL,
  `regionale_Feiertage` varchar(45) DEFAULT NULL,
  `GueltigAb` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Aktiv` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`LKZ`,`PLZ`,`GueltigAb`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblschadensakte`
--

DROP TABLE IF EXISTS `tblschadensakte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblschadensakte` (
  `OrderID` double NOT NULL DEFAULT '0',
  `schadennummer` varchar(45) DEFAULT NULL,
  `sendungsnummer` double DEFAULT NULL,
  `collienummer` double DEFAULT NULL,
  `anlagedatum` date DEFAULT NULL,
  `anzahl` double DEFAULT NULL,
  `gewicht` double DEFAULT NULL,
  `verladedatum` date DEFAULT NULL,
  `name1` varchar(50) DEFAULT NULL,
  `name2` varchar(50) DEFAULT NULL,
  `name3` varchar(50) DEFAULT NULL,
  `adresse1` varchar(100) DEFAULT NULL,
  `adresse2` varchar(100) DEFAULT NULL,
  `kategorie` int(11) DEFAULT NULL,
  `bezeichnung` varchar(150) DEFAULT NULL,
  `forderung` double DEFAULT NULL,
  `auszahlung` double DEFAULT NULL,
  `regulierung` double DEFAULT NULL,
  `weiterbelastung` double DEFAULT NULL,
  `differenz` double DEFAULT NULL,
  `kulanz` int(11) DEFAULT NULL,
  `rueckstellung` double DEFAULT NULL,
  `gutschriftsnr` varchar(20) DEFAULT NULL,
  `gutschriftsdat` date DEFAULT NULL,
  `weiternr` varchar(20) DEFAULT NULL,
  `weiterdat` date DEFAULT NULL,
  `rechnungsnr` varchar(20) DEFAULT NULL,
  `rechnungsdat` date DEFAULT NULL,
  `andepot` double DEFAULT NULL,
  `anname1` varchar(50) DEFAULT NULL,
  `anname2` varchar(50) DEFAULT NULL,
  `anname3` varchar(50) DEFAULT NULL,
  `anadr` varchar(100) DEFAULT NULL,
  `anplz` varchar(100) DEFAULT NULL,
  `ansonst` varchar(50) DEFAULT NULL,
  `aufdepot` double DEFAULT NULL,
  `aufname1` varchar(50) DEFAULT NULL,
  `aufname2` varchar(50) DEFAULT NULL,
  `aufname3` varchar(50) DEFAULT NULL,
  `aufadr` varchar(100) DEFAULT NULL,
  `aufplz` varchar(100) DEFAULT NULL,
  `aufsonst` varchar(50) DEFAULT NULL,
  `verdepot` double DEFAULT NULL,
  `vername1` varchar(50) DEFAULT NULL,
  `vername2` varchar(50) DEFAULT NULL,
  `vername3` varchar(50) DEFAULT NULL,
  `veradr` varchar(100) DEFAULT NULL,
  `verplz` varchar(100) DEFAULT NULL,
  `versonst` varchar(50) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `valore` int(11) DEFAULT NULL,
  `hoeherhaftung` int(11) DEFAULT NULL,
  `zustelldatum` date DEFAULT NULL,
  `liniennummer` double DEFAULT NULL,
  `rekladatum` date DEFAULT NULL,
  `smail` varchar(100) DEFAULT NULL,
  `mailsendok` int(11) DEFAULT NULL,
  `istatus` int(11) NOT NULL DEFAULT '0',
  `depotlinie` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  KEY `schadennummer` (`schadennummer`),
  KEY `sendungsnummer` (`sendungsnummer`),
  KEY `anlagedatum` (`anlagedatum`),
  KEY `name1` (`name1`),
  KEY `bezeichnung` (`bezeichnung`),
  KEY `forderung` (`forderung`),
  KEY `regulierung` (`regulierung`),
  KEY `kulanz` (`kulanz`),
  KEY `andepot` (`andepot`),
  KEY `verdepot` (`verdepot`),
  KEY `valore` (`valore`),
  KEY `hoeherhaftung` (`hoeherhaftung`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblschadensaktebemerkung`
--

DROP TABLE IF EXISTS `tblschadensaktebemerkung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblschadensaktebemerkung` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `orderid` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Bemerkung` text,
  `usr` varchar(10) DEFAULT NULL,
  `shl` varchar(5) DEFAULT NULL,
  `aktionsdatum` datetime DEFAULT NULL,
  `aktionszeit` datetime DEFAULT NULL,
  `dAktion` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `orderid` (`orderid`)
) ENGINE=MyISAM AUTO_INCREMENT=19439 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblschadensaktepdf`
--

DROP TABLE IF EXISTS `tblschadensaktepdf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblschadensaktepdf` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `orderid` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pdfname` varchar(200) DEFAULT NULL,
  `pdfalias` varchar(200) DEFAULT NULL,
  `pdfpfad` varchar(200) DEFAULT NULL,
  `usr` varchar(10) DEFAULT NULL,
  `shl` varchar(5) DEFAULT NULL,
  `aktionsdatum` datetime DEFAULT NULL,
  `aktionszeit` datetime DEFAULT NULL,
  `dAktion` int(10) unsigned DEFAULT NULL,
  `ice` int(11) DEFAULT NULL,
  `iceng` int(11) NOT NULL DEFAULT '-1',
  `icenok` int(11) DEFAULT NULL,
  `iceok` int(11) DEFAULT NULL,
  `iaw` int(11) DEFAULT NULL,
  `iawng` int(11) NOT NULL DEFAULT '-1',
  `iawnok` int(11) DEFAULT NULL,
  `iawok` int(11) DEFAULT NULL,
  `iah` int(11) DEFAULT NULL,
  `iahng` int(11) NOT NULL DEFAULT '-1',
  `iahnok` int(11) DEFAULT NULL,
  `iahok` int(11) DEFAULT NULL,
  `iwv` int(11) DEFAULT NULL,
  `datwv` datetime DEFAULT NULL,
  `danid` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `orderid` (`orderid`),
  KEY `datwv` (`datwv`)
) ENGINE=MyISAM AUTO_INCREMENT=19774 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblschadensaktepic`
--

DROP TABLE IF EXISTS `tblschadensaktepic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblschadensaktepic` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `orderid` double DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `bildname` varchar(200) DEFAULT NULL,
  `bildalias` varchar(200) DEFAULT NULL,
  `bildpfad` varchar(200) DEFAULT NULL,
  `usr` varchar(10) DEFAULT NULL,
  `shl` varchar(5) DEFAULT NULL,
  `aktionsdatum` datetime DEFAULT NULL,
  `aktionszeit` datetime DEFAULT NULL,
  `dAktion` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `orderid` (`orderid`)
) ENGINE=MyISAM AUTO_INCREMENT=19463 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblservicekennzeichen`
--

DROP TABLE IF EXISTS `tblservicekennzeichen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblservicekennzeichen` (
  `OrderID` double unsigned zerofill NOT NULL DEFAULT '0000000000000000000000',
  `Wert` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `Transportart` varchar(55) DEFAULT NULL,
  `Preis` int(10) unsigned DEFAULT '0',
  `setzen` int(1) DEFAULT '0',
  `summe_wert` int(10) unsigned zerofill DEFAULT '0000000000',
  `summe_preis` tinyint(6) unsigned zerofill DEFAULT '000000',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `OrderID_X` varchar(20) DEFAULT NULL,
  `Timestamp2` datetime DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`Wert`),
  KEY `OrderID` (`OrderID`),
  KEY `Orderid_X` (`OrderID_X`),
  KEY `wert` (`Wert`),
  KEY `setzen` (`setzen`),
  KEY `Timestamp2` (`Timestamp2`),
  KEY `Timestamp` (`Timestamp`),
  KEY `Transportart` (`Transportart`),
  KEY `Preis` (`Preis`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblservicekennzeichen_odbc`
--

DROP TABLE IF EXISTS `tblservicekennzeichen_odbc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblservicekennzeichen_odbc` (
  `Wert` int(5) unsigned NOT NULL DEFAULT '0',
  `Transportart` varchar(35) DEFAULT NULL,
  `Preis` int(5) unsigned DEFAULT NULL,
  `Kuerzel` varchar(10) DEFAULT NULL,
  `dSortOrder` char(2) DEFAULT NULL,
  `istgueltig` double NOT NULL DEFAULT '0',
  `abs_empf` double DEFAULT NULL,
  `excell_aktiv` int(11) DEFAULT NULL,
  `aufRollkarte` double DEFAULT NULL,
  `aufLabel` double DEFAULT NULL,
  `aufPuAuftrag` double DEFAULT NULL,
  `beschreibung` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`Wert`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblservicezeitxchange`
--

DROP TABLE IF EXISTS `tblservicezeitxchange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblservicezeitxchange` (
  `Zeit` char(8) NOT NULL DEFAULT '',
  `Wert` char(2) DEFAULT NULL,
  PRIMARY KEY (`Zeit`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsms`
--

DROP TABLE IF EXISTS `tblsms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblsms` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `message_id` int(10) unsigned DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `fromsender` varchar(45) DEFAULT NULL,
  `ref` varchar(45) DEFAULT NULL,
  `sendstatus` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `sendstatus` (`sendstatus`)
) ENGINE=MyISAM AUTO_INCREMENT=52085 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsmsclients`
--

DROP TABLE IF EXISTS `tblsmsclients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblsmsclients` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Depotnr` int(10) unsigned DEFAULT NULL,
  `MobilNr` varchar(20) NOT NULL DEFAULT '0',
  `Aktiv` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `MobilNr` (`MobilNr`)
) ENGINE=MyISAM AUTO_INCREMENT=288 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsonderauftrag`
--

DROP TABLE IF EXISTS `tblsonderauftrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblsonderauftrag` (
  `OrderID` double NOT NULL DEFAULT '0',
  `AuftragsID` char(25) NOT NULL DEFAULT '',
  `Belegnummer` double NOT NULL DEFAULT '0',
  `DepotNrED` int(11) NOT NULL DEFAULT '999',
  `DepotNrAD` int(11) NOT NULL DEFAULT '999',
  `DepotNrZD` int(11) NOT NULL DEFAULT '0',
  `DepotNrLD` int(11) NOT NULL DEFAULT '0',
  `lockflag` smallint(6) NOT NULL DEFAULT '1',
  `dtCreateAD` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dtSendAD2Z` datetime DEFAULT NULL,
  `dtReceiveAD2Z` datetime DEFAULT NULL,
  `dtSendZ2ZD` datetime DEFAULT NULL,
  `dtReceiveZ2ZD` datetime DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ClearingArt` smallint(6) NOT NULL DEFAULT '0',
  `Zeile1` char(30) DEFAULT NULL,
  `Zeile2` char(30) DEFAULT NULL,
  `Zeile3` char(30) DEFAULT NULL,
  `PreisEP` double(20,2) NOT NULL DEFAULT '0.00',
  `ClearingDate` date DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  UNIQUE KEY `AuftragsID` (`AuftragsID`),
  UNIQUE KEY `DepotNrED` (`DepotNrED`,`DepotNrAD`,`DepotNrZD`,`DepotNrLD`,`OrderID`),
  UNIQUE KEY `DepotNrLD` (`DepotNrLD`,`DepotNrZD`,`DepotNrAD`,`DepotNrED`,`OrderID`),
  KEY `Belegnummer` (`Belegnummer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblstatus`
--

DROP TABLE IF EXISTS `tblstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblstatus` (
  `KZ_Statuserzeuger` char(1) NOT NULL DEFAULT 'E',
  `Packstuecknummer` double unsigned zerofill NOT NULL DEFAULT '0000000000000000000000',
  `Datum` char(8) NOT NULL DEFAULT '',
  `Zeit` char(8) NOT NULL DEFAULT '',
  `KZ_Status` int(4) unsigned zerofill NOT NULL DEFAULT '0000',
  `Fehlercode` int(4) unsigned zerofill NOT NULL DEFAULT '0000',
  `Erzeugerstation` char(3) NOT NULL DEFAULT '',
  `Text` char(200) DEFAULT NULL,
  `Wartezeit` char(2) DEFAULT NULL,
  `Exportstation` char(3) NOT NULL DEFAULT '',
  `Frei1` char(1) DEFAULT NULL,
  `Infotext` char(11) DEFAULT NULL,
  `Frei2` char(2) DEFAULT NULL,
  `uebertragen` int(1) DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Timestamp2` datetime DEFAULT NULL,
  `Zaehler` int(11) NOT NULL AUTO_INCREMENT,
  `UploadStatus` tinyint(4) NOT NULL DEFAULT '0',
  `SendStatus` tinyint(4) NOT NULL DEFAULT '0',
  `OrderIDSta` double DEFAULT NULL,
  PRIMARY KEY (`Zaehler`),
  UNIQUE KEY `Zaehler` (`Zaehler`),
  KEY `kzstatus` (`KZ_Status`),
  KEY `erzeugerstation` (`Erzeugerstation`),
  KEY `Packstuecknummer` (`Packstuecknummer`),
  KEY `Fehlercode` (`Fehlercode`),
  KEY `KZ_Statuserzeuger` (`KZ_Statuserzeuger`),
  KEY `timestamp2` (`Timestamp2`),
  KEY `SendStatus` (`SendStatus`),
  KEY `Datum` (`Datum`),
  KEY `Zeit` (`Zeit`),
  KEY `KZ_Status` (`KZ_Status`),
  KEY `OrderidSta` (`OrderIDSta`)
) ENGINE=MyISAM AUTO_INCREMENT=169228322 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblstatus_pza`
--

DROP TABLE IF EXISTS `tblstatus_pza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblstatus_pza` (
  `KZ_Statuserzeuger` char(1) NOT NULL DEFAULT 'E',
  `Packstuecknummer` double unsigned zerofill NOT NULL DEFAULT '0000000000000000000000',
  `Datum` char(8) DEFAULT NULL,
  `Zeit` char(8) DEFAULT NULL,
  `KZ_Status` int(4) unsigned zerofill NOT NULL DEFAULT '0000',
  `Fehlercode` int(4) unsigned zerofill NOT NULL DEFAULT '0000',
  `Erzeugerstation` char(3) NOT NULL DEFAULT '',
  `Text` char(15) DEFAULT NULL,
  `Exportstation` char(3) NOT NULL DEFAULT '',
  `Infotext` char(11) DEFAULT NULL,
  `uebertragen` int(1) DEFAULT '0',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Timestamp2` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UploadStatus` tinyint(4) NOT NULL DEFAULT '0',
  `pza_bag` double NOT NULL DEFAULT '0',
  `Hauptstatus` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`KZ_Statuserzeuger`,`Packstuecknummer`,`KZ_Status`,`Fehlercode`,`Erzeugerstation`,`Exportstation`,`Timestamp2`),
  KEY `kzstatus` (`KZ_Status`),
  KEY `erzeugerstation` (`Erzeugerstation`),
  KEY `Packstuecknummer` (`Packstuecknummer`),
  KEY `timestamp2` (`Timestamp2`),
  KEY `pzabag` (`pza_bag`),
  KEY `hauptstatus` (`Hauptstatus`),
  KEY `Datum` (`Datum`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblstrassen`
--

DROP TABLE IF EXISTS `tblstrassen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblstrassen` (
  `LPID` char(10) NOT NULL DEFAULT 'DE00099999',
  `Strasse` char(50) DEFAULT NULL,
  `LPSID` int(11) NOT NULL AUTO_INCREMENT,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`LPID`,`LPSID`),
  UNIQUE KEY `Strasse` (`Strasse`,`LPID`),
  KEY `LPID` (`LPID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsyscollections`
--

DROP TABLE IF EXISTS `tblsyscollections`;
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
  PRIMARY KEY (`id`),
  KEY `typ` (`typ`),
  KEY `idvalue` (`idValue`),
  KEY `sort` (`sort`),
  KEY `txtvalue` (`txtValue`),
  KEY `txtP1` (`txtP1`),
  KEY `txtP2` (`txtP2`),
  KEY `txtP3` (`txtP3`),
  KEY `txtP4` (`txtP4`)
) ENGINE=MyISAM AUTO_INCREMENT=879 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsysinfo`
--

DROP TABLE IF EXISTS `tblsysinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblsysinfo` (
  `DepotNrED` int(11) NOT NULL DEFAULT '0',
  `Zentrale` int(11) NOT NULL DEFAULT '0',
  `Info` longtext,
  `Info1` varchar(50) DEFAULT NULL,
  `Info2` varchar(50) DEFAULT NULL,
  `Info3` varchar(50) DEFAULT NULL,
  `Info4` varchar(50) DEFAULT NULL,
  `Info5` varchar(50) DEFAULT NULL,
  `Datum1` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Datum2` datetime DEFAULT NULL,
  `Datum3` datetime DEFAULT NULL,
  `jn` int(1) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `SysID` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`SysID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblsysstrings`
--

DROP TABLE IF EXISTS `tblsysstrings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblsysstrings` (
  `id` double NOT NULL AUTO_INCREMENT,
  `qrttyp` int(10) unsigned DEFAULT '0',
  `Form` varchar(50) DEFAULT NULL,
  `Beschreibung` varchar(50) DEFAULT NULL,
  `sortid` int(10) unsigned DEFAULT NULL,
  `level` int(10) unsigned DEFAULT NULL,
  `tbl` text,
  `PreSelect` varchar(255) DEFAULT NULL,
  `where` text,
  `order` varchar(50) DEFAULT NULL,
  `p1` text,
  `p2` text,
  `p3` varchar(50) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sortid` (`sortid`)
) ENGINE=MyISAM AUTO_INCREMENT=1336 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltasksadmin`
--

DROP TABLE IF EXISTS `tbltasksadmin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltasksadmin` (
  `ID` double NOT NULL DEFAULT '0',
  `Benutzer` varchar(45) DEFAULT NULL,
  `toDo` longtext,
  `tTimestamp` datetime DEFAULT NULL,
  `angefordertAm` varchar(45) DEFAULT NULL,
  `toDoBis` varchar(45) DEFAULT NULL,
  `Status` int(10) unsigned DEFAULT NULL,
  `Bearbeiter` int(10) unsigned DEFAULT NULL,
  `Projekt` int(10) unsigned DEFAULT NULL,
  `KontingentMT` double DEFAULT NULL,
  `TeilProjekt` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltasksadminprojekte`
--

DROP TABLE IF EXISTS `tbltasksadminprojekte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltasksadminprojekte` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `Benutzer` varchar(45) DEFAULT NULL,
  `Projekt` varchar(45) DEFAULT NULL,
  `Beschreibung` text,
  `tTimestamp` datetime DEFAULT NULL,
  `angefordertAm` datetime DEFAULT NULL,
  `toDoBisTermin` datetime DEFAULT NULL,
  `Status` int(10) unsigned DEFAULT NULL,
  `Verantwortlich` int(10) unsigned DEFAULT NULL,
  `MTKontingent` double DEFAULT NULL,
  `Projektbereich` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltaskshelper`
--

DROP TABLE IF EXISTS `tbltaskshelper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltaskshelper` (
  `ID` double NOT NULL AUTO_INCREMENT,
  `App` varchar(50) DEFAULT NULL,
  `Disk` varchar(50) DEFAULT NULL,
  `Min` varchar(50) DEFAULT NULL,
  `sort` double DEFAULT '0',
  `AppName` varchar(50) DEFAULT NULL,
  `DiskAlias` varchar(50) DEFAULT NULL,
  `bAktiv` int(11) DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltaskshub`
--

DROP TABLE IF EXISTS `tbltaskshub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltaskshub` (
  `ID` double NOT NULL DEFAULT '0',
  `Beschreibung` longtext,
  `tTimestamp` datetime DEFAULT NULL,
  `Datum` datetime DEFAULT NULL,
  `Bereich` varchar(45) DEFAULT NULL,
  `Task` varchar(45) DEFAULT NULL,
  `Stunden` double DEFAULT NULL,
  `User` varchar(45) DEFAULT NULL,
  `KW` double DEFAULT NULL,
  `Wochentag` varchar(45) DEFAULT NULL,
  `Kalenderjahr` int(10) unsigned DEFAULT NULL,
  `Teilprojekt` int(10) unsigned DEFAULT NULL,
  `zeit_von` datetime DEFAULT NULL,
  `zeit_bis` datetime DEFAULT NULL,
  `station` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltasksjournal`
--

DROP TABLE IF EXISTS `tbltasksjournal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltasksjournal` (
  `ID` double NOT NULL DEFAULT '0',
  `Beschreibung` longtext,
  `tTimestamp` datetime DEFAULT NULL,
  `Datum` datetime DEFAULT NULL,
  `Bereich` varchar(45) DEFAULT NULL,
  `Task` varchar(45) DEFAULT NULL,
  `Stunden` double DEFAULT NULL,
  `User` varchar(45) DEFAULT NULL,
  `KW` double DEFAULT NULL,
  `Wochentag` varchar(45) DEFAULT NULL,
  `Kalenderjahr` int(10) unsigned DEFAULT NULL,
  `Teilprojekt` varchar(45) DEFAULT NULL,
  `zeit_von` datetime DEFAULT NULL,
  `zeit_bis` datetime DEFAULT NULL,
  `station` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `task` (`Task`),
  KEY `kw` (`KW`),
  KEY `station` (`station`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltransferlog`
--

DROP TABLE IF EXISTS `tbltransferlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltransferlog` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ExIm` int(11) DEFAULT NULL,
  `DateiName` varchar(50) DEFAULT NULL,
  `Belegnummer` double DEFAULT NULL,
  `OrderID` double DEFAULT NULL,
  `txt` text,
  `status` varchar(200) DEFAULT NULL,
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `exim` (`ExIm`),
  KEY `DateiName` (`DateiName`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `OrderID` (`OrderID`),
  KEY `status` (`status`),
  KEY `timestamp` (`TIMESTAMP`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltranslation`
--

DROP TABLE IF EXISTS `tbltranslation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltranslation` (
  `id` int(11) NOT NULL DEFAULT '0',
  `DicID` int(11) DEFAULT NULL,
  `Programm` varchar(255) DEFAULT NULL,
  `DE` text,
  `GB` text,
  `HU` text,
  `NL` text,
  `DK` text,
  `AT` text,
  `BE` text,
  `CH` text,
  `FR` text,
  `PL` text,
  PRIMARY KEY (`id`),
  KEY `Programm` (`Programm`),
  KEY `DicID` (`DicID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltransportart`
--

DROP TABLE IF EXISTS `tbltransportart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltransportart` (
  `Code` int(6) unsigned NOT NULL DEFAULT '0',
  `Transportart` varchar(35) NOT NULL DEFAULT '',
  `KZ` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`Code`,`Transportart`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbltyphistorie`
--

DROP TABLE IF EXISTS `tbltyphistorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbltyphistorie` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `Belegnummer` double DEFAULT NULL,
  `OrderID` double DEFAULT NULL,
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dCase` double DEFAULT NULL,
  `dIF` double DEFAULT NULL,
  `sMessage` varchar(255) DEFAULT NULL,
  `dDepotID` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Belegnummer` (`Belegnummer`),
  KEY `OrderID` (`OrderID`),
  KEY `timestamp` (`TIMESTAMP`) USING BTREE,
  KEY `dCase` (`dCase`),
  KEY `dIF` (`dIF`),
  KEY `dDepotID` (`dDepotID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblverpackungen`
--

DROP TABLE IF EXISTS `tblverpackungen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblverpackungen` (
  `VerpackungArt` int(11) NOT NULL AUTO_INCREMENT,
  `Beschreibung` char(50) NOT NULL DEFAULT '',
  `PreisVA` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`VerpackungArt`),
  KEY `VerpackungsArt` (`VerpackungArt`)
) ENGINE=MyISAM AUTO_INCREMENT=92 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tblzuschlaege`
--

DROP TABLE IF EXISTS `tblzuschlaege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblzuschlaege` (
  `ZuschlagsID` int(11) NOT NULL AUTO_INCREMENT,
  `Beschreibung` char(50) NOT NULL DEFAULT '',
  `BetragEK` double(20,2) NOT NULL DEFAULT '0.00',
  `BetragEP` double(20,2) NOT NULL DEFAULT '0.00',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ZuschlagsID`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tomclearfinal`
--

DROP TABLE IF EXISTS `tomclearfinal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tomclearfinal` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `vonDatum` datetime DEFAULT NULL,
  `bisDatum` datetime DEFAULT NULL,
  `DepotNr` int(11) DEFAULT NULL,
  `ReNr` int(10) unsigned DEFAULT NULL,
  `FinalisierErrs` varchar(200) DEFAULT NULL,
  `TaxCode` varchar(5) DEFAULT NULL,
  `MwstShl` int(11) DEFAULT NULL,
  `istInternational` int(11) DEFAULT NULL,
  `PID` int(11) DEFAULT NULL,
  `UID` varchar(50) DEFAULT NULL,
  `dBetrag` double DEFAULT NULL,
  `MasterID` int(11) DEFAULT NULL,
  `BtrMwst` double DEFAULT NULL,
  `SAPLa` varchar(45) DEFAULT NULL,
  `KstPctr` varchar(45) DEFAULT NULL,
  `Leistungsmonat` varchar(45) DEFAULT NULL,
  `LA` varchar(45) DEFAULT NULL,
  `Belegtext` varchar(45) DEFAULT NULL,
  `ReNrP` int(10) unsigned DEFAULT NULL,
  `MasterIDP` int(11) DEFAULT NULL,
  `nTage` int(11) DEFAULT NULL,
  `SetNr` int(11) DEFAULT NULL,
  `liniennr` int(11) DEFAULT NULL,
  `abfdatum` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ReNrP` (`ReNrP`),
  KEY `ReNr` (`ReNr`),
  KEY `LA` (`LA`),
  KEY `Leistungsmonat` (`Leistungsmonat`),
  KEY `MwstShl` (`MwstShl`),
  KEY `MasterID` (`MasterID`),
  KEY `DepotNr` (`DepotNr`)
) ENGINE=MyISAM AUTO_INCREMENT=516956 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tomcount`
--

DROP TABLE IF EXISTS `tomcount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tomcount` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `setid` int(10) unsigned DEFAULT NULL,
  `activvon` datetime DEFAULT NULL,
  `activebis` datetime DEFAULT NULL,
  `abgerechnetbis` datetime DEFAULT NULL,
  `statusid` int(10) unsigned NOT NULL,
  `depot` int(10) unsigned NOT NULL,
  `Bemerkung` varchar(150) DEFAULT NULL,
  `isAbgerechnet` int(11) NOT NULL DEFAULT '0',
  `abgerechnetbisSave` datetime DEFAULT NULL,
  `ohneBerechnung` int(11) NOT NULL,
  `Liniennr` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=11393 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tomhistorie`
--

DROP TABLE IF EXISTS `tomhistorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tomhistorie` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `setid` int(10) unsigned DEFAULT NULL,
  `mcid` int(10) unsigned DEFAULT NULL,
  `mobileid` int(10) unsigned DEFAULT NULL,
  `event` varchar(45) NOT NULL,
  `txt` text,
  `depot` int(10) unsigned DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `setid` (`setid`),
  KEY `timestamp` (`timestamp`)
) ENGINE=MyISAM AUTO_INCREMENT=2904 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tomsets`
--

DROP TABLE IF EXISTS `tomsets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tomsets` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `txt` text,
  `Nummer` varchar(45) DEFAULT NULL,
  `tarif` varchar(45) DEFAULT NULL,
  `aktcode` varchar(45) DEFAULT NULL,
  `sim` varchar(45) DEFAULT NULL,
  `devicetyp` varchar(45) DEFAULT NULL,
  `snnr` varchar(45) DEFAULT NULL,
  `swversion` varchar(45) DEFAULT NULL,
  `startdat` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `endedat` datetime DEFAULT NULL,
  `roaming` int(11) DEFAULT NULL,
  `tagpreis` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=999986012 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `uebersichtseintraege`
--

DROP TABLE IF EXISTS `uebersichtseintraege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uebersichtseintraege` (
  `SwitchboardID` int(11) NOT NULL DEFAULT '0',
  `ItemNumber` smallint(6) NOT NULL DEFAULT '0',
  `ItemText` char(255) DEFAULT NULL,
  `Command` smallint(6) NOT NULL DEFAULT '0',
  `Argument` char(50) DEFAULT NULL,
  PRIMARY KEY (`SwitchboardID`,`ItemNumber`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usysprinter`
--

DROP TABLE IF EXISTS `usysprinter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usysprinter` (
  `Id` int(6) unsigned NOT NULL AUTO_INCREMENT,
  `User` char(50) NOT NULL DEFAULT '',
  `PrinterName` char(100) NOT NULL DEFAULT '',
  `left` double(10,2) NOT NULL DEFAULT '0.00',
  `right` double(10,2) NOT NULL DEFAULT '0.00',
  `top` double(10,2) NOT NULL DEFAULT '0.00',
  `bot` double(10,2) NOT NULL DEFAULT '0.00',
  `StationNr` int(10) unsigned NOT NULL DEFAULT '0',
  `PrinterTyp` int(10) unsigned NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `APP` varchar(15) DEFAULT NULL,
  `LOGIN` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `user` (`User`),
  KEY `printertyp` (`PrinterTyp`),
  KEY `stationsnummer` (`StationNr`)
) ENGINE=MyISAM AUTO_INCREMENT=273749 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usystbllocks`
--

DROP TABLE IF EXISTS `usystbllocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usystbllocks` (
  `LockID` int(11) NOT NULL DEFAULT '0',
  `sBenutzer` char(50) NOT NULL DEFAULT 'Unbekannt',
  `Rechner` char(50) NOT NULL DEFAULT 'Unbekannt',
  `LockParameter` char(50) NOT NULL DEFAULT 'Nichts',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LockCounter` int(11) NOT NULL AUTO_INCREMENT,
  `LockLevel` int(11) NOT NULL DEFAULT '0',
  `bExclusiv` tinyint(4) DEFAULT NULL,
  `sAppName` char(255) NOT NULL DEFAULT 'Unknown',
  `lPID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`LockLevel`,`sAppName`,`lPID`,`sBenutzer`,`Rechner`,`LockParameter`,`LockID`),
  UNIQUE KEY `LockCounter` (`LockCounter`),
  KEY `LockID` (`LockID`,`sAppName`,`bExclusiv`),
  KEY `LockID_2` (`LockID`),
  KEY `LockID_3` (`LockID`,`sAppName`,`LockParameter`)
) ENGINE=MyISAM AUTO_INCREMENT=1712750 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usystblplzstrahl`
--

DROP TABLE IF EXISTS `usystblplzstrahl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usystblplzstrahl` (
  `LPOID` char(10) NOT NULL DEFAULT '',
  `LKZDE` char(2) NOT NULL DEFAULT 'DE',
  `LKZFR` char(2) NOT NULL DEFAULT 'FR',
  `LKZCH` char(2) NOT NULL DEFAULT 'CH',
  `LKZCZ` char(2) NOT NULL DEFAULT 'CZ',
  `LKZLU` char(2) NOT NULL DEFAULT 'LU',
  `LKZNL` char(2) NOT NULL DEFAULT 'NL',
  PRIMARY KEY (`LPOID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usystblsemaphor`
--

DROP TABLE IF EXISTS `usystblsemaphor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usystblsemaphor` (
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sBenutzer` char(50) DEFAULT NULL,
  `sRechner` char(50) DEFAULT NULL,
  `bLimit` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `bExclusiv` tinyint(4) DEFAULT NULL,
  `sAppName` char(255) DEFAULT NULL,
  `lPID` int(11) DEFAULT NULL,
  PRIMARY KEY (`bLimit`),
  KEY `lPID` (`lPID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usystblzaehler`
--

DROP TABLE IF EXISTS `usystblzaehler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usystblzaehler` (
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DateID` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Tageszaehler` int(11) NOT NULL DEFAULT '1',
  `CounterTyp` int(11) NOT NULL DEFAULT '0',
  `dateser` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`DateID`,`CounterTyp`),
  KEY `countertyp` (`CounterTyp`),
  KEY `Dateid` (`DateID`),
  KEY `timestamp` (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usystblzaehlerms`
--

DROP TABLE IF EXISTS `usystblzaehlerms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usystblzaehlerms` (
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Tageszaehler` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usystblzahlenstrahl`
--

DROP TABLE IF EXISTS `usystblzahlenstrahl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usystblzahlenstrahl` (
  `LPOID` int(11) NOT NULL AUTO_INCREMENT,
  `Muell` char(1) NOT NULL DEFAULT '',
  PRIMARY KEY (`LPOID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zentiptext`
--

DROP TABLE IF EXISTS `zentiptext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zentiptext` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sFormRep` varchar(100) DEFAULT NULL,
  `sFeldName` varchar(100) DEFAULT NULL,
  `dFeldArt` double DEFAULT NULL,
  `sFeldBezeichnung` varchar(100) DEFAULT NULL,
  `sTipText` varchar(200) DEFAULT NULL,
  `sBeschriftung` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `dFeldArt` (`dFeldArt`),
  KEY `sFormRep` (`sFormRep`),
  KEY `sFeldName` (`sFeldName`)
) ENGINE=MyISAM AUTO_INCREMENT=2132 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zulauf_tmp`
--

DROP TABLE IF EXISTS `zulauf_tmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zulauf_tmp` (
  `id` double NOT NULL AUTO_INCREMENT,
  `AnzPakZ` double DEFAULT NULL,
  `SummeEffektivZ` double DEFAULT NULL,
  `dAllZulauf` double DEFAULT NULL,
  `dUntZulauf` double DEFAULT NULL,
  `dAnzPakZulauf` double DEFAULT NULL,
  `dGewPakZulauf` double DEFAULT NULL,
  `dAllStationZulauf` double DEFAULT NULL,
  `dUntStationZulauf` double DEFAULT NULL,
  `dAllAblauf` double DEFAULT NULL,
  `dHub2Ablauf` double DEFAULT NULL,
  `dAllStationAblauf` double DEFAULT NULL,
  `dUntStationAblauf` double DEFAULT NULL,
  `dAnzPakAblauf` double DEFAULT NULL,
  `dGewPakAblauf` double DEFAULT NULL,
  `dAllZulaufStat` double DEFAULT NULL,
  `dUntZulaufStat` double DEFAULT NULL,
  `dAnzPakAblauf_gruen` double DEFAULT NULL,
  `dGewPakAblauf_gruen` double DEFAULT NULL,
  `dAnzPakAblauf_rot` double DEFAULT NULL,
  `dGewPakAblauf_rot` double DEFAULT NULL,
  `dAllStationAblaufStat` double DEFAULT NULL,
  `dUntStationAblaufStat` double DEFAULT NULL,
  `dAnzPakAblaufStat` double DEFAULT NULL,
  `dGewPakAblaufStat` double DEFAULT NULL,
  `mtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mtime` (`mtime`)
) ENGINE=MyISAM AUTO_INCREMENT=20521 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `zustelldispo`
--

DROP TABLE IF EXISTS `zustelldispo`;
/*!50001 DROP VIEW IF EXISTS `zustelldispo`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `zustelldispo` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `Ladelisten_Nummer` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `OrderIDy` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `ColliesGesamty` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `tblauftrag_Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `frei3` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `verladelinie` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `BagIDNrC` tinyint NOT NULL,
  `BagBelegNrC` tinyint NOT NULL,
  `BagBelegNrAbC` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `zustelldispo10`
--

DROP TABLE IF EXISTS `zustelldispo10`;
/*!50001 DROP VIEW IF EXISTS `zustelldispo10`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `zustelldispo10` (
  `FirmaD` tinyint NOT NULL,
  `PLZD` tinyint NOT NULL,
  `Ladelisten_Nummer` tinyint NOT NULL,
  `OrtD` tinyint NOT NULL,
  `FirmaS` tinyint NOT NULL,
  `FirmaD2` tinyint NOT NULL,
  `FirmaD3` tinyint NOT NULL,
  `AuftragsID` tinyint NOT NULL,
  `OrderID` tinyint NOT NULL,
  `OrderIDy` tinyint NOT NULL,
  `SdgStatus` tinyint NOT NULL,
  `SdgType` tinyint NOT NULL,
  `GKNr` tinyint NOT NULL,
  `Belegnummer` tinyint NOT NULL,
  `DepotNrAD` tinyint NOT NULL,
  `DepotNrLD` tinyint NOT NULL,
  `DepotNrZD` tinyint NOT NULL,
  `lockflag` tinyint NOT NULL,
  `dtCreateAD` tinyint NOT NULL,
  `dtSendAD2Z` tinyint NOT NULL,
  `dtReceiveAD2Z` tinyint NOT NULL,
  `dtSendZ2H` tinyint NOT NULL,
  `dtReceiveZ2H` tinyint NOT NULL,
  `dtSendH2Z` tinyint NOT NULL,
  `dtReceiveH2Z` tinyint NOT NULL,
  `dtSendZ2ZD` tinyint NOT NULL,
  `dtReceiveZ2ZD` tinyint NOT NULL,
  `dtModifyZD` tinyint NOT NULL,
  `dtTermin` tinyint NOT NULL,
  `dtAuslieferung` tinyint NOT NULL,
  `Timestamp` tinyint NOT NULL,
  `KDNR` tinyint NOT NULL,
  `LandS` tinyint NOT NULL,
  `PLZS` tinyint NOT NULL,
  `OrtS` tinyint NOT NULL,
  `StrasseS` tinyint NOT NULL,
  `StrNrS` tinyint NOT NULL,
  `TelefonNrS` tinyint NOT NULL,
  `TelefaxNrS` tinyint NOT NULL,
  `LandD` tinyint NOT NULL,
  `StrasseD` tinyint NOT NULL,
  `StrNrD` tinyint NOT NULL,
  `TelefonVWD` tinyint NOT NULL,
  `TelefonNrD` tinyint NOT NULL,
  `TelefaxNrD` tinyint NOT NULL,
  `GewichtGesamt` tinyint NOT NULL,
  `ColliesGesamt` tinyint NOT NULL,
  `ColliesGesamty` tinyint NOT NULL,
  `dtAuslieferDatum` tinyint NOT NULL,
  `dtAuslieferZeit` tinyint NOT NULL,
  `Empfaenger` tinyint NOT NULL,
  `PreisNN` tinyint NOT NULL,
  `KZ_Fahrzeug` tinyint NOT NULL,
  `KZ_Transportart` tinyint NOT NULL,
  `dtTermin_von` tinyint NOT NULL,
  `Service` tinyint NOT NULL,
  `Feiertag_2` tinyint NOT NULL,
  `Zone` tinyint NOT NULL,
  `Insel` tinyint NOT NULL,
  `Zonea` tinyint NOT NULL,
  `Insela` tinyint NOT NULL,
  `Verladedatum` tinyint NOT NULL,
  `Verladezeit_von` tinyint NOT NULL,
  `Verladezeit_bis` tinyint NOT NULL,
  `FahrerNr` tinyint NOT NULL,
  `Inhalt` tinyint NOT NULL,
  `Versicherungswert` tinyint NOT NULL,
  `Wert` tinyint NOT NULL,
  `Frei10` tinyint NOT NULL,
  `tblauftrag_Frei4` tinyint NOT NULL,
  `Information1` tinyint NOT NULL,
  `Information2` tinyint NOT NULL,
  `Info_Rollkarte` tinyint NOT NULL,
  `Info_Intern` tinyint NOT NULL,
  `Sondervereinbarung` tinyint NOT NULL,
  `Importkosten` tinyint NOT NULL,
  `Betrag_Importkosten` tinyint NOT NULL,
  `Betrag_Exportkosten` tinyint NOT NULL,
  `Betrag_Importkosten_best` tinyint NOT NULL,
  `Betrag_Exportkosten_best` tinyint NOT NULL,
  `DepotNrAbD` tinyint NOT NULL,
  `SdgArt` tinyint NOT NULL,
  `IDSdgArt` tinyint NOT NULL,
  `termin_i` tinyint NOT NULL,
  `CollieBelegNr` tinyint NOT NULL,
  `Bemerkung` tinyint NOT NULL,
  `OrderPos` tinyint NOT NULL,
  `Laenge` tinyint NOT NULL,
  `Breite` tinyint NOT NULL,
  `Hoehe` tinyint NOT NULL,
  `GewichtReal` tinyint NOT NULL,
  `GewichtEffektiv` tinyint NOT NULL,
  `GewichtLBH` tinyint NOT NULL,
  `VerpackungsArt` tinyint NOT NULL,
  `Rollkartennummer` tinyint NOT NULL,
  `RollkartennummerD` tinyint NOT NULL,
  `frei3` tinyint NOT NULL,
  `Frei2` tinyint NOT NULL,
  `Frei4` tinyint NOT NULL,
  `verladelinie` tinyint NOT NULL,
  `dtAusgangHup3` tinyint NOT NULL,
  `dtEingangHup3` tinyint NOT NULL,
  `TourNr2` tinyint NOT NULL,
  `dtAusgangDepot2` tinyint NOT NULL,
  `dtEingangDepot2` tinyint NOT NULL,
  `mydepotid2` tinyint NOT NULL,
  `BagIDNrC` tinyint NOT NULL,
  `BagBelegNrC` tinyint NOT NULL,
  `BagBelegNrAbC` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `adminstatistiken`
--

/*!50001 DROP TABLE IF EXISTS `adminstatistiken`*/;
/*!50001 DROP VIEW IF EXISTS `adminstatistiken`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `adminstatistiken` AS select `tblauftragclear`.`orderid` AS `orderid`,`tblauftragclear`.`Belegnummer` AS `Belegnummer`,`tblauftragclear`.`stat` AS `stat`,`tblauftragclear`.`Dat` AS `Dat`,`tblauftragclear`.`statuszaehler` AS `statuszaehler`,`tblauftragclear`.`type` AS `type`,`tblauftragclear`.`ld` AS `ld`,`tblauftragclear`.`lieferdatum` AS `lieferdatum`,`tblauftragclear`.`ldlkz` AS `ldlkz`,`tblauftragclear`.`KZ_Transportart` AS `KZ_Transportart` from `tblauftragclear` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_ac`
--

/*!50001 DROP TABLE IF EXISTS `al_view_ac`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_ac` AS select `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin_von` AS `dttermin_von`,`tblauftrag`.`dtTermin` AS `dttermin`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`Zonea` AS `zonea`,`tblauftrag`.`Zone` AS `zone`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferdatum`,`tblauftrag`.`OrderID` AS `orderid` from `tblauftrag` where ((`tblauftrag`.`Belegnummer` > 1000000000) and (`tblauftrag`.`ColliesGesamt` > 0) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`KZ_Transportart` = 1)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_ac_all`
--

/*!50001 DROP TABLE IF EXISTS `al_view_ac_all`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_all`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_ac_all` AS select `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin_von` AS `dttermin_von`,`tblauftrag`.`dtTermin` AS `dttermin`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Verladelinie` AS `Verladelinie`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`Belegnummer` > 1000000000) and (`tblauftrag`.`ColliesGesamt` > 0) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`KZ_Transportart` = 1)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_ac_tr`
--

/*!50001 DROP TABLE IF EXISTS `al_view_ac_tr`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_tr`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_ac_tr` AS select `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin_von` AS `dttermin_von`,`tblauftrag`.`dtTermin` AS `dttermin`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`Zonea` AS `zonea`,`tblauftrag`.`Zone` AS `zone`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferdatum`,`tblauftrag`.`OrderID` AS `orderid`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`Referenz2` AS `Referenz2` from `tblauftrag` where (`tblauftrag`.`lockflag` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_ac_tra`
--

/*!50001 DROP TABLE IF EXISTS `al_view_ac_tra`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_tra`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_ac_tra` AS select `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin_von` AS `dttermin_von`,`tblauftrag`.`dtTermin` AS `dttermin`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`Zonea` AS `zonea`,`tblauftrag`.`Zone` AS `zone`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferdatum`,`tblauftrag`.`OrderID` AS `orderid`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`Referenz2` AS `Referenz2`,`tblauftrag`.`ClearingArtMaster` AS `clearingartmaster` from `tblauftrag` where (`tblauftrag`.`lockflag` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_ac_trall`
--

/*!50001 DROP TABLE IF EXISTS `al_view_ac_trall`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_trall`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_ac_trall` AS select `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin_von` AS `dttermin_von`,`tblauftrag`.`dtTermin` AS `dttermin`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`Zonea` AS `zonea`,`tblauftrag`.`Zone` AS `zone`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferdatum`,`tblauftrag`.`OrderID` AS `orderid` from `tblauftrag` where (`tblauftrag`.`lockflag` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_ac_trb`
--

/*!50001 DROP TABLE IF EXISTS `al_view_ac_trb`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_ac_trb`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_ac_trb` AS select `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin_von` AS `dttermin_von`,`tblauftrag`.`dtTermin` AS `dttermin`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`Zonea` AS `zonea`,`tblauftrag`.`Zone` AS `zone`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferdatum`,`tblauftrag`.`OrderID` AS `orderid`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`Referenz2` AS `Referenz2`,`tblauftrag`.`ClearingArtMaster` AS `clearingartmaster`,`tblauftrag`.`Service` AS `service` from `tblauftrag` where (`tblauftrag`.`lockflag` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_acs`
--

/*!50001 DROP TABLE IF EXISTS `al_view_acs`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_acs`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_acs` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblstatus`.`KZ_Statuserzeuger` AS `KZ_Statuserzeuger`,`tblstatus`.`Packstuecknummer` AS `Packstuecknummer`,`tblstatus`.`Datum` AS `Datum`,`tblstatus`.`Zeit` AS `Zeit`,`tblstatus`.`KZ_Status` AS `KZ_Status`,`tblstatus`.`Fehlercode` AS `Fehlercode`,`tblstatus`.`Erzeugerstation` AS `Erzeugerstation`,`tblstatus`.`Text` AS `text`,`tblstatus`.`Infotext` AS `Infotext`,`tblstatus`.`Frei2` AS `Frei2`,`tblstatus`.`Timestamp2` AS `Timestamp2`,`tblauftrag`.`Verladedatum` AS `Verladedatum` from ((`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) join `tblstatus` on((`tblauftragcollies`.`CollieBelegNr` = `tblstatus`.`Packstuecknummer`))) where ((`tblauftrag`.`OrderID` between 10000000000 and 40000000000) and (`tblauftrag`.`lockflag` = 0)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_af`
--

/*!50001 DROP TABLE IF EXISTS `al_view_af`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_af`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_af` AS select distinct `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`lockflag` AS `lockflag`,`tblfeldhistorie`.`Belegnummer` AS `Belegnummer`,`tblfeldhistorie`.`OldValue` AS `OldValue`,`tblfeldhistorie`.`NewValue` AS `NewValue`,`tblfeldhistorie`.`TIMESTAMP` AS `Timestamp`,`tblfeldhistorie`.`Tabelle` AS `Tabelle`,`tblfeldhistorie`.`FeldName` AS `FeldName`,`tblfeldhistorie`.`OrderID` AS `OrderID` from (`tblauftrag` join `tblfeldhistorie` on((`tblauftrag`.`OrderID` = `tblfeldhistorie`.`OrderID`))) where ((`tblfeldhistorie`.`Belegnummer` > 1000000000) and (`tblfeldhistorie`.`Changer` = _latin1'H') and (`tblauftrag`.`lockflag` = 0)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_tblauftrag`
--

/*!50001 DROP TABLE IF EXISTS `al_view_tblauftrag`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_tblauftrag`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_tblauftrag` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrbev` AS `DepotNrbev`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`DatumNN` AS `DatumNN`,`tblauftrag`.`erhaltenNN` AS `erhaltenNN`,`tblauftrag`.`Feiertag_1` AS `Feiertag_1`,`tblauftrag`.`FeiertagShlD` AS `FeiertagShlD`,`tblauftrag`.`FeiertagShlS` AS `FeiertagShlS`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Satzart` AS `Satzart`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`Referenz2` AS `Referenz2`,`tblauftrag`.`Frei` AS `Frei`,`tblauftrag`.`Adr_Nr_Absender` AS `Adr_Nr_Absender`,`tblauftrag`.`Adr_Nr_Empfaenger` AS `Adr_Nr_Empfaenger`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Frei2` AS `Frei2`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Lieferdatum` AS `Lieferdatum`,`tblauftrag`.`Lieferzeit_von` AS `Lieferzeit_von`,`tblauftrag`.`Lieferzeit_bis` AS `Lieferzeit_bis`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`Frei3` AS `Frei3`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`KZServiceLeo` AS `KZServiceLeo`,`tblauftrag`.`Sendungsstatus` AS `Sendungsstatus`,`tblauftrag`.`Ausliefertour` AS `Ausliefertour`,`tblauftrag`.`KZ_erweitert` AS `KZ_erweitert`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Frei5` AS `Frei5`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`Besteller_Name` AS `Besteller_Name`,`tblauftrag`.`Ladelisten_Nummer` AS `Ladelisten_Nummer`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`KZ_Zusatz` AS `KZ_Zusatz`,`tblauftrag`.`CR` AS `CR`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`d` AS `d`,`tblauftrag`.`a` AS `a`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`RechnungsNr` AS `RechnungsNr`,`tblauftrag`.`Satzart_ava` AS `Satzart_ava`,`tblauftrag`.`FirmaD3X` AS `FirmaD3X`,`tblauftrag`.`LandDX` AS `LandDX`,`tblauftrag`.`PLZDX` AS `PLZDX`,`tblauftrag`.`OrtDX` AS `OrtDX`,`tblauftrag`.`StrasseDX` AS `StrasseDX`,`tblauftrag`.`StrNrDX` AS `StrNrDX`,`tblauftrag`.`Satzartp` AS `Satzartp`,`tblauftrag`.`PZA_bag` AS `PZA_bag`,`tblauftrag`.`Belegnummer_akt` AS `Belegnummer_akt`,`tblauftrag`.`UploadStatus` AS `UploadStatus`,`tblauftrag`.`SendStatus` AS `SendStatus`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`OrderID_X` AS `OrderID_X`,`tblauftrag`.`PZAImageZIP` AS `PZAImageZIP`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`RueckDate` AS `RueckDate`,`tblauftrag`.`ClearingArtMaster` AS `ClearingArtMaster`,`tblauftrag`.`ZoneS` AS `ZoneS`,`tblauftrag`.`Locking` AS `Locking`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`ROrderID` AS `ROrderID`,`tblauftrag`.`EBOrderID` AS `EBOrderID`,`tblauftrag`.`EXAuftragsIDRef` AS `EXAuftragsIDRef`,`tblauftrag`.`EBRueckGrund` AS `EBRueckGrund`,`tblauftrag`.`product_spec` AS `product_spec` from `tblauftrag` where ((`tblauftrag`.`Belegnummer` > 1000000000) and (`tblauftrag`.`ColliesGesamt` > 0) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`SdgStatus` = _latin1'S')) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `al_view_tblauftrag_collies`
--

/*!50001 DROP TABLE IF EXISTS `al_view_tblauftrag_collies`*/;
/*!50001 DROP VIEW IF EXISTS `al_view_tblauftrag_collies`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `al_view_tblauftrag_collies` AS select `tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin_von` AS `dttermin_von`,`tblauftrag`.`dtTermin` AS `dttermin`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftragcollies`.`OrderID` AS `OrderID`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`Empfaenger2` AS `Empfaenger2`,`tblauftragcollies`.`VerpackungsArt` AS `Verpackungsart`,`tblauftragcollies`.`Verladelinie` AS `Verladelinie`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`cReferenz` AS `creferenz`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`Belegnummer` > 1000000000) and (`tblauftragcollies`.`CollieBelegNr` > 1000000000) and (`tblauftrag`.`ColliesGesamt` > 0) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`SdgStatus` = _latin1'S')) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `auftragcollie10d`
--

/*!50001 DROP TABLE IF EXISTS `auftragcollie10d`*/;
/*!50001 DROP VIEW IF EXISTS `auftragcollie10d`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `auftragcollie10d` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`EBOrderID` AS `eborderid`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftragcollies`.`Verladelinie` AS `verladelinie`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`Frei2` AS `frei2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtLagereingang` AS `dtLagereingang`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`OrderPos` AS `orderpos`,`tblauftragcollies`.`dtLagerausgang` AS `dtLagerausgang`,`tblauftragcollies`.`Lagerplatz` AS `Lagerplatz` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`Belegnummer` > 1000000000) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `hubsendungen`
--

/*!50001 DROP TABLE IF EXISTS `hubsendungen`*/;
/*!50001 DROP VIEW IF EXISTS `hubsendungen`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `hubsendungen` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`Ladelisten_Nummer` AS `Ladelisten_Nummer`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`OrderID` AS `OrderIDy`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamty`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `tblauftrag_Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`Frei3` AS `frei3`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`Verladelinie` AS `verladelinie`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`BagIDNrC` AS `BagIDNrC`,`tblauftragcollies`.`BagBelegNrC` AS `BagBelegNrC`,`tblauftragcollies`.`BagBelegNrAbC` AS `BagBelegNrAbC` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `hubsendungen20`
--

/*!50001 DROP TABLE IF EXISTS `hubsendungen20`*/;
/*!50001 DROP VIEW IF EXISTS `hubsendungen20`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `hubsendungen20` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`Ladelisten_Nummer` AS `Ladelisten_Nummer`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`OrderID` AS `OrderIDy`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamty`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `tblauftrag_Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`Frei3` AS `frei3`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`Verladelinie` AS `verladelinie`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where (`tblauftrag`.`Verladedatum` > (curdate() + interval -(20) day)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `hubsendungenakt`
--

/*!50001 DROP TABLE IF EXISTS `hubsendungenakt`*/;
/*!50001 DROP VIEW IF EXISTS `hubsendungenakt`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `hubsendungenakt` AS select `tblauftrag`.`KZ_Transportart` AS `kz_transportart`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`BagIDNrA` AS `BagIDNrA`,`tblauftrag`.`ClearingArtMaster` AS `ClearingArtMaster`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`LadelistennummerD` AS `LadelistennummerD`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`Verladelinie` AS `verladelinie`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`BagIDNrC` AS `BagIDNrC`,`tblauftragcollies`.`BagBelegNrC` AS `BagBelegNrC`,`tblauftragcollies`.`BagBelegNrAbC` AS `BagBelegNrAbC`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where (((`tblauftrag`.`Service` & 134217728) = 0) and (`tblauftrag`.`KZ_Transportart` = 1) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` = cast((now() + interval -(10) hour) as date))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `hubsscount`
--

/*!50001 DROP TABLE IF EXISTS `hubsscount`*/;
/*!50001 DROP VIEW IF EXISTS `hubsscount`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `hubsscount` AS select ((`tblauftrag`.`DepotNrAD` = `tblauftrag`.`DepotNrAbD`) & (`tblauftrag`.`DepotNrAbD` = `tblauftrag`.`DepotNrLD`)) AS `ES`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`Verladelinie` AS `verladelinie`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`KZ_Transportart` = 1) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(3) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `hubstatus`
--

/*!50001 DROP TABLE IF EXISTS `hubstatus`*/;
/*!50001 DROP VIEW IF EXISTS `hubstatus`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `hubstatus` AS select `tblstatus`.`KZ_Statuserzeuger` AS `KZ_Statuserzeuger`,`tblstatus`.`Packstuecknummer` AS `Packstuecknummer`,`tblstatus`.`Datum` AS `Datum`,`tblstatus`.`Zeit` AS `Zeit`,`tblstatus`.`KZ_Status` AS `KZ_Status`,`tblstatus`.`Fehlercode` AS `Fehlercode`,`tblstatus`.`Erzeugerstation` AS `Erzeugerstation`,`tblstatus`.`Text` AS `Text`,`tblstatus`.`Wartezeit` AS `Wartezeit`,`tblstatus`.`Exportstation` AS `Exportstation`,`tblstatus`.`Frei1` AS `Frei1`,`tblstatus`.`Infotext` AS `Infotext`,`tblstatus`.`Frei2` AS `Frei2`,`tblstatus`.`uebertragen` AS `uebertragen`,`tblstatus`.`Timestamp` AS `Timestamp`,`tblstatus`.`Timestamp2` AS `Timestamp2`,`tblstatus`.`Zaehler` AS `Zaehler`,`tblstatus`.`UploadStatus` AS `UploadStatus`,`tblstatus`.`SendStatus` AS `SendStatus` from `tblstatus` where (`tblstatus`.`Timestamp2` > (curdate() + interval -(10) day)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `lag_view_ac`
--

/*!50001 DROP TABLE IF EXISTS `lag_view_ac`*/;
/*!50001 DROP VIEW IF EXISTS `lag_view_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `lag_view_ac` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`EBOrderID` AS `EBOrderID`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`EXAuftragsIDRef` AS `EXAuftragsIDRef`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`Frei2` AS `frei2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtLagereingang` AS `dtLagereingang`,`tblauftragcollies`.`dtLagerausgang` AS `dtLagerausgang`,`tblauftragcollies`.`Verladelinie` AS `Verladelinie`,`tblauftragcollies`.`AuslieferDatum2` AS `Auslieferdatum2`,`tblauftragcollies`.`Lagerplatz` AS `Lagerplatz` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`Belegnummer` > 1000000000) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`EXAuftragsIDRef` is not null) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(30) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `lag_view_ac_total`
--

/*!50001 DROP TABLE IF EXISTS `lag_view_ac_total`*/;
/*!50001 DROP VIEW IF EXISTS `lag_view_ac_total`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `lag_view_ac_total` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`EBOrderID` AS `EBOrderID`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`EXAuftragsIDRef` AS `EXAuftragsIDRef`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`Frei2` AS `frei2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtLagereingang` AS `dtLagereingang`,`tblauftragcollies`.`dtLagerausgang` AS `dtLagerausgang`,`tblauftragcollies`.`Verladelinie` AS `Verladelinie`,`tblauftragcollies`.`AuslieferDatum2` AS `Auslieferdatum2`,`tblauftragcollies`.`Lagerplatz` AS `Lagerplatz` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`Belegnummer` > 1000000000) and (`tblauftrag`.`ColliesGesamt` > 0) and (`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`EXAuftragsIDRef` is not null) and (`tblauftrag`.`Verladedatum` > _utf8'2008-01-01 00:00:00')) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `lag_view_c`
--

/*!50001 DROP TABLE IF EXISTS `lag_view_c`*/;
/*!50001 DROP VIEW IF EXISTS `lag_view_c`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `lag_view_c` AS select `tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Lagerplatz` AS `Lagerplatz`,`tblauftragcollies`.`dtLagereingang` AS `dtLagereingang`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`Empfaenger2` AS `Empfaenger2`,`tblauftragcollies`.`VerpackungsArt` AS `Verpackungsart`,`tblauftragcollies`.`Verladelinie` AS `Verladelinie`,`tblauftragcollies`.`AuslieferDatum2` AS `Auslieferdatum2`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where (`tblauftrag`.`Verladedatum` > 20080101) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_clearingstatus_a`
--

/*!50001 DROP TABLE IF EXISTS `leo_clearingstatus_a`*/;
/*!50001 DROP VIEW IF EXISTS `leo_clearingstatus_a`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_clearingstatus_a` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i` from `tblauftrag` where (`tblauftrag`.`lockflag` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_clearingstatus_ac`
--

/*!50001 DROP TABLE IF EXISTS `leo_clearingstatus_ac`*/;
/*!50001 DROP VIEW IF EXISTS `leo_clearingstatus_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_clearingstatus_ac` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`TourNr2` AS `TourNr2` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_clearingstatus_lj_ac`
--

/*!50001 DROP TABLE IF EXISTS `leo_clearingstatus_lj_ac`*/;
/*!50001 DROP VIEW IF EXISTS `leo_clearingstatus_lj_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_clearingstatus_lj_ac` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Referenz` AS `referenz`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2` from (`tblauftrag` left join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_dispo_a`
--

/*!50001 DROP TABLE IF EXISTS `leo_dispo_a`*/;
/*!50001 DROP VIEW IF EXISTS `leo_dispo_a`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_dispo_a` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrbev` AS `DepotNrbev`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`DatumNN` AS `DatumNN`,`tblauftrag`.`erhaltenNN` AS `erhaltenNN`,`tblauftrag`.`Feiertag_1` AS `Feiertag_1`,`tblauftrag`.`FeiertagShlD` AS `FeiertagShlD`,`tblauftrag`.`FeiertagShlS` AS `FeiertagShlS`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Satzart` AS `Satzart`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`Referenz2` AS `Referenz2`,`tblauftrag`.`Frei` AS `Frei`,`tblauftrag`.`Adr_Nr_Absender` AS `Adr_Nr_Absender`,`tblauftrag`.`Adr_Nr_Empfaenger` AS `Adr_Nr_Empfaenger`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Frei2` AS `Frei2`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Lieferdatum` AS `Lieferdatum`,`tblauftrag`.`Lieferzeit_von` AS `Lieferzeit_von`,`tblauftrag`.`Lieferzeit_bis` AS `Lieferzeit_bis`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`Frei3` AS `Frei3`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`KZServiceLeo` AS `KZServiceLeo`,`tblauftrag`.`Sendungsstatus` AS `Sendungsstatus`,`tblauftrag`.`Ausliefertour` AS `Ausliefertour`,`tblauftrag`.`KZ_erweitert` AS `KZ_erweitert`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Frei5` AS `Frei5`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`Besteller_Name` AS `Besteller_Name`,`tblauftrag`.`Ladelisten_Nummer` AS `Ladelisten_Nummer`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`KZ_Zusatz` AS `KZ_Zusatz`,`tblauftrag`.`CR` AS `CR`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`d` AS `d`,`tblauftrag`.`a` AS `a`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`RechnungsNr` AS `RechnungsNr`,`tblauftrag`.`Satzart_ava` AS `Satzart_ava`,`tblauftrag`.`FirmaD3X` AS `FirmaD3X`,`tblauftrag`.`LandDX` AS `LandDX`,`tblauftrag`.`PLZDX` AS `PLZDX`,`tblauftrag`.`OrtDX` AS `OrtDX`,`tblauftrag`.`StrasseDX` AS `StrasseDX`,`tblauftrag`.`StrNrDX` AS `StrNrDX`,`tblauftrag`.`frueheste_zustellzeit` AS `frueheste_zustellzeit`,`tblauftrag`.`Satzartp` AS `Satzartp`,`tblauftrag`.`PZA_bag` AS `PZA_bag`,`tblauftrag`.`Belegnummer_akt` AS `Belegnummer_akt`,`tblauftrag`.`UploadStatus` AS `UploadStatus`,`tblauftrag`.`SendStatus` AS `SendStatus`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`OrderID_X` AS `OrderID_X`,`tblauftrag`.`PZAImageZIP` AS `PZAImageZIP`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`RueckDate` AS `RueckDate`,`tblauftrag`.`ClearingArtMaster` AS `ClearingArtMaster`,`tblauftrag`.`ZoneS` AS `ZoneS`,`tblauftrag`.`Locking` AS `Locking`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`ROrderID` AS `ROrderID`,`tblauftrag`.`EBOrderID` AS `EBOrderID`,`tblauftrag`.`EXAuftragsIDRef` AS `EXAuftragsIDRef`,`tblauftrag`.`EBRueckGrund` AS `EBRueckGrund` from `tblauftrag` where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_dispo_ac`
--

/*!50001 DROP TABLE IF EXISTS `leo_dispo_ac`*/;
/*!50001 DROP VIEW IF EXISTS `leo_dispo_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_dispo_ac` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrbev` AS `DepotNrbev`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`DatumNN` AS `DatumNN`,`tblauftrag`.`erhaltenNN` AS `erhaltenNN`,`tblauftrag`.`Feiertag_1` AS `Feiertag_1`,`tblauftrag`.`FeiertagShlD` AS `FeiertagShlD`,`tblauftrag`.`FeiertagShlS` AS `FeiertagShlS`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Satzart` AS `Satzart`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`Referenz2` AS `Referenz2`,`tblauftrag`.`Frei` AS `Frei`,`tblauftrag`.`Adr_Nr_Absender` AS `Adr_Nr_Absender`,`tblauftrag`.`Adr_Nr_Empfaenger` AS `Adr_Nr_Empfaenger`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Frei2` AS `Frei2`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Lieferdatum` AS `Lieferdatum`,`tblauftrag`.`Lieferzeit_von` AS `Lieferzeit_von`,`tblauftrag`.`Lieferzeit_bis` AS `Lieferzeit_bis`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`Frei3` AS `Frei3`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`KZServiceLeo` AS `KZServiceLeo`,`tblauftrag`.`Sendungsstatus` AS `Sendungsstatus`,`tblauftrag`.`Ausliefertour` AS `Ausliefertour`,`tblauftrag`.`KZ_erweitert` AS `KZ_erweitert`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Frei5` AS `Frei5`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`Besteller_Name` AS `Besteller_Name`,`tblauftrag`.`Ladelisten_Nummer` AS `Ladelisten_Nummer`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`KZ_Zusatz` AS `KZ_Zusatz`,`tblauftrag`.`CR` AS `CR`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`d` AS `d`,`tblauftrag`.`a` AS `a`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`RechnungsNr` AS `RechnungsNr`,`tblauftrag`.`Satzart_ava` AS `Satzart_ava`,`tblauftrag`.`FirmaD3X` AS `FirmaD3X`,`tblauftrag`.`LandDX` AS `LandDX`,`tblauftrag`.`PLZDX` AS `PLZDX`,`tblauftrag`.`OrtDX` AS `OrtDX`,`tblauftrag`.`StrasseDX` AS `StrasseDX`,`tblauftrag`.`StrNrDX` AS `StrNrDX`,`tblauftrag`.`frueheste_zustellzeit` AS `frueheste_zustellzeit`,`tblauftrag`.`Satzartp` AS `Satzartp`,`tblauftrag`.`PZA_bag` AS `PZA_bag`,`tblauftrag`.`Belegnummer_akt` AS `Belegnummer_akt`,`tblauftrag`.`UploadStatus` AS `UploadStatus`,`tblauftrag`.`SendStatus` AS `SendStatus`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`OrderID_X` AS `OrderID_X`,`tblauftrag`.`PZAImageZIP` AS `PZAImageZIP`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`RueckDate` AS `RueckDate`,`tblauftrag`.`ClearingArtMaster` AS `ClearingArtMaster`,`tblauftrag`.`ZoneS` AS `ZoneS`,`tblauftrag`.`Locking` AS `Locking`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`ROrderID` AS `ROrderID`,`tblauftrag`.`EBOrderID` AS `EBOrderID`,`tblauftrag`.`EXAuftragsIDRef` AS `EXAuftragsIDRef`,`tblauftrag`.`EBRueckGrund` AS `EBRueckGrund`,`tblauftrag`.`OrderID` AS `YOrderid`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`i_scan` AS `i_scan`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv` from (`tblauftrag` left join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_erfassung_a`
--

/*!50001 DROP TABLE IF EXISTS `leo_erfassung_a`*/;
/*!50001 DROP VIEW IF EXISTS `leo_erfassung_a`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_erfassung_a` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`OrderID` AS `OrderIDy`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamty`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i` from `tblauftrag` where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_erfassung_ac`
--

/*!50001 DROP TABLE IF EXISTS `leo_erfassung_ac`*/;
/*!50001 DROP VIEW IF EXISTS `leo_erfassung_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_erfassung_ac` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`OrderID` AS `OrderIDy`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamty`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2` from (`tblauftrag` left join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_fr_repl_a`
--

/*!50001 DROP TABLE IF EXISTS `leo_fr_repl_a`*/;
/*!50001 DROP VIEW IF EXISTS `leo_fr_repl_a`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_fr_repl_a` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`Referenz` AS `Referenz`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i` from `tblauftrag` where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_fr_repl_ac`
--

/*!50001 DROP TABLE IF EXISTS `leo_fr_repl_ac`*/;
/*!50001 DROP VIEW IF EXISTS `leo_fr_repl_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_fr_repl_ac` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`DepotNrED` AS `depotnred`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`Ausliefertour` AS `Ausliefertour`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingang2` AS `dtEingang2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`Timestamp22` AS `Timestamp22`,`tblauftragcollies`.`i_scan` AS `i_scan`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftrag`.`product_spec` AS `product_spec` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_fr_repl_lj_ac`
--

/*!50001 DROP TABLE IF EXISTS `leo_fr_repl_lj_ac`*/;
/*!50001 DROP VIEW IF EXISTS `leo_fr_repl_lj_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_fr_repl_lj_ac` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`DepotNrED` AS `depotnred`,`tblauftrag`.`Referenz` AS `referenz`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`Ausliefertour` AS `Ausliefertour`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingang2` AS `dtEingang2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`Timestamp22` AS `Timestamp22`,`tblauftragcollies`.`i_scan` AS `i_scan`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2` from (`tblauftrag` left join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_ladelistenscan_ac`
--

/*!50001 DROP TABLE IF EXISTS `leo_ladelistenscan_ac`*/;
/*!50001 DROP VIEW IF EXISTS `leo_ladelistenscan_ac`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_ladelistenscan_ac` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`DepotNrED` AS `depotnred`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`Ausliefertour` AS `Ausliefertour`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingang2` AS `dtEingang2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`Timestamp22` AS `Timestamp22`,`tblauftragcollies`.`i_scan` AS `i_scan`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`LadelistennummerD` AS `LadelistennummerD`,`tblauftragcollies`.`Beladelinie` AS `Beladelinie` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and isnull(`tblauftrag`.`Empfaenger`) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(2) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_ladelistenscan_ac_20120626`
--

/*!50001 DROP TABLE IF EXISTS `leo_ladelistenscan_ac_20120626`*/;
/*!50001 DROP VIEW IF EXISTS `leo_ladelistenscan_ac_20120626`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_ladelistenscan_ac_20120626` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`DepotNrED` AS `depotnred`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaS2` AS `FirmaS2`,`tblauftrag`.`FirmaS3` AS `FirmaS3`,`tblauftrag`.`Ausliefertour` AS `Ausliefertour`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingang2` AS `dtEingang2`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`Timestamp22` AS `Timestamp22`,`tblauftragcollies`.`i_scan` AS `i_scan`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`LadelistennummerD` AS `LadelistennummerD`,`tblauftragcollies`.`Beladelinie` AS `Beladelinie`,`tblauftragcollies`.`RUP` AS `RUP`,`tblauftragcollies`.`RUP_org` AS `RUP_org` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and isnull(`tblauftrag`.`Empfaenger`) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(2) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_view_rollkartenscan`
--

/*!50001 DROP TABLE IF EXISTS `leo_view_rollkartenscan`*/;
/*!50001 DROP VIEW IF EXISTS `leo_view_rollkartenscan`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_view_rollkartenscan` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`Timestamp22` AS `Timestamp22`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Frei4` AS `frei4_`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`rKM` AS `rKM`,`tblauftragcollies`.`rkLR` AS `rkLR`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and (`tblauftragcollies`.`Timestamp22` > (now() + interval -(7) hour))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_view_rpt_rollkarte`
--

/*!50001 DROP TABLE IF EXISTS `leo_view_rpt_rollkarte`*/;
/*!50001 DROP VIEW IF EXISTS `leo_view_rpt_rollkarte`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_view_rpt_rollkarte` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`DepotNrED` AS `DepotNrED`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`Timestamp22` AS `Timestamp22`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Frei4` AS `frei4`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`rKM` AS `rKM`,`tblauftragcollies`.`rkLR` AS `rkLR`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `leo_view_rueckerfassung`
--

/*!50001 DROP TABLE IF EXISTS `leo_view_rueckerfassung`*/;
/*!50001 DROP VIEW IF EXISTS `leo_view_rueckerfassung`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `leo_view_rueckerfassung` AS select `tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`Frei4` AS `Frei4_`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Frei1` AS `Frei1`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei3` AS `Frei3`,`tblauftragcollies`.`Frei4` AS `frei4`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`AuslieferDatum2` AS `AuslieferDatum2`,`tblauftragcollies`.`AuslieferZeit2` AS `AuslieferZeit2`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`Empfaenger2` AS `Empfaenger2` from (`tblauftragcollies` join `tblauftrag` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`lockflag` = 0) and (`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `scerr_dt`
--

/*!50001 DROP TABLE IF EXISTS `scerr_dt`*/;
/*!50001 DROP VIEW IF EXISTS `scerr_dt`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `scerr_dt` AS select `scaerror`.`id` AS `id`,`scaerror`.`errorNr` AS `errorNr`,`scaerror`.`errorText` AS `errorText`,`scaerror`.`errorTs` AS `errorTs`,`scaerror`.`timestamp` AS `timestamp`,`scaerror`.`sendstatus` AS `sendstatus`,`scaerror`.`uidLogin` AS `uidLogin`,`scaerror`.`sender` AS `sender` from `scaerror` where (`scaerror`.`errorTs` > cast((now() + interval -(48) hour) as date)) order by `scaerror`.`errorTs` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `zustelldispo`
--

/*!50001 DROP TABLE IF EXISTS `zustelldispo`*/;
/*!50001 DROP VIEW IF EXISTS `zustelldispo`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `zustelldispo` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`Ladelisten_Nummer` AS `Ladelisten_Nummer`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`OrderID` AS `OrderIDy`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamty`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `tblauftrag_Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`Frei3` AS `frei3`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`Verladelinie` AS `verladelinie`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`BagIDNrC` AS `BagIDNrC`,`tblauftragcollies`.`BagBelegNrC` AS `BagBelegNrC`,`tblauftragcollies`.`BagBelegNrAbC` AS `BagBelegNrAbC` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where (`tblauftragcollies`.`Frei2` <> _latin1'4') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `zustelldispo10`
--

/*!50001 DROP TABLE IF EXISTS `zustelldispo10`*/;
/*!50001 DROP VIEW IF EXISTS `zustelldispo10`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `zustelldispo10` AS select `tblauftrag`.`FirmaD` AS `FirmaD`,`tblauftrag`.`PLZD` AS `PLZD`,`tblauftrag`.`Ladelisten_Nummer` AS `Ladelisten_Nummer`,`tblauftrag`.`OrtD` AS `OrtD`,`tblauftrag`.`FirmaS` AS `FirmaS`,`tblauftrag`.`FirmaD2` AS `FirmaD2`,`tblauftrag`.`FirmaD3` AS `FirmaD3`,`tblauftrag`.`AuftragsID` AS `AuftragsID`,`tblauftrag`.`OrderID` AS `OrderID`,`tblauftrag`.`OrderID` AS `OrderIDy`,`tblauftrag`.`SdgStatus` AS `SdgStatus`,`tblauftrag`.`SdgType` AS `SdgType`,`tblauftrag`.`GKNr` AS `GKNr`,`tblauftrag`.`Belegnummer` AS `Belegnummer`,`tblauftrag`.`DepotNrAD` AS `DepotNrAD`,`tblauftrag`.`DepotNrLD` AS `DepotNrLD`,`tblauftrag`.`DepotNrZD` AS `DepotNrZD`,`tblauftrag`.`lockflag` AS `lockflag`,`tblauftrag`.`dtCreateAD` AS `dtCreateAD`,`tblauftrag`.`dtSendAD2Z` AS `dtSendAD2Z`,`tblauftrag`.`dtReceiveAD2Z` AS `dtReceiveAD2Z`,`tblauftrag`.`dtSendZ2H` AS `dtSendZ2H`,`tblauftrag`.`dtReceiveZ2H` AS `dtReceiveZ2H`,`tblauftrag`.`dtSendH2Z` AS `dtSendH2Z`,`tblauftrag`.`dtReceiveH2Z` AS `dtReceiveH2Z`,`tblauftrag`.`dtSendZ2ZD` AS `dtSendZ2ZD`,`tblauftrag`.`dtReceiveZ2ZD` AS `dtReceiveZ2ZD`,`tblauftrag`.`dtModifyZD` AS `dtModifyZD`,`tblauftrag`.`dtTermin` AS `dtTermin`,`tblauftrag`.`dtAuslieferung` AS `dtAuslieferung`,`tblauftrag`.`Timestamp` AS `Timestamp`,`tblauftrag`.`KDNR` AS `KDNR`,`tblauftrag`.`LandS` AS `LandS`,`tblauftrag`.`PLZS` AS `PLZS`,`tblauftrag`.`OrtS` AS `OrtS`,`tblauftrag`.`StrasseS` AS `StrasseS`,`tblauftrag`.`StrNrS` AS `StrNrS`,`tblauftrag`.`TelefonNrS` AS `TelefonNrS`,`tblauftrag`.`TelefaxNrS` AS `TelefaxNrS`,`tblauftrag`.`LandD` AS `LandD`,`tblauftrag`.`StrasseD` AS `StrasseD`,`tblauftrag`.`StrNrD` AS `StrNrD`,`tblauftrag`.`TelefonVWD` AS `TelefonVWD`,`tblauftrag`.`TelefonNrD` AS `TelefonNrD`,`tblauftrag`.`TelefaxNrD` AS `TelefaxNrD`,`tblauftrag`.`GewichtGesamt` AS `GewichtGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamt`,`tblauftrag`.`ColliesGesamt` AS `ColliesGesamty`,`tblauftrag`.`dtAuslieferDatum` AS `dtAuslieferDatum`,`tblauftrag`.`dtAuslieferZeit` AS `dtAuslieferZeit`,`tblauftrag`.`Empfaenger` AS `Empfaenger`,`tblauftrag`.`PreisNN` AS `PreisNN`,`tblauftrag`.`KZ_Fahrzeug` AS `KZ_Fahrzeug`,`tblauftrag`.`KZ_Transportart` AS `KZ_Transportart`,`tblauftrag`.`dtTermin_von` AS `dtTermin_von`,`tblauftrag`.`Service` AS `Service`,`tblauftrag`.`Feiertag_2` AS `Feiertag_2`,`tblauftrag`.`Zone` AS `Zone`,`tblauftrag`.`Insel` AS `Insel`,`tblauftrag`.`Zonea` AS `Zonea`,`tblauftrag`.`Insela` AS `Insela`,`tblauftrag`.`Verladedatum` AS `Verladedatum`,`tblauftrag`.`Verladezeit_von` AS `Verladezeit_von`,`tblauftrag`.`Verladezeit_bis` AS `Verladezeit_bis`,`tblauftrag`.`FahrerNr` AS `FahrerNr`,`tblauftrag`.`Inhalt` AS `Inhalt`,`tblauftrag`.`Versicherungswert` AS `Versicherungswert`,`tblauftrag`.`Wert` AS `Wert`,`tblauftrag`.`Frei10` AS `Frei10`,`tblauftrag`.`Frei4` AS `tblauftrag_Frei4`,`tblauftrag`.`Information1` AS `Information1`,`tblauftrag`.`Information2` AS `Information2`,`tblauftrag`.`Info_Rollkarte` AS `Info_Rollkarte`,`tblauftrag`.`Info_Intern` AS `Info_Intern`,`tblauftrag`.`Sondervereinbarung` AS `Sondervereinbarung`,`tblauftrag`.`Importkosten` AS `Importkosten`,`tblauftrag`.`Betrag_Importkosten` AS `Betrag_Importkosten`,`tblauftrag`.`Betrag_Exportkosten` AS `Betrag_Exportkosten`,`tblauftrag`.`Betrag_Importkosten_best` AS `Betrag_Importkosten_best`,`tblauftrag`.`Betrag_Exportkosten_best` AS `Betrag_Exportkosten_best`,`tblauftrag`.`DepotNrAbD` AS `DepotNrAbD`,`tblauftrag`.`SdgArt` AS `SdgArt`,`tblauftrag`.`IDSdgArt` AS `IDSdgArt`,`tblauftrag`.`termin_i` AS `termin_i`,`tblauftragcollies`.`CollieBelegNr` AS `CollieBelegNr`,`tblauftragcollies`.`Bemerkung` AS `Bemerkung`,`tblauftragcollies`.`OrderPos` AS `OrderPos`,`tblauftragcollies`.`Laenge` AS `Laenge`,`tblauftragcollies`.`Breite` AS `Breite`,`tblauftragcollies`.`Hoehe` AS `Hoehe`,`tblauftragcollies`.`GewichtReal` AS `GewichtReal`,`tblauftragcollies`.`GewichtEffektiv` AS `GewichtEffektiv`,`tblauftragcollies`.`GewichtLBH` AS `GewichtLBH`,`tblauftragcollies`.`VerpackungsArt` AS `VerpackungsArt`,`tblauftragcollies`.`Rollkartennummer` AS `Rollkartennummer`,`tblauftragcollies`.`RollkartennummerD` AS `RollkartennummerD`,`tblauftragcollies`.`Frei3` AS `frei3`,`tblauftragcollies`.`Frei2` AS `Frei2`,`tblauftragcollies`.`Frei4` AS `Frei4`,`tblauftragcollies`.`Verladelinie` AS `verladelinie`,`tblauftragcollies`.`dtAusgangHup3` AS `dtAusgangHup3`,`tblauftragcollies`.`dtEingangHup3` AS `dtEingangHup3`,`tblauftragcollies`.`TourNr2` AS `TourNr2`,`tblauftragcollies`.`dtAusgangDepot2` AS `dtAusgangDepot2`,`tblauftragcollies`.`dtEingangDepot2` AS `dtEingangDepot2`,`tblauftragcollies`.`mydepotid2` AS `mydepotid2`,`tblauftragcollies`.`BagIDNrC` AS `BagIDNrC`,`tblauftragcollies`.`BagBelegNrC` AS `BagBelegNrC`,`tblauftragcollies`.`BagBelegNrAbC` AS `BagBelegNrAbC` from (`tblauftrag` join `tblauftragcollies` on((`tblauftrag`.`OrderID` = `tblauftragcollies`.`OrderID`))) where ((`tblauftrag`.`Verladedatum` > (curdate() + interval -(10) day)) and (`tblauftragcollies`.`Frei2` <> _latin1'4')) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-07-25 16:26:56

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `LKZ` char(2) NOT NULL DEFAULT '',
  `LNAME` varchar(50) NOT NULL DEFAULT '',
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `RoutingTyp` int(10) unsigned NOT NULL DEFAULT '0',
  `MinLen` int(11) DEFAULT NULL,
 `MaxLen` int(11) DEFAULT NULL,
  `ZipFormat` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LKZ`),
  UNIQUE KEY `LNAME` (`LNAME`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `holidayctrl`
--

DROP TABLE IF EXISTS `holidayctrl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `holidayctrl` (
  `Holiday` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CtrlPos` int(11) NOT NULL DEFAULT '0',
  `Country` char(2) NOT NULL DEFAULT '',
  `Description` varchar(45) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Holiday`,`Country`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `route`
--

DROP TABLE IF EXISTS `route`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `route` (
  `product` varchar(5) NOT NULL,
  `lkz` varchar(5) NOT NULL,
  `zip` varchar(5) NOT NULL,
  `validfrom` datetime NOT NULL,
 `validto` datetime DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `station` int(11) DEFAULT NULL,
  `sector` varchar(5) DEFAULT NULL,
  `area` varchar(5) DEFAULT NULL,
  `etod` time DEFAULT NULL,
  `ltop` time DEFAULT NULL,
  `transittime` int(11) DEFAULT NULL,
  `ltodsa` time DEFAULT NULL,
  `ltodholiday` time DEFAULT NULL,
  `island` int(11) DEFAULT NULL,
  `etod2` time DEFAULT NULL,
  `ltop2` time DEFAULT NULL,
  `holidayctrl` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`product`,`lkz`,`zip`,`validfrom`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sector`
--

DROP TABLE IF EXISTS `sector`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sector` (
  `product` varchar(5) NOT NULL,
  `sectorfrom` varchar(5) NOT NULL,
  `sectorto` varchar(5) NOT NULL,
  `validfrom` datetime NOT NULL,
  `validto` datetime DEFAULT NULL,
  `via` varchar(45) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product`,`sectorfrom`,`sectorto`,`validfrom`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `station`
--

DROP TABLE IF EXISTS `station`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `station` (
  `StationID` int(11) NOT NULL DEFAULT '0',
  `Adress1` varchar(50) DEFAULT NULL,
  `Adress2` varchar(50) DEFAULT NULL,
  `Country` varchar(2) DEFAULT NULL,
  `ZIP` varchar(8) DEFAULT NULL,
  `City` varchar(50) DEFAULT NULL,
  `Street` varchar(50) DEFAULT NULL,
  `HouseNr` varchar(10) DEFAULT NULL,
  `Phone1` varchar(50) DEFAULT NULL,
  `Phone2` varchar(50) DEFAULT NULL,
  `Telefax` varchar(50) DEFAULT NULL,
  `Mobile` varchar(50) DEFAULT NULL,
  `ServicePhone1` varchar(50) DEFAULT NULL,
  `ServicePhone2` varchar(50) DEFAULT NULL,
  `ContactPerson1` varchar(50) DEFAULT NULL,
  `ContactPerson2` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `Webadress` varchar(255) DEFAULT NULL,
  `StationNr` int(11) DEFAULT NULL,
  `Strang` int(11) DEFAULT NULL,
  `poslong` double DEFAULT NULL,
  `poslat` double DEFAULT NULL,
  `Sector` varchar(5) DEFAULT NULL,
  `UStId` varchar(50) DEFAULT NULL,
  `BillingAdress1` varchar(50) DEFAULT NULL,
  `BillingAdress2` varchar(50) DEFAULT NULL,
  `BillingCountry` varchar(50) DEFAULT NULL,
  `BillingZip` varchar(8) DEFAULT NULL,
  `BillingCity` varchar(50) DEFAULT NULL,
  `BillingStreet` varchar(50) DEFAULT NULL,
  `BillingHouseNr` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`StationID`),
  UNIQUE KEY `DepotTree` (`StationID`),
  UNIQUE KEY `DepotMatchcode` (`StationID`),
  UNIQUE KEY `LKZ` (`Country`,`ZIP`,`City`,`StationID`),
  KEY `Strang` (`Strang`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


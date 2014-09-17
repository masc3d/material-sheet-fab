package org.leo2.rest.v1.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by masc on 16.09.14.
 */
@Entity
@javax.persistence.Table(name = "tbldepotliste", schema = "", catalog = "dekuclient")
public class Depot {
    private Integer depotNr;

    @Id
    @javax.persistence.Column(name = "DepotNr", nullable = false, insertable = true, updatable = true)
    public Integer getDepotNr() {
        return depotNr;
    }

    public void setDepotNr(Integer depotNr) {
        this.depotNr = depotNr;
    }

    private Integer depotLevel;

    @Basic
    @javax.persistence.Column(name = "DepotLevel", nullable = false, insertable = true, updatable = true)
    public Integer getDepotLevel() {
        return depotLevel;
    }

    public void setDepotLevel(Integer depotLevel) {
        this.depotLevel = depotLevel;
    }

    private Integer depotParent;

    @Basic
    @javax.persistence.Column(name = "DepotParent", nullable = false, insertable = true, updatable = true)
    public Integer getDepotParent() {
        return depotParent;
    }

    public void setDepotParent(Integer depotParent) {
        this.depotParent = depotParent;
    }

    private String depotMatchcode;

    @Basic
    @javax.persistence.Column(name = "DepotMatchcode", nullable = false, insertable = true, updatable = true, length = 50)
    public String getDepotMatchcode() {
        return depotMatchcode;
    }

    public void setDepotMatchcode(String depotMatchcode) {
        this.depotMatchcode = depotMatchcode;
    }

    private Short linienNr;

    @Basic
    @javax.persistence.Column(name = "LinienNr", nullable = false, insertable = true, updatable = true)
    public Short getLinienNr() {
        return linienNr;
    }

    public void setLinienNr(Short linienNr) {
        this.linienNr = linienNr;
    }

    private Timestamp linienankunft;

    @Basic
    @javax.persistence.Column(name = "Linienankunft", nullable = true, insertable = true, updatable = true)
    public Timestamp getLinienankunft() {
        return linienankunft;
    }

    public void setLinienankunft(Timestamp linienankunft) {
        this.linienankunft = linienankunft;
    }

    private Timestamp linienabfahrt;

    @Basic
    @javax.persistence.Column(name = "Linienabfahrt", nullable = true, insertable = true, updatable = true)
    public Timestamp getLinienabfahrt() {
        return linienabfahrt;
    }

    public void setLinienabfahrt(Timestamp linienabfahrt) {
        this.linienabfahrt = linienabfahrt;
    }

    private Timestamp aktivierungsdatum;

    @Basic
    @javax.persistence.Column(name = "Aktivierungsdatum", nullable = false, insertable = true, updatable = true)
    public Timestamp getAktivierungsdatum() {
        return aktivierungsdatum;
    }

    public void setAktivierungsdatum(Timestamp aktivierungsdatum) {
        this.aktivierungsdatum = aktivierungsdatum;
    }

    private Timestamp deaktivierungsdatum;

    @Basic
    @javax.persistence.Column(name = "Deaktivierungsdatum", nullable = false, insertable = true, updatable = true)
    public Timestamp getDeaktivierungsdatum() {
        return deaktivierungsdatum;
    }

    public void setDeaktivierungsdatum(Timestamp deaktivierungsdatum) {
        this.deaktivierungsdatum = deaktivierungsdatum;
    }

    private Short istGueltig;

    @Basic
    @javax.persistence.Column(name = "IstGueltig", nullable = false, insertable = true, updatable = true)
    public Short getIstGueltig() {
        return istGueltig;
    }

    public void setIstGueltig(Short istGueltig) {
        this.istGueltig = istGueltig;
    }

    private String firma1;

    @Basic
    @javax.persistence.Column(name = "Firma1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getFirma1() {
        return firma1;
    }

    public void setFirma1(String firma1) {
        this.firma1 = firma1;
    }

    private String firma2;

    @Basic
    @javax.persistence.Column(name = "Firma2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getFirma2() {
        return firma2;
    }

    public void setFirma2(String firma2) {
        this.firma2 = firma2;
    }

    private String lkz;

    @Basic
    @javax.persistence.Column(name = "LKZ", nullable = true, insertable = true, updatable = true, length = 2)
    public String getLkz() {
        return lkz;
    }

    public void setLkz(String lkz) {
        this.lkz = lkz;
    }

    private String plz;

    @Basic
    @javax.persistence.Column(name = "PLZ", nullable = true, insertable = true, updatable = true, length = 8)
    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    private String ort;

    @Basic
    @javax.persistence.Column(name = "Ort", nullable = true, insertable = true, updatable = true, length = 50)
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    private String strasse;

    @Basic
    @javax.persistence.Column(name = "Strasse", nullable = true, insertable = true, updatable = true, length = 50)
    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    private String strNr;

    @Basic
    @javax.persistence.Column(name = "StrNr", nullable = true, insertable = true, updatable = true, length = 10)
    public String getStrNr() {
        return strNr;
    }

    public void setStrNr(String strNr) {
        this.strNr = strNr;
    }

    private Short lvw;

    @Basic
    @javax.persistence.Column(name = "LVW", nullable = true, insertable = true, updatable = true)
    public Short getLvw() {
        return lvw;
    }

    public void setLvw(Short lvw) {
        this.lvw = lvw;
    }

    private Integer ovw;

    @Basic
    @javax.persistence.Column(name = "OVW", nullable = true, insertable = true, updatable = true)
    public Integer getOvw() {
        return ovw;
    }

    public void setOvw(Integer ovw) {
        this.ovw = ovw;
    }

    private String telefon1;

    @Basic
    @javax.persistence.Column(name = "Telefon1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getTelefon1() {
        return telefon1;
    }

    public void setTelefon1(String telefon1) {
        this.telefon1 = telefon1;
    }

    private String telefon2;

    @Basic
    @javax.persistence.Column(name = "Telefon2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getTelefon2() {
        return telefon2;
    }

    public void setTelefon2(String telefon2) {
        this.telefon2 = telefon2;
    }

    private String telefax;

    @Basic
    @javax.persistence.Column(name = "Telefax", nullable = true, insertable = true, updatable = true, length = 50)
    public String getTelefax() {
        return telefax;
    }

    public void setTelefax(String telefax) {
        this.telefax = telefax;
    }

    private String mobil;

    @Basic
    @javax.persistence.Column(name = "Mobil", nullable = true, insertable = true, updatable = true, length = 50)
    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
        this.mobil = mobil;
    }

    private String nottelefon1;

    @Basic
    @javax.persistence.Column(name = "Nottelefon1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getNottelefon1() {
        return nottelefon1;
    }

    public void setNottelefon1(String nottelefon1) {
        this.nottelefon1 = nottelefon1;
    }

    private String nottelefon2;

    @Basic
    @javax.persistence.Column(name = "Nottelefon2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getNottelefon2() {
        return nottelefon2;
    }

    public void setNottelefon2(String nottelefon2) {
        this.nottelefon2 = nottelefon2;
    }

    private String anprechpartner1;

    @Basic
    @javax.persistence.Column(name = "Anprechpartner1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAnprechpartner1() {
        return anprechpartner1;
    }

    public void setAnprechpartner1(String anprechpartner1) {
        this.anprechpartner1 = anprechpartner1;
    }

    private String anprechpartner2;

    @Basic
    @javax.persistence.Column(name = "Anprechpartner2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAnprechpartner2() {
        return anprechpartner2;
    }

    public void setAnprechpartner2(String anprechpartner2) {
        this.anprechpartner2 = anprechpartner2;
    }

    private String ustId;

    @Basic
    @javax.persistence.Column(name = "UstID", nullable = true, insertable = true, updatable = true, length = 50)
    public String getUstId() {
        return ustId;
    }

    public void setUstId(String ustId) {
        this.ustId = ustId;
    }

    private String ekStNr;

    @Basic
    @javax.persistence.Column(name = "EKStNr", nullable = true, insertable = true, updatable = true, length = 50)
    public String getEkStNr() {
        return ekStNr;
    }

    public void setEkStNr(String ekStNr) {
        this.ekStNr = ekStNr;
    }

    private String blz;

    @Basic
    @javax.persistence.Column(name = "BLZ", nullable = true, insertable = true, updatable = true, length = 8)
    public String getBlz() {
        return blz;
    }

    public void setBlz(String blz) {
        this.blz = blz;
    }

    private String ktoNr;

    @Basic
    @javax.persistence.Column(name = "KtoNr", nullable = true, insertable = true, updatable = true, length = 12)
    public String getKtoNr() {
        return ktoNr;
    }

    public void setKtoNr(String ktoNr) {
        this.ktoNr = ktoNr;
    }

    private String bank;

    @Basic
    @javax.persistence.Column(name = "Bank", nullable = true, insertable = true, updatable = true, length = 25)
    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    private String ktoInhaber;

    @Basic
    @javax.persistence.Column(name = "KtoInhaber", nullable = true, insertable = true, updatable = true, length = 27)
    public String getKtoInhaber() {
        return ktoInhaber;
    }

    public void setKtoInhaber(String ktoInhaber) {
        this.ktoInhaber = ktoInhaber;
    }

    private String rName1;

    @Basic
    @javax.persistence.Column(name = "RName1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getrName1() {
        return rName1;
    }

    public void setrName1(String rName1) {
        this.rName1 = rName1;
    }

    private String rName2;

    @Basic
    @javax.persistence.Column(name = "RName2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getrName2() {
        return rName2;
    }

    public void setrName2(String rName2) {
        this.rName2 = rName2;
    }

    private String rlkz;

    @Basic
    @javax.persistence.Column(name = "RLKZ", nullable = true, insertable = true, updatable = true, length = 2)
    public String getRlkz() {
        return rlkz;
    }

    public void setRlkz(String rlkz) {
        this.rlkz = rlkz;
    }

    private String rplz;

    @Basic
    @javax.persistence.Column(name = "RPLZ", nullable = true, insertable = true, updatable = true, length = 8)
    public String getRplz() {
        return rplz;
    }

    public void setRplz(String rplz) {
        this.rplz = rplz;
    }

    private String rOrt;

    @Basic
    @javax.persistence.Column(name = "ROrt", nullable = true, insertable = true, updatable = true, length = 50)
    public String getrOrt() {
        return rOrt;
    }

    public void setrOrt(String rOrt) {
        this.rOrt = rOrt;
    }

    private String rStrasse;

    @Basic
    @javax.persistence.Column(name = "RStrasse", nullable = true, insertable = true, updatable = true, length = 50)
    public String getrStrasse() {
        return rStrasse;
    }

    public void setrStrasse(String rStrasse) {
        this.rStrasse = rStrasse;
    }

    private String rStrNr;

    @Basic
    @javax.persistence.Column(name = "RStrNr", nullable = true, insertable = true, updatable = true, length = 10)
    public String getrStrNr() {
        return rStrNr;
    }

    public void setrStrNr(String rStrNr) {
        this.rStrNr = rStrNr;
    }

    private String info;

    @Basic
    @javax.persistence.Column(name = "Info", nullable = true, insertable = true, updatable = true, length = 255)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private String email;

    @Basic
    @javax.persistence.Column(name = "Email", nullable = true, insertable = true, updatable = true, length = 75)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private Byte samstag;

    @Basic
    @javax.persistence.Column(name = "Samstag", nullable = true, insertable = true, updatable = true)
    public Byte getSamstag() {
        return samstag;
    }

    public void setSamstag(Byte samstag) {
        this.samstag = samstag;
    }

    private Byte sonntag;

    @Basic
    @javax.persistence.Column(name = "Sonntag", nullable = true, insertable = true, updatable = true)
    public Byte getSonntag() {
        return sonntag;
    }

    public void setSonntag(Byte sonntag) {
        this.sonntag = sonntag;
    }

    private Byte einzug;

    @Basic
    @javax.persistence.Column(name = "Einzug", nullable = true, insertable = true, updatable = true)
    public Byte getEinzug() {
        return einzug;
    }

    public void setEinzug(Byte einzug) {
        this.einzug = einzug;
    }

    private Timestamp uhrzeitAnfang;

    @Basic
    @javax.persistence.Column(name = "UhrzeitAnfang", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitAnfang() {
        return uhrzeitAnfang;
    }

    public void setUhrzeitAnfang(Timestamp uhrzeitAnfang) {
        this.uhrzeitAnfang = uhrzeitAnfang;
    }

    private Timestamp uhrzeitEnde;

    @Basic
    @javax.persistence.Column(name = "UhrzeitEnde", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitEnde() {
        return uhrzeitEnde;
    }

    public void setUhrzeitEnde(Timestamp uhrzeitEnde) {
        this.uhrzeitEnde = uhrzeitEnde;
    }

    private Timestamp uhrzeitAnfangSa;

    @Basic
    @javax.persistence.Column(name = "UhrzeitAnfangSa", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitAnfangSa() {
        return uhrzeitAnfangSa;
    }

    public void setUhrzeitAnfangSa(Timestamp uhrzeitAnfangSa) {
        this.uhrzeitAnfangSa = uhrzeitAnfangSa;
    }

    private Timestamp uhrzeitEndeSa;

    @Basic
    @javax.persistence.Column(name = "UhrzeitEndeSa", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitEndeSa() {
        return uhrzeitEndeSa;
    }

    public void setUhrzeitEndeSa(Timestamp uhrzeitEndeSa) {
        this.uhrzeitEndeSa = uhrzeitEndeSa;
    }

    private Timestamp uhrzeitAnfangSo;

    @Basic
    @javax.persistence.Column(name = "UhrzeitAnfangSo", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitAnfangSo() {
        return uhrzeitAnfangSo;
    }

    public void setUhrzeitAnfangSo(Timestamp uhrzeitAnfangSo) {
        this.uhrzeitAnfangSo = uhrzeitAnfangSo;
    }

    private Timestamp uhrzeitEndeSo;

    @Basic
    @javax.persistence.Column(name = "UhrzeitEndeSo", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitEndeSo() {
        return uhrzeitEndeSo;
    }

    public void setUhrzeitEndeSo(Timestamp uhrzeitEndeSo) {
        this.uhrzeitEndeSo = uhrzeitEndeSo;
    }

    private Timestamp timestamp;

    @Basic
    @javax.persistence.Column(name = "Timestamp", nullable = false, insertable = true, updatable = true)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    private String serverNameSmtp;

    @Basic
    @javax.persistence.Column(name = "ServerNameSMTP", nullable = true, insertable = true, updatable = true, length = 45)
    public String getServerNameSmtp() {
        return serverNameSmtp;
    }

    public void setServerNameSmtp(String serverNameSmtp) {
        this.serverNameSmtp = serverNameSmtp;
    }

    private String serverNamePop3;

    @Basic
    @javax.persistence.Column(name = "ServerNamePOP3", nullable = true, insertable = true, updatable = true, length = 45)
    public String getServerNamePop3() {
        return serverNamePop3;
    }

    public void setServerNamePop3(String serverNamePop3) {
        this.serverNamePop3 = serverNamePop3;
    }

    private String userName;

    @Basic
    @javax.persistence.Column(name = "UserName", nullable = true, insertable = true, updatable = true, length = 45)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String password;

    @Basic
    @javax.persistence.Column(name = "Password", nullable = true, insertable = true, updatable = true, length = 45)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String intRoutingLkz;

    @Basic
    @javax.persistence.Column(name = "IntRoutingLKZ", nullable = true, insertable = true, updatable = true, length = 45)
    public String getIntRoutingLkz() {
        return intRoutingLkz;
    }

    public void setIntRoutingLkz(String intRoutingLkz) {
        this.intRoutingLkz = intRoutingLkz;
    }

    private Integer mwStShl;

    @Basic
    @javax.persistence.Column(name = "MwStShl", nullable = true, insertable = true, updatable = true)
    public Integer getMwStShl() {
        return mwStShl;
    }

    public void setMwStShl(Integer mwStShl) {
        this.mwStShl = mwStShl;
    }

    private Integer mwStPflicht;

    @Basic
    @javax.persistence.Column(name = "MwStPflicht", nullable = true, insertable = true, updatable = true)
    public Integer getMwStPflicht() {
        return mwStPflicht;
    }

    public void setMwStPflicht(Integer mwStPflicht) {
        this.mwStPflicht = mwStPflicht;
    }

    private String f4F;

    @Basic
    @javax.persistence.Column(name = "F4F", nullable = true, insertable = true, updatable = true, length = 100)
    public String getF4F() {
        return f4F;
    }

    public void setF4F(String f4F) {
        this.f4F = f4F;
    }

    private String exportEmail;

    @Basic
    @javax.persistence.Column(name = "ExportEmail", nullable = true, insertable = true, updatable = true, length = 245)
    public String getExportEmail() {
        return exportEmail;
    }

    public void setExportEmail(String exportEmail) {
        this.exportEmail = exportEmail;
    }

    private String exportFtpServer;

    @Basic
    @javax.persistence.Column(name = "ExportFTPServer", nullable = true, insertable = true, updatable = true, length = 45)
    public String getExportFtpServer() {
        return exportFtpServer;
    }

    public void setExportFtpServer(String exportFtpServer) {
        this.exportFtpServer = exportFtpServer;
    }

    private String exportFtpUser;

    @Basic
    @javax.persistence.Column(name = "ExportFTPUser", nullable = true, insertable = true, updatable = true, length = 45)
    public String getExportFtpUser() {
        return exportFtpUser;
    }

    public void setExportFtpUser(String exportFtpUser) {
        this.exportFtpUser = exportFtpUser;
    }

    private String exportFtpPwd;

    @Basic
    @javax.persistence.Column(name = "ExportFTPPwd", nullable = true, insertable = true, updatable = true, length = 45)
    public String getExportFtpPwd() {
        return exportFtpPwd;
    }

    public void setExportFtpPwd(String exportFtpPwd) {
        this.exportFtpPwd = exportFtpPwd;
    }

    private Integer exportToGlo;

    @Basic
    @javax.persistence.Column(name = "ExportToGLO", nullable = true, insertable = true, updatable = true)
    public Integer getExportToGlo() {
        return exportToGlo;
    }

    public void setExportToGlo(Integer exportToGlo) {
        this.exportToGlo = exportToGlo;
    }

    private Integer exportToXml;

    @Basic
    @javax.persistence.Column(name = "ExportToXML", nullable = true, insertable = true, updatable = true)
    public Integer getExportToXml() {
        return exportToXml;
    }

    public void setExportToXml(Integer exportToXml) {
        this.exportToXml = exportToXml;
    }

    private Integer kondition;

    @Basic
    @javax.persistence.Column(name = "Kondition", nullable = true, insertable = true, updatable = true)
    public Integer getKondition() {
        return kondition;
    }

    public void setKondition(Integer kondition) {
        this.kondition = kondition;
    }

    private String qualiMail;

    @Basic
    @javax.persistence.Column(name = "QualiMail", nullable = true, insertable = true, updatable = true, length = 255)
    public String getQualiMail() {
        return qualiMail;
    }

    public void setQualiMail(String qualiMail) {
        this.qualiMail = qualiMail;
    }

    private Integer ebSdgDepot;

    @Basic
    @javax.persistence.Column(name = "EBSdgDepot", nullable = true, insertable = true, updatable = true)
    public Integer getEbSdgDepot() {
        return ebSdgDepot;
    }

    public void setEbSdgDepot(Integer ebSdgDepot) {
        this.ebSdgDepot = ebSdgDepot;
    }

    private Integer ebDepotAd;

    @Basic
    @javax.persistence.Column(name = "EBDepotAD", nullable = true, insertable = true, updatable = true)
    public Integer getEbDepotAd() {
        return ebDepotAd;
    }

    public void setEbDepotAd(Integer ebDepotAd) {
        this.ebDepotAd = ebDepotAd;
    }

    private Integer ebGen;

    @Basic
    @javax.persistence.Column(name = "EBGen", nullable = true, insertable = true, updatable = true)
    public Integer getEbGen() {
        return ebGen;
    }

    public void setEbGen(Integer ebGen) {
        this.ebGen = ebGen;
    }

    private Integer ebUmvDepot;

    @Basic
    @javax.persistence.Column(name = "EBUmvDepot", nullable = true, insertable = true, updatable = true)
    public Integer getEbUmvDepot() {
        return ebUmvDepot;
    }

    public void setEbUmvDepot(Integer ebUmvDepot) {
        this.ebUmvDepot = ebUmvDepot;
    }

    private String umRoutung;

    @Basic
    @javax.persistence.Column(name = "UmRoutung", nullable = true, insertable = true, updatable = true, length = 250)
    public String getUmRoutung() {
        return umRoutung;
    }

    public void setUmRoutung(String umRoutung) {
        this.umRoutung = umRoutung;
    }

    private Double kontokorrentnr;

    @Basic
    @javax.persistence.Column(name = "Kontokorrentnr", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getKontokorrentnr() {
        return kontokorrentnr;
    }

    public void setKontokorrentnr(Double kontokorrentnr) {
        this.kontokorrentnr = kontokorrentnr;
    }

    private Integer zahlungsbedingungen;

    @Basic
    @javax.persistence.Column(name = "Zahlungsbedingungen", nullable = true, insertable = true, updatable = true)
    public Integer getZahlungsbedingungen() {
        return zahlungsbedingungen;
    }

    public void setZahlungsbedingungen(Integer zahlungsbedingungen) {
        this.zahlungsbedingungen = zahlungsbedingungen;
    }

    private Integer verbundenesU;

    @Basic
    @javax.persistence.Column(name = "VerbundenesU", nullable = true, insertable = true, updatable = true)
    public Integer getVerbundenesU() {
        return verbundenesU;
    }

    public void setVerbundenesU(Integer verbundenesU) {
        this.verbundenesU = verbundenesU;
    }

    private Double debitorNr;

    @Basic
    @javax.persistence.Column(name = "DebitorNr", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getDebitorNr() {
        return debitorNr;
    }

    public void setDebitorNr(Double debitorNr) {
        this.debitorNr = debitorNr;
    }

    private Double kreditorNr;

    @Basic
    @javax.persistence.Column(name = "KreditorNr", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getKreditorNr() {
        return kreditorNr;
    }

    public void setKreditorNr(Double kreditorNr) {
        this.kreditorNr = kreditorNr;
    }

    private String rgGutschrift;

    @Basic
    @javax.persistence.Column(name = "RgGutschrift", nullable = true, insertable = true, updatable = true, length = 1)
    public String getRgGutschrift() {
        return rgGutschrift;
    }

    public void setRgGutschrift(String rgGutschrift) {
        this.rgGutschrift = rgGutschrift;
    }

    private String rgRechnung;

    @Basic
    @javax.persistence.Column(name = "RgRechnung", nullable = true, insertable = true, updatable = true, length = 1)
    public String getRgRechnung() {
        return rgRechnung;
    }

    public void setRgRechnung(String rgRechnung) {
        this.rgRechnung = rgRechnung;
    }

    private Integer xmlStammdaten;

    @Basic
    @javax.persistence.Column(name = "XMLStammdaten", nullable = true, insertable = true, updatable = true)
    public Integer getXmlStammdaten() {
        return xmlStammdaten;
    }

    public void setXmlStammdaten(Integer xmlStammdaten) {
        this.xmlStammdaten = xmlStammdaten;
    }

    private String region;

    @Basic
    @javax.persistence.Column(name = "Region", nullable = true, insertable = true, updatable = true, length = 10)
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    private String firmenverbund;

    @Basic
    @javax.persistence.Column(name = "Firmenverbund", nullable = true, insertable = true, updatable = true, length = 100)
    public String getFirmenverbund() {
        return firmenverbund;
    }

    public void setFirmenverbund(String firmenverbund) {
        this.firmenverbund = firmenverbund;
    }

    private Integer qualitaet;

    @Basic
    @javax.persistence.Column(name = "Qualitaet", nullable = true, insertable = true, updatable = true)
    public Integer getQualitaet() {
        return qualitaet;
    }

    public void setQualitaet(Integer qualitaet) {
        this.qualitaet = qualitaet;
    }

    private Integer sonntagsLinientyp;

    @Basic
    @javax.persistence.Column(name = "SonntagsLinientyp", nullable = true, insertable = true, updatable = true)
    public Integer getSonntagsLinientyp() {
        return sonntagsLinientyp;
    }

    public void setSonntagsLinientyp(Integer sonntagsLinientyp) {
        this.sonntagsLinientyp = sonntagsLinientyp;
    }

    private Integer comCode;

    @Basic
    @javax.persistence.Column(name = "Com_code", nullable = true, insertable = true, updatable = true)
    public Integer getComCode() {
        return comCode;
    }

    public void setComCode(Integer comCode) {
        this.comCode = comCode;
    }

    private String eMailPas;

    @Basic
    @javax.persistence.Column(name = "eMail_pas", nullable = true, insertable = true, updatable = true, length = 100)
    public String geteMailPas() {
        return eMailPas;
    }

    public void seteMailPas(String eMailPas) {
        this.eMailPas = eMailPas;
    }

    private Integer xmlAcn;

    @Basic
    @javax.persistence.Column(name = "XML_ACN", nullable = true, insertable = true, updatable = true)
    public Integer getXmlAcn() {
        return xmlAcn;
    }

    public void setXmlAcn(Integer xmlAcn) {
        this.xmlAcn = xmlAcn;
    }

    private String webemail;

    @Basic
    @javax.persistence.Column(name = "webemail", nullable = true, insertable = true, updatable = true, length = 255)
    public String getWebemail() {
        return webemail;
    }

    public void setWebemail(String webemail) {
        this.webemail = webemail;
    }

    private String webadresse;

    @Basic
    @javax.persistence.Column(name = "webadresse", nullable = true, insertable = true, updatable = true, length = 255)
    public String getWebadresse() {
        return webadresse;
    }

    public void setWebadresse(String webadresse) {
        this.webadresse = webadresse;
    }

    private Integer paXml;

    @Basic
    @javax.persistence.Column(name = "PA_XML", nullable = true, insertable = true, updatable = true)
    public Integer getPaXml() {
        return paXml;
    }

    public void setPaXml(Integer paXml) {
        this.paXml = paXml;
    }

    private Integer paPdf;

    @Basic
    @javax.persistence.Column(name = "PA_PDF", nullable = true, insertable = true, updatable = true)
    public Integer getPaPdf() {
        return paPdf;
    }

    public void setPaPdf(Integer paPdf) {
        this.paPdf = paPdf;
    }

    private Integer zahlungsbedingungenR;

    @Basic
    @javax.persistence.Column(name = "ZahlungsbedingungenR", nullable = true, insertable = true, updatable = true)
    public Integer getZahlungsbedingungenR() {
        return zahlungsbedingungenR;
    }

    public void setZahlungsbedingungenR(Integer zahlungsbedingungenR) {
        this.zahlungsbedingungenR = zahlungsbedingungenR;
    }

    private Integer easyOk;

    @Basic
    @javax.persistence.Column(name = "EASYOk", nullable = true, insertable = true, updatable = true)
    public Integer getEasyOk() {
        return easyOk;
    }

    public void setEasyOk(Integer easyOk) {
        this.easyOk = easyOk;
    }

    private Double vofiPrz;

    @Basic
    @javax.persistence.Column(name = "VofiPrz", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getVofiPrz() {
        return vofiPrz;
    }

    public void setVofiPrz(Double vofiPrz) {
        this.vofiPrz = vofiPrz;
    }

    private String feLang;

    @Basic
    @javax.persistence.Column(name = "FELang", nullable = true, insertable = true, updatable = true, length = 5)
    public String getFeLang() {
        return feLang;
    }

    public void setFeLang(String feLang) {
        this.feLang = feLang;
    }

    private Integer mentorDepotNr;

    @Basic
    @javax.persistence.Column(name = "MentorDepotNr", nullable = true, insertable = true, updatable = true)
    public Integer getMentorDepotNr() {
        return mentorDepotNr;
    }

    public void setMentorDepotNr(Integer mentorDepotNr) {
        this.mentorDepotNr = mentorDepotNr;
    }

    private Double trzProz;

    @Basic
    @javax.persistence.Column(name = "TRZProz", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getTrzProz() {
        return trzProz;
    }

    public void setTrzProz(Double trzProz) {
        this.trzProz = trzProz;
    }

    private Integer nnOk;

    @Basic
    @javax.persistence.Column(name = "NNOk", nullable = true, insertable = true, updatable = true)
    public Integer getNnOk() {
        return nnOk;
    }

    public void setNnOk(Integer nnOk) {
        this.nnOk = nnOk;
    }

    private String cod1;

    @Basic
    @javax.persistence.Column(name = "COD1", nullable = true, insertable = true, updatable = true, length = 5)
    public String getCod1() {
        return cod1;
    }

    public void setCod1(String cod1) {
        this.cod1 = cod1;
    }

    private Integer xlsAuftragOk;

    @Basic
    @javax.persistence.Column(name = "XLSAuftragOK", nullable = true, insertable = true, updatable = true)
    public Integer getXlsAuftragOk() {
        return xlsAuftragOk;
    }

    public void setXlsAuftragOk(Integer xlsAuftragOk) {
        this.xlsAuftragOk = xlsAuftragOk;
    }

    private Integer id;

    @Basic
    @javax.persistence.Column(name = "ID", nullable = true, insertable = true, updatable = true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String hanReg;

    @Basic
    @javax.persistence.Column(name = "HanReg", nullable = true, insertable = true, updatable = true, length = 45)
    public String getHanReg() {
        return hanReg;
    }

    public void setHanReg(String hanReg) {
        this.hanReg = hanReg;
    }

    private Integer coloader;

    @Basic
    @javax.persistence.Column(name = "Coloader", nullable = true, insertable = true, updatable = true)
    public Integer getColoader() {
        return coloader;
    }

    public void setColoader(Integer coloader) {
        this.coloader = coloader;
    }

    private Integer abrechDepot;

    @Basic
    @javax.persistence.Column(name = "AbrechDepot", nullable = true, insertable = true, updatable = true)
    public Integer getAbrechDepot() {
        return abrechDepot;
    }

    public void setAbrechDepot(Integer abrechDepot) {
        this.abrechDepot = abrechDepot;
    }

    private String ladehilfeWas;

    @Basic
    @javax.persistence.Column(name = "LadehilfeWas", nullable = true, insertable = true, updatable = true, length = 100)
    public String getLadehilfeWas() {
        return ladehilfeWas;
    }

    public void setLadehilfeWas(String ladehilfeWas) {
        this.ladehilfeWas = ladehilfeWas;
    }

    private Integer ladehilfeKg;

    @Basic
    @javax.persistence.Column(name = "LadehilfeKg", nullable = true, insertable = true, updatable = true)
    public Integer getLadehilfeKg() {
        return ladehilfeKg;
    }

    public void setLadehilfeKg(Integer ladehilfeKg) {
        this.ladehilfeKg = ladehilfeKg;
    }

    private String ladehilfeAb;

    @Basic
    @javax.persistence.Column(name = "LadehilfeAb", nullable = true, insertable = true, updatable = true, length = 5)
    public String getLadehilfeAb() {
        return ladehilfeAb;
    }

    public void setLadehilfeAb(String ladehilfeAb) {
        this.ladehilfeAb = ladehilfeAb;
    }

    private Integer ladehilfeLinie;

    @Basic
    @javax.persistence.Column(name = "LadehilfeLinie", nullable = true, insertable = true, updatable = true)
    public Integer getLadehilfeLinie() {
        return ladehilfeLinie;
    }

    public void setLadehilfeLinie(Integer ladehilfeLinie) {
        this.ladehilfeLinie = ladehilfeLinie;
    }

    private Integer paDruck;

    @Basic
    @javax.persistence.Column(name = "PA_Druck", nullable = true, insertable = true, updatable = true)
    public Integer getPaDruck() {
        return paDruck;
    }

    public void setPaDruck(Integer paDruck) {
        this.paDruck = paDruck;
    }

    private String rup;

    @Basic
    @javax.persistence.Column(name = "RUP", nullable = true, insertable = true, updatable = true, length = 15)
    public String getRup() {
        return rup;
    }

    public void setRup(String rup) {
        this.rup = rup;
    }

    private Integer masterVertrag;

    @Basic
    @javax.persistence.Column(name = "MasterVertrag", nullable = true, insertable = true, updatable = true)
    public Integer getMasterVertrag() {
        return masterVertrag;
    }

    public void setMasterVertrag(Integer masterVertrag) {
        this.masterVertrag = masterVertrag;
    }

    private Integer strang;

    @Basic
    @javax.persistence.Column(name = "Strang", nullable = true, insertable = true, updatable = true)
    public Integer getStrang() {
        return strang;
    }

    public void setStrang(Integer strang) {
        this.strang = strang;
    }

    private Integer masterDepot;

    @Basic
    @javax.persistence.Column(name = "MasterDepot", nullable = true, insertable = true, updatable = true)
    public Integer getMasterDepot() {
        return masterDepot;
    }

    public void setMasterDepot(Integer masterDepot) {
        this.masterDepot = masterDepot;
    }

    private Timestamp webshopInit;

    @Basic
    @javax.persistence.Column(name = "WebshopInit", nullable = true, insertable = true, updatable = true)
    public Timestamp getWebshopInit() {
        return webshopInit;
    }

    public void setWebshopInit(Timestamp webshopInit) {
        this.webshopInit = webshopInit;
    }

    private Double multiBag;

    @Basic
    @javax.persistence.Column(name = "MultiBag", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMultiBag() {
        return multiBag;
    }

    public void setMultiBag(Double multiBag) {
        this.multiBag = multiBag;
    }

    private Integer bagKontingent;

    @Basic
    @javax.persistence.Column(name = "BagKontingent", nullable = true, insertable = true, updatable = true)
    public Integer getBagKontingent() {
        return bagKontingent;
    }

    public void setBagKontingent(Integer bagKontingent) {
        this.bagKontingent = bagKontingent;
    }

    private String bagBemerkung;

    @Basic
    @javax.persistence.Column(name = "BagBemerkung", nullable = true, insertable = true, updatable = true, length = 15)
    public String getBagBemerkung() {
        return bagBemerkung;
    }

    public void setBagBemerkung(String bagBemerkung) {
        this.bagBemerkung = bagBemerkung;
    }

    private Integer konditionAbD;

    @Basic
    @javax.persistence.Column(name = "KonditionAbD", nullable = true, insertable = true, updatable = true)
    public Integer getKonditionAbD() {
        return konditionAbD;
    }

    public void setKonditionAbD(Integer konditionAbD) {
        this.konditionAbD = konditionAbD;
    }

    private Integer konditionLd;

    @Basic
    @javax.persistence.Column(name = "KonditionLD", nullable = true, insertable = true, updatable = true)
    public Integer getKonditionLd() {
        return konditionLd;
    }

    public void setKonditionLd(Integer konditionLd) {
        this.konditionLd = konditionLd;
    }

    private Integer bagCo;

    @Basic
    @javax.persistence.Column(name = "BagCo", nullable = true, insertable = true, updatable = true)
    public Integer getBagCo() {
        return bagCo;
    }

    public void setBagCo(Integer bagCo) {
        this.bagCo = bagCo;
    }

    private Timestamp strangDatum;

    @Basic
    @javax.persistence.Column(name = "StrangDatum", nullable = true, insertable = true, updatable = true)
    public Timestamp getStrangDatum() {
        return strangDatum;
    }

    public void setStrangDatum(Timestamp strangDatum) {
        this.strangDatum = strangDatum;
    }

    private Double strangZ;

    @Basic
    @javax.persistence.Column(name = "StrangZ", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getStrangZ() {
        return strangZ;
    }

    public void setStrangZ(Double strangZ) {
        this.strangZ = strangZ;
    }

    private Double strangOrder;

    @Basic
    @javax.persistence.Column(name = "StrangOrder", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getStrangOrder() {
        return strangOrder;
    }

    public void setStrangOrder(Double strangOrder) {
        this.strangOrder = strangOrder;
    }

    private String smspwd;

    @Basic
    @javax.persistence.Column(name = "smspwd", nullable = true, insertable = true, updatable = true, length = 20)
    public String getSmspwd() {
        return smspwd;
    }

    public void setSmspwd(String smspwd) {
        this.smspwd = smspwd;
    }

    private Integer valOk;

    @Basic
    @javax.persistence.Column(name = "ValOk", nullable = true, insertable = true, updatable = true)
    public Integer getValOk() {
        return valOk;
    }

    public void setValOk(Integer valOk) {
        this.valOk = valOk;
    }

    private Double maxValwert;

    @Basic
    @javax.persistence.Column(name = "maxValwert", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMaxValwert() {
        return maxValwert;
    }

    public void setMaxValwert(Double maxValwert) {
        this.maxValwert = maxValwert;
    }

    private Double maxHoeherhaftung;

    @Basic
    @javax.persistence.Column(name = "maxHoeherhaftung", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMaxHoeherhaftung() {
        return maxHoeherhaftung;
    }

    public void setMaxHoeherhaftung(Double maxHoeherhaftung) {
        this.maxHoeherhaftung = maxHoeherhaftung;
    }

    private Double maxWarenwert;

    @Basic
    @javax.persistence.Column(name = "maxWarenwert", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMaxWarenwert() {
        return maxWarenwert;
    }

    public void setMaxWarenwert(Double maxWarenwert) {
        this.maxWarenwert = maxWarenwert;
    }

    private String sapCostCenter;

    @Basic
    @javax.persistence.Column(name = "SAPCostCenter", nullable = true, insertable = true, updatable = true, length = 10)
    public String getSapCostCenter() {
        return sapCostCenter;
    }

    public void setSapCostCenter(String sapCostCenter) {
        this.sapCostCenter = sapCostCenter;
    }

    private Integer adHocKondiDepot;

    @Basic
    @javax.persistence.Column(name = "AdHocKondiDepot", nullable = true, insertable = true, updatable = true)
    public Integer getAdHocKondiDepot() {
        return adHocKondiDepot;
    }

    public void setAdHocKondiDepot(Integer adHocKondiDepot) {
        this.adHocKondiDepot = adHocKondiDepot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Depot that = (Depot) o;

        if (abrechDepot != null ? !abrechDepot.equals(that.abrechDepot) : that.abrechDepot != null) return false;
        if (adHocKondiDepot != null ? !adHocKondiDepot.equals(that.adHocKondiDepot) : that.adHocKondiDepot != null)
            return false;
        if (aktivierungsdatum != null ? !aktivierungsdatum.equals(that.aktivierungsdatum) : that.aktivierungsdatum != null)
            return false;
        if (anprechpartner1 != null ? !anprechpartner1.equals(that.anprechpartner1) : that.anprechpartner1 != null)
            return false;
        if (anprechpartner2 != null ? !anprechpartner2.equals(that.anprechpartner2) : that.anprechpartner2 != null)
            return false;
        if (bagBemerkung != null ? !bagBemerkung.equals(that.bagBemerkung) : that.bagBemerkung != null) return false;
        if (bagCo != null ? !bagCo.equals(that.bagCo) : that.bagCo != null) return false;
        if (bagKontingent != null ? !bagKontingent.equals(that.bagKontingent) : that.bagKontingent != null)
            return false;
        if (bank != null ? !bank.equals(that.bank) : that.bank != null) return false;
        if (blz != null ? !blz.equals(that.blz) : that.blz != null) return false;
        if (cod1 != null ? !cod1.equals(that.cod1) : that.cod1 != null) return false;
        if (coloader != null ? !coloader.equals(that.coloader) : that.coloader != null) return false;
        if (comCode != null ? !comCode.equals(that.comCode) : that.comCode != null) return false;
        if (deaktivierungsdatum != null ? !deaktivierungsdatum.equals(that.deaktivierungsdatum) : that.deaktivierungsdatum != null)
            return false;
        if (debitorNr != null ? !debitorNr.equals(that.debitorNr) : that.debitorNr != null) return false;
        if (depotLevel != null ? !depotLevel.equals(that.depotLevel) : that.depotLevel != null) return false;
        if (depotMatchcode != null ? !depotMatchcode.equals(that.depotMatchcode) : that.depotMatchcode != null)
            return false;
        if (depotNr != null ? !depotNr.equals(that.depotNr) : that.depotNr != null) return false;
        if (depotParent != null ? !depotParent.equals(that.depotParent) : that.depotParent != null) return false;
        if (eMailPas != null ? !eMailPas.equals(that.eMailPas) : that.eMailPas != null) return false;
        if (easyOk != null ? !easyOk.equals(that.easyOk) : that.easyOk != null) return false;
        if (ebDepotAd != null ? !ebDepotAd.equals(that.ebDepotAd) : that.ebDepotAd != null) return false;
        if (ebGen != null ? !ebGen.equals(that.ebGen) : that.ebGen != null) return false;
        if (ebSdgDepot != null ? !ebSdgDepot.equals(that.ebSdgDepot) : that.ebSdgDepot != null) return false;
        if (ebUmvDepot != null ? !ebUmvDepot.equals(that.ebUmvDepot) : that.ebUmvDepot != null) return false;
        if (einzug != null ? !einzug.equals(that.einzug) : that.einzug != null) return false;
        if (ekStNr != null ? !ekStNr.equals(that.ekStNr) : that.ekStNr != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (exportEmail != null ? !exportEmail.equals(that.exportEmail) : that.exportEmail != null) return false;
        if (exportFtpPwd != null ? !exportFtpPwd.equals(that.exportFtpPwd) : that.exportFtpPwd != null) return false;
        if (exportFtpServer != null ? !exportFtpServer.equals(that.exportFtpServer) : that.exportFtpServer != null)
            return false;
        if (exportFtpUser != null ? !exportFtpUser.equals(that.exportFtpUser) : that.exportFtpUser != null)
            return false;
        if (exportToGlo != null ? !exportToGlo.equals(that.exportToGlo) : that.exportToGlo != null) return false;
        if (exportToXml != null ? !exportToXml.equals(that.exportToXml) : that.exportToXml != null) return false;
        if (f4F != null ? !f4F.equals(that.f4F) : that.f4F != null) return false;
        if (feLang != null ? !feLang.equals(that.feLang) : that.feLang != null) return false;
        if (firma1 != null ? !firma1.equals(that.firma1) : that.firma1 != null) return false;
        if (firma2 != null ? !firma2.equals(that.firma2) : that.firma2 != null) return false;
        if (firmenverbund != null ? !firmenverbund.equals(that.firmenverbund) : that.firmenverbund != null)
            return false;
        if (hanReg != null ? !hanReg.equals(that.hanReg) : that.hanReg != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (info != null ? !info.equals(that.info) : that.info != null) return false;
        if (intRoutingLkz != null ? !intRoutingLkz.equals(that.intRoutingLkz) : that.intRoutingLkz != null)
            return false;
        if (istGueltig != null ? !istGueltig.equals(that.istGueltig) : that.istGueltig != null) return false;
        if (kondition != null ? !kondition.equals(that.kondition) : that.kondition != null) return false;
        if (konditionAbD != null ? !konditionAbD.equals(that.konditionAbD) : that.konditionAbD != null) return false;
        if (konditionLd != null ? !konditionLd.equals(that.konditionLd) : that.konditionLd != null) return false;
        if (kontokorrentnr != null ? !kontokorrentnr.equals(that.kontokorrentnr) : that.kontokorrentnr != null)
            return false;
        if (kreditorNr != null ? !kreditorNr.equals(that.kreditorNr) : that.kreditorNr != null) return false;
        if (ktoInhaber != null ? !ktoInhaber.equals(that.ktoInhaber) : that.ktoInhaber != null) return false;
        if (ktoNr != null ? !ktoNr.equals(that.ktoNr) : that.ktoNr != null) return false;
        if (ladehilfeAb != null ? !ladehilfeAb.equals(that.ladehilfeAb) : that.ladehilfeAb != null) return false;
        if (ladehilfeKg != null ? !ladehilfeKg.equals(that.ladehilfeKg) : that.ladehilfeKg != null) return false;
        if (ladehilfeLinie != null ? !ladehilfeLinie.equals(that.ladehilfeLinie) : that.ladehilfeLinie != null)
            return false;
        if (ladehilfeWas != null ? !ladehilfeWas.equals(that.ladehilfeWas) : that.ladehilfeWas != null) return false;
        if (linienNr != null ? !linienNr.equals(that.linienNr) : that.linienNr != null) return false;
        if (linienabfahrt != null ? !linienabfahrt.equals(that.linienabfahrt) : that.linienabfahrt != null)
            return false;
        if (linienankunft != null ? !linienankunft.equals(that.linienankunft) : that.linienankunft != null)
            return false;
        if (lkz != null ? !lkz.equals(that.lkz) : that.lkz != null) return false;
        if (lvw != null ? !lvw.equals(that.lvw) : that.lvw != null) return false;
        if (masterDepot != null ? !masterDepot.equals(that.masterDepot) : that.masterDepot != null) return false;
        if (masterVertrag != null ? !masterVertrag.equals(that.masterVertrag) : that.masterVertrag != null)
            return false;
        if (maxHoeherhaftung != null ? !maxHoeherhaftung.equals(that.maxHoeherhaftung) : that.maxHoeherhaftung != null)
            return false;
        if (maxValwert != null ? !maxValwert.equals(that.maxValwert) : that.maxValwert != null) return false;
        if (maxWarenwert != null ? !maxWarenwert.equals(that.maxWarenwert) : that.maxWarenwert != null) return false;
        if (mentorDepotNr != null ? !mentorDepotNr.equals(that.mentorDepotNr) : that.mentorDepotNr != null)
            return false;
        if (mobil != null ? !mobil.equals(that.mobil) : that.mobil != null) return false;
        if (multiBag != null ? !multiBag.equals(that.multiBag) : that.multiBag != null) return false;
        if (mwStPflicht != null ? !mwStPflicht.equals(that.mwStPflicht) : that.mwStPflicht != null) return false;
        if (mwStShl != null ? !mwStShl.equals(that.mwStShl) : that.mwStShl != null) return false;
        if (nnOk != null ? !nnOk.equals(that.nnOk) : that.nnOk != null) return false;
        if (nottelefon1 != null ? !nottelefon1.equals(that.nottelefon1) : that.nottelefon1 != null) return false;
        if (nottelefon2 != null ? !nottelefon2.equals(that.nottelefon2) : that.nottelefon2 != null) return false;
        if (ort != null ? !ort.equals(that.ort) : that.ort != null) return false;
        if (ovw != null ? !ovw.equals(that.ovw) : that.ovw != null) return false;
        if (paDruck != null ? !paDruck.equals(that.paDruck) : that.paDruck != null) return false;
        if (paPdf != null ? !paPdf.equals(that.paPdf) : that.paPdf != null) return false;
        if (paXml != null ? !paXml.equals(that.paXml) : that.paXml != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (plz != null ? !plz.equals(that.plz) : that.plz != null) return false;
        if (qualiMail != null ? !qualiMail.equals(that.qualiMail) : that.qualiMail != null) return false;
        if (qualitaet != null ? !qualitaet.equals(that.qualitaet) : that.qualitaet != null) return false;
        if (rName1 != null ? !rName1.equals(that.rName1) : that.rName1 != null) return false;
        if (rName2 != null ? !rName2.equals(that.rName2) : that.rName2 != null) return false;
        if (rOrt != null ? !rOrt.equals(that.rOrt) : that.rOrt != null) return false;
        if (rStrNr != null ? !rStrNr.equals(that.rStrNr) : that.rStrNr != null) return false;
        if (rStrasse != null ? !rStrasse.equals(that.rStrasse) : that.rStrasse != null) return false;
        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        if (rgGutschrift != null ? !rgGutschrift.equals(that.rgGutschrift) : that.rgGutschrift != null) return false;
        if (rgRechnung != null ? !rgRechnung.equals(that.rgRechnung) : that.rgRechnung != null) return false;
        if (rlkz != null ? !rlkz.equals(that.rlkz) : that.rlkz != null) return false;
        if (rplz != null ? !rplz.equals(that.rplz) : that.rplz != null) return false;
        if (rup != null ? !rup.equals(that.rup) : that.rup != null) return false;
        if (samstag != null ? !samstag.equals(that.samstag) : that.samstag != null) return false;
        if (sapCostCenter != null ? !sapCostCenter.equals(that.sapCostCenter) : that.sapCostCenter != null)
            return false;
        if (serverNamePop3 != null ? !serverNamePop3.equals(that.serverNamePop3) : that.serverNamePop3 != null)
            return false;
        if (serverNameSmtp != null ? !serverNameSmtp.equals(that.serverNameSmtp) : that.serverNameSmtp != null)
            return false;
        if (smspwd != null ? !smspwd.equals(that.smspwd) : that.smspwd != null) return false;
        if (sonntag != null ? !sonntag.equals(that.sonntag) : that.sonntag != null) return false;
        if (sonntagsLinientyp != null ? !sonntagsLinientyp.equals(that.sonntagsLinientyp) : that.sonntagsLinientyp != null)
            return false;
        if (strNr != null ? !strNr.equals(that.strNr) : that.strNr != null) return false;
        if (strang != null ? !strang.equals(that.strang) : that.strang != null) return false;
        if (strangDatum != null ? !strangDatum.equals(that.strangDatum) : that.strangDatum != null) return false;
        if (strangOrder != null ? !strangOrder.equals(that.strangOrder) : that.strangOrder != null) return false;
        if (strangZ != null ? !strangZ.equals(that.strangZ) : that.strangZ != null) return false;
        if (strasse != null ? !strasse.equals(that.strasse) : that.strasse != null) return false;
        if (telefax != null ? !telefax.equals(that.telefax) : that.telefax != null) return false;
        if (telefon1 != null ? !telefon1.equals(that.telefon1) : that.telefon1 != null) return false;
        if (telefon2 != null ? !telefon2.equals(that.telefon2) : that.telefon2 != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (trzProz != null ? !trzProz.equals(that.trzProz) : that.trzProz != null) return false;
        if (uhrzeitAnfang != null ? !uhrzeitAnfang.equals(that.uhrzeitAnfang) : that.uhrzeitAnfang != null)
            return false;
        if (uhrzeitAnfangSa != null ? !uhrzeitAnfangSa.equals(that.uhrzeitAnfangSa) : that.uhrzeitAnfangSa != null)
            return false;
        if (uhrzeitAnfangSo != null ? !uhrzeitAnfangSo.equals(that.uhrzeitAnfangSo) : that.uhrzeitAnfangSo != null)
            return false;
        if (uhrzeitEnde != null ? !uhrzeitEnde.equals(that.uhrzeitEnde) : that.uhrzeitEnde != null) return false;
        if (uhrzeitEndeSa != null ? !uhrzeitEndeSa.equals(that.uhrzeitEndeSa) : that.uhrzeitEndeSa != null)
            return false;
        if (uhrzeitEndeSo != null ? !uhrzeitEndeSo.equals(that.uhrzeitEndeSo) : that.uhrzeitEndeSo != null)
            return false;
        if (umRoutung != null ? !umRoutung.equals(that.umRoutung) : that.umRoutung != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (ustId != null ? !ustId.equals(that.ustId) : that.ustId != null) return false;
        if (valOk != null ? !valOk.equals(that.valOk) : that.valOk != null) return false;
        if (verbundenesU != null ? !verbundenesU.equals(that.verbundenesU) : that.verbundenesU != null) return false;
        if (vofiPrz != null ? !vofiPrz.equals(that.vofiPrz) : that.vofiPrz != null) return false;
        if (webadresse != null ? !webadresse.equals(that.webadresse) : that.webadresse != null) return false;
        if (webemail != null ? !webemail.equals(that.webemail) : that.webemail != null) return false;
        if (webshopInit != null ? !webshopInit.equals(that.webshopInit) : that.webshopInit != null) return false;
        if (xlsAuftragOk != null ? !xlsAuftragOk.equals(that.xlsAuftragOk) : that.xlsAuftragOk != null) return false;
        if (xmlAcn != null ? !xmlAcn.equals(that.xmlAcn) : that.xmlAcn != null) return false;
        if (xmlStammdaten != null ? !xmlStammdaten.equals(that.xmlStammdaten) : that.xmlStammdaten != null)
            return false;
        if (zahlungsbedingungen != null ? !zahlungsbedingungen.equals(that.zahlungsbedingungen) : that.zahlungsbedingungen != null)
            return false;
        if (zahlungsbedingungenR != null ? !zahlungsbedingungenR.equals(that.zahlungsbedingungenR) : that.zahlungsbedingungenR != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = depotNr != null ? depotNr.hashCode() : 0;
        result = 31 * result + (depotLevel != null ? depotLevel.hashCode() : 0);
        result = 31 * result + (depotParent != null ? depotParent.hashCode() : 0);
        result = 31 * result + (depotMatchcode != null ? depotMatchcode.hashCode() : 0);
        result = 31 * result + (linienNr != null ? linienNr.hashCode() : 0);
        result = 31 * result + (linienankunft != null ? linienankunft.hashCode() : 0);
        result = 31 * result + (linienabfahrt != null ? linienabfahrt.hashCode() : 0);
        result = 31 * result + (aktivierungsdatum != null ? aktivierungsdatum.hashCode() : 0);
        result = 31 * result + (deaktivierungsdatum != null ? deaktivierungsdatum.hashCode() : 0);
        result = 31 * result + (istGueltig != null ? istGueltig.hashCode() : 0);
        result = 31 * result + (firma1 != null ? firma1.hashCode() : 0);
        result = 31 * result + (firma2 != null ? firma2.hashCode() : 0);
        result = 31 * result + (lkz != null ? lkz.hashCode() : 0);
        result = 31 * result + (plz != null ? plz.hashCode() : 0);
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        result = 31 * result + (strasse != null ? strasse.hashCode() : 0);
        result = 31 * result + (strNr != null ? strNr.hashCode() : 0);
        result = 31 * result + (lvw != null ? lvw.hashCode() : 0);
        result = 31 * result + (ovw != null ? ovw.hashCode() : 0);
        result = 31 * result + (telefon1 != null ? telefon1.hashCode() : 0);
        result = 31 * result + (telefon2 != null ? telefon2.hashCode() : 0);
        result = 31 * result + (telefax != null ? telefax.hashCode() : 0);
        result = 31 * result + (mobil != null ? mobil.hashCode() : 0);
        result = 31 * result + (nottelefon1 != null ? nottelefon1.hashCode() : 0);
        result = 31 * result + (nottelefon2 != null ? nottelefon2.hashCode() : 0);
        result = 31 * result + (anprechpartner1 != null ? anprechpartner1.hashCode() : 0);
        result = 31 * result + (anprechpartner2 != null ? anprechpartner2.hashCode() : 0);
        result = 31 * result + (ustId != null ? ustId.hashCode() : 0);
        result = 31 * result + (ekStNr != null ? ekStNr.hashCode() : 0);
        result = 31 * result + (blz != null ? blz.hashCode() : 0);
        result = 31 * result + (ktoNr != null ? ktoNr.hashCode() : 0);
        result = 31 * result + (bank != null ? bank.hashCode() : 0);
        result = 31 * result + (ktoInhaber != null ? ktoInhaber.hashCode() : 0);
        result = 31 * result + (rName1 != null ? rName1.hashCode() : 0);
        result = 31 * result + (rName2 != null ? rName2.hashCode() : 0);
        result = 31 * result + (rlkz != null ? rlkz.hashCode() : 0);
        result = 31 * result + (rplz != null ? rplz.hashCode() : 0);
        result = 31 * result + (rOrt != null ? rOrt.hashCode() : 0);
        result = 31 * result + (rStrasse != null ? rStrasse.hashCode() : 0);
        result = 31 * result + (rStrNr != null ? rStrNr.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (samstag != null ? samstag.hashCode() : 0);
        result = 31 * result + (sonntag != null ? sonntag.hashCode() : 0);
        result = 31 * result + (einzug != null ? einzug.hashCode() : 0);
        result = 31 * result + (uhrzeitAnfang != null ? uhrzeitAnfang.hashCode() : 0);
        result = 31 * result + (uhrzeitEnde != null ? uhrzeitEnde.hashCode() : 0);
        result = 31 * result + (uhrzeitAnfangSa != null ? uhrzeitAnfangSa.hashCode() : 0);
        result = 31 * result + (uhrzeitEndeSa != null ? uhrzeitEndeSa.hashCode() : 0);
        result = 31 * result + (uhrzeitAnfangSo != null ? uhrzeitAnfangSo.hashCode() : 0);
        result = 31 * result + (uhrzeitEndeSo != null ? uhrzeitEndeSo.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (serverNameSmtp != null ? serverNameSmtp.hashCode() : 0);
        result = 31 * result + (serverNamePop3 != null ? serverNamePop3.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (intRoutingLkz != null ? intRoutingLkz.hashCode() : 0);
        result = 31 * result + (mwStShl != null ? mwStShl.hashCode() : 0);
        result = 31 * result + (mwStPflicht != null ? mwStPflicht.hashCode() : 0);
        result = 31 * result + (f4F != null ? f4F.hashCode() : 0);
        result = 31 * result + (exportEmail != null ? exportEmail.hashCode() : 0);
        result = 31 * result + (exportFtpServer != null ? exportFtpServer.hashCode() : 0);
        result = 31 * result + (exportFtpUser != null ? exportFtpUser.hashCode() : 0);
        result = 31 * result + (exportFtpPwd != null ? exportFtpPwd.hashCode() : 0);
        result = 31 * result + (exportToGlo != null ? exportToGlo.hashCode() : 0);
        result = 31 * result + (exportToXml != null ? exportToXml.hashCode() : 0);
        result = 31 * result + (kondition != null ? kondition.hashCode() : 0);
        result = 31 * result + (qualiMail != null ? qualiMail.hashCode() : 0);
        result = 31 * result + (ebSdgDepot != null ? ebSdgDepot.hashCode() : 0);
        result = 31 * result + (ebDepotAd != null ? ebDepotAd.hashCode() : 0);
        result = 31 * result + (ebGen != null ? ebGen.hashCode() : 0);
        result = 31 * result + (ebUmvDepot != null ? ebUmvDepot.hashCode() : 0);
        result = 31 * result + (umRoutung != null ? umRoutung.hashCode() : 0);
        result = 31 * result + (kontokorrentnr != null ? kontokorrentnr.hashCode() : 0);
        result = 31 * result + (zahlungsbedingungen != null ? zahlungsbedingungen.hashCode() : 0);
        result = 31 * result + (verbundenesU != null ? verbundenesU.hashCode() : 0);
        result = 31 * result + (debitorNr != null ? debitorNr.hashCode() : 0);
        result = 31 * result + (kreditorNr != null ? kreditorNr.hashCode() : 0);
        result = 31 * result + (rgGutschrift != null ? rgGutschrift.hashCode() : 0);
        result = 31 * result + (rgRechnung != null ? rgRechnung.hashCode() : 0);
        result = 31 * result + (xmlStammdaten != null ? xmlStammdaten.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (firmenverbund != null ? firmenverbund.hashCode() : 0);
        result = 31 * result + (qualitaet != null ? qualitaet.hashCode() : 0);
        result = 31 * result + (sonntagsLinientyp != null ? sonntagsLinientyp.hashCode() : 0);
        result = 31 * result + (comCode != null ? comCode.hashCode() : 0);
        result = 31 * result + (eMailPas != null ? eMailPas.hashCode() : 0);
        result = 31 * result + (xmlAcn != null ? xmlAcn.hashCode() : 0);
        result = 31 * result + (webemail != null ? webemail.hashCode() : 0);
        result = 31 * result + (webadresse != null ? webadresse.hashCode() : 0);
        result = 31 * result + (paXml != null ? paXml.hashCode() : 0);
        result = 31 * result + (paPdf != null ? paPdf.hashCode() : 0);
        result = 31 * result + (zahlungsbedingungenR != null ? zahlungsbedingungenR.hashCode() : 0);
        result = 31 * result + (easyOk != null ? easyOk.hashCode() : 0);
        result = 31 * result + (vofiPrz != null ? vofiPrz.hashCode() : 0);
        result = 31 * result + (feLang != null ? feLang.hashCode() : 0);
        result = 31 * result + (mentorDepotNr != null ? mentorDepotNr.hashCode() : 0);
        result = 31 * result + (trzProz != null ? trzProz.hashCode() : 0);
        result = 31 * result + (nnOk != null ? nnOk.hashCode() : 0);
        result = 31 * result + (cod1 != null ? cod1.hashCode() : 0);
        result = 31 * result + (xlsAuftragOk != null ? xlsAuftragOk.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (hanReg != null ? hanReg.hashCode() : 0);
        result = 31 * result + (coloader != null ? coloader.hashCode() : 0);
        result = 31 * result + (abrechDepot != null ? abrechDepot.hashCode() : 0);
        result = 31 * result + (ladehilfeWas != null ? ladehilfeWas.hashCode() : 0);
        result = 31 * result + (ladehilfeKg != null ? ladehilfeKg.hashCode() : 0);
        result = 31 * result + (ladehilfeAb != null ? ladehilfeAb.hashCode() : 0);
        result = 31 * result + (ladehilfeLinie != null ? ladehilfeLinie.hashCode() : 0);
        result = 31 * result + (paDruck != null ? paDruck.hashCode() : 0);
        result = 31 * result + (rup != null ? rup.hashCode() : 0);
        result = 31 * result + (masterVertrag != null ? masterVertrag.hashCode() : 0);
        result = 31 * result + (strang != null ? strang.hashCode() : 0);
        result = 31 * result + (masterDepot != null ? masterDepot.hashCode() : 0);
        result = 31 * result + (webshopInit != null ? webshopInit.hashCode() : 0);
        result = 31 * result + (multiBag != null ? multiBag.hashCode() : 0);
        result = 31 * result + (bagKontingent != null ? bagKontingent.hashCode() : 0);
        result = 31 * result + (bagBemerkung != null ? bagBemerkung.hashCode() : 0);
        result = 31 * result + (konditionAbD != null ? konditionAbD.hashCode() : 0);
        result = 31 * result + (konditionLd != null ? konditionLd.hashCode() : 0);
        result = 31 * result + (bagCo != null ? bagCo.hashCode() : 0);
        result = 31 * result + (strangDatum != null ? strangDatum.hashCode() : 0);
        result = 31 * result + (strangZ != null ? strangZ.hashCode() : 0);
        result = 31 * result + (strangOrder != null ? strangOrder.hashCode() : 0);
        result = 31 * result + (smspwd != null ? smspwd.hashCode() : 0);
        result = 31 * result + (valOk != null ? valOk.hashCode() : 0);
        result = 31 * result + (maxValwert != null ? maxValwert.hashCode() : 0);
        result = 31 * result + (maxHoeherhaftung != null ? maxHoeherhaftung.hashCode() : 0);
        result = 31 * result + (maxWarenwert != null ? maxWarenwert.hashCode() : 0);
        result = 31 * result + (sapCostCenter != null ? sapCostCenter.hashCode() : 0);
        result = 31 * result + (adHocKondiDepot != null ? adHocKondiDepot.hashCode() : 0);
        return result;
    }
}

package org.deku.leo2.central.data.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by masc on 21.04.15.
 */
@Entity
@javax.persistence.Table(name = "tbldepotliste", schema = "", catalog = "dekuclient")
public class Depot {
    private Integer mDepotNr;

    @Id
    @javax.persistence.Column(name = "DepotNr", nullable = false, insertable = true, updatable = true)
    public Integer getDepotNr() {
        return mDepotNr;
    }

    public void setDepotNr(Integer depotNr) {
        mDepotNr = depotNr;
    }

    private Integer mDepotLevel;

    @Basic
    @javax.persistence.Column(name = "DepotLevel", nullable = false, insertable = true, updatable = true)
    public Integer getDepotLevel() {
        return mDepotLevel;
    }

    public void setDepotLevel(Integer depotLevel) {
        mDepotLevel = depotLevel;
    }

    private Integer mDepotParent;

    @Basic
    @javax.persistence.Column(name = "DepotParent", nullable = false, insertable = true, updatable = true)
    public Integer getDepotParent() {
        return mDepotParent;
    }

    public void setDepotParent(Integer depotParent) {
        mDepotParent = depotParent;
    }

    private String mDepotMatchcode;

    @Basic
    @javax.persistence.Column(name = "DepotMatchcode", nullable = false, insertable = true, updatable = true, length = 50)
    public String getDepotMatchcode() {
        return mDepotMatchcode;
    }

    public void setDepotMatchcode(String depotMatchcode) {
        mDepotMatchcode = depotMatchcode;
    }

    private Short mLinienNr;

    @Basic
    @javax.persistence.Column(name = "LinienNr", nullable = false, insertable = true, updatable = true)
    public Short getLinienNr() {
        return mLinienNr;
    }

    public void setLinienNr(Short linienNr) {
        mLinienNr = linienNr;
    }

    private Timestamp mLinienankunft;

    @Basic
    @javax.persistence.Column(name = "Linienankunft", nullable = true, insertable = true, updatable = true)
    public Timestamp getLinienankunft() {
        return mLinienankunft;
    }

    public void setLinienankunft(Timestamp linienankunft) {
        mLinienankunft = linienankunft;
    }

    private Timestamp mLinienabfahrt;

    @Basic
    @javax.persistence.Column(name = "Linienabfahrt", nullable = true, insertable = true, updatable = true)
    public Timestamp getLinienabfahrt() {
        return mLinienabfahrt;
    }

    public void setLinienabfahrt(Timestamp linienabfahrt) {
        mLinienabfahrt = linienabfahrt;
    }

    private Timestamp mAktivierungsdatum;

    @Basic
    @javax.persistence.Column(name = "Aktivierungsdatum", nullable = false, insertable = true, updatable = true)
    public Timestamp getAktivierungsdatum() {
        return mAktivierungsdatum;
    }

    public void setAktivierungsdatum(Timestamp aktivierungsdatum) {
        mAktivierungsdatum = aktivierungsdatum;
    }

    private Timestamp mDeaktivierungsdatum;

    @Basic
    @javax.persistence.Column(name = "Deaktivierungsdatum", nullable = false, insertable = true, updatable = true)
    public Timestamp getDeaktivierungsdatum() {
        return mDeaktivierungsdatum;
    }

    public void setDeaktivierungsdatum(Timestamp deaktivierungsdatum) {
        mDeaktivierungsdatum = deaktivierungsdatum;
    }

    private Short mIstGueltig;

    @Basic
    @javax.persistence.Column(name = "IstGueltig", nullable = false, insertable = true, updatable = true)
    public Short getIstGueltig() {
        return mIstGueltig;
    }

    public void setIstGueltig(Short istGueltig) {
        mIstGueltig = istGueltig;
    }

    private String mFirma1;

    @Basic
    @javax.persistence.Column(name = "Firma1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getFirma1() {
        return mFirma1;
    }

    public void setFirma1(String firma1) {
        mFirma1 = firma1;
    }

    private String mFirma2;

    @Basic
    @javax.persistence.Column(name = "Firma2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getFirma2() {
        return mFirma2;
    }

    public void setFirma2(String firma2) {
        mFirma2 = firma2;
    }

    private String mLkz;

    @Basic
    @javax.persistence.Column(name = "LKZ", nullable = true, insertable = true, updatable = true, length = 2)
    public String getLkz() {
        return mLkz;
    }

    public void setLkz(String lkz) {
        mLkz = lkz;
    }

    private String mPlz;

    @Basic
    @javax.persistence.Column(name = "PLZ", nullable = true, insertable = true, updatable = true, length = 8)
    public String getPlz() {
        return mPlz;
    }

    public void setPlz(String plz) {
        mPlz = plz;
    }

    private String mOrt;

    @Basic
    @javax.persistence.Column(name = "Ort", nullable = true, insertable = true, updatable = true, length = 50)
    public String getOrt() {
        return mOrt;
    }

    public void setOrt(String ort) {
        mOrt = ort;
    }

    private String mStrasse;

    @Basic
    @javax.persistence.Column(name = "Strasse", nullable = true, insertable = true, updatable = true, length = 50)
    public String getStrasse() {
        return mStrasse;
    }

    public void setStrasse(String strasse) {
        mStrasse = strasse;
    }

    private String mStrNr;

    @Basic
    @javax.persistence.Column(name = "StrNr", nullable = true, insertable = true, updatable = true, length = 10)
    public String getStrNr() {
        return mStrNr;
    }

    public void setStrNr(String strNr) {
        mStrNr = strNr;
    }

    private Short mLvw;

    @Basic
    @javax.persistence.Column(name = "LVW", nullable = true, insertable = true, updatable = true)
    public Short getLvw() {
        return mLvw;
    }

    public void setLvw(Short lvw) {
        mLvw = lvw;
    }

    private Integer mOvw;

    @Basic
    @javax.persistence.Column(name = "OVW", nullable = true, insertable = true, updatable = true)
    public Integer getOvw() {
        return mOvw;
    }

    public void setOvw(Integer ovw) {
        mOvw = ovw;
    }

    private String mTelefon1;

    @Basic
    @javax.persistence.Column(name = "Telefon1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getTelefon1() {
        return mTelefon1;
    }

    public void setTelefon1(String telefon1) {
        mTelefon1 = telefon1;
    }

    private String mTelefon2;

    @Basic
    @javax.persistence.Column(name = "Telefon2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getTelefon2() {
        return mTelefon2;
    }

    public void setTelefon2(String telefon2) {
        mTelefon2 = telefon2;
    }

    private String mTelefax;

    @Basic
    @javax.persistence.Column(name = "Telefax", nullable = true, insertable = true, updatable = true, length = 50)
    public String getTelefax() {
        return mTelefax;
    }

    public void setTelefax(String telefax) {
        mTelefax = telefax;
    }

    private String mMobil;

    @Basic
    @javax.persistence.Column(name = "Mobil", nullable = true, insertable = true, updatable = true, length = 50)
    public String getMobil() {
        return mMobil;
    }

    public void setMobil(String mobil) {
        mMobil = mobil;
    }

    private String mNottelefon1;

    @Basic
    @javax.persistence.Column(name = "Nottelefon1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getNottelefon1() {
        return mNottelefon1;
    }

    public void setNottelefon1(String nottelefon1) {
        mNottelefon1 = nottelefon1;
    }

    private String mNottelefon2;

    @Basic
    @javax.persistence.Column(name = "Nottelefon2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getNottelefon2() {
        return mNottelefon2;
    }

    public void setNottelefon2(String nottelefon2) {
        mNottelefon2 = nottelefon2;
    }

    private String mAnprechpartner1;

    @Basic
    @javax.persistence.Column(name = "Anprechpartner1", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAnprechpartner1() {
        return mAnprechpartner1;
    }

    public void setAnprechpartner1(String anprechpartner1) {
        mAnprechpartner1 = anprechpartner1;
    }

    private String mAnprechpartner2;

    @Basic
    @javax.persistence.Column(name = "Anprechpartner2", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAnprechpartner2() {
        return mAnprechpartner2;
    }

    public void setAnprechpartner2(String anprechpartner2) {
        mAnprechpartner2 = anprechpartner2;
    }

    private String mUstId;

    @Basic
    @javax.persistence.Column(name = "UstID", nullable = true, insertable = true, updatable = true, length = 50)
    public String getUstId() {
        return mUstId;
    }

    public void setUstId(String ustId) {
        mUstId = ustId;
    }

    private String mEkStNr;

    @Basic
    @javax.persistence.Column(name = "EKStNr", nullable = true, insertable = true, updatable = true, length = 50)
    public String getEkStNr() {
        return mEkStNr;
    }

    public void setEkStNr(String ekStNr) {
        mEkStNr = ekStNr;
    }

    private String mBlz;

    @Basic
    @javax.persistence.Column(name = "BLZ", nullable = true, insertable = true, updatable = true, length = 8)
    public String getBlz() {
        return mBlz;
    }

    public void setBlz(String blz) {
        mBlz = blz;
    }

    private String mKtoNr;

    @Basic
    @javax.persistence.Column(name = "KtoNr", nullable = true, insertable = true, updatable = true, length = 12)
    public String getKtoNr() {
        return mKtoNr;
    }

    public void setKtoNr(String ktoNr) {
        mKtoNr = ktoNr;
    }

    private String mBank;

    @Basic
    @javax.persistence.Column(name = "Bank", nullable = true, insertable = true, updatable = true, length = 25)
    public String getBank() {
        return mBank;
    }

    public void setBank(String bank) {
        mBank = bank;
    }

    private String mKtoInhaber;

    @Basic
    @javax.persistence.Column(name = "KtoInhaber", nullable = true, insertable = true, updatable = true, length = 27)
    public String getKtoInhaber() {
        return mKtoInhaber;
    }

    public void setKtoInhaber(String ktoInhaber) {
        mKtoInhaber = ktoInhaber;
    }

    private String mRName1;

    @Basic
    public String getRName1() {
        return mRName1;
    }

    public void setRName1(String RName1) {
        mRName1 = RName1;
    }

    private String mRName2;

    @Basic
    public String getRName2() {
        return mRName2;
    }

    public void setRName2(String RName2) {
        mRName2 = RName2;
    }

    private String mRlkz;

    @Basic
    @javax.persistence.Column(name = "RLKZ", nullable = true, insertable = true, updatable = true, length = 2)
    public String getRlkz() {
        return mRlkz;
    }

    public void setRlkz(String rlkz) {
        mRlkz = rlkz;
    }

    private String mRplz;

    @Basic
    @javax.persistence.Column(name = "RPLZ", nullable = true, insertable = true, updatable = true, length = 8)
    public String getRplz() {
        return mRplz;
    }

    public void setRplz(String rplz) {
        mRplz = rplz;
    }

    private String mROrt;

    @Basic
    public String getROrt() {
        return mROrt;
    }

    public void setROrt(String ROrt) {
        mROrt = ROrt;
    }

    private String mRStrasse;

    @Basic
    public String getRStrasse() {
        return mRStrasse;
    }

    public void setRStrasse(String RStrasse) {
        mRStrasse = RStrasse;
    }

    private String mRStrNr;

    @Basic
    public String getRStrNr() {
        return mRStrNr;
    }

    public void setRStrNr(String RStrNr) {
        mRStrNr = RStrNr;
    }

    private String mInfo;

    @Basic
    @javax.persistence.Column(name = "Info", nullable = true, insertable = true, updatable = true, length = 255)
    public String getInfo() {
        return mInfo;
    }

    public void setInfo(String info) {
        mInfo = info;
    }

    private String mEmail;

    @Basic
    @javax.persistence.Column(name = "Email", nullable = true, insertable = true, updatable = true, length = 75)
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    private Byte mSamstag;

    @Basic
    @javax.persistence.Column(name = "Samstag", nullable = true, insertable = true, updatable = true)
    public Byte getSamstag() {
        return mSamstag;
    }

    public void setSamstag(Byte samstag) {
        mSamstag = samstag;
    }

    private Byte mSonntag;

    @Basic
    @javax.persistence.Column(name = "Sonntag", nullable = true, insertable = true, updatable = true)
    public Byte getSonntag() {
        return mSonntag;
    }

    public void setSonntag(Byte sonntag) {
        mSonntag = sonntag;
    }

    private Byte mEinzug;

    @Basic
    @javax.persistence.Column(name = "Einzug", nullable = true, insertable = true, updatable = true)
    public Byte getEinzug() {
        return mEinzug;
    }

    public void setEinzug(Byte einzug) {
        mEinzug = einzug;
    }

    private Timestamp mUhrzeitAnfang;

    @Basic
    @javax.persistence.Column(name = "UhrzeitAnfang", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitAnfang() {
        return mUhrzeitAnfang;
    }

    public void setUhrzeitAnfang(Timestamp uhrzeitAnfang) {
        mUhrzeitAnfang = uhrzeitAnfang;
    }

    private Timestamp mUhrzeitEnde;

    @Basic
    @javax.persistence.Column(name = "UhrzeitEnde", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitEnde() {
        return mUhrzeitEnde;
    }

    public void setUhrzeitEnde(Timestamp uhrzeitEnde) {
        mUhrzeitEnde = uhrzeitEnde;
    }

    private Timestamp mUhrzeitAnfangSa;

    @Basic
    @javax.persistence.Column(name = "UhrzeitAnfangSa", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitAnfangSa() {
        return mUhrzeitAnfangSa;
    }

    public void setUhrzeitAnfangSa(Timestamp uhrzeitAnfangSa) {
        mUhrzeitAnfangSa = uhrzeitAnfangSa;
    }

    private Timestamp mUhrzeitEndeSa;

    @Basic
    @javax.persistence.Column(name = "UhrzeitEndeSa", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitEndeSa() {
        return mUhrzeitEndeSa;
    }

    public void setUhrzeitEndeSa(Timestamp uhrzeitEndeSa) {
        mUhrzeitEndeSa = uhrzeitEndeSa;
    }

    private Timestamp mUhrzeitAnfangSo;

    @Basic
    @javax.persistence.Column(name = "UhrzeitAnfangSo", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitAnfangSo() {
        return mUhrzeitAnfangSo;
    }

    public void setUhrzeitAnfangSo(Timestamp uhrzeitAnfangSo) {
        mUhrzeitAnfangSo = uhrzeitAnfangSo;
    }

    private Timestamp mUhrzeitEndeSo;

    @Basic
    @javax.persistence.Column(name = "UhrzeitEndeSo", nullable = true, insertable = true, updatable = true)
    public Timestamp getUhrzeitEndeSo() {
        return mUhrzeitEndeSo;
    }

    public void setUhrzeitEndeSo(Timestamp uhrzeitEndeSo) {
        mUhrzeitEndeSo = uhrzeitEndeSo;
    }

    private Timestamp mTimestamp;

    @Basic
    @javax.persistence.Column(name = "Timestamp", nullable = false, insertable = true, updatable = true)
    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }

    private String mServerNameSmtp;

    @Basic
    @javax.persistence.Column(name = "ServerNameSMTP", nullable = true, insertable = true, updatable = true, length = 45)
    public String getServerNameSmtp() {
        return mServerNameSmtp;
    }

    public void setServerNameSmtp(String serverNameSmtp) {
        mServerNameSmtp = serverNameSmtp;
    }

    private String mServerNamePop3;

    @Basic
    @javax.persistence.Column(name = "ServerNamePOP3", nullable = true, insertable = true, updatable = true, length = 45)
    public String getServerNamePop3() {
        return mServerNamePop3;
    }

    public void setServerNamePop3(String serverNamePop3) {
        mServerNamePop3 = serverNamePop3;
    }

    private String mUserName;

    @Basic
    @javax.persistence.Column(name = "UserName", nullable = true, insertable = true, updatable = true, length = 45)
    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    private String mPassword;

    @Basic
    @javax.persistence.Column(name = "Password", nullable = true, insertable = true, updatable = true, length = 45)
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    private String mIntRoutingLkz;

    @Basic
    @javax.persistence.Column(name = "IntRoutingLKZ", nullable = true, insertable = true, updatable = true, length = 45)
    public String getIntRoutingLkz() {
        return mIntRoutingLkz;
    }

    public void setIntRoutingLkz(String intRoutingLkz) {
        mIntRoutingLkz = intRoutingLkz;
    }

    private Integer mMwStShl;

    @Basic
    @javax.persistence.Column(name = "MwStShl", nullable = true, insertable = true, updatable = true)
    public Integer getMwStShl() {
        return mMwStShl;
    }

    public void setMwStShl(Integer mwStShl) {
        mMwStShl = mwStShl;
    }

    private Integer mMwStPflicht;

    @Basic
    @javax.persistence.Column(name = "MwStPflicht", nullable = true, insertable = true, updatable = true)
    public Integer getMwStPflicht() {
        return mMwStPflicht;
    }

    public void setMwStPflicht(Integer mwStPflicht) {
        mMwStPflicht = mwStPflicht;
    }

    private String mF4F;

    @Basic
    @javax.persistence.Column(name = "F4F", nullable = true, insertable = true, updatable = true, length = 100)
    public String getF4F() {
        return mF4F;
    }

    public void setF4F(String f4F) {
        mF4F = f4F;
    }

    private String mExportEmail;

    @Basic
    @javax.persistence.Column(name = "ExportEmail", nullable = true, insertable = true, updatable = true, length = 245)
    public String getExportEmail() {
        return mExportEmail;
    }

    public void setExportEmail(String exportEmail) {
        mExportEmail = exportEmail;
    }

    private String mExportFtpServer;

    @Basic
    @javax.persistence.Column(name = "ExportFTPServer", nullable = true, insertable = true, updatable = true, length = 45)
    public String getExportFtpServer() {
        return mExportFtpServer;
    }

    public void setExportFtpServer(String exportFtpServer) {
        mExportFtpServer = exportFtpServer;
    }

    private String mExportFtpUser;

    @Basic
    @javax.persistence.Column(name = "ExportFTPUser", nullable = true, insertable = true, updatable = true, length = 45)
    public String getExportFtpUser() {
        return mExportFtpUser;
    }

    public void setExportFtpUser(String exportFtpUser) {
        mExportFtpUser = exportFtpUser;
    }

    private String mExportFtpPwd;

    @Basic
    @javax.persistence.Column(name = "ExportFTPPwd", nullable = true, insertable = true, updatable = true, length = 45)
    public String getExportFtpPwd() {
        return mExportFtpPwd;
    }

    public void setExportFtpPwd(String exportFtpPwd) {
        mExportFtpPwd = exportFtpPwd;
    }

    private Integer mExportToGlo;

    @Basic
    @javax.persistence.Column(name = "ExportToGLO", nullable = true, insertable = true, updatable = true)
    public Integer getExportToGlo() {
        return mExportToGlo;
    }

    public void setExportToGlo(Integer exportToGlo) {
        mExportToGlo = exportToGlo;
    }

    private Integer mExportToXml;

    @Basic
    @javax.persistence.Column(name = "ExportToXML", nullable = true, insertable = true, updatable = true)
    public Integer getExportToXml() {
        return mExportToXml;
    }

    public void setExportToXml(Integer exportToXml) {
        mExportToXml = exportToXml;
    }

    private Integer mKondition;

    @Basic
    @javax.persistence.Column(name = "Kondition", nullable = true, insertable = true, updatable = true)
    public Integer getKondition() {
        return mKondition;
    }

    public void setKondition(Integer kondition) {
        mKondition = kondition;
    }

    private String mQualiMail;

    @Basic
    @javax.persistence.Column(name = "QualiMail", nullable = true, insertable = true, updatable = true, length = 255)
    public String getQualiMail() {
        return mQualiMail;
    }

    public void setQualiMail(String qualiMail) {
        mQualiMail = qualiMail;
    }

    private Integer mEbSdgDepot;

    @Basic
    @javax.persistence.Column(name = "EBSdgDepot", nullable = true, insertable = true, updatable = true)
    public Integer getEbSdgDepot() {
        return mEbSdgDepot;
    }

    public void setEbSdgDepot(Integer ebSdgDepot) {
        mEbSdgDepot = ebSdgDepot;
    }

    private Integer mEbDepotAd;

    @Basic
    @javax.persistence.Column(name = "EBDepotAD", nullable = true, insertable = true, updatable = true)
    public Integer getEbDepotAd() {
        return mEbDepotAd;
    }

    public void setEbDepotAd(Integer ebDepotAd) {
        mEbDepotAd = ebDepotAd;
    }

    private Integer mEbGen;

    @Basic
    @javax.persistence.Column(name = "EBGen", nullable = true, insertable = true, updatable = true)
    public Integer getEbGen() {
        return mEbGen;
    }

    public void setEbGen(Integer ebGen) {
        mEbGen = ebGen;
    }

    private Integer mEbUmvDepot;

    @Basic
    @javax.persistence.Column(name = "EBUmvDepot", nullable = true, insertable = true, updatable = true)
    public Integer getEbUmvDepot() {
        return mEbUmvDepot;
    }

    public void setEbUmvDepot(Integer ebUmvDepot) {
        mEbUmvDepot = ebUmvDepot;
    }

    private String mUmRoutung;

    @Basic
    @javax.persistence.Column(name = "UmRoutung", nullable = true, insertable = true, updatable = true, length = 250)
    public String getUmRoutung() {
        return mUmRoutung;
    }

    public void setUmRoutung(String umRoutung) {
        mUmRoutung = umRoutung;
    }

    private Double mKontokorrentnr;

    @Basic
    @javax.persistence.Column(name = "Kontokorrentnr", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getKontokorrentnr() {
        return mKontokorrentnr;
    }

    public void setKontokorrentnr(Double kontokorrentnr) {
        mKontokorrentnr = kontokorrentnr;
    }

    private Integer mZahlungsbedingungen;

    @Basic
    @javax.persistence.Column(name = "Zahlungsbedingungen", nullable = true, insertable = true, updatable = true)
    public Integer getZahlungsbedingungen() {
        return mZahlungsbedingungen;
    }

    public void setZahlungsbedingungen(Integer zahlungsbedingungen) {
        mZahlungsbedingungen = zahlungsbedingungen;
    }

    private Integer mVerbundenesU;

    @Basic
    @javax.persistence.Column(name = "VerbundenesU", nullable = true, insertable = true, updatable = true)
    public Integer getVerbundenesU() {
        return mVerbundenesU;
    }

    public void setVerbundenesU(Integer verbundenesU) {
        mVerbundenesU = verbundenesU;
    }

    private Double mDebitorNr;

    @Basic
    @javax.persistence.Column(name = "DebitorNr", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getDebitorNr() {
        return mDebitorNr;
    }

    public void setDebitorNr(Double debitorNr) {
        mDebitorNr = debitorNr;
    }

    private Double mKreditorNr;

    @Basic
    @javax.persistence.Column(name = "KreditorNr", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getKreditorNr() {
        return mKreditorNr;
    }

    public void setKreditorNr(Double kreditorNr) {
        mKreditorNr = kreditorNr;
    }

    private String mRgGutschrift;

    @Basic
    @javax.persistence.Column(name = "RgGutschrift", nullable = true, insertable = true, updatable = true, length = 1)
    public String getRgGutschrift() {
        return mRgGutschrift;
    }

    public void setRgGutschrift(String rgGutschrift) {
        mRgGutschrift = rgGutschrift;
    }

    private String mRgRechnung;

    @Basic
    @javax.persistence.Column(name = "RgRechnung", nullable = true, insertable = true, updatable = true, length = 1)
    public String getRgRechnung() {
        return mRgRechnung;
    }

    public void setRgRechnung(String rgRechnung) {
        mRgRechnung = rgRechnung;
    }

    private Integer mXmlStammdaten;

    @Basic
    @javax.persistence.Column(name = "XMLStammdaten", nullable = true, insertable = true, updatable = true)
    public Integer getXmlStammdaten() {
        return mXmlStammdaten;
    }

    public void setXmlStammdaten(Integer xmlStammdaten) {
        mXmlStammdaten = xmlStammdaten;
    }

    private String mRegion;

    @Basic
    @javax.persistence.Column(name = "Region", nullable = true, insertable = true, updatable = true, length = 10)
    public String getRegion() {
        return mRegion;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    private String mFirmenverbund;

    @Basic
    @javax.persistence.Column(name = "Firmenverbund", nullable = true, insertable = true, updatable = true, length = 100)
    public String getFirmenverbund() {
        return mFirmenverbund;
    }

    public void setFirmenverbund(String firmenverbund) {
        mFirmenverbund = firmenverbund;
    }

    private Integer mQualitaet;

    @Basic
    @javax.persistence.Column(name = "Qualitaet", nullable = true, insertable = true, updatable = true)
    public Integer getQualitaet() {
        return mQualitaet;
    }

    public void setQualitaet(Integer qualitaet) {
        mQualitaet = qualitaet;
    }

    private Integer mSonntagsLinientyp;

    @Basic
    @javax.persistence.Column(name = "SonntagsLinientyp", nullable = true, insertable = true, updatable = true)
    public Integer getSonntagsLinientyp() {
        return mSonntagsLinientyp;
    }

    public void setSonntagsLinientyp(Integer sonntagsLinientyp) {
        mSonntagsLinientyp = sonntagsLinientyp;
    }

    private Integer mComCode;

    @Basic
    @javax.persistence.Column(name = "Com_code", nullable = true, insertable = true, updatable = true)
    public Integer getComCode() {
        return mComCode;
    }

    public void setComCode(Integer comCode) {
        mComCode = comCode;
    }

    private String mEMailPas;

    @Basic
    @javax.persistence.Column(name = "Email_pas", nullable = true, insertable = true, updatable = true)
    public String getEMailPas() {
        return mEMailPas;
    }

    public void setEMailPas(String EMailPas) {
        mEMailPas = EMailPas;
    }

    private Integer mXmlAcn;

    @Basic
    @javax.persistence.Column(name = "XML_ACN", nullable = true, insertable = true, updatable = true)
    public Integer getXmlAcn() {
        return mXmlAcn;
    }

    public void setXmlAcn(Integer xmlAcn) {
        mXmlAcn = xmlAcn;
    }

    private String mWebemail;

    @Basic
    @javax.persistence.Column(name = "webemail", nullable = true, insertable = true, updatable = true, length = 255)
    public String getWebemail() {
        return mWebemail;
    }

    public void setWebemail(String webemail) {
        mWebemail = webemail;
    }

    private String mWebadresse;

    @Basic
    @javax.persistence.Column(name = "webadresse", nullable = true, insertable = true, updatable = true, length = 255)
    public String getWebadresse() {
        return mWebadresse;
    }

    public void setWebadresse(String webadresse) {
        mWebadresse = webadresse;
    }

    private Integer mPaXml;

    @Basic
    @javax.persistence.Column(name = "PA_XML", nullable = true, insertable = true, updatable = true)
    public Integer getPaXml() {
        return mPaXml;
    }

    public void setPaXml(Integer paXml) {
        mPaXml = paXml;
    }

    private Integer mPaPdf;

    @Basic
    @javax.persistence.Column(name = "PA_PDF", nullable = true, insertable = true, updatable = true)
    public Integer getPaPdf() {
        return mPaPdf;
    }

    public void setPaPdf(Integer paPdf) {
        mPaPdf = paPdf;
    }

    private Integer mZahlungsbedingungenR;

    @Basic
    @javax.persistence.Column(name = "ZahlungsbedingungenR", nullable = true, insertable = true, updatable = true)
    public Integer getZahlungsbedingungenR() {
        return mZahlungsbedingungenR;
    }

    public void setZahlungsbedingungenR(Integer zahlungsbedingungenR) {
        mZahlungsbedingungenR = zahlungsbedingungenR;
    }

    private Integer mEasyOk;

    @Basic
    @javax.persistence.Column(name = "EASYOk", nullable = true, insertable = true, updatable = true)
    public Integer getEasyOk() {
        return mEasyOk;
    }

    public void setEasyOk(Integer easyOk) {
        mEasyOk = easyOk;
    }

    private Double mVofiPrz;

    @Basic
    @javax.persistence.Column(name = "VofiPrz", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getVofiPrz() {
        return mVofiPrz;
    }

    public void setVofiPrz(Double vofiPrz) {
        mVofiPrz = vofiPrz;
    }

    private String mFeLang;

    @Basic
    @javax.persistence.Column(name = "FELang", nullable = true, insertable = true, updatable = true, length = 5)
    public String getFeLang() {
        return mFeLang;
    }

    public void setFeLang(String feLang) {
        mFeLang = feLang;
    }

    private Integer mMentorDepotNr;

    @Basic
    @javax.persistence.Column(name = "MentorDepotNr", nullable = true, insertable = true, updatable = true)
    public Integer getMentorDepotNr() {
        return mMentorDepotNr;
    }

    public void setMentorDepotNr(Integer mentorDepotNr) {
        mMentorDepotNr = mentorDepotNr;
    }

    private Double mTrzProz;

    @Basic
    @javax.persistence.Column(name = "TRZProz", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getTrzProz() {
        return mTrzProz;
    }

    public void setTrzProz(Double trzProz) {
        mTrzProz = trzProz;
    }

    private Integer mNnOk;

    @Basic
    @javax.persistence.Column(name = "NNOk", nullable = true, insertable = true, updatable = true)
    public Integer getNnOk() {
        return mNnOk;
    }

    public void setNnOk(Integer nnOk) {
        mNnOk = nnOk;
    }

    private String mCod1;

    @Basic
    @javax.persistence.Column(name = "COD1", nullable = true, insertable = true, updatable = true, length = 5)
    public String getCod1() {
        return mCod1;
    }

    public void setCod1(String cod1) {
        mCod1 = cod1;
    }

    private Integer mXlsAuftragOk;

    @Basic
    @javax.persistence.Column(name = "XLSAuftragOK", nullable = true, insertable = true, updatable = true)
    public Integer getXlsAuftragOk() {
        return mXlsAuftragOk;
    }

    public void setXlsAuftragOk(Integer xlsAuftragOk) {
        mXlsAuftragOk = xlsAuftragOk;
    }

    private Integer mId;

    @Basic
    @javax.persistence.Column(name = "ID", nullable = true, insertable = true, updatable = true)
    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    private String mHanReg;

    @Basic
    @javax.persistence.Column(name = "HanReg", nullable = true, insertable = true, updatable = true, length = 45)
    public String getHanReg() {
        return mHanReg;
    }

    public void setHanReg(String hanReg) {
        mHanReg = hanReg;
    }

    private Integer mColoader;

    @Basic
    @javax.persistence.Column(name = "Coloader", nullable = true, insertable = true, updatable = true)
    public Integer getColoader() {
        return mColoader;
    }

    public void setColoader(Integer coloader) {
        mColoader = coloader;
    }

    private Integer mAbrechDepot;

    @Basic
    @javax.persistence.Column(name = "AbrechDepot", nullable = true, insertable = true, updatable = true)
    public Integer getAbrechDepot() {
        return mAbrechDepot;
    }

    public void setAbrechDepot(Integer abrechDepot) {
        mAbrechDepot = abrechDepot;
    }

    private String mLadehilfeWas;

    @Basic
    @javax.persistence.Column(name = "LadehilfeWas", nullable = true, insertable = true, updatable = true, length = 100)
    public String getLadehilfeWas() {
        return mLadehilfeWas;
    }

    public void setLadehilfeWas(String ladehilfeWas) {
        mLadehilfeWas = ladehilfeWas;
    }

    private Integer mLadehilfeKg;

    @Basic
    @javax.persistence.Column(name = "LadehilfeKg", nullable = true, insertable = true, updatable = true)
    public Integer getLadehilfeKg() {
        return mLadehilfeKg;
    }

    public void setLadehilfeKg(Integer ladehilfeKg) {
        mLadehilfeKg = ladehilfeKg;
    }

    private String mLadehilfeAb;

    @Basic
    @javax.persistence.Column(name = "LadehilfeAb", nullable = true, insertable = true, updatable = true, length = 5)
    public String getLadehilfeAb() {
        return mLadehilfeAb;
    }

    public void setLadehilfeAb(String ladehilfeAb) {
        mLadehilfeAb = ladehilfeAb;
    }

    private Integer mLadehilfeLinie;

    @Basic
    @javax.persistence.Column(name = "LadehilfeLinie", nullable = true, insertable = true, updatable = true)
    public Integer getLadehilfeLinie() {
        return mLadehilfeLinie;
    }

    public void setLadehilfeLinie(Integer ladehilfeLinie) {
        mLadehilfeLinie = ladehilfeLinie;
    }

    private Integer mPaDruck;

    @Basic
    @javax.persistence.Column(name = "PA_Druck", nullable = true, insertable = true, updatable = true)
    public Integer getPaDruck() {
        return mPaDruck;
    }

    public void setPaDruck(Integer paDruck) {
        mPaDruck = paDruck;
    }

    private String mRup;

    @Basic
    @javax.persistence.Column(name = "RUP", nullable = true, insertable = true, updatable = true, length = 15)
    public String getRup() {
        return mRup;
    }

    public void setRup(String rup) {
        mRup = rup;
    }

    private Integer mMasterVertrag;

    @Basic
    @javax.persistence.Column(name = "MasterVertrag", nullable = true, insertable = true, updatable = true)
    public Integer getMasterVertrag() {
        return mMasterVertrag;
    }

    public void setMasterVertrag(Integer masterVertrag) {
        mMasterVertrag = masterVertrag;
    }

    private Integer mStrang;

    @Basic
    @javax.persistence.Column(name = "Strang", nullable = true, insertable = true, updatable = true)
    public Integer getStrang() {
        return mStrang;
    }

    public void setStrang(Integer strang) {
        mStrang = strang;
    }

    private Integer mMasterDepot;

    @Basic
    @javax.persistence.Column(name = "MasterDepot", nullable = true, insertable = true, updatable = true)
    public Integer getMasterDepot() {
        return mMasterDepot;
    }

    public void setMasterDepot(Integer masterDepot) {
        mMasterDepot = masterDepot;
    }

    private Timestamp mWebshopInit;

    @Basic
    @javax.persistence.Column(name = "WebshopInit", nullable = true, insertable = true, updatable = true)
    public Timestamp getWebshopInit() {
        return mWebshopInit;
    }

    public void setWebshopInit(Timestamp webshopInit) {
        mWebshopInit = webshopInit;
    }

    private Double mMultiBag;

    @Basic
    @javax.persistence.Column(name = "MultiBag", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMultiBag() {
        return mMultiBag;
    }

    public void setMultiBag(Double multiBag) {
        mMultiBag = multiBag;
    }

    private Integer mBagKontingent;

    @Basic
    @javax.persistence.Column(name = "BagKontingent", nullable = true, insertable = true, updatable = true)
    public Integer getBagKontingent() {
        return mBagKontingent;
    }

    public void setBagKontingent(Integer bagKontingent) {
        mBagKontingent = bagKontingent;
    }

    private String mBagBemerkung;

    @Basic
    @javax.persistence.Column(name = "BagBemerkung", nullable = true, insertable = true, updatable = true, length = 15)
    public String getBagBemerkung() {
        return mBagBemerkung;
    }

    public void setBagBemerkung(String bagBemerkung) {
        mBagBemerkung = bagBemerkung;
    }

    private Integer mKonditionAbD;

    @Basic
    @javax.persistence.Column(name = "KonditionAbD", nullable = true, insertable = true, updatable = true)
    public Integer getKonditionAbD() {
        return mKonditionAbD;
    }

    public void setKonditionAbD(Integer konditionAbD) {
        mKonditionAbD = konditionAbD;
    }

    private Integer mKonditionLd;

    @Basic
    @javax.persistence.Column(name = "KonditionLD", nullable = true, insertable = true, updatable = true)
    public Integer getKonditionLd() {
        return mKonditionLd;
    }

    public void setKonditionLd(Integer konditionLd) {
        mKonditionLd = konditionLd;
    }

    private Integer mBagCo;

    @Basic
    @javax.persistence.Column(name = "BagCo", nullable = true, insertable = true, updatable = true)
    public Integer getBagCo() {
        return mBagCo;
    }

    public void setBagCo(Integer bagCo) {
        mBagCo = bagCo;
    }

    private Timestamp mStrangDatum;

    @Basic
    @javax.persistence.Column(name = "StrangDatum", nullable = true, insertable = true, updatable = true)
    public Timestamp getStrangDatum() {
        return mStrangDatum;
    }

    public void setStrangDatum(Timestamp strangDatum) {
        mStrangDatum = strangDatum;
    }

    private Double mStrangZ;

    @Basic
    @javax.persistence.Column(name = "StrangZ", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getStrangZ() {
        return mStrangZ;
    }

    public void setStrangZ(Double strangZ) {
        mStrangZ = strangZ;
    }

    private Double mStrangOrder;

    @Basic
    @javax.persistence.Column(name = "StrangOrder", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getStrangOrder() {
        return mStrangOrder;
    }

    public void setStrangOrder(Double strangOrder) {
        mStrangOrder = strangOrder;
    }

    private String mSmspwd;

    @Basic
    @javax.persistence.Column(name = "smspwd", nullable = true, insertable = true, updatable = true, length = 20)
    public String getSmspwd() {
        return mSmspwd;
    }

    public void setSmspwd(String smspwd) {
        mSmspwd = smspwd;
    }

    private Integer mValOk;

    @Basic
    @javax.persistence.Column(name = "ValOk", nullable = true, insertable = true, updatable = true)
    public Integer getValOk() {
        return mValOk;
    }

    public void setValOk(Integer valOk) {
        mValOk = valOk;
    }

    private Double mMaxValwert;

    @Basic
    @javax.persistence.Column(name = "maxValwert", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMaxValwert() {
        return mMaxValwert;
    }

    public void setMaxValwert(Double maxValwert) {
        mMaxValwert = maxValwert;
    }

    private Double mMaxHoeherhaftung;

    @Basic
    @javax.persistence.Column(name = "maxHoeherhaftung", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMaxHoeherhaftung() {
        return mMaxHoeherhaftung;
    }

    public void setMaxHoeherhaftung(Double maxHoeherhaftung) {
        mMaxHoeherhaftung = maxHoeherhaftung;
    }

    private Double mMaxWarenwert;

    @Basic
    @javax.persistence.Column(name = "maxWarenwert", nullable = true, insertable = true, updatable = true, precision = 0)
    public Double getMaxWarenwert() {
        return mMaxWarenwert;
    }

    public void setMaxWarenwert(Double maxWarenwert) {
        mMaxWarenwert = maxWarenwert;
    }

    private String mSapCostCenter;

    @Basic
    @javax.persistence.Column(name = "SAPCostCenter", nullable = true, insertable = true, updatable = true, length = 10)
    public String getSapCostCenter() {
        return mSapCostCenter;
    }

    public void setSapCostCenter(String sapCostCenter) {
        mSapCostCenter = sapCostCenter;
    }

    private Integer mAdHocKondiDepot;

    @Basic
    @javax.persistence.Column(name = "AdHocKondiDepot", nullable = true, insertable = true, updatable = true)
    public Integer getAdHocKondiDepot() {
        return mAdHocKondiDepot;
    }

    public void setAdHocKondiDepot(Integer adHocKondiDepot) {
        mAdHocKondiDepot = adHocKondiDepot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Depot that = (Depot) o;

        if (mDepotNr != null ? !mDepotNr.equals(that.mDepotNr) : that.mDepotNr != null) return false;
        if (mDepotLevel != null ? !mDepotLevel.equals(that.mDepotLevel) : that.mDepotLevel != null) return false;
        if (mDepotParent != null ? !mDepotParent.equals(that.mDepotParent) : that.mDepotParent != null) return false;
        if (mDepotMatchcode != null ? !mDepotMatchcode.equals(that.mDepotMatchcode) : that.mDepotMatchcode != null)
            return false;
        if (mLinienNr != null ? !mLinienNr.equals(that.mLinienNr) : that.mLinienNr != null) return false;
        if (mLinienankunft != null ? !mLinienankunft.equals(that.mLinienankunft) : that.mLinienankunft != null)
            return false;
        if (mLinienabfahrt != null ? !mLinienabfahrt.equals(that.mLinienabfahrt) : that.mLinienabfahrt != null)
            return false;
        if (mAktivierungsdatum != null ? !mAktivierungsdatum.equals(that.mAktivierungsdatum) : that.mAktivierungsdatum != null)
            return false;
        if (mDeaktivierungsdatum != null ? !mDeaktivierungsdatum.equals(that.mDeaktivierungsdatum) : that.mDeaktivierungsdatum != null)
            return false;
        if (mIstGueltig != null ? !mIstGueltig.equals(that.mIstGueltig) : that.mIstGueltig != null) return false;
        if (mFirma1 != null ? !mFirma1.equals(that.mFirma1) : that.mFirma1 != null) return false;
        if (mFirma2 != null ? !mFirma2.equals(that.mFirma2) : that.mFirma2 != null) return false;
        if (mLkz != null ? !mLkz.equals(that.mLkz) : that.mLkz != null) return false;
        if (mPlz != null ? !mPlz.equals(that.mPlz) : that.mPlz != null) return false;
        if (mOrt != null ? !mOrt.equals(that.mOrt) : that.mOrt != null) return false;
        if (mStrasse != null ? !mStrasse.equals(that.mStrasse) : that.mStrasse != null) return false;
        if (mStrNr != null ? !mStrNr.equals(that.mStrNr) : that.mStrNr != null) return false;
        if (mLvw != null ? !mLvw.equals(that.mLvw) : that.mLvw != null) return false;
        if (mOvw != null ? !mOvw.equals(that.mOvw) : that.mOvw != null) return false;
        if (mTelefon1 != null ? !mTelefon1.equals(that.mTelefon1) : that.mTelefon1 != null) return false;
        if (mTelefon2 != null ? !mTelefon2.equals(that.mTelefon2) : that.mTelefon2 != null) return false;
        if (mTelefax != null ? !mTelefax.equals(that.mTelefax) : that.mTelefax != null) return false;
        if (mMobil != null ? !mMobil.equals(that.mMobil) : that.mMobil != null) return false;
        if (mNottelefon1 != null ? !mNottelefon1.equals(that.mNottelefon1) : that.mNottelefon1 != null) return false;
        if (mNottelefon2 != null ? !mNottelefon2.equals(that.mNottelefon2) : that.mNottelefon2 != null) return false;
        if (mAnprechpartner1 != null ? !mAnprechpartner1.equals(that.mAnprechpartner1) : that.mAnprechpartner1 != null)
            return false;
        if (mAnprechpartner2 != null ? !mAnprechpartner2.equals(that.mAnprechpartner2) : that.mAnprechpartner2 != null)
            return false;
        if (mUstId != null ? !mUstId.equals(that.mUstId) : that.mUstId != null) return false;
        if (mEkStNr != null ? !mEkStNr.equals(that.mEkStNr) : that.mEkStNr != null) return false;
        if (mBlz != null ? !mBlz.equals(that.mBlz) : that.mBlz != null) return false;
        if (mKtoNr != null ? !mKtoNr.equals(that.mKtoNr) : that.mKtoNr != null) return false;
        if (mBank != null ? !mBank.equals(that.mBank) : that.mBank != null) return false;
        if (mKtoInhaber != null ? !mKtoInhaber.equals(that.mKtoInhaber) : that.mKtoInhaber != null) return false;
        if (mRlkz != null ? !mRlkz.equals(that.mRlkz) : that.mRlkz != null) return false;
        if (mRplz != null ? !mRplz.equals(that.mRplz) : that.mRplz != null) return false;
        if (mInfo != null ? !mInfo.equals(that.mInfo) : that.mInfo != null) return false;
        if (mEmail != null ? !mEmail.equals(that.mEmail) : that.mEmail != null) return false;
        if (mSamstag != null ? !mSamstag.equals(that.mSamstag) : that.mSamstag != null) return false;
        if (mSonntag != null ? !mSonntag.equals(that.mSonntag) : that.mSonntag != null) return false;
        if (mEinzug != null ? !mEinzug.equals(that.mEinzug) : that.mEinzug != null) return false;
        if (mUhrzeitAnfang != null ? !mUhrzeitAnfang.equals(that.mUhrzeitAnfang) : that.mUhrzeitAnfang != null)
            return false;
        if (mUhrzeitEnde != null ? !mUhrzeitEnde.equals(that.mUhrzeitEnde) : that.mUhrzeitEnde != null) return false;
        if (mUhrzeitAnfangSa != null ? !mUhrzeitAnfangSa.equals(that.mUhrzeitAnfangSa) : that.mUhrzeitAnfangSa != null)
            return false;
        if (mUhrzeitEndeSa != null ? !mUhrzeitEndeSa.equals(that.mUhrzeitEndeSa) : that.mUhrzeitEndeSa != null)
            return false;
        if (mUhrzeitAnfangSo != null ? !mUhrzeitAnfangSo.equals(that.mUhrzeitAnfangSo) : that.mUhrzeitAnfangSo != null)
            return false;
        if (mUhrzeitEndeSo != null ? !mUhrzeitEndeSo.equals(that.mUhrzeitEndeSo) : that.mUhrzeitEndeSo != null)
            return false;
        if (mTimestamp != null ? !mTimestamp.equals(that.mTimestamp) : that.mTimestamp != null) return false;
        if (mServerNameSmtp != null ? !mServerNameSmtp.equals(that.mServerNameSmtp) : that.mServerNameSmtp != null)
            return false;
        if (mServerNamePop3 != null ? !mServerNamePop3.equals(that.mServerNamePop3) : that.mServerNamePop3 != null)
            return false;
        if (mUserName != null ? !mUserName.equals(that.mUserName) : that.mUserName != null) return false;
        if (mPassword != null ? !mPassword.equals(that.mPassword) : that.mPassword != null) return false;
        if (mIntRoutingLkz != null ? !mIntRoutingLkz.equals(that.mIntRoutingLkz) : that.mIntRoutingLkz != null)
            return false;
        if (mMwStShl != null ? !mMwStShl.equals(that.mMwStShl) : that.mMwStShl != null) return false;
        if (mMwStPflicht != null ? !mMwStPflicht.equals(that.mMwStPflicht) : that.mMwStPflicht != null) return false;
        if (mF4F != null ? !mF4F.equals(that.mF4F) : that.mF4F != null) return false;
        if (mExportEmail != null ? !mExportEmail.equals(that.mExportEmail) : that.mExportEmail != null) return false;
        if (mExportFtpServer != null ? !mExportFtpServer.equals(that.mExportFtpServer) : that.mExportFtpServer != null)
            return false;
        if (mExportFtpUser != null ? !mExportFtpUser.equals(that.mExportFtpUser) : that.mExportFtpUser != null)
            return false;
        if (mExportFtpPwd != null ? !mExportFtpPwd.equals(that.mExportFtpPwd) : that.mExportFtpPwd != null)
            return false;
        if (mExportToGlo != null ? !mExportToGlo.equals(that.mExportToGlo) : that.mExportToGlo != null) return false;
        if (mExportToXml != null ? !mExportToXml.equals(that.mExportToXml) : that.mExportToXml != null) return false;
        if (mKondition != null ? !mKondition.equals(that.mKondition) : that.mKondition != null) return false;
        if (mQualiMail != null ? !mQualiMail.equals(that.mQualiMail) : that.mQualiMail != null) return false;
        if (mEbSdgDepot != null ? !mEbSdgDepot.equals(that.mEbSdgDepot) : that.mEbSdgDepot != null) return false;
        if (mEbDepotAd != null ? !mEbDepotAd.equals(that.mEbDepotAd) : that.mEbDepotAd != null) return false;
        if (mEbGen != null ? !mEbGen.equals(that.mEbGen) : that.mEbGen != null) return false;
        if (mEbUmvDepot != null ? !mEbUmvDepot.equals(that.mEbUmvDepot) : that.mEbUmvDepot != null) return false;
        if (mUmRoutung != null ? !mUmRoutung.equals(that.mUmRoutung) : that.mUmRoutung != null) return false;
        if (mKontokorrentnr != null ? !mKontokorrentnr.equals(that.mKontokorrentnr) : that.mKontokorrentnr != null)
            return false;
        if (mZahlungsbedingungen != null ? !mZahlungsbedingungen.equals(that.mZahlungsbedingungen) : that.mZahlungsbedingungen != null)
            return false;
        if (mVerbundenesU != null ? !mVerbundenesU.equals(that.mVerbundenesU) : that.mVerbundenesU != null)
            return false;
        if (mDebitorNr != null ? !mDebitorNr.equals(that.mDebitorNr) : that.mDebitorNr != null) return false;
        if (mKreditorNr != null ? !mKreditorNr.equals(that.mKreditorNr) : that.mKreditorNr != null) return false;
        if (mRgGutschrift != null ? !mRgGutschrift.equals(that.mRgGutschrift) : that.mRgGutschrift != null)
            return false;
        if (mRgRechnung != null ? !mRgRechnung.equals(that.mRgRechnung) : that.mRgRechnung != null) return false;
        if (mXmlStammdaten != null ? !mXmlStammdaten.equals(that.mXmlStammdaten) : that.mXmlStammdaten != null)
            return false;
        if (mRegion != null ? !mRegion.equals(that.mRegion) : that.mRegion != null) return false;
        if (mFirmenverbund != null ? !mFirmenverbund.equals(that.mFirmenverbund) : that.mFirmenverbund != null)
            return false;
        if (mQualitaet != null ? !mQualitaet.equals(that.mQualitaet) : that.mQualitaet != null) return false;
        if (mSonntagsLinientyp != null ? !mSonntagsLinientyp.equals(that.mSonntagsLinientyp) : that.mSonntagsLinientyp != null)
            return false;
        if (mComCode != null ? !mComCode.equals(that.mComCode) : that.mComCode != null) return false;
        if (mXmlAcn != null ? !mXmlAcn.equals(that.mXmlAcn) : that.mXmlAcn != null) return false;
        if (mWebemail != null ? !mWebemail.equals(that.mWebemail) : that.mWebemail != null) return false;
        if (mWebadresse != null ? !mWebadresse.equals(that.mWebadresse) : that.mWebadresse != null) return false;
        if (mPaXml != null ? !mPaXml.equals(that.mPaXml) : that.mPaXml != null) return false;
        if (mPaPdf != null ? !mPaPdf.equals(that.mPaPdf) : that.mPaPdf != null) return false;
        if (mZahlungsbedingungenR != null ? !mZahlungsbedingungenR.equals(that.mZahlungsbedingungenR) : that.mZahlungsbedingungenR != null)
            return false;
        if (mEasyOk != null ? !mEasyOk.equals(that.mEasyOk) : that.mEasyOk != null) return false;
        if (mVofiPrz != null ? !mVofiPrz.equals(that.mVofiPrz) : that.mVofiPrz != null) return false;
        if (mFeLang != null ? !mFeLang.equals(that.mFeLang) : that.mFeLang != null) return false;
        if (mMentorDepotNr != null ? !mMentorDepotNr.equals(that.mMentorDepotNr) : that.mMentorDepotNr != null)
            return false;
        if (mTrzProz != null ? !mTrzProz.equals(that.mTrzProz) : that.mTrzProz != null) return false;
        if (mNnOk != null ? !mNnOk.equals(that.mNnOk) : that.mNnOk != null) return false;
        if (mCod1 != null ? !mCod1.equals(that.mCod1) : that.mCod1 != null) return false;
        if (mXlsAuftragOk != null ? !mXlsAuftragOk.equals(that.mXlsAuftragOk) : that.mXlsAuftragOk != null)
            return false;
        if (mId != null ? !mId.equals(that.mId) : that.mId != null) return false;
        if (mHanReg != null ? !mHanReg.equals(that.mHanReg) : that.mHanReg != null) return false;
        if (mColoader != null ? !mColoader.equals(that.mColoader) : that.mColoader != null) return false;
        if (mAbrechDepot != null ? !mAbrechDepot.equals(that.mAbrechDepot) : that.mAbrechDepot != null) return false;
        if (mLadehilfeWas != null ? !mLadehilfeWas.equals(that.mLadehilfeWas) : that.mLadehilfeWas != null)
            return false;
        if (mLadehilfeKg != null ? !mLadehilfeKg.equals(that.mLadehilfeKg) : that.mLadehilfeKg != null) return false;
        if (mLadehilfeAb != null ? !mLadehilfeAb.equals(that.mLadehilfeAb) : that.mLadehilfeAb != null) return false;
        if (mLadehilfeLinie != null ? !mLadehilfeLinie.equals(that.mLadehilfeLinie) : that.mLadehilfeLinie != null)
            return false;
        if (mPaDruck != null ? !mPaDruck.equals(that.mPaDruck) : that.mPaDruck != null) return false;
        if (mRup != null ? !mRup.equals(that.mRup) : that.mRup != null) return false;
        if (mMasterVertrag != null ? !mMasterVertrag.equals(that.mMasterVertrag) : that.mMasterVertrag != null)
            return false;
        if (mStrang != null ? !mStrang.equals(that.mStrang) : that.mStrang != null) return false;
        if (mMasterDepot != null ? !mMasterDepot.equals(that.mMasterDepot) : that.mMasterDepot != null) return false;
        if (mWebshopInit != null ? !mWebshopInit.equals(that.mWebshopInit) : that.mWebshopInit != null) return false;
        if (mMultiBag != null ? !mMultiBag.equals(that.mMultiBag) : that.mMultiBag != null) return false;
        if (mBagKontingent != null ? !mBagKontingent.equals(that.mBagKontingent) : that.mBagKontingent != null)
            return false;
        if (mBagBemerkung != null ? !mBagBemerkung.equals(that.mBagBemerkung) : that.mBagBemerkung != null)
            return false;
        if (mKonditionAbD != null ? !mKonditionAbD.equals(that.mKonditionAbD) : that.mKonditionAbD != null)
            return false;
        if (mKonditionLd != null ? !mKonditionLd.equals(that.mKonditionLd) : that.mKonditionLd != null) return false;
        if (mBagCo != null ? !mBagCo.equals(that.mBagCo) : that.mBagCo != null) return false;
        if (mStrangDatum != null ? !mStrangDatum.equals(that.mStrangDatum) : that.mStrangDatum != null) return false;
        if (mStrangZ != null ? !mStrangZ.equals(that.mStrangZ) : that.mStrangZ != null) return false;
        if (mStrangOrder != null ? !mStrangOrder.equals(that.mStrangOrder) : that.mStrangOrder != null) return false;
        if (mSmspwd != null ? !mSmspwd.equals(that.mSmspwd) : that.mSmspwd != null) return false;
        if (mValOk != null ? !mValOk.equals(that.mValOk) : that.mValOk != null) return false;
        if (mMaxValwert != null ? !mMaxValwert.equals(that.mMaxValwert) : that.mMaxValwert != null) return false;
        if (mMaxHoeherhaftung != null ? !mMaxHoeherhaftung.equals(that.mMaxHoeherhaftung) : that.mMaxHoeherhaftung != null)
            return false;
        if (mMaxWarenwert != null ? !mMaxWarenwert.equals(that.mMaxWarenwert) : that.mMaxWarenwert != null)
            return false;
        if (mSapCostCenter != null ? !mSapCostCenter.equals(that.mSapCostCenter) : that.mSapCostCenter != null)
            return false;
        if (mAdHocKondiDepot != null ? !mAdHocKondiDepot.equals(that.mAdHocKondiDepot) : that.mAdHocKondiDepot != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mDepotNr != null ? mDepotNr.hashCode() : 0;
        result = 31 * result + (mDepotLevel != null ? mDepotLevel.hashCode() : 0);
        result = 31 * result + (mDepotParent != null ? mDepotParent.hashCode() : 0);
        result = 31 * result + (mDepotMatchcode != null ? mDepotMatchcode.hashCode() : 0);
        result = 31 * result + (mLinienNr != null ? mLinienNr.hashCode() : 0);
        result = 31 * result + (mLinienankunft != null ? mLinienankunft.hashCode() : 0);
        result = 31 * result + (mLinienabfahrt != null ? mLinienabfahrt.hashCode() : 0);
        result = 31 * result + (mAktivierungsdatum != null ? mAktivierungsdatum.hashCode() : 0);
        result = 31 * result + (mDeaktivierungsdatum != null ? mDeaktivierungsdatum.hashCode() : 0);
        result = 31 * result + (mIstGueltig != null ? mIstGueltig.hashCode() : 0);
        result = 31 * result + (mFirma1 != null ? mFirma1.hashCode() : 0);
        result = 31 * result + (mFirma2 != null ? mFirma2.hashCode() : 0);
        result = 31 * result + (mLkz != null ? mLkz.hashCode() : 0);
        result = 31 * result + (mPlz != null ? mPlz.hashCode() : 0);
        result = 31 * result + (mOrt != null ? mOrt.hashCode() : 0);
        result = 31 * result + (mStrasse != null ? mStrasse.hashCode() : 0);
        result = 31 * result + (mStrNr != null ? mStrNr.hashCode() : 0);
        result = 31 * result + (mLvw != null ? mLvw.hashCode() : 0);
        result = 31 * result + (mOvw != null ? mOvw.hashCode() : 0);
        result = 31 * result + (mTelefon1 != null ? mTelefon1.hashCode() : 0);
        result = 31 * result + (mTelefon2 != null ? mTelefon2.hashCode() : 0);
        result = 31 * result + (mTelefax != null ? mTelefax.hashCode() : 0);
        result = 31 * result + (mMobil != null ? mMobil.hashCode() : 0);
        result = 31 * result + (mNottelefon1 != null ? mNottelefon1.hashCode() : 0);
        result = 31 * result + (mNottelefon2 != null ? mNottelefon2.hashCode() : 0);
        result = 31 * result + (mAnprechpartner1 != null ? mAnprechpartner1.hashCode() : 0);
        result = 31 * result + (mAnprechpartner2 != null ? mAnprechpartner2.hashCode() : 0);
        result = 31 * result + (mUstId != null ? mUstId.hashCode() : 0);
        result = 31 * result + (mEkStNr != null ? mEkStNr.hashCode() : 0);
        result = 31 * result + (mBlz != null ? mBlz.hashCode() : 0);
        result = 31 * result + (mKtoNr != null ? mKtoNr.hashCode() : 0);
        result = 31 * result + (mBank != null ? mBank.hashCode() : 0);
        result = 31 * result + (mKtoInhaber != null ? mKtoInhaber.hashCode() : 0);
        result = 31 * result + (mRlkz != null ? mRlkz.hashCode() : 0);
        result = 31 * result + (mRplz != null ? mRplz.hashCode() : 0);
        result = 31 * result + (mInfo != null ? mInfo.hashCode() : 0);
        result = 31 * result + (mEmail != null ? mEmail.hashCode() : 0);
        result = 31 * result + (mSamstag != null ? mSamstag.hashCode() : 0);
        result = 31 * result + (mSonntag != null ? mSonntag.hashCode() : 0);
        result = 31 * result + (mEinzug != null ? mEinzug.hashCode() : 0);
        result = 31 * result + (mUhrzeitAnfang != null ? mUhrzeitAnfang.hashCode() : 0);
        result = 31 * result + (mUhrzeitEnde != null ? mUhrzeitEnde.hashCode() : 0);
        result = 31 * result + (mUhrzeitAnfangSa != null ? mUhrzeitAnfangSa.hashCode() : 0);
        result = 31 * result + (mUhrzeitEndeSa != null ? mUhrzeitEndeSa.hashCode() : 0);
        result = 31 * result + (mUhrzeitAnfangSo != null ? mUhrzeitAnfangSo.hashCode() : 0);
        result = 31 * result + (mUhrzeitEndeSo != null ? mUhrzeitEndeSo.hashCode() : 0);
        result = 31 * result + (mTimestamp != null ? mTimestamp.hashCode() : 0);
        result = 31 * result + (mServerNameSmtp != null ? mServerNameSmtp.hashCode() : 0);
        result = 31 * result + (mServerNamePop3 != null ? mServerNamePop3.hashCode() : 0);
        result = 31 * result + (mUserName != null ? mUserName.hashCode() : 0);
        result = 31 * result + (mPassword != null ? mPassword.hashCode() : 0);
        result = 31 * result + (mIntRoutingLkz != null ? mIntRoutingLkz.hashCode() : 0);
        result = 31 * result + (mMwStShl != null ? mMwStShl.hashCode() : 0);
        result = 31 * result + (mMwStPflicht != null ? mMwStPflicht.hashCode() : 0);
        result = 31 * result + (mF4F != null ? mF4F.hashCode() : 0);
        result = 31 * result + (mExportEmail != null ? mExportEmail.hashCode() : 0);
        result = 31 * result + (mExportFtpServer != null ? mExportFtpServer.hashCode() : 0);
        result = 31 * result + (mExportFtpUser != null ? mExportFtpUser.hashCode() : 0);
        result = 31 * result + (mExportFtpPwd != null ? mExportFtpPwd.hashCode() : 0);
        result = 31 * result + (mExportToGlo != null ? mExportToGlo.hashCode() : 0);
        result = 31 * result + (mExportToXml != null ? mExportToXml.hashCode() : 0);
        result = 31 * result + (mKondition != null ? mKondition.hashCode() : 0);
        result = 31 * result + (mQualiMail != null ? mQualiMail.hashCode() : 0);
        result = 31 * result + (mEbSdgDepot != null ? mEbSdgDepot.hashCode() : 0);
        result = 31 * result + (mEbDepotAd != null ? mEbDepotAd.hashCode() : 0);
        result = 31 * result + (mEbGen != null ? mEbGen.hashCode() : 0);
        result = 31 * result + (mEbUmvDepot != null ? mEbUmvDepot.hashCode() : 0);
        result = 31 * result + (mUmRoutung != null ? mUmRoutung.hashCode() : 0);
        result = 31 * result + (mKontokorrentnr != null ? mKontokorrentnr.hashCode() : 0);
        result = 31 * result + (mZahlungsbedingungen != null ? mZahlungsbedingungen.hashCode() : 0);
        result = 31 * result + (mVerbundenesU != null ? mVerbundenesU.hashCode() : 0);
        result = 31 * result + (mDebitorNr != null ? mDebitorNr.hashCode() : 0);
        result = 31 * result + (mKreditorNr != null ? mKreditorNr.hashCode() : 0);
        result = 31 * result + (mRgGutschrift != null ? mRgGutschrift.hashCode() : 0);
        result = 31 * result + (mRgRechnung != null ? mRgRechnung.hashCode() : 0);
        result = 31 * result + (mXmlStammdaten != null ? mXmlStammdaten.hashCode() : 0);
        result = 31 * result + (mRegion != null ? mRegion.hashCode() : 0);
        result = 31 * result + (mFirmenverbund != null ? mFirmenverbund.hashCode() : 0);
        result = 31 * result + (mQualitaet != null ? mQualitaet.hashCode() : 0);
        result = 31 * result + (mSonntagsLinientyp != null ? mSonntagsLinientyp.hashCode() : 0);
        result = 31 * result + (mComCode != null ? mComCode.hashCode() : 0);
        result = 31 * result + (mXmlAcn != null ? mXmlAcn.hashCode() : 0);
        result = 31 * result + (mWebemail != null ? mWebemail.hashCode() : 0);
        result = 31 * result + (mWebadresse != null ? mWebadresse.hashCode() : 0);
        result = 31 * result + (mPaXml != null ? mPaXml.hashCode() : 0);
        result = 31 * result + (mPaPdf != null ? mPaPdf.hashCode() : 0);
        result = 31 * result + (mZahlungsbedingungenR != null ? mZahlungsbedingungenR.hashCode() : 0);
        result = 31 * result + (mEasyOk != null ? mEasyOk.hashCode() : 0);
        result = 31 * result + (mVofiPrz != null ? mVofiPrz.hashCode() : 0);
        result = 31 * result + (mFeLang != null ? mFeLang.hashCode() : 0);
        result = 31 * result + (mMentorDepotNr != null ? mMentorDepotNr.hashCode() : 0);
        result = 31 * result + (mTrzProz != null ? mTrzProz.hashCode() : 0);
        result = 31 * result + (mNnOk != null ? mNnOk.hashCode() : 0);
        result = 31 * result + (mCod1 != null ? mCod1.hashCode() : 0);
        result = 31 * result + (mXlsAuftragOk != null ? mXlsAuftragOk.hashCode() : 0);
        result = 31 * result + (mId != null ? mId.hashCode() : 0);
        result = 31 * result + (mHanReg != null ? mHanReg.hashCode() : 0);
        result = 31 * result + (mColoader != null ? mColoader.hashCode() : 0);
        result = 31 * result + (mAbrechDepot != null ? mAbrechDepot.hashCode() : 0);
        result = 31 * result + (mLadehilfeWas != null ? mLadehilfeWas.hashCode() : 0);
        result = 31 * result + (mLadehilfeKg != null ? mLadehilfeKg.hashCode() : 0);
        result = 31 * result + (mLadehilfeAb != null ? mLadehilfeAb.hashCode() : 0);
        result = 31 * result + (mLadehilfeLinie != null ? mLadehilfeLinie.hashCode() : 0);
        result = 31 * result + (mPaDruck != null ? mPaDruck.hashCode() : 0);
        result = 31 * result + (mRup != null ? mRup.hashCode() : 0);
        result = 31 * result + (mMasterVertrag != null ? mMasterVertrag.hashCode() : 0);
        result = 31 * result + (mStrang != null ? mStrang.hashCode() : 0);
        result = 31 * result + (mMasterDepot != null ? mMasterDepot.hashCode() : 0);
        result = 31 * result + (mWebshopInit != null ? mWebshopInit.hashCode() : 0);
        result = 31 * result + (mMultiBag != null ? mMultiBag.hashCode() : 0);
        result = 31 * result + (mBagKontingent != null ? mBagKontingent.hashCode() : 0);
        result = 31 * result + (mBagBemerkung != null ? mBagBemerkung.hashCode() : 0);
        result = 31 * result + (mKonditionAbD != null ? mKonditionAbD.hashCode() : 0);
        result = 31 * result + (mKonditionLd != null ? mKonditionLd.hashCode() : 0);
        result = 31 * result + (mBagCo != null ? mBagCo.hashCode() : 0);
        result = 31 * result + (mStrangDatum != null ? mStrangDatum.hashCode() : 0);
        result = 31 * result + (mStrangZ != null ? mStrangZ.hashCode() : 0);
        result = 31 * result + (mStrangOrder != null ? mStrangOrder.hashCode() : 0);
        result = 31 * result + (mSmspwd != null ? mSmspwd.hashCode() : 0);
        result = 31 * result + (mValOk != null ? mValOk.hashCode() : 0);
        result = 31 * result + (mMaxValwert != null ? mMaxValwert.hashCode() : 0);
        result = 31 * result + (mMaxHoeherhaftung != null ? mMaxHoeherhaftung.hashCode() : 0);
        result = 31 * result + (mMaxWarenwert != null ? mMaxWarenwert.hashCode() : 0);
        result = 31 * result + (mSapCostCenter != null ? mSapCostCenter.hashCode() : 0);
        result = 31 * result + (mAdHocKondiDepot != null ? mAdHocKondiDepot.hashCode() : 0);
        return result;
    }
}

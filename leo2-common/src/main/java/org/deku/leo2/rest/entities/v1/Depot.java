package org.deku.leo2.rest.entities.v1;

/**
 * Created by masc on 04.05.15.
 */
public class Depot {
    private Integer mDepotNr;
    private String mDepotMatchcode;
    private String mFirma1;
    private String mFirma2;
    private String mLkz;
    private String mPlz;
    private String mOrt;
    private String mStrasse;

    public Integer getDepotNr() {
        return mDepotNr;
    }

    public void setDepotNr(Integer depotNr) {
        mDepotNr = depotNr;
    }

    public String getDepotMatchcode() {
        return mDepotMatchcode;
    }

    public void setDepotMatchcode(String depotMatchcode) {
        mDepotMatchcode = depotMatchcode;
    }

    public String getFirma1() {
        return mFirma1;
    }

    public void setFirma1(String firma1) {
        mFirma1 = firma1;
    }

    public String getFirma2() {
        return mFirma2;
    }

    public void setFirma2(String firma2) {
        mFirma2 = firma2;
    }

    public String getLkz() {
        return mLkz;
    }

    public void setLkz(String lkz) {
        mLkz = lkz;
    }

    public String getPlz() {
        return mPlz;
    }

    public void setPlz(String plz) {
        mPlz = plz;
    }

    public String getOrt() {
        return mOrt;
    }

    public void setOrt(String ort) {
        mOrt = ort;
    }

    public String getStrasse() {
        return mStrasse;
    }

    public void setStrasse(String strasse) {
        mStrasse = strasse;
    }
}



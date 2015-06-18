package org.deku.leo2.node.data.entities;

import org.eclipse.persistence.annotations.Index;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@IdClass(RoutePK.class)
public class Route implements Serializable {
    private static final long serialVersionUID = -3738208098013706941L;

    private Integer layer;
    private String country;
    private String zipFrom;
    private String zipTo;
    private Integer validCRTR;
    private Timestamp validFrom;
    private Timestamp validTo;
    private Timestamp timestamp;
    private Integer station;
    private String area;
    private Time etod;
    private Time ltop;
    private Integer term;
    private Integer saturdayOK;
    private Time ltodsa;
    private Time ltodholiday;
    private Integer island;
    private String holidayCtrl;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Id
    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    @Id
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Id
    public String getZipFrom() {
        return zipFrom;
    }

    public void setZipFrom(String zipFrom) {
        this.zipFrom = zipFrom;
    }

    @Basic
    public String getZipTo() {
        return zipTo;
    }

    public void setZipTo(String zipTo) {
        this.zipTo = zipTo;
    }

    @Id
    public Integer getValidCRTR() {
        return validCRTR;
    }

    public void setValidCRTR(Integer validCRTR) {
        this.validCRTR = validCRTR;
    }

    @Basic
    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    @Basic
    public Integer getSaturdayOK() {
        return saturdayOK;
    }

    public void setSaturdayOK(Integer saturdayOK) {
        this.saturdayOK = saturdayOK;
    }


    @Id
    public Timestamp getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Timestamp validfrom) {
        this.validFrom = validfrom;
    }

    @Basic
    public Timestamp getValidTo() {
        return validTo;
    }

    public void setValidTo(Timestamp validTo) {
        this.validTo = validTo;
    }

    @Basic
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    public Integer getStation() {
        return station;
    }

    public void setStation(Integer station) {
        this.station = station;
    }


    @Basic
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Basic
    public Time getEtod() {
        return etod;
    }

    public void setEtod(Time etod) {
        this.etod = etod;
    }

    @Basic
    public Time getLtop() {
        return ltop;
    }

    public void setLtop(Time ltop) {
        this.ltop = ltop;
    }


    @Basic
    public Time getLtodsa() {
        return ltodsa;
    }

    public void setLtodsa(Time ltodsa) {
        this.ltodsa = ltodsa;
    }

    @Basic
    public Time getLtodholiday() {
        return ltodholiday;
    }

    public void setLtodholiday(Time ltodholiday) {
        this.ltodholiday = ltodholiday;
    }

    @Basic
    public Integer getIsland() {
        return island;
    }

    public void setIsland(Integer island) {
        this.island = island;
    }


    @Basic
    public String getHolidayCtrl() {
        return holidayCtrl;
    }

    public void setHolidayCtrl(String holidayCtrl) {
        this.holidayCtrl = holidayCtrl;
    }

}

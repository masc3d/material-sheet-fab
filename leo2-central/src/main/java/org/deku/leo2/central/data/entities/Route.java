package org.deku.leo2.central.data.entities;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@IdClass(RoutePK.class)
public class Route {
    private String product;
    private String lkz;
    private String zip;
    private Timestamp validfrom;
    private Timestamp validto;
    private Timestamp timestamp;
    private Integer station;
    private String sector;
    private String area;
    private Time etod;
    private Time ltop;
    private Integer transittime;
    private Time ltodsa;
    private Time ltodholiday;
    private Integer island;
    private Time etod2;
    private Time ltop2;
    private String holidayctrl;

    @Id
    @Column(name = "product")
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Id
    @Column(name = "lkz")
    public String getLkz() {
        return lkz;
    }

    public void setLkz(String lkz) {
        this.lkz = lkz;
    }

    @Id
    @Column(name = "zip")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Id
    @Column(name = "validfrom")
    public Timestamp getValidfrom() {
        return validfrom;
    }

    public void setValidfrom(Timestamp validfrom) {
        this.validfrom = validfrom;
    }

    @Basic
    @Column(name = "validto")
    public Timestamp getValidto() {
        return validto;
    }

    public void setValidto(Timestamp validto) {
        this.validto = validto;
    }

    @Basic
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "station")
    public Integer getStation() {
        return station;
    }

    public void setStation(Integer station) {
        this.station = station;
    }

    @Basic
    @Column(name = "sector")
    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    @Basic
    @Column(name = "area")
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Basic
    @Column(name = "etod")
    public Time getEtod() {
        return etod;
    }

    public void setEtod(Time etod) {
        this.etod = etod;
    }

    @Basic
    @Column(name = "ltop")
    public Time getLtop() {
        return ltop;
    }

    public void setLtop(Time ltop) {
        this.ltop = ltop;
    }

    @Basic
    @Column(name = "transittime")
    public Integer getTransittime() {
        return transittime;
    }

    public void setTransittime(Integer transittime) {
        this.transittime = transittime;
    }

    @Basic
    @Column(name = "ltodsa")
    public Time getLtodsa() {
        return ltodsa;
    }

    public void setLtodsa(Time ltodsa) {
        this.ltodsa = ltodsa;
    }

    @Basic
    @Column(name = "ltodholiday")
    public Time getLtodholiday() {
        return ltodholiday;
    }

    public void setLtodholiday(Time ltodholiday) {
        this.ltodholiday = ltodholiday;
    }

    @Basic
    @Column(name = "island")
    public Integer getIsland() {
        return island;
    }

    public void setIsland(Integer island) {
        this.island = island;
    }

    @Basic
    @Column(name = "etod2")
    public Time getEtod2() {
        return etod2;
    }

    public void setEtod2(Time etod2) {
        this.etod2 = etod2;
    }

    @Basic
    @Column(name = "ltop2")
    public Time getLtop2() {
        return ltop2;
    }

    public void setLtop2(Time ltop2) {
        this.ltop2 = ltop2;
    }

    @Basic
    @Column(name = "holidayctrl")
    public String getHolidayctrl() {
        return holidayctrl;
    }

    public void setHolidayctrl(String holidayctrl) {
        this.holidayctrl = holidayctrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (product != null ? !product.equals(route.product) : route.product != null) return false;
        if (lkz != null ? !lkz.equals(route.lkz) : route.lkz != null) return false;
        if (zip != null ? !zip.equals(route.zip) : route.zip != null) return false;
        if (validfrom != null ? !validfrom.equals(route.validfrom) : route.validfrom != null) return false;
        if (validto != null ? !validto.equals(route.validto) : route.validto != null) return false;
        if (timestamp != null ? !timestamp.equals(route.timestamp) : route.timestamp != null) return false;
        if (station != null ? !station.equals(route.station) : route.station != null) return false;
        if (sector != null ? !sector.equals(route.sector) : route.sector != null) return false;
        if (area != null ? !area.equals(route.area) : route.area != null) return false;
        if (etod != null ? !etod.equals(route.etod) : route.etod != null) return false;
        if (ltop != null ? !ltop.equals(route.ltop) : route.ltop != null) return false;
        if (transittime != null ? !transittime.equals(route.transittime) : route.transittime != null) return false;
        if (ltodsa != null ? !ltodsa.equals(route.ltodsa) : route.ltodsa != null) return false;
        if (ltodholiday != null ? !ltodholiday.equals(route.ltodholiday) : route.ltodholiday != null) return false;
        if (island != null ? !island.equals(route.island) : route.island != null) return false;
        if (etod2 != null ? !etod2.equals(route.etod2) : route.etod2 != null) return false;
        if (ltop2 != null ? !ltop2.equals(route.ltop2) : route.ltop2 != null) return false;
        if (holidayctrl != null ? !holidayctrl.equals(route.holidayctrl) : route.holidayctrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (lkz != null ? lkz.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (validfrom != null ? validfrom.hashCode() : 0);
        result = 31 * result + (validto != null ? validto.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (station != null ? station.hashCode() : 0);
        result = 31 * result + (sector != null ? sector.hashCode() : 0);
        result = 31 * result + (area != null ? area.hashCode() : 0);
        result = 31 * result + (etod != null ? etod.hashCode() : 0);
        result = 31 * result + (ltop != null ? ltop.hashCode() : 0);
        result = 31 * result + (transittime != null ? transittime.hashCode() : 0);
        result = 31 * result + (ltodsa != null ? ltodsa.hashCode() : 0);
        result = 31 * result + (ltodholiday != null ? ltodholiday.hashCode() : 0);
        result = 31 * result + (island != null ? island.hashCode() : 0);
        result = 31 * result + (etod2 != null ? etod2.hashCode() : 0);
        result = 31 * result + (ltop2 != null ? ltop2.hashCode() : 0);
        result = 31 * result + (holidayctrl != null ? holidayctrl.hashCode() : 0);
        return result;
    }
}

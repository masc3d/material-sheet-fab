package org.deku.leoz.node.data.entities.master;

import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.CacheIndexes;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.config.QueryHints;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_route",
        indexes = {@javax.persistence.Index(
                columnList = "layer, country, zipFrom, zipTo, validFrom, validTo",
                unique = false)})
// Preliminary optimization. Query result cache currently only seems to work with static
// parameterized named queries (eg. not with query dsl, as hints are added dynamically)
// as documented here. http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache008.htm
@NamedQueries(
        {
                @NamedQuery(name = "Route.find",
                query = "SELECT r FROM Route r WHERE r.layer = :layer AND r.country = :country " +
                        "AND r.zipFrom <= :zipFrom AND r.zipTo >= :zipTo AND r.validFrom < :time " +
                        "AND r.validTo > :time",
                hints = {
                        @QueryHint( name = QueryHints.QUERY_RESULTS_CACHE, value = "true"),
                        @QueryHint( name = QueryHints.QUERY_RESULTS_CACHE_SIZE, value = "500")
                })
        }
)

public class Route implements Serializable {
    private static final long serialVersionUID = 6472457478560400106L;

    private Long id;
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
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    @Basic
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Basic
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

    @Basic
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

    @Basic
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
    @Column(nullable = false)
    @Index
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

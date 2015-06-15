package org.deku.leo2.node.data.entities;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mas_country")
public class Country {
    private String lkz;
    private String lname;
    private Timestamp timestamp;
    private Integer routingTyp;
    private Integer minLen;
    private Integer maxLen;
    private String zipFormat;

    @Id
    public String getLkz() {
        return lkz;
    }

    public void setLkz(String lkz) {
        this.lkz = lkz;
    }

    @Basic
    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    @Basic
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    public Integer getRoutingTyp() {
        return routingTyp;
    }

    public void setRoutingTyp(Integer routingTyp) {
        this.routingTyp = routingTyp;
    }

    @Basic
    @Column(name = "MinLen")
    public Integer getMinLen() {
        return minLen;
    }

    public void setMinLen(Integer minLen) {
        this.minLen = minLen;
    }

    @Basic
    @Column(name = "MaxLen")
    public Integer getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(Integer maxLen) {
        this.maxLen = maxLen;
    }

    @Basic
    @Column(name = "ZipFormat")
    public String getZipFormat() {
        return zipFormat;
    }

    public void setZipFormat(String zipFormat) {
        this.zipFormat = zipFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (lkz != null ? !lkz.equals(country.lkz) : country.lkz != null) return false;
        if (lname != null ? !lname.equals(country.lname) : country.lname != null) return false;
        if (timestamp != null ? !timestamp.equals(country.timestamp) : country.timestamp != null) return false;
        if (routingTyp != null ? !routingTyp.equals(country.routingTyp) : country.routingTyp != null) return false;
        if (minLen != null ? !minLen.equals(country.minLen) : country.minLen != null) return false;
        if (maxLen != null ? !maxLen.equals(country.maxLen) : country.maxLen != null) return false;
        if (zipFormat != null ? !zipFormat.equals(country.zipFormat) : country.zipFormat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lkz != null ? lkz.hashCode() : 0;
        result = 31 * result + (lname != null ? lname.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (routingTyp != null ? routingTyp.hashCode() : 0);
        result = 31 * result + (minLen != null ? minLen.hashCode() : 0);
        result = 31 * result + (maxLen != null ? maxLen.hashCode() : 0);
        result = 31 * result + (zipFormat != null ? zipFormat.hashCode() : 0);
        return result;
    }
}

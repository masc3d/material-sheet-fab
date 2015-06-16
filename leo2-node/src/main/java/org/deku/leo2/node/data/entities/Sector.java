package org.deku.leo2.node.data.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@IdClass(SectorPK.class)
public class Sector implements Serializable {
    private static final long serialVersionUID = 5174752592128866406L;

    private String product;
    private String sectorFrom;
    private String sectorTo;
    private Timestamp validFrom;
    private Timestamp validTo;
    private String via;
    private Timestamp timestamp;

    @Id
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Id
    public String getSectorFrom() {
        return sectorFrom;
    }

    public void setSectorFrom(String sectorFrom) {
        this.sectorFrom = sectorFrom;
    }

    @Id
    public String getSectorTo() {
        return sectorTo;
    }

    public void setSectorTo(String sectorTo) {
        this.sectorTo = sectorTo;
    }

    @Id
    public Timestamp getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Timestamp validFrom) {
        this.validFrom = validFrom;
    }

    @Basic
    public Timestamp getValidTo() {
        return validTo;
    }

    public void setValidTo(Timestamp validTo) {
        this.validTo = validTo;
    }

    @Basic
    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    @Basic
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sector sector = (Sector) o;

        if (product != null ? !product.equals(sector.product) : sector.product != null) return false;
        if (sectorFrom != null ? !sectorFrom.equals(sector.sectorFrom) : sector.sectorFrom != null) return false;
        if (sectorTo != null ? !sectorTo.equals(sector.sectorTo) : sector.sectorTo != null) return false;
        if (validFrom != null ? !validFrom.equals(sector.validFrom) : sector.validFrom != null) return false;
        if (validTo != null ? !validTo.equals(sector.validTo) : sector.validTo != null) return false;
        if (via != null ? !via.equals(sector.via) : sector.via != null) return false;
        if (timestamp != null ? !timestamp.equals(sector.timestamp) : sector.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (sectorFrom != null ? sectorFrom.hashCode() : 0);
        result = 31 * result + (sectorTo != null ? sectorTo.hashCode() : 0);
        result = 31 * result + (validFrom != null ? validFrom.hashCode() : 0);
        result = 31 * result + (validTo != null ? validTo.hashCode() : 0);
        result = 31 * result + (via != null ? via.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}

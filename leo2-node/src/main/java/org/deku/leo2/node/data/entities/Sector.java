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
    private String sectorfrom;
    private String sectorto;
    private Timestamp validfrom;
    private Timestamp validto;
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
    public String getSectorfrom() {
        return sectorfrom;
    }

    public void setSectorfrom(String sectorfrom) {
        this.sectorfrom = sectorfrom;
    }

    @Id
    public String getSectorto() {
        return sectorto;
    }

    public void setSectorto(String sectorto) {
        this.sectorto = sectorto;
    }

    @Id
    public Timestamp getValidfrom() {
        return validfrom;
    }

    public void setValidfrom(Timestamp validfrom) {
        this.validfrom = validfrom;
    }

    @Basic
    public Timestamp getValidto() {
        return validto;
    }

    public void setValidto(Timestamp validto) {
        this.validto = validto;
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
        if (sectorfrom != null ? !sectorfrom.equals(sector.sectorfrom) : sector.sectorfrom != null) return false;
        if (sectorto != null ? !sectorto.equals(sector.sectorto) : sector.sectorto != null) return false;
        if (validfrom != null ? !validfrom.equals(sector.validfrom) : sector.validfrom != null) return false;
        if (validto != null ? !validto.equals(sector.validto) : sector.validto != null) return false;
        if (via != null ? !via.equals(sector.via) : sector.via != null) return false;
        if (timestamp != null ? !timestamp.equals(sector.timestamp) : sector.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (sectorfrom != null ? sectorfrom.hashCode() : 0);
        result = 31 * result + (sectorto != null ? sectorto.hashCode() : 0);
        result = 31 * result + (validfrom != null ? validfrom.hashCode() : 0);
        result = 31 * result + (validto != null ? validto.hashCode() : 0);
        result = 31 * result + (via != null ? via.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}

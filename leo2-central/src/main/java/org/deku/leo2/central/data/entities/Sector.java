package org.deku.leo2.central.data.entities;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@IdClass(SectorPK.class)
public class Sector {
    private String product;
    private String sectorfrom;
    private String sectorto;
    private Timestamp validfrom;
    private Timestamp validto;
    private String via;
    private Timestamp timestamp;

    @Id
    @Column(name = "product")
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Id
    @Column(name = "sectorfrom")
    public String getSectorfrom() {
        return sectorfrom;
    }

    public void setSectorfrom(String sectorfrom) {
        this.sectorfrom = sectorfrom;
    }

    @Id
    @Column(name = "sectorto")
    public String getSectorto() {
        return sectorto;
    }

    public void setSectorto(String sectorto) {
        this.sectorto = sectorto;
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
    @Column(name = "via")
    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    @Basic
    @Column(name = "timestamp")
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

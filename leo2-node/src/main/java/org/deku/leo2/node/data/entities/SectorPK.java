package org.deku.leo2.node.data.entities;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
public class SectorPK implements Serializable {
    private static final long serialVersionUID = -5461559970235583222L;

    private String product;

    @Id
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    private String sectorFrom;

    @Id
    public String getSectorFrom() {
        return sectorFrom;
    }

    public void setSectorFrom(String sectorFrom) {
        this.sectorFrom = sectorFrom;
    }

    private String sectorTo;

    @Id
    public String getSectorTo() {
        return sectorTo;
    }

    public void setSectorTo(String sectorTo) {
        this.sectorTo = sectorTo;
    }

    private Timestamp validFrom;

    @Id
    public Timestamp getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Timestamp validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectorPK sectorPK = (SectorPK) o;

        if (product != null ? !product.equals(sectorPK.product) : sectorPK.product != null) return false;
        if (sectorFrom != null ? !sectorFrom.equals(sectorPK.sectorFrom) : sectorPK.sectorFrom != null) return false;
        if (sectorTo != null ? !sectorTo.equals(sectorPK.sectorTo) : sectorPK.sectorTo != null) return false;
        if (validFrom != null ? !validFrom.equals(sectorPK.validFrom) : sectorPK.validFrom != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (sectorFrom != null ? sectorFrom.hashCode() : 0);
        result = 31 * result + (sectorTo != null ? sectorTo.hashCode() : 0);
        result = 31 * result + (validFrom != null ? validFrom.hashCode() : 0);
        return result;
    }
}

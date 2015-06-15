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

    private String sectorfrom;

    @Id
    public String getSectorfrom() {
        return sectorfrom;
    }

    public void setSectorfrom(String sectorfrom) {
        this.sectorfrom = sectorfrom;
    }

    private String sectorto;

    @Id
    public String getSectorto() {
        return sectorto;
    }

    public void setSectorto(String sectorto) {
        this.sectorto = sectorto;
    }

    private Timestamp validfrom;

    @Id
    public Timestamp getValidfrom() {
        return validfrom;
    }

    public void setValidfrom(Timestamp validfrom) {
        this.validfrom = validfrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectorPK sectorPK = (SectorPK) o;

        if (product != null ? !product.equals(sectorPK.product) : sectorPK.product != null) return false;
        if (sectorfrom != null ? !sectorfrom.equals(sectorPK.sectorfrom) : sectorPK.sectorfrom != null) return false;
        if (sectorto != null ? !sectorto.equals(sectorPK.sectorto) : sectorPK.sectorto != null) return false;
        if (validfrom != null ? !validfrom.equals(sectorPK.validfrom) : sectorPK.validfrom != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (sectorfrom != null ? sectorfrom.hashCode() : 0);
        result = 31 * result + (sectorto != null ? sectorto.hashCode() : 0);
        result = 31 * result + (validfrom != null ? validfrom.hashCode() : 0);
        return result;
    }
}

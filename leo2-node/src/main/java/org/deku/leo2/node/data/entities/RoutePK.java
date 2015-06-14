package org.deku.leo2.node.data.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
public class RoutePK implements Serializable {
    private static final long serialVersionUID = -334675901549944875L;

    private String product;

    @Id
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    private String lkz;

    @Id
    public String getLkz() {
        return lkz;
    }

    public void setLkz(String lkz) {
        this.lkz = lkz;
    }

    private String zip;

    @Id
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

        RoutePK routePK = (RoutePK) o;

        if (product != null ? !product.equals(routePK.product) : routePK.product != null) return false;
        if (lkz != null ? !lkz.equals(routePK.lkz) : routePK.lkz != null) return false;
        if (zip != null ? !zip.equals(routePK.zip) : routePK.zip != null) return false;
        if (validfrom != null ? !validfrom.equals(routePK.validfrom) : routePK.validfrom != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (lkz != null ? lkz.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (validfrom != null ? validfrom.hashCode() : 0);
        return result;
    }
}

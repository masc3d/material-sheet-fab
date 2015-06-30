package org.deku.leo2.node.data.entities.master;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
public class RoutePK implements Serializable {
    private static final long serialVersionUID = -1383941583614900890L;

    private Integer layer;
    private String country;
    private String zipFrom;
    private Integer validCRTR;
    private Timestamp validFrom;

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

    @Id
    public Integer getValidCRTR() {
        return validCRTR;
    }

    public void setValidCRTR(Integer validCRTR) {
        this.validCRTR = validCRTR;
    }

    @Id
    public Timestamp getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Timestamp validFrom) {
        this.validFrom = validFrom;
    }


    public RoutePK() {
    }

    public RoutePK(Integer layer, String country, String zipFrom, Integer validCRTR, Timestamp validFrom) {
        this.layer = layer;
        this.country = country;
        this.zipFrom = zipFrom;
        this.validCRTR = validCRTR;
        this.validFrom = validFrom;

    }


}

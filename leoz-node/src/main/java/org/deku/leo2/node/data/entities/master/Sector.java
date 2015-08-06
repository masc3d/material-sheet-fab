package org.deku.leo2.node.data.entities.master;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_sector")
@IdClass(SectorPK.class)
public class Sector implements Serializable {
    private static final long serialVersionUID = 421077640946522219L;

    private String sectorFrom;
    private String sectorTo;
    private Timestamp validFrom;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private Timestamp validTo;
    private String via;
    private Timestamp timestamp;

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
    @Column(nullable = false)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}

package org.deku.leoz.node.data.entities.master;

import sx.io.serialization.Serializable;

import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Serializable(uid = 0xd432b301532a4aL)
public class SectorPK implements java.io.Serializable {
    private static final long serialVersionUID = 0xd432b301532a4aL;

    private String sectorFrom;
    private String sectorTo;
    private Timestamp validFrom;

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

    public SectorPK() {
    }

    public SectorPK(String sectorFrom, String sectorTo, Timestamp validFrom) {
        this.sectorFrom = sectorFrom;
        this.sectorTo = sectorTo;
        this.validFrom = validFrom;
    }
}

package org.deku.leo2.node.data.entities;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
public class SectorPK implements Serializable {
    private static final long serialVersionUID = -5461559970235583222L;

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

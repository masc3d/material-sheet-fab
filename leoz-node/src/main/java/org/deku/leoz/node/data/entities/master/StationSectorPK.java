package org.deku.leoz.node.data.entities.master;

import sx.io.serialization.Serializable;

import javax.persistence.Id;

/**
 * Created by JT on 29.06.15.
 */
@Serializable(uid = 0x4a81cc447bdc43L)
public class StationSectorPK implements java.io.Serializable {
    private static final long serialVersionUID = 0x4a81cc447bdc43L;

    private int mStationNr;
    private String mSector;

    public StationSectorPK() {
    }

    @Id
    public Integer getStationNr() {
        return mStationNr;
    }

    public void setStationNr(Integer stationNr) {
        mStationNr = stationNr;
    }

    @Id
    public String getSector() {
        return mSector;
    }

    public void setSector(String sector) {
        mSector = sector;
    }

    public StationSectorPK(Integer stationNr, String sector) {
        mStationNr = stationNr;
        mSector = sector;
    }
}

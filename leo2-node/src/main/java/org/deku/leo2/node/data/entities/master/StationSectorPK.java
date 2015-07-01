package org.deku.leo2.node.data.entities.master;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by JT on 29.06.15.
 */
public class StationSectorPK implements Serializable {
    private static final long serialVersionUID = -5369887873791810474L;

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

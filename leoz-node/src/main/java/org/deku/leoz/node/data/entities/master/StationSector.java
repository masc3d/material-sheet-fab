package org.deku.leoz.node.data.entities.master;

import sx.io.serialization.Serializable;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by JT on 29.06.15.
 */
@Entity
@Table(name = "mst_station_sector")
@IdClass(StationSectorPK.class)
@Serializable(uid = 0x0d1eaebfd81899L )
public class StationSector {
    private Integer mStationNr;
    private String mSector;
    private int mRoutingLayer;
    private Timestamp mTimestamp;
    private Long mSyncId;

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

    @Basic
    public Integer getRoutingLayer() {
        return mRoutingLayer;
    }

    public void setRoutingLayer(Integer routingLayer) {
        mRoutingLayer = routingLayer;
    }

    @Basic
    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }

    @Basic
    @Column(nullable = false)
    public Long getSyncId() {
        return mSyncId;
    }

    public void setSyncId(Long syncId) {
        mSyncId = syncId;
    }
}


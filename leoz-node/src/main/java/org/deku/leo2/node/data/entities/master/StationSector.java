package org.deku.leo2.node.data.entities.master;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 29.06.15.
 */
@Entity
@Table(name = "mst_station_sector")
@IdClass(StationSectorPK.class)
public class StationSector implements Serializable {
    private static final long serialVersionUID = -7143786177620868528L;

    private Integer mStationNr;
    private String mSector;
    private int mRoutingLayer;
    private Timestamp mTimestamp;

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
}


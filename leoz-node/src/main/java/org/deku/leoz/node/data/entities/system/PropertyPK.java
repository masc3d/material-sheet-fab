package org.deku.leoz.node.data.entities.system;

import sx.io.serialization.Serializable;

import javax.persistence.Id;

/**
 * Created by JT on 29.06.15.
 */
@Serializable(uid = 0x72b4356618387dL)
public class PropertyPK implements java.io.Serializable {
    private static final long serialVersionUID = 0x72b4356618387dL;

    private Integer mId;
    private Integer mStation;

    @Id
    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    @Id
    public Integer getStation() {
        return mStation;
    }

    public void setStation(Integer station) {
        mStation = station;
    }

    public PropertyPK() {

    }

    public PropertyPK(Integer id, Integer station) {
        mId = id;
        mStation = station;
    }
}

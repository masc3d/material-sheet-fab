package org.deku.leoz.node.data.entities.system;


import sx.io.serialization.Serializable;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by JT on 29.06.15.
 */
@Entity
@Table(name = "sys_property")
@IdClass(PropertyPK.class)
@Serializable(uid = 0xaa946790064006L)
public class Property {
    private Integer mId;
    private Integer mStation;
    private String mDescription;
    private String mValue;
    private boolean mEnabled;
    private Timestamp mTimestamp;

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


    @Basic
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Basic
    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Basic
    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Basic
    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }
}

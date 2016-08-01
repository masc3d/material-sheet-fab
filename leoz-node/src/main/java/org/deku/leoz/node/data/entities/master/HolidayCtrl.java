package org.deku.leoz.node.data.entities.master;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_holidayctrl")
@IdClass(HolidayCtrlPK.class)
public class HolidayCtrl implements Serializable {
    private static final long serialVersionUID = -4009423696998516814L;

    private Timestamp mHoliday;
    private Integer mCtrlPos;
    private String mCountry;
    private String mDescription;
    private Timestamp mTimestamp;
    private Long mSyncId;

    @Id
    public Timestamp getHoliday() {
        return mHoliday;
    }

    public void setHoliday(Timestamp holiday) {
        this.mHoliday = holiday;
    }

    @Basic
    public Integer getCtrlPos() {
        return mCtrlPos;
    }

    public void setCtrlPos(Integer ctrlPos) {
        this.mCtrlPos = ctrlPos;
    }

    @Id
    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    @Basic
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    @Basic
    @Column(nullable = false)
    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.mTimestamp = timestamp;
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

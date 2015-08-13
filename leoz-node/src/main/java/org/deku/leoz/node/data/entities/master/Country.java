package org.deku.leoz.node.data.entities.master;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_country")
public class Country implements Serializable {
    private static final long serialVersionUID = -8829817367882199490L;

    private String mCode;
    private Integer mNameStringId;
    private Timestamp mTimestamp;
    private Integer mRoutingTyp;
    private Integer mMinLen;
    private Integer mMaxLen;
    private String mZipFormat;

    @Id
    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    @Basic
    @Column(nullable = false)
    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }

    @Basic
    public Integer getRoutingTyp() {
        return mRoutingTyp;
    }

    public void setRoutingTyp(Integer routingTyp) {
        mRoutingTyp = routingTyp;
    }

    @Basic
    public Integer getMinLen() {
        return mMinLen;
    }

    public void setMinLen(Integer minLen) {
        mMinLen = minLen;
    }

    @Basic
    public Integer getMaxLen() {
        return mMaxLen;
    }

    public void setMaxLen(Integer maxLen) {
        mMaxLen = maxLen;
    }

    @Basic
    public String getZipFormat() {
        return mZipFormat;
    }

    public void setZipFormat(String zipFormat) {
        mZipFormat = zipFormat;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Basic
    public Integer getNameStringId() {
        return mNameStringId;
    }

    public void setNameStringId(Integer nameStringId) {
        mNameStringId = nameStringId;
    }
}

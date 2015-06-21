package org.deku.leo2.node.data.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_country")
public class Country implements Serializable {
    private static final long serialVersionUID = 2361810454760700247L;

    private String mCode;
    private String mName;
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
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Basic
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

}

package org.deku.leo2.node.data.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mas_country")
public class Country implements Serializable {
    private static final long serialVersionUID = 2361810454760700247L;

    private String code;
    private String name;
    private Timestamp timestamp;
    private Integer routingTyp;
    private Integer minLen;
    private Integer maxLen;
    private String zipFormat;

    @Id
    public String getCode() {
        return code;
    }

    public void setCode(String zipCode) {
        this.code = code;
    }

    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.code = name;
    }

    @Basic
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    public Integer getRoutingTyp() {
        return routingTyp;
    }

    public void setRoutingTyp(Integer routingTyp) {
        this.routingTyp = routingTyp;
    }

    @Basic
    public Integer getMinLen() {
        return minLen;
    }

    public void setMinLen(Integer minLen) {
        this.minLen = minLen;
    }

    @Basic
    public Integer getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(Integer maxLen) {
        this.maxLen = maxLen;
    }

    @Basic
    public String getZipFormat() {
        return zipFormat;
    }

    public void setZipFormat(String zipFormat) {
        this.zipFormat = zipFormat;
    }

}

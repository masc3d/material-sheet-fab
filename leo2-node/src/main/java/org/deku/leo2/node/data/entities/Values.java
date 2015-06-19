package org.deku.leo2.node.data.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * Created by JT on 17.06.15.
 */
@Entity
@Table(name = "mst_values")
@IdClass(ValuesPK.class)
public class Values implements Serializable {
    private static final long serialVersionUID = 2361810454760700247L;

    private Timestamp Timestamp;
    private Integer Typ;
    private Integer Id;
    private Integer Sort;
    private String Description;
    private Integer P1i;
    private Integer P2i;
    private String P3s;
    private String P4s;


    public java.sql.Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {
        Timestamp = timestamp;
    }

    @Id
    public Integer getTyp() {
        return Typ;
    }

    public void setTyp(Integer typ) {
        this.Typ = typ;
    }

    @Id
    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    @Id
    public Integer getSort() {
        return Sort;
    }

    public void setSort(Integer sort) {
        Sort = sort;
    }

    @Basic
    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Basic
    public Integer getP1i() {
        return P1i;
    }

    public void setP1i(Integer p1i) {
        P1i = p1i;
    }

    @Basic
    public Integer getP2i() {
        return P2i;
    }

    public void setP2i(Integer p2i) {
        P2i = p2i;
    }

    @Basic
    public String getP3s() {
        return P3s;
    }

    public void setP3s(String p3s) {
        P3s = p3s;
    }

    @Basic
    public String getP4s() {
        return P4s;
    }

    public void setP4s(String p4s) {
        P4s = p4s;
    }


}

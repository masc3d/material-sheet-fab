package org.deku.leo2.node.data.entities;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 17.06.15.
 */
public class ValuesPK implements Serializable {

    private static final long serialVersionUID = -5461559970235583222L;
    private Integer Typ;
    private Integer Id;
    private Integer Sort;


    @Id
    public Integer getTyp() {
        return Typ;
    }

    public void setTyp(Integer typ) {
        Typ = typ;
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

    public ValuesPK() {
    }
    public  ValuesPK(Integer typ,Integer id,Integer sort ){
        this.Typ=typ;
        this.Id=id;
        this.Sort=sort;
    }

}

package org.deku.leoz.node.data.entities.system;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by JT on 29.06.15.
 */
public class PropertyPK implements Serializable {

    private static final long serialVersionUID = -9156533861595632384L;


    private Integer mId;
    private Integer mStation;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

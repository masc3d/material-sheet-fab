package org.deku.leo2.node.data.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by JT on 22.06.15.
 */
@Entity
@Table(name = "sys_routinglayer")
public class RoutingLayer implements Serializable {

    private static final long serialVersionUID = 3764621353532731477L;

    private Integer layer;
    private Integer services;
    private String description;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    @Id
    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    @Basic
    public Integer getServices() {
        return services;
    }

    public void setServices(Integer services) {
        this.services = services;
    }

    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

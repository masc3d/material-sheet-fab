package org.deku.leo2.node.data.entities.master;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 22.06.15.
 */
@Entity
@Table(name = "mst_routinglayer")
public class RoutingLayer implements Serializable {

    private static final long serialVersionUID = 3331634651828126770L;

    private Integer layer;
    private Integer services;
    private String description;
    private Timestamp timestamp;

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

    @Basic
    @Column(nullable = false)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

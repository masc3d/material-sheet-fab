package org.deku.leoz.node.data.entities.master;

import org.eclipse.persistence.annotations.CacheIndex;
import sx.io.serialization.Serializable;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by JT on 22.06.15.
 */
@Entity
@Table(name = "mst_routinglayer")
@Serializable(uid = 0x92ed6a2fc3f79fL)
public class RoutingLayer {
    private Integer layer;
    private Integer services;
    private String description;
    private Timestamp timestamp;
    private Long syncId;

    @Id
    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    @CacheIndex
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

    @Basic
    @Column(nullable = false)
    public Long getSyncId() {
        return syncId;
    }

    public void setSyncId(Long syncId) {
        this.syncId = syncId;
    }
}

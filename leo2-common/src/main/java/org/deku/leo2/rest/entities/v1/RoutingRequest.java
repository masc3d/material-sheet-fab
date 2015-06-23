package org.deku.leo2.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by masc on 23.06.15.
 */
public class RoutingRequest {
    private Float mWeight;
    private String mServices;
    private Participant mSender;
    private Participant mConsignee;

    public RoutingRequest() { }

    @ApiModelProperty(value="Services", position = 10, required = true)
    public void setServices(String services) {
        mServices = services;
    }

    public String getServices() {
        return mServices;
    }

    @ApiModelProperty(value="Weight", position = 20, required = true)
    public Float getWeight() {
        return mWeight;
    }

    public void setWeight(Float weight) {
        mWeight = weight;
    }

    @ApiModelProperty(value="Sender", position = 30, required = true)
    public Participant getSender() {
        return mSender;
    }

    public void setSender(Participant sender) {
        mSender = sender;
    }

    @ApiModelProperty(value="Consignee", position = 40, required = true)
    public Participant getConsignee() {
        return mConsignee;
    }

    public void setConsignee(Participant consignee) {
        mConsignee = consignee;
    }
}

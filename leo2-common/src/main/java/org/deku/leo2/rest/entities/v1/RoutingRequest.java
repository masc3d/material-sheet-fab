package org.deku.leo2.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leo2.rest.entities.ShortDate;

/**
 * Created by masc on 23.06.15.
 */
@ApiModel(description = "Routing request")
public class RoutingRequest {
    private Float mWeight;
    private Integer mServices;
    private requestParticipant mSender;
    private requestParticipant mConsignee;
    private ShortDate mSendDate;
    private ShortDate mDeliveryDate;

    public RoutingRequest() { }


    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 10, required = true, value = "Senddate", allowableValues = "2015-06-01")
    public ShortDate getSendDate() {
        return mSendDate;
    }

    public void setSendDate(ShortDate sendDate) {
        mSendDate = sendDate;
    }

    @ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Deliverydate", allowableValues = "2015-06-02")
    public ShortDate getDeliveryDate() {
        return mDeliveryDate;
    }

    public void setDeliveryDate(ShortDate deliveryDate) {
        mDeliveryDate = deliveryDate;
    }


    @ApiModelProperty(value="Sum of DeKu Servicvalues", position = 22, required = false)
    public void setServices(Integer services) {
        mServices = services;
    }

    public Integer getServices() {
        return mServices;
    }

    @ApiModelProperty(value="Real weight", position = 24, required = false)
    public Float getWeight() {
        return mWeight;
    }

    public void setWeight(Float weight) {
        mWeight = weight;
    }


    @ApiModelProperty(value="Sender", position = 30, required = false)
    public requestParticipant getSender() {
        return mSender;
    }

    public void setSender(requestParticipant sender) {
        mSender = sender;
    }

    @ApiModelProperty(value="Consignee", position = 40, required = false)
    public requestParticipant getConsignee() {
        return mConsignee;
    }

    public void setConsignee(requestParticipant consignee) {
        mConsignee = consignee;
    }


}

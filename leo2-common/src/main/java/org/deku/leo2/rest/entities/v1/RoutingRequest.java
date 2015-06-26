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
    private Participant mSender;
    private Participant mConsignee;
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
    public ShortDate getDeliverDate() {
        return mDeliveryDate;
    }

    public void setDeliverDate(ShortDate deliveryDate) {
        mDeliveryDate = deliveryDate;
    }

    @ApiModelProperty(value="Sender", position = 30, required = true)
    public Participant getSender() {
        return mSender;
    }

    public void setSender(Participant sender) {
        mSender = sender;
    }

    @ApiModelProperty(value="Consignee", position = 40, required = false)
    public Participant getConsignee() {
        return mConsignee;
    }

    public void setConsignee(Participant consignee) {
        mConsignee = consignee;
    }


    @ApiModelProperty(value="Sum of DeKu Servicvalues", position = 50, required = false)
    public void setServices(Integer services) {
        mServices = services;
    }

    public Integer getServices() {
        return mServices;
    }

    @ApiModelProperty(value="Real weight", position = 60, required = false)
    public Float getWeight() {
        return mWeight;
    }

    public void setWeight(Float weight) {
        mWeight = weight;
    }

}

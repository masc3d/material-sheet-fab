package org.deku.leo2.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;

/**
 * Routing service request response
 * Created by JT on 23.06.15.
 */

public class Routing {

    private RoutingParticipant mRoutingSender= new RoutingParticipant();
    private RoutingParticipant mRoutingConsignee= new RoutingParticipant();
    private String[] mViaHubs;
    private String mLabelContent="";
    private String mMessage="";
    private ShortDate mSendDate;
    private ShortDate mDeliveryDate;

    public Routing() {
    }

    public Routing(ShortDate sendDate,ShortDate deliveryDate, RoutingParticipant routingSender, RoutingParticipant routingConsignee, String[] viaHubs, String labelContent, String message) {
        mRoutingSender = routingSender;
        mRoutingConsignee = routingConsignee;
        mViaHubs = viaHubs;
        mLabelContent = labelContent;
        mMessage = message;
        mSendDate=sendDate;
        mDeliveryDate=deliveryDate;
    }

    public RoutingParticipant getRoutingSender() {
        return mRoutingSender;
    }

    public void setRoutingSender(RoutingParticipant routingSender) {
        mRoutingSender = routingSender;
    }

    public RoutingParticipant getRoutingConsignee() {
        return mRoutingConsignee;
    }

    public void setRoutingConsignee(RoutingParticipant routingConsignee) {
        mRoutingConsignee = routingConsignee;
    }

    @ApiModelProperty(dataType = "string", example = "01-1010", position = 10, required = true, value = "Zipcode contry conform: \"PL: 01-1010\"")
    public String[] getViaHubs() {
        return mViaHubs;
    }

    public void setViaHubs(String[] viaHubs) {
        mViaHubs = viaHubs;
    }

    @ApiModelProperty(dataType = "string", example = "01-1010", position = 10, required = true, value = "Zipcode contry conform: \"PL: 01-1010\"")
    public String getLabelContent() {
        return mLabelContent;
    }

    public void setLabelContent(String labelContent) {
        mLabelContent = labelContent;
    }

    @ApiModelProperty(dataType = "string", example = "01-1010", position = 10, required = true, value = "Zipcode contry conform: \"PL: 01-1010\"")
    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    @ApiModelProperty(dataType = "string", example = "01-1010", position = 10, required = true, value = "Zipcode contry conform: \"PL: 01-1010\"")
    public ShortDate getSendDate() {
        return mSendDate;
    }

    public void setSendDate(ShortDate sendDate) {
        mSendDate = sendDate;
    }

    @ApiModelProperty(dataType = "string", example = "01-1010", position = 10, required = true, value = "Zipcode contry conform: \"PL: 01-1010\"")
    public ShortDate getDeliveryDate() {
        return mDeliveryDate;
    }

    public void setDeliveryDate(ShortDate deliveryDate) {
        mDeliveryDate = deliveryDate;
    }
}

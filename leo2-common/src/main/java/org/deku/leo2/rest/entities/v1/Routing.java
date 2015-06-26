package org.deku.leo2.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;

/**
 * Routing service request response
 * Created by JT on 23.06.15.
 */

public class Routing {

    private RoutingParticipant mSender = new RoutingParticipant();
    private RoutingParticipant mConsignee = new RoutingParticipant();
    private String[] mViaHubs;
    private String mLabelContent = "";
    private String mMessage = "";
    private ShortDate mSendDate;
    private ShortDate mDeliveryDate;

    public Routing() {
    }

    public Routing(ShortDate sendDate, ShortDate deliveryDate, RoutingParticipant sender, RoutingParticipant consignee, String[] viaHubs,
                   String labelContent, String message) {

        mSender = sender;
        mConsignee = consignee;
        mViaHubs = viaHubs;
        mLabelContent = labelContent;
        mMessage = message;
        mSendDate = sendDate;
        mDeliveryDate = deliveryDate;
    }

    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 10, required = true, value = "Senddate", allowableValues = "2015-06-01")
    public ShortDate getSendDate() {
        return mSendDate;
    }

    public void setSendDate(ShortDate sendDate) {
        mSendDate = sendDate;
    }

    @ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Deliverydate", allowableValues = "00:00 - 23:59")
    public ShortDate getDeliveryDate() {
        return mDeliveryDate;
    }

    public void setDeliveryDate(ShortDate deliveryDate) {
        mDeliveryDate = deliveryDate;
    }

    @ApiModelProperty(position = 30)
    public RoutingParticipant getSender() {
        return mSender;
    }

    public void setSender(RoutingParticipant sender) {
        mSender = sender;
    }

    @ApiModelProperty(position = 40)
    public RoutingParticipant getConsignee() {
        return mConsignee;
    }

    public void setConsignee(RoutingParticipant consignee) {
        mConsignee = consignee;
    }

    @ApiModelProperty(dataType = "string", example = "F,N", position = 50, required = true, value = "Used via Hubs: \"F,N\"")
    public String[] getViaHubs() {
        return mViaHubs;
    }

    public void setViaHubs(String[] viaHubs) {
        mViaHubs = viaHubs;
    }

    @ApiModelProperty(dataType = "string", example = "F,N", position = 60, required = true, value = "Routingstring on Label: \"F,N\"")
    public String getLabelContent() {
        return mLabelContent;
    }

    public void setLabelContent(String labelContent) {
        mLabelContent = labelContent;
    }

    @ApiModelProperty(dataType = "string", example = "OK", position = 70, required = true, value = "Infomassage: \"OK\"")
    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

}

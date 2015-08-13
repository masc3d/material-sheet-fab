package org.deku.leoz.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leoz.rest.entities.ShortDate;

/**
 * Created by masc on 23.06.15.
 */
@ApiModel(value = "RoutingRequest", subTypes = {RoutingRequest.RequestParticipant.class}, description = "Routing request")
public class RoutingRequest {
    /**
     * Sender or consignee attributes
     * Created by masc on 23.06.15.
     */
    @ApiModel(value = "RequestParticipant", description = "Request Participant. Delivery or consignee")
    public static class RequestParticipant {
        //private ShortDate mDate;
        private String mTimeFrom;
        private String mTimeTo;
        private String mCountry;
        private String mZip;
        private String mDesireStation;

        public RequestParticipant() {
        }

        public RequestParticipant(String timeFrom, String timeTo, String country, String zip) {
            //mDate = date;
            mTimeFrom = timeFrom;
            mTimeTo = timeTo;
            mCountry = country;
            mZip = zip;
        }

        @ApiModelProperty(dataType = "string", example = "DE", value = "Country two-letter ISO-3166", position = 10, required = true)
        public String getCountry() {
            return mCountry;
        }

        public void setCountry(String country) {
            mCountry = country;
        }

        @ApiModelProperty(dataType = "string", example = "36286", value = "Zip code accordant to country spezification", position = 20, required = true)
        public String getZip() {
            return mZip;
        }

        public void setZip(String zip) {
            mZip = zip;
        }


        @ApiModelProperty(dataType = "string", example = "09:00", position = 40, required = false, value = "Time window (from)", allowableValues = "00:00 - 23:59")
        public String getTimeFrom() {
            return mTimeFrom;
        }

        public void setTimeFrom(String timefrom) {
            mTimeFrom = timefrom;
        }

        @ApiModelProperty(dataType = "string", example = "12:00", position = 50, required = false, value = "Time window (to)", allowableValues = "00:00 - 23:59")
        public String getTimeTo() {
            return mTimeTo;
        }

        public void setTimeTo(String timeto) {
            mTimeTo = timeto;
        }

        @ApiModelProperty(dataType = "string", example = "020", position = 60, required = false, value = "Desire Stationnumber", allowableValues = "010 - 999")
        public String getDesireStation() {
            return mDesireStation;
        }

        public void setDesireStation(String desireStation) {
            mDesireStation = desireStation;
        }
    }

    private Float mWeight;
    private Integer mServices;
    private RequestParticipant mSender;
    private RequestParticipant mConsignee;
    private ShortDate mSendDate;
    private ShortDate mDesireDeliveryDate;

    public RoutingRequest() {
    }


    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 10, required = true, value = "Senddate", allowableValues = "2015-06-01")
    public ShortDate getSendDate() {
        return mSendDate;
    }

    public void setSendDate(ShortDate sendDate) {
        mSendDate = sendDate;
    }

    @ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Desire Deliverydate", allowableValues = "2015-06-02")
    public ShortDate getDesireDeliveryDate() {
        return mDesireDeliveryDate;
    }

    public void setDesireDeliveryDate(ShortDate desireDeliveryDate) {
        mDesireDeliveryDate = desireDeliveryDate;
    }


    @ApiModelProperty(value = "Sum of DeKu Servicvalues", position = 22, required = false)
    public void setServices(Integer services) {
        mServices = services;
    }

    public Integer getServices() {
        return mServices;
    }

    @ApiModelProperty(value = "Real weight", position = 24, required = false)
    public Float getWeight() {
        return mWeight;
    }

    public void setWeight(Float weight) {
        mWeight = weight;
    }


    @ApiModelProperty(value = "Sender", position = 30, required = false)
    public RequestParticipant getSender() {
        return mSender;
    }

    public void setSender(RequestParticipant sender) {
        mSender = sender;
    }

    @ApiModelProperty(value = "Consignee", position = 40, required = false)
    public RequestParticipant getConsignee() {
        return mConsignee;
    }

    public void setConsignee(RequestParticipant consignee) {
        mConsignee = consignee;
    }


}

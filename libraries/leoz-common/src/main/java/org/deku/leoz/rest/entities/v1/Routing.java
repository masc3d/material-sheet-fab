package org.deku.leoz.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leoz.rest.entities.ShortDate;
import org.deku.leoz.rest.entities.ShortTime;
import java.time.LocalDate;


/**
 * Routing service request response
 * Created by JT on 23.06.15.
 */
@ApiModel(value = "Routing", subTypes = {Routing.Participant.class}, description = "Routing response")
public class Routing {
    /**
     * Routing service request response member
     * Created by JT on 23.06.15.
     */
    @ApiModel(value = "Participant", description = "Response Participant. Delivery or consignee")
    public static class Participant {
        private String mSector = "";
        private String mZone = "";
        private ShortTime mEarliestTimeOfDelivery = new ShortTime();
        private Integer mStation = 0;
        //private DayType mDayType = DayType.Workday;
        private String mDayType = DayType.Workday.toString();
        private Boolean mIsland = false;
        //private ShortDate mDelieveryDay = new ShortDate();
        //private ShortDate mPreviousDeliveryDay = new ShortDate();
        private String mZipCode = "";
        private Integer mTerm = 1;
        private ShortTime mSaturdayDeliveryUntil = new ShortTime();
        private ShortTime mSundayDeliveryUntil = new ShortTime();
        private ShortTime mPickupUntil = new ShortTime();
        private String mPartnerManager = "";
        private String mCountry = "";

        private LocalDate mDate;
        private String mMessage="";

        @ApiModelProperty(hidden = true)
        public LocalDate getDate() {
            return mDate;
        }

        public void setDate(LocalDate date) {
            this.mDate = date;
        }

        @ApiModelProperty(hidden = true)
        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }


        public Participant() {
        }

        //    public resultParticipant(String sector, String zone, ShortTime earliestTimeOfDelivery, Integer station, DayType daytype, Boolean island,
//                              String zipCode, Integer term, ShortTime saturdayDeliveryUntil, ShortTime sundayDeliveryUntil,
//                              ShortTime pickupUntil, String partnerManager, String country) {
//
//        mSector = sector;
//        mZone = zone;
//        mEarliestTimeOfDelivery = earliestTimeOfDelivery;
//        mStation = station;
//        mDayType = daytype;
//        mIsland = island;
//        mZipCode = zipCode;
//        mTerm = term;
//        mSaturdayDeliveryUntil = saturdayDeliveryUntil;
//        mSundayDeliveryUntil = sundayDeliveryUntil;
//        mPickupUntil = pickupUntil;
//        mPartnerManager = partnerManager;
//        mCountry = country;
//
//    }
//: "020""
        @ApiModelProperty(dataType = "string", example = "020", position = 10, required = true, value = "Stationnumber", allowableValues = "010 - 999")
        public String getStation() {
            return com.google.common.base.Strings.padStart(mStation.toString(), 3, '0');
        }

        public void setStation(Integer station) {
            mStation = station;
        }

        @ApiModelProperty(dataType = "string", example = "PL", position = 20, required = true, value = "Country two-letter ISO-3166")
        public String getCountry() {
            return mCountry;
        }

        public void setCountry(String country) {
            mCountry = country;
        }

        @ApiModelProperty(dataType = "string", example = "01-1010", position = 30, required = true, value = "Zipcode contry conform")
        public String getZipCode() {
            return mZipCode;
        }

        public void setZipCode(String ZipCode) {
            mZipCode = ZipCode;
        }

        @ApiModelProperty(dataType = "string", example = "WR", position = 40, required = true, value = "specify Zone", allowableValues = "A,B,C,D,WR,UL")
        public String getZone() {
            return mZone;
        }

        public void setZone(String zone) {
            mZone = zone;
        }

        @ApiModelProperty(dataType = "string", example = "X", position = 50, required = true, value = "specify Sector", allowableValues = "A-Z")
        public String getSector() {
            return mSector;
        }

        public void setSector(String sector) {
            mSector = sector;
        }

        @ApiModelProperty(dataType = "string", example = "Workday", position = 60, required = true, value = "Type of Day")
        public String getDayType() {
            return mDayType;
        }

        public void setDayType(String daytype) {
            mDayType = daytype;
        }

        @ApiModelProperty(example = "false", position = 70, required = true, value = "specify Islands")
        public Boolean getIsland() {
            return mIsland;
        }

        public void setIsland(Boolean island) {
            mIsland = island;
        }


        @ApiModelProperty(dataType = "integer", example = "1", position = 80, required = true, value = "termtime in days", allowableValues = ">=1")
        public Integer getTerm() {
            return mTerm;
        }

        public void setTerm(Integer term) {
            mTerm = term;
        }

        @ApiModelProperty(dataType = "string", example = "08:01", position = 90, required = true, value = "Earliest time of delivery", allowableValues = "00:00 - 23:59")
        public ShortTime getEarliestTimeOfDelivery() {
            return mEarliestTimeOfDelivery;
        }

        public void setEarliestTimeOfDelivery(ShortTime earliestTimeOfDelivery) {
            mEarliestTimeOfDelivery = earliestTimeOfDelivery;
        }


//    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 100, required = true, value = "delivery day regarding holidays and term: \"2015-06-01\"", allowableValues = "00:00 - 23:59")
//    public ShortDate getDelieveryDay() {
//        return mDelieveryDay;
//    }
//
//    public void setDelieveryDay(ShortDate delieveryDay) {
//        mDelieveryDay = delieveryDay;
//    }

//    @ApiModelProperty(hidden = true)
//    public ShortDate getPreviousDeliveryDay() {
//        return mPreviousDeliveryDay;
//    }
//
//    public void setPreviousDeliveryDay(ShortDate PreviousDeliveryDay) {
//        mPreviousDeliveryDay = PreviousDeliveryDay;
//    }


        @ApiModelProperty(dataType = "string", example = "12:00", position = 120, required = true, value = "delivery time until on Saturday", allowableValues = "00:00 - 23:59")
        public ShortTime getSaturdayDeliveryUntil() {
            return mSaturdayDeliveryUntil;
        }

        public void setSaturdayDeliveryUntil(ShortTime saturdayDeliveryUntil) {
            mSaturdayDeliveryUntil = saturdayDeliveryUntil;
        }

        @ApiModelProperty(dataType = "string", example = "12:00", position = 130, required = true, value = "delivery time until on Sunday", allowableValues = "00:00 - 23:59")
        public ShortTime getSundayDeliveryUntil() {
            return mSundayDeliveryUntil;
        }

        public void setSundayDeliveryUntil(ShortTime SundayDeliveryUntil) {
            mSundayDeliveryUntil = SundayDeliveryUntil;
        }

        @ApiModelProperty(dataType = "string", example = "16:00", position = 140, required = true, value = "pick up time until", allowableValues = "00:00 - 23:59")
        public ShortTime getPickupUntil() {
            return mPickupUntil;
        }

        public void setPickupUntil(ShortTime PickupUntil) {
            mPickupUntil = PickupUntil;
        }

        @ApiModelProperty(dataType = "string", example = "AH", position = 150, required = true, value = "Partner Manager")
        public String getPartnerManager() {
            return mPartnerManager;
        }

        public void setPartnerManager(String PartnerManager) {
            mPartnerManager = PartnerManager;
        }

    }

    private Routing.Participant mSender = new Participant();
    private Routing.Participant mConsignee = new Participant();
    private String[] mViaHubs;
    private String mLabelContent = "";
    private String mMessage = "";
    private ShortDate mSendDate;
    private ShortDate mDeliveryDate;

    public Routing() {
    }

    public Routing(ShortDate sendDate, ShortDate deliveryDate, Routing.Participant sender, Routing.Participant consignee, String[] viaHubs,
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

    @ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Deliverydate", allowableValues = "2015-06-01")
    public ShortDate getDeliveryDate() {
        return mDeliveryDate;
    }

    public void setDesiredDeliveryDate(ShortDate deliveryDate) {
        mDeliveryDate = deliveryDate;
    }

    @ApiModelProperty(position = 30)
    public Routing.Participant getSender() {
        return mSender;
    }

    public void setSender(Routing.Participant sender) {
        mSender = sender;
    }

    @ApiModelProperty(position = 40)
    public Routing.Participant getConsignee() {
        return mConsignee;
    }

    public void setConsignee(Routing.Participant consignee) {
        mConsignee = consignee;
    }

    @ApiModelProperty(dataType = "string", example = "F,N", position = 50, required = true, value = "Used via Hubs: \"F,N\"")
    public String[] getViaHubs() {
        return mViaHubs;
    }

    public void setViaHubs(String[] viaHubs) {
        mViaHubs = viaHubs;
    }

    @ApiModelProperty(dataType = "string", example = "F,N 100", position = 60, required = true, value = "Routingstring on Label: \"F,N 100\"")
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

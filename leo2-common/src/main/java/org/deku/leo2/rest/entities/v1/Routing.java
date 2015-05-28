package org.deku.leo2.rest.entities.v1;

import org.deku.leo2.rest.adapters.LocalDateParam;
import org.deku.leo2.rest.adapters.LocalTimeAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Routing service find response
 * Created by masc on 20.04.15.
 */
@XmlRootElement()
public class Routing {
    private String mSector;
    private String mZone;
    private LocalTime mEarliestTimeOfDelivery;
    private LocalTime mEarliestTimeOfDelivery2;
    private Integer mRouting;
    private HolidayType mHoliday;
    private Boolean mIsland;
    private LocalDate mNextDelieveryDay;
    private LocalDateParam mPreviousDeliveryDay;

    public Routing() {
    }

    public Routing(String sector, String zone, LocalTime earliestTimeOfDelivery, Integer routing, HolidayType holiday, Boolean island, LocalDate NextDelieveryDay, LocalDateParam mPreviousDeliveryDay) {
        mSector = sector;
        mZone = zone;
        mEarliestTimeOfDelivery = earliestTimeOfDelivery;
        mRouting = routing;
        mHoliday = holiday;
        mIsland = island;
    }

    public String getSector() {
        return mSector;
    }

    public void setSector(String sector) {
        mSector = sector;
    }

    public String getZone() {
        return mZone;
    }

    public void setZone(String zone) {
        mZone = zone;
    }

    @XmlJavaTypeAdapter(type = LocalTime.class, value = LocalTimeAdapter.class)
    public LocalTime getEarliestTimeOfDelivery() {
        return mEarliestTimeOfDelivery;
    }

    public void setEarliestTimeOfDelivery(LocalTime earliestTimeOfDelivery) {
        mEarliestTimeOfDelivery = earliestTimeOfDelivery;
    }

    public LocalTime getEarliestTimeOfDelivery2() {
        return mEarliestTimeOfDelivery2;
    }

    public void setEarliestTimeOfDelivery2(LocalTime earliestTimeOfDelivery2) {
        mEarliestTimeOfDelivery2 = earliestTimeOfDelivery2;
    }


    public Integer getRouting() {
        return mRouting;
    }

    public void setRouting(Integer routing) {
        mRouting = routing;
    }

    public HolidayType getHoliday() {
        return mHoliday;
    }

    public void setHoliday(HolidayType holiday) {
        mHoliday = holiday;
    }

    public Boolean getIsland() {
        return mIsland;
    }

    public void setIsland(Boolean island) {
        mIsland = island;
    }

    public LocalDate getNextDelieveryDay() {
        return mNextDelieveryDay;
    }

    public void setNextDelieveryDay(LocalDate NextDelieveryDay) {
        mNextDelieveryDay = NextDelieveryDay;
    }

    public LocalDateParam getPreviousDeliveryDay() {
        return mPreviousDeliveryDay;
    }

    public void setPreviousDeliveryDay(LocalDateParam PreviousDeliveryDay) {
        mPreviousDeliveryDay= PreviousDeliveryDay;
    }


}

package org.deku.leo2.rest.entities.v1;

import java.time.LocalTime;

/**
 * Created by masc on 20.04.15.
 */
public class Routing {
    private String mSector;
    private String mZone;
    private LocalTime mEarliestDelivery;
    private Integer mRouting;
    private HolidayType mHoliday;
    private Boolean mIsland;

    public Routing() {
    }

    public Routing(String sector, String zone, LocalTime earliestDelivery, Integer routing, HolidayType holiday, Boolean island) {
        mSector = sector;
        mZone = zone;
        mEarliestDelivery = earliestDelivery;
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

    public LocalTime getEarliestDelivery() {
        return mEarliestDelivery;
    }

    public void setEarliestDelivery(LocalTime earliestDelivery) {
        mEarliestDelivery = earliestDelivery;
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
}

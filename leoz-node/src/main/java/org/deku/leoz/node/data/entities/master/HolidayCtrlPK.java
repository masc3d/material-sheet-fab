package org.deku.leoz.node.data.entities.master;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
public class HolidayCtrlPK implements Serializable {
    private static final long serialVersionUID = 5299169800497336816L;

    private Timestamp mHoliday;
    private String mCountry;

    @Id
    public Timestamp getHoliday() {
        return mHoliday;
    }

    public void setHoliday(Timestamp holiday) {
        this.mHoliday = holiday;
    }

    @Id
    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public HolidayCtrlPK() {
    }

    public HolidayCtrlPK(Timestamp holiday, String country) {
        this.mHoliday = holiday;
        this.mCountry = country;
    }

}

package org.deku.leoz.node.data.entities.master;

import sx.io.serialization.Serializable;

import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Serializable(uid = 0x0bef5f538e4ed9L)
public class HolidayCtrlPK implements java.io.Serializable {
    private static final long serialVersionUID = 0x0bef5f538e4ed9L;

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

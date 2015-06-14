package org.deku.leo2.node.data.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
public class HolidayctrlPK implements Serializable {
    private Timestamp holiday;
    private String country;

    @Id
    public Timestamp getHoliday() {
        return holiday;
    }

    public void setHoliday(Timestamp holiday) {
        this.holiday = holiday;
    }

    @Id
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public HolidayctrlPK() {
    }

    public HolidayctrlPK(Timestamp holiday, String country) {
        this.holiday = holiday;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HolidayctrlPK that = (HolidayctrlPK) o;

        if (holiday != null ? !holiday.equals(that.holiday) : that.holiday != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = holiday != null ? holiday.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}

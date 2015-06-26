package org.deku.leo2.node.data.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_holidayctrl")
@IdClass(HolidayCtrlPK.class)
public class HolidayCtrl implements Serializable {
    private static final long serialVersionUID = -1785072859096492614L;

    private Timestamp holiday;
    private Integer ctrlPos;
    private String country;
    private String description;
    private Timestamp timestamp;

    @Id
    public Timestamp getHoliday() {
        return holiday;
    }

    public void setHoliday(Timestamp holiday) {
        this.holiday = holiday;
    }

    @Basic
    public Integer getCtrlPos() {
        return ctrlPos;
    }

    public void setCtrlPos(Integer ctrlPos) {
        this.ctrlPos = ctrlPos;
    }

    @Id
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HolidayCtrl that = (HolidayCtrl) o;

        if (holiday != null ? !holiday.equals(that.holiday) : that.holiday != null) return false;
        if (ctrlPos != null ? !ctrlPos.equals(that.ctrlPos) : that.ctrlPos != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = holiday != null ? holiday.hashCode() : 0;
        result = 31 * result + (ctrlPos != null ? ctrlPos.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}

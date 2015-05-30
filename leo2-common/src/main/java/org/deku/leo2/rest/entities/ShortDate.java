package org.deku.leo2.rest.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDate;

/**
 * LocalDate wrapper for rest operations, serializing date in short iso format (eg. "2015-01-01")
 * Created by masc on 21.04.15.
 */
@JsonSerialize(using = ToStringSerializer.class)
public class ShortDate {
    private LocalDate mLocalDate;

    public ShortDate(String localDate) {
        mLocalDate = LocalDate.parse(localDate);
    }
    public ShortDate(LocalDate localDate) {
        mLocalDate = localDate;
    }
    public ShortDate() {
        this(LocalDate.now());
    }

    public LocalDate getLocalDate() {
        return mLocalDate;
    }

    @Override
    public String toString() {
        return mLocalDate.toString();
    }
}

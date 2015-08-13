package org.deku.leoz.rest.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * LocalTime wrapper for rest operations, serializing to short time format (eg. "10:00")
 * Created by masc on 29.05.15.
 */
@JsonSerialize(using = ToStringSerializer.class)
public class ShortTime {
    private LocalTime mLocalTime;

    public ShortTime(String localTime) {
        mLocalTime = LocalTime.parse(localTime);
    }
    public ShortTime(LocalTime localTime) {
        mLocalTime = localTime;
    }
    public ShortTime() {
        this(LocalTime.now());
    }

    public LocalTime getLocalTime() {
        return mLocalTime;
    }

    @Override
    public String toString() {
        return mLocalTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    }
}

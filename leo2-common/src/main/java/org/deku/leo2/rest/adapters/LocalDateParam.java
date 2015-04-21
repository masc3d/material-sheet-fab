package org.deku.leo2.rest.adapters;

import java.time.LocalDate;

/**
 * jsr-310 LocalDate wrapper for passing LocalDate as a webservice parameters
 * Created by masc on 21.04.15.
 */
public class LocalDateParam {
    private LocalDate mLocalDate;

    public LocalDateParam(String localDate) {
        mLocalDate = LocalDate.parse(localDate);
    }
    public LocalDateParam(LocalDate localDate) {
        mLocalDate = localDate;
    }

    @Override
    public String toString() {
        return mLocalDate.toString();
    }
}

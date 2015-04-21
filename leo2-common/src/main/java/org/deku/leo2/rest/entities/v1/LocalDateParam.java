package org.deku.leo2.rest.entities.v1;

import java.time.LocalDate;

/**
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

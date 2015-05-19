package org.deku.leo2.central.data.repositories.custom;

import org.deku.leo2.central.data.entities.Station;

import java.util.List;

/**
 * Created by masc on 07.05.15.
 */
public interface StationRepositoryCustom {
    public List<Station> findWithQuery(String query);
}
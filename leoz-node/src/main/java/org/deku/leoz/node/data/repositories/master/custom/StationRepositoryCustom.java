package org.deku.leoz.node.data.repositories.master.custom;

import org.deku.leoz.node.data.entities.master.Station;

import java.util.List;

/**
 * Created by masc on 07.05.15.
 */
public interface StationRepositoryCustom {
    List<Station> findWithQuery(String query);
}
package org.deku.leo2.central.entities;

import java.util.List;

/**
 * Created by masc on 07.05.15.
 */
public interface DepotRepositoryCustom {
    public List<Depot> findWithQuery(String query);
}

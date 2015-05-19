package org.deku.leo2.central.data.repositories.custom;

import org.deku.leo2.central.data.entities.Route;
import org.deku.leo2.central.data.entities.RoutePK;

/**
 * Created by JT on 13.05.15.
 */
public interface RouteRepositoryCustom {

    public Route findActualRoute(RoutePK routePK);

}

package org.deku.leo2.central.data.repositories;

import org.deku.leo2.central.data.entities.Route;
import org.deku.leo2.central.data.entities.RoutePK;

/**
 * Created by JT on 13.05.15.
 */
public interface RouteRepositoryCostum {

    public Route findActualRoute(RoutePK routePK);

}

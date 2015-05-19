package org.deku.leo2.central.data.repositories;

import org.deku.leo2.central.data.entities.QRoute;
import org.deku.leo2.central.data.entities.Route;
import org.deku.leo2.central.data.entities.RoutePK;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by JT on 13.05.15.
 */
@Named
public class RouteRepositoryImpl implements RouteRepositoryCostum{

    @Inject
    RouteRepository mRouteRepository;

    @Transactional("jpa")
    public Route findActualRoute(RoutePK mroutePK)    {
        QRoute qRoute= QRoute.route;
        Iterable <Route> rRoute=mRouteRepository.findAll(
                qRoute.lkz.eq(mroutePK.getLkz())
                        .and(qRoute.zip.eq(mroutePK.getZip()))
                .and(qRoute.validfrom.goe(mroutePK.getValidfrom()))

        );

        Route routeFound=rRoute.iterator().next();

        return routeFound;
    }

}

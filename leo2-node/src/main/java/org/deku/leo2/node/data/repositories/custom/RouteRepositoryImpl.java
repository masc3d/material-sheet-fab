package org.deku.leo2.node.data.repositories.custom;

import org.deku.leo2.node.data.entities.QRoute;
import org.deku.leo2.node.data.entities.Route;
import org.deku.leo2.node.data.entities.RoutePK;
import org.deku.leo2.node.data.repositories.RouteRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by JT on 13.05.15.
 */
public class RouteRepositoryImpl implements RouteRepositoryCustom {
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

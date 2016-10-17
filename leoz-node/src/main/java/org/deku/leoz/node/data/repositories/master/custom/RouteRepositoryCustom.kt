package org.deku.leoz.node.data.repositories.master.custom

import org.deku.leoz.node.data.repositories.master.RouteRepository
import javax.inject.Inject

/**
 * Created by JT on 13.05.15.
 */
interface RouteRepositoryCustom

/**
 * Created by JT on 13.05.15.
 */
class RouteRepositoryImpl : RouteRepositoryCustom {
    @Inject
    internal var mRouteRepository: RouteRepository? = null
}

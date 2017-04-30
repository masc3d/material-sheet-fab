package org.deku.leoz.central.service.internal.v2

import org.deku.leoz.central.Application
import org.springframework.context.annotation.Profile
import sx.rs.auth.ApiKey
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 */
@Named
@ApiKey(false)
@Profile(Application.PROFILE_CENTRAL)
@Path("internal/v2/bundle")
class BundleService : org.deku.leoz.node.service.internal.v2.BundleService() { }
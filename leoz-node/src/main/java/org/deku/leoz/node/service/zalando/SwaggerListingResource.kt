package org.deku.leoz.node.service.zalando

import org.deku.leoz.node.rest.swagger.SwaggerListingResourceBase
import sx.rs.auth.ApiKey

import javax.inject.Named
import javax.ws.rs.Path

/**
 * Delivers swagger.json schema information
 * Created by masc on 20.05.15.
 */
@Named
@Path("/zalando")
class SwaggerListingResource : SwaggerListingResourceBase()

package org.deku.leoz.node.rest.service

import org.deku.leoz.node.rest.swagger.SwaggerListingResourceBase
import sx.rs.ApiKey

import javax.inject.Named
import javax.ws.rs.Path

/**
 * Delivers swagger.json schema information for public leoz APIs
 * Created by masc on 20.05.15.
 */
@Named
@ApiKey(false)
@Path("/")
class SwaggerListingResource : SwaggerListingResourceBase()

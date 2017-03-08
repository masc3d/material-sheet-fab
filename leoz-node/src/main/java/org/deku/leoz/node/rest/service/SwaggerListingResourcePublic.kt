package org.deku.leoz.node.rest.service

import org.deku.leoz.node.rest.swagger.SwaggerContextPublic
import org.deku.leoz.node.rest.swagger.SwaggerListingResource
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
class SwaggerListingResourcePublic : SwaggerListingResource(
        // Inject public swagger context
        SwaggerContextPublic)

package org.deku.leoz.node.service.internal

import org.springframework.stereotype.Component
import javax.ws.rs.Path

/**
 * Delivers swagger.json schema information
 * Created by masc on 20.05.15.
 */
@Component
@Path("/internal")
class SwaggerListingResource : org.deku.leoz.node.rest.swagger.SwaggerListingResourceBase()

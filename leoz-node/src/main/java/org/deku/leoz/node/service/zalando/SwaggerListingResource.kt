package org.deku.leoz.node.service.zalando

import org.deku.leoz.node.rest.swagger.SwaggerListingResourceBase
import org.springframework.stereotype.Component
import javax.ws.rs.Path

/**
 * Delivers swagger.json schema information
 * Created by masc on 20.05.15.
 */
@Component
@Path("/zalando")
class SwaggerListingResource : SwaggerListingResourceBase()

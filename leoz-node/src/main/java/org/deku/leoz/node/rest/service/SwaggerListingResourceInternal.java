package org.deku.leoz.node.rest.service;

import org.deku.leoz.node.rest.swagger.SwaggerContextInternal;
import org.deku.leoz.node.rest.swagger.SwaggerListingResource;
import sx.rs.ApiKey;

import javax.inject.Named;
import javax.ws.rs.Path;

/**
 * Delivers swagger.json schema information for internal leoz APIs
 * Created by masc on 20.05.15.
 */
@Named
@ApiKey(false)
@Path("/internal")
public class SwaggerListingResourceInternal extends SwaggerListingResource {
    public SwaggerListingResourceInternal() {
        // Inject internal swagger context
        super(SwaggerContextInternal.instance());
    }
}

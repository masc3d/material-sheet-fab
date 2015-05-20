package org.deku.leo2.central.rest.services;

import org.deku.leo2.central.rest.swagger.SwaggerContextInternal;
import org.deku.leo2.central.rest.swagger.SwaggerListingResource;

import javax.ws.rs.Path;

/**
 * Delivers swagger.json schema information for internal leo2 APIs
 * Created by masc on 20.05.15.
 */
@Path("/internal")
public class SwaggerListingResourceInternal extends SwaggerListingResource {
    public SwaggerListingResourceInternal() {
        // Inject internal swagger context
        super(SwaggerContextInternal.instance());
    }
}

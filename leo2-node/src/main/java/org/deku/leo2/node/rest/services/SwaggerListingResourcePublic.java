package org.deku.leo2.node.rest.services;

import org.deku.leo2.node.rest.swagger.SwaggerContextPublic;
import org.deku.leo2.node.rest.swagger.SwaggerListingResource;

import javax.ws.rs.Path;

/**
 * Delivers swagger.json schema information for public leo2 APIs
 * Created by masc on 20.05.15.
 */
@Path("/")
public class SwaggerListingResourcePublic extends SwaggerListingResource {
    public SwaggerListingResourcePublic() {
        // Inject public swagger context
        super(SwaggerContextPublic.instance());
    }
}

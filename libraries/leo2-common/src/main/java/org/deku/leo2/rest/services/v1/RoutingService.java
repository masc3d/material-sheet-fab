package org.deku.leo2.rest.services.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

//import org.deku.leo2.rest.entities.v1.RoutingVia;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Routing operations")
public interface RoutingService {
    @POST
    @Path("/request")
    @ApiOperation(value = "Determ routing and via")
    Routing request(@ApiParam(value = "Routing request") RoutingRequest routingRequest);
}

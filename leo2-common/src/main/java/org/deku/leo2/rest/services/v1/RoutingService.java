package org.deku.leo2.rest.services.v1;

import com.wordnik.swagger.annotations.*;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.v1.Participant;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingRequest;
//import org.deku.leo2.rest.entities.v1.RoutingVia;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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

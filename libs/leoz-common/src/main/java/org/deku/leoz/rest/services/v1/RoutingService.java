package org.deku.leoz.rest.services.v1;

import com.wordnik.swagger.annotations.*;
import org.deku.leoz.rest.entities.v1.Routing;
import org.deku.leoz.rest.entities.v1.RoutingRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

//import org.deku.leoz.rest.entities.v1.RoutingVia;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Routing operations")
public interface RoutingService {
    /**
     * Routing service specific error codes
     */
    enum ErrorCode {
        ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER(1000);

        private int mValue;
        ErrorCode(int value) {
            mValue = value;
        }
    }

    @POST
    @Path("/request")
    @ApiOperation(value = "Request routing information")
    @ApiResponses( {
            @ApiResponse(code = 400, message = "Bad request/parameter", response = Error.class)
    })
    Routing request(@ApiParam(value = "Routing request") RoutingRequest routingRequest);
}

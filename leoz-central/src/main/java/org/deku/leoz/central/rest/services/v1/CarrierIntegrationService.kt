package org.deku.leoz.central.rest.services.v1

import org.deku.leoz.rest.entity.zalando.v1.DeliveryOption
import sx.rs.ApiKey
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Created by 27694066 on 02.03.2017.
 */
@Named
@ApiKey(false)
@Path("v1/ldn")
class CarrierIntegrationService :  org.deku.leoz.rest.service.zalando.v1.CarrierIntegrationService {

    override fun requestDeliveryOption(source_address_country_code: String, source_address_city: String, source_address_zip_code: String, source_address_address_line: String, target_address_country_code: String, target_address_city: String, target_address_zip_code: String, target_address_address_line: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val response: DeliveryOption = DeliveryOption("1", "2", "3", "4", "5")
        Response.ok("test")
    }

}
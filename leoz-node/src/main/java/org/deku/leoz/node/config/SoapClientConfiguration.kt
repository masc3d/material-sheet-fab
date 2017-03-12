package org.deku.leoz.node.config

import org.deku.leoz.ws.blz.BLZService
import org.deku.leoz.ws.blz.BLZServicePortType
import org.deku.leoz.ws.gls.shipment.ShipmentProcessingPortType
import org.deku.leoz.ws.gls.shipment.ShipmentProcessingService
import org.deku.leoz.ws.gls.tracking.Tracking
import org.deku.leoz.ws.gls.tracking.Tracking_Service
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sx.annotationOfType
import sx.annotationOfTypeOrNull
import java.net.URL
import javax.transaction.NotSupportedException
import javax.xml.ws.WebServiceClient

/**
 * Created by masc on 12/03/2017.
 */
@Configuration
open class SoapClientConfiguration {
    /**
     * Tries to determine the embedded wsdl resource from a generated type annotated with {@link javax.xml.ws.WebServceClient}
     */
    private fun wsdlResourceFromType(type: Class<*>): URL {
        val wsdlLocation = type.annotationOfType(WebServiceClient::class.java).wsdlLocation
        val resourcePathStart = wsdlLocation.indexOf("wsdl")
        if (resourcePathStart < 0)
            throw IllegalArgumentException("WSDL resource path could not be determined for [${wsdlLocation}]")

        val resourceName = wsdlLocation.substring(resourcePathStart)
        return Thread.currentThread().contextClassLoader.getResource(resourceName)
            ?: throw IllegalArgumentException("WSDL resource [${resourceName}] could not be found")
    }

    @Bean
    open fun blzService(): BLZServicePortType {
        val wsdl = this.wsdlResourceFromType(BLZService::class.java)
        return BLZService(wsdl).blzServiceSOAP12PortHttp
    }

    @Bean
    open fun glsShipmentProcessingService(): ShipmentProcessingPortType {
        val wsdl = this.wsdlResourceFromType(ShipmentProcessingService::class.java)
        return ShipmentProcessingService(wsdl).shipmentProcessingPortTypePort
    }

    @Bean
    open fun glsTrackingService(): Tracking {
        val wsdl = this.wsdlResourceFromType(Tracking_Service::class.java)
        return Tracking_Service(wsdl).tracking
    }
}
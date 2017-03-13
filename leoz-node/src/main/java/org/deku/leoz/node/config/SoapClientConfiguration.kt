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
    @Bean
    open fun blzService(): BLZServicePortType {
        return BLZService().blzServiceSOAP12PortHttp
    }

    @Bean
    open fun glsShipmentProcessingService(): ShipmentProcessingPortType {
        return ShipmentProcessingService().shipmentProcessingPortTypePort
    }

    @Bean
    open fun glsTrackingService(): Tracking {
        return Tracking_Service().tracking
    }
}
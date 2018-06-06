package org.deku.leoz.node.config

import org.deku.leoz.ws.blz.BLZService
import org.deku.leoz.ws.blz.BLZServicePortType
import org.deku.leoz.ws.gls.shipment.ShipmentProcessingPortType
import org.deku.leoz.ws.gls.shipment.ShipmentProcessingService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.annotationOfType
import java.net.URI
import javax.jws.WebService
import javax.xml.ws.BindingProvider

/**
 * Created by masc on 12/03/2017.
 */
@Configuration
@Lazy(true)
class SoapClientConfiguration {
    /**
     * Common SOAP endpoint settings
     */
    class EndpointSettings {
        var namespace: String? = null
        var username: String? = null
        var password: String? = null
    }

    @get:ConfigurationProperties("remote.gls.soap.shipment-processing")
    @get:Bean
    val shipmentProcessingServiceSettings: EndpointSettings = EndpointSettings()

    @get:Bean
    val blzService: BLZServicePortType = BLZService().blzServiceSOAP12PortHttp

    /**
     * Updates endpoint settings of XML WS service
     * @param service Service/port instance
     * @param serviceType Service/port type
     * @param settings Endpoint settings
     */
    private fun <T> updateEndpoint(service: T, serviceType: Class<T>, settings: EndpointSettings) {
        val bindingProvider = service as BindingProvider

        val name = serviceType.annotationOfType(WebService::class.java).name
        val uri = URI.create(settings.namespace + "/").resolve(name)

        bindingProvider.requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, uri.toString())
        bindingProvider.requestContext.put(BindingProvider.USERNAME_PROPERTY, settings.username)
        bindingProvider.requestContext.put(BindingProvider.PASSWORD_PROPERTY, settings.password)
    }

    @get:Bean
    val glsShipmentProcessingService: ShipmentProcessingPortType
        get() {
            val service = ShipmentProcessingService().shipmentProcessingPortTypePort

            this.updateEndpoint(
                    service = service,
                    serviceType = ShipmentProcessingPortType::class.java,
                    settings = this.shipmentProcessingServiceSettings)

            return service
        }
}
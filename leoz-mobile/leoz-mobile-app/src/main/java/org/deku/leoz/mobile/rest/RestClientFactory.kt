package org.deku.leoz.mobile.rest

import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.deku.leoz.config.Rest
import org.deku.leoz.rest.RestClientFactory
import sx.rs.client.FeignClient
import sx.rs.client.RestClient
import java.net.URI

/**
 * Mobile REST client confuguration
 * Created by n3 on 15/02/2017.
 */
class RestClientFactory : RestClientFactory() {
    override fun create(baseUri: URI, ignoreSsl: Boolean, apiKey: String?): RestClient =
            FeignClient(
                    baseUri = baseUri,
                    ignoreSslCertificate = ignoreSsl,
                    headers = apiKey?.let { mapOf(Rest.API_KEY to apiKey) },
                    encoder = JacksonEncoder(),
                    decoder = JacksonDecoder())


}
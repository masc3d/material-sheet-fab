package org.deku.leoz.mobile

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.provider
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.deku.leoz.config.Rest
import org.deku.leoz.RestClientFactory
import org.deku.leoz.mobile.settings.RemoteSettings
import org.slf4j.LoggerFactory
import sx.rs.proxy.FeignClient
import sx.rs.proxy.RestClient
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
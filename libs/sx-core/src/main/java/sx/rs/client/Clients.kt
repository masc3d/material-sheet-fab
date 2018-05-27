package sx.rs.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider
import org.slf4j.LoggerFactory
import sx.rs.LoggingFilter
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.client.ClientRequestFilter
import javax.ws.rs.core.HttpHeaders

/**
 * Jersey client proxy implementation
 */
class JerseyClient(
        baseUri: URI,
        ignoreSslCertificate: Boolean = false) : RestClient(baseUri, ignoreSslCertificate) {

    private val client by lazy {
        var clientBuilder = JerseyClientBuilder()
                .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout.toMillis().toInt())
                .property(ClientProperties.READ_TIMEOUT, readTimeout.toMillis().toInt())

        if (ignoreSslCertificate) {
            clientBuilder = clientBuilder
                    .sslContext(ignoringCertificateSslContext)
                    .hostnameVerifier { _, _ -> true }
        }
        clientBuilder.build()
    }


    override fun <T> proxy(serviceClass: Class<T>): T {
        val webTarget = this.client.target(baseUri)
        return WebResourceFactory.newResource(serviceClass, webTarget)
    }
}

/**
 * RESTEasy client proxy implementation
 *
 * @param baseUri The base uri for all targets
 * @param objectMapper Jackson object mapper
 * @param ignoreSslCertificate Ignore ssl certificate (useful for test)
 */
class RestEasyClient(
        baseUri: URI,
        objectMapper: ObjectMapper = ObjectMapper(),
        connectionPoolSize: Int = 0,
        ignoreSslCertificate: Boolean = false
) :
        RestClient(baseUri, ignoreSslCertificate) {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    /** JWT token to include in header */
    var jwtToken: String? = null

    private val client by lazy {
        ResteasyClientBuilder()
                .connectionPoolSize(connectionPoolSize)
                .connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .also {
                    if (ignoreSslCertificate) {
                        it
                                .sslContext(ignoringCertificateSslContext)
                                .hostnameVerifier { _, _ -> true }
                    }
                }
                // masc20171116. it's mandatory to derive from ResteasyJackson2Provider so it's recognized as an override
                .register(object : ResteasyJackson2Provider() {}.also {
                    it.setMapper(objectMapper)
                })
                .register(object : ClientRequestFilter {
                    override fun filter(requestContext: ClientRequestContext) {
                        if (this@RestEasyClient.jwtToken != null)
                            requestContext.headers.add(HttpHeaders.AUTHORIZATION, "JWT ${this@RestEasyClient.jwtToken}")
                    }
                })
                .register(LoggingFilter())
                .build()
    }

    /**
     * Create service proxy
     */
    override fun <T> proxy(serviceClass: Class<T>): T {
        return this.proxy(
                serviceClass = serviceClass,
                path = null)
    }

    fun createUri(path: String? = null): URI =
            if (path != null)
                this.baseUri.resolve(path)
            else
                this.baseUri

    /**
     * Create target for consumers which require connection pooling on customized requests
     *
     * @param path Optional path override
     * @param jwtToken Optional jwt token override
     */
    private fun target(path: String? = null, jwtToken: () -> String? = { null }): ResteasyWebTarget {
        val webTarget: ClientWebTarget = this.client.target(
                this.createUri(path)
        ) as ClientWebTarget

        return webTarget
                .register(object : ClientRequestFilter {
                    override fun filter(requestContext: ClientRequestContext) {
                        jwtToken()?.also {
                            requestContext.headers.add("Authorization", "JWT ${it}")
                        }
                    }
                })
    }

    /**
     * Create proxy for consumers which require connection pooling on customized requests
     *
     * @param serviceClass Service class
     * @param path Optional path override
     * @param jwtToken Optional jwt token override
     */
    fun <T> proxy(serviceClass: Class<T>, path: String? = null, jwtToken: () -> String? = { null }): T {
        return this.target(
                path = path,
                jwtToken = jwtToken
        )
                .proxyBuilder(serviceClass)
                // return webTarget.proxy(serviceClass)
                // masc20170329. for compatibility with spring-boot-devtools restart, need to use the serviceclass' classloader
                // to avoid `IllegalArgumentException` `is not visible from class loader`
                .classloader(serviceClass.classLoader)
                .build()
    }
}

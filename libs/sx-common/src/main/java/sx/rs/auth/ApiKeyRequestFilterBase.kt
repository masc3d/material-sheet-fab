package sx.rs.auth

import sx.reflect.allInterfaces
import java.io.IOException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

/**
 * JAX RS request filter for api key authorization
 * Created by masc on 23.06.15.
 */
abstract class ApiKeyRequestFilterBase(
        val apiKeyParameterName: String = "x-api-key")
    : ContainerRequestFilter {

    @Context
    private lateinit var resourceInfo: ResourceInfo

    /**
     * Verify api key
     */
    protected abstract fun verify(
            requestContext: ContainerRequestContext,
            resourceInfo: ResourceInfo,
            apiKey: String
    ): Boolean

    @Throws(IOException::class)
    override fun filter(requestContext: ContainerRequestContext) {
        // Don't verify by default (eg. annotation is missing)
        var verifyApiKey = false

        // ApiKey annotation on interface level (first strike)
        resourceInfo.resourceClass.allInterfaces.map { it.getAnnotation(ApiKey::class.java) }.firstOrNull()?.also {
            verifyApiKey = it.value
        }

        // ApiKey annotation on method level (overrides class level)
        resourceInfo.resourceMethod.getAnnotation(ApiKey::class.java)?.also {
            verifyApiKey = it.value
        }

        if (verifyApiKey) {
            val apiKey =
                    // Try query params, then headers
                    requestContext.uriInfo.queryParameters.getFirst(this.apiKeyParameterName)
                            ?:
                            // Then headers
                            requestContext.headers.getFirst(this.apiKeyParameterName)

            if (apiKey == null) {
                throw WebApplicationException("Not authorized, missing api key", Response.Status.UNAUTHORIZED)
            }
            if (!this.verify(requestContext, resourceInfo, apiKey)) {
                throw WebApplicationException("Not authorized, invalid api key", Response.Status.UNAUTHORIZED)
            }
        }
    }
}

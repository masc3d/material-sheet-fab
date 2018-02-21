package org.deku.leoz.node.rest

import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.node.Application
import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.service.internal.UserService
import org.springframework.context.annotation.Profile
import sx.reflect.allInterfaces
import sx.rs.RestProblem
import sx.rs.auth.ApiKeyRequestFilterBase
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
/**
 * Global leoz API key request filter
 * Created by masc on 08.07.15.
 */
@Profile(Application.PROFILE_CLIENT_NODE)
@Named
@Provider
open class ApiKeyRequestFilter : ApiKeyRequestFilterBase(
        apiKeyParameterName = Rest.API_KEY) {

    @Context
    private lateinit var httpRequest: HttpServletRequest

    companion object {
        const val REQUEST_AUTHORIZED_USER = "AUTHORIZED_USER"
    }

    /**
     * Returns either method or class level annotation
     * @param a Annotation type
     */
    private fun <A : Annotation> ResourceInfo.annotationOfType(a: Class<A>): A? {
        return this.resourceMethod.getAnnotation(a)
                ?: this.resourceClass.allInterfaces.map { it.getAnnotation(a) }.firstOrNull()
    }

    override fun verify(resourceInfo: ResourceInfo, apiKey: String): Boolean {
        // TODO: implement api key support for nodes
        return true
    }
}

/**
 * Authorized user
 * @throws WebApplicationException with Status.UNAUTHORIZED when there's no authorized user
 */
val HttpServletRequest.authorizedUser: UserService.User
    get() = this.getAttribute(ApiKeyRequestFilter.REQUEST_AUTHORIZED_USER) as UserService.User?
            ?: throw WebApplicationException(Response.Status.UNAUTHORIZED)

/**
 * Restrict access by debitor
 * @param debitorId The debitor id to check against
 * @throws WebApplicationException If authorized user's debitor id doesn't match
 */
fun HttpServletRequest.restrictByDebitor(debitorId: () -> Int?) {
    val user = this.authorizedUser
    val userRole = user.role

    if (userRole == null || UserRole.valueOf(userRole) != UserRole.ADMIN) {
        if (this.authorizedUser.debitorId != debitorId.invoke())
            throw WebApplicationException(Response.Status.FORBIDDEN)
    }
}
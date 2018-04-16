package org.deku.leoz.node.rest

import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.node.Application
import org.deku.leoz.node.data.repository.StationRepository
import org.deku.leoz.node.data.repository.UserRepository
import org.deku.leoz.node.data.repository.toUser
import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.rest.RestrictStation
import org.deku.leoz.service.internal.UserService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.reflect.allInterfaces
import sx.rs.RestProblem
import sx.rs.annotatedParametersOfType
import sx.rs.annotationOfType
import sx.rs.auth.ApiKeyRequestFilterBase
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

/**
 * Global leoz API key request filter
 * Created by masc on 08.07.15.
 */
@Profile(Application.PROFILE_CLIENT_NODE)
@Component
@Provider
class ApiKeyRequestFilter : ApiKeyRequestFilterBase(
        apiKeyParameterName = Rest.API_KEY) {

    @Context
    private lateinit var httpRequest: HttpServletRequest

    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var stationRepository: StationRepository

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

    override fun verify(requestContext: ContainerRequestContext, resourceInfo: ResourceInfo, apiKey: String): Boolean {
        // TODO: implement api key support for nodes
        val user=this.userRepository.findByKey(apiKey)
                ?.toUser()?.also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(x.id!!.toLong()) }
        val userRole = user?.role
        // Store authorized user domain object on http request level for consumers (eg. services)
        this.httpRequest.setAttribute(REQUEST_AUTHORIZED_USER, user)
        if (user != null) {
            // Check for user activity & expiry
            if (user.active != true) {
                throw RestProblem(
                        title = "User [${user.id}] has been deactivated",
                        status = Response.Status.UNAUTHORIZED)
            }
            if (Date() > user.expiresOn) {
                throw RestProblem(
                        title = "User [${user.id}] expired on ${user.expiresOn}",
                        status = Response.Status.UNAUTHORIZED)
            }
        }
        // Check role restrictions
        resourceInfo.annotationOfType(RestrictRoles::class.java)
                ?.also {
                    if (userRole == null || !it.role.contains(UserRole.valueOf(userRole)))
                        return false
                }

        resourceInfo.annotatedParametersOfType(RestrictStation::class.java, requestContext)
                .firstOrNull()
                ?.also { ap ->
                    if (user == null)
                        return false
                    if (!user.allowedStations!!.contains(ap.value!!.toInt()))
                        return false
                }

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
 * Indicates if http request is valid / available
 */
val HttpServletRequest.isAvailable: Boolean
    get() = try { this.requestURI != null } catch(t: Throwable) { false }

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
package org.deku.leoz.central.rest

import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.deku.leoz.central.data.repository.toUser
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.service.internal.UserService
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
@Named
@Provider
class ApiKeyRequestFilter : ApiKeyRequestFilterBase(
        apiKeyParameterName = Rest.API_KEY) {

    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    @Inject
    private lateinit var userJooqRepository: JooqUserRepository

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
        val user = this.userJooqRepository.findByKey(apiKey)
                ?.toUser()

        val userRole = user?.role

        // Set authorized user on request level
        this.httpRequest.setAttribute(REQUEST_AUTHORIZED_USER, user)

        if (user != null) {
            if (!(user.active ?: false)) {
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

        // Check for node key
        if (user == null)
            return nodeJooqRepository.hasAuthorizedKey(apiKey)

        // All checks passed, permission granted
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
fun HttpServletRequest.restrictByDebitor(debitorId: () -> Int) {
    val user = this.authorizedUser
    val userRole = user.role

    if (userRole == null || UserRole.valueOf(userRole) != UserRole.ADMIN) {
        if (this.authorizedUser.debitorId != debitorId.invoke())
            throw WebApplicationException(Response.Status.FORBIDDEN)
    }
}
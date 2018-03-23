package org.deku.leoz.central.rest

import org.deku.leoz.central.Application
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.deku.leoz.central.data.repository.toUser
import org.deku.leoz.model.UserRole
import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.rest.RestrictStation
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.rs.RestProblem
import sx.rs.annotatedParametersOfType
import sx.rs.annotationOfType
import java.lang.reflect.Parameter
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

/**
 * Global leoz API key request filter
 * Created by masc on 08.07.15.
 */
@Profile(Application.PROFILE_CENTRAL)
@Component
@Provider
class ApiKeyRequestFilter : org.deku.leoz.node.rest.ApiKeyRequestFilter() {
    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    @Inject
    private lateinit var userJooqRepository: JooqUserRepository

    @Context
    private lateinit var httpRequest: HttpServletRequest

    /** Annotated parameter */
    private data class AnnotatedParameter<A : Annotation>(
            val parameter: Parameter,
            val annotation: A,
            val value: String?
    )

    override fun verify(requestContext: ContainerRequestContext, resourceInfo: ResourceInfo, apiKey: String): Boolean {
        val user = this.userJooqRepository.findByKey(apiKey)
                ?.toUser()?.also { x -> x.allowedStations = this.userJooqRepository.findAllowedStationsByUserId(x.id!!) }

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

        if (user == null) {
            // Fallback to node api key check
            return nodeJooqRepository.hasAuthorizedKey(apiKey)
        }

        // All checks passed, permission granted
        return true
    }
}

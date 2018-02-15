package org.deku.leoz.central.rest

import org.deku.leoz.central.data.repository.JooqKeyRepository
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.rest.RestrictRoles
import sx.annotationOfType
import sx.reflect.allInterfaces
import sx.rs.auth.ApiKey
import sx.rs.auth.ApiKeyRequestFilterBase

import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.container.ResourceInfo
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

        // Check role restrictions
        resourceInfo.annotationOfType(RestrictRoles::class.java)
                ?.also {
                    if (user == null || !it.role.contains(UserRole.valueOf(user.role)))
                        return false
                }

        // Check for node key
        if (user == null)
            return nodeJooqRepository.hasAuthorizedKey(apiKey)

        // All checks passed, permission granted
        return true
    }
}

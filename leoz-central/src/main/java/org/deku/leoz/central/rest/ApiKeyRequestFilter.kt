package org.deku.leoz.central.rest

import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.config.RestConfiguration
import sx.rs.auth.ApiKeyRequestFilterBase

import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.ext.Provider

/**
 * Global leoz API key request filter
 * Created by masc on 08.07.15.
 */
@Named
@Provider
class ApiKeyRequestFilter : ApiKeyRequestFilterBase(
        apiKeyParameterName = RestConfiguration.AUTH_APIKEY_NAME) {

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Inject
    private lateinit var userJooqRepository: UserJooqRepository

    override fun verify(apiKey: String): Boolean {
        return nodeJooqRepository.hasAuthorizedKey(apiKey) || userJooqRepository.hasAuthorizedKey(apiKey)
    }
}

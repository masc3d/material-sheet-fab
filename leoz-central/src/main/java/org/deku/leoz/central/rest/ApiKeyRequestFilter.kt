package org.deku.leoz.central.rest

import org.deku.leoz.central.data.repository.KeyJooqRepository
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.config.Rest
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
        apiKeyParameterName = Rest.API_KEY) {

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Inject
    private lateinit var userJooqRepository: UserJooqRepository

    @Inject
    private  lateinit  var keyJooqRepository: KeyJooqRepository

    override fun verify(apiKey: String): Boolean {
        return nodeJooqRepository.hasAuthorizedKey(apiKey)
                || keyJooqRepository.findValidByKey(apiKey)
        // || userJooqRepository.hasAuthorizedKey(apiKey)
    }
}

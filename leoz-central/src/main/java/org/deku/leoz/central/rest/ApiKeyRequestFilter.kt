package org.deku.leoz.central.rest

import org.deku.leoz.central.data.repository.JooqKeyRepository
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
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
    private lateinit var nodeJooqRepository: JooqNodeRepository

    @Inject
    private lateinit var userJooqRepository: JooqUserRepository

    @Inject
    private  lateinit  var keyJooqRepository: JooqKeyRepository

    override fun verify(apiKey: String): Boolean {
        return nodeJooqRepository.hasAuthorizedKey(apiKey)
                || keyJooqRepository.findValidByKey(apiKey)
        // || userJooqRepository.hasAuthorizedKey(apiKey)
    }
}

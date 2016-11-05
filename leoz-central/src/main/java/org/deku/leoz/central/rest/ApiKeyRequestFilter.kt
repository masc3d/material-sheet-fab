package org.deku.leoz.central.rest

import org.deku.leoz.central.data.repository.NodeJooqRepository
import sx.rs.ApiKeyRequestFilterBase

import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.ext.Provider

/**
 * Created by masc on 08.07.15.
 */
@Named
@Provider
class ApiKeyRequestFilter : ApiKeyRequestFilterBase() {
    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    override fun verify(apiKey: String): Boolean {
        return nodeJooqRepository.hasAuthorizedKey(apiKey)
    }
}

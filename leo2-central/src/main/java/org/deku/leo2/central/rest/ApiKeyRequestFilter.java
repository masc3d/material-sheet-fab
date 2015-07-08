package org.deku.leo2.central.rest;

import org.deku.leo2.central.data.repositories.NodeRepository;
import sx.rs.ApiKeyRequestFilterBase;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ext.Provider;

/**
 * Created by masc on 08.07.15.
 */
@Named
@Provider
public class ApiKeyRequestFilter extends ApiKeyRequestFilterBase {
    @Inject
    NodeRepository mNodeRepository;

    @Override
    protected boolean verify(String apiKey) {
        return mNodeRepository.hasAuthorizedKey(apiKey);
    }
}

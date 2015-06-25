package org.deku.leo2.node.rest;

import org.h2.server.web.WebApp;

import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * JAX RS request filter for api key authorization
 * Created by masc on 23.06.15.
 */
@Named
@Provider
public class ApiKeyRequestFilter implements ContainerRequestFilter {
    private static final String API_KEY_PARAM = "api_key";

    @Context
    ResourceInfo mResourceInfo;

    private ApiKeyVerifier mApiKeyVerifier;

    public ApiKeyRequestFilter() {
        // TODO: for preliminary testing of request filter
//        mApiKeyVerifier = new ApiKeyVerifier() {
//            @Override
//            public boolean verify(String apiKey) {
//                return false;
//            }
//        };
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (mApiKeyVerifier != null) {
            boolean verifyApiKey = true;
            // Check for ApiKey annotation
            if (mResourceInfo.getResourceClass().isAnnotationPresent(ApiKey.class)) {
                ApiKey ak = mResourceInfo.getResourceClass().getAnnotation(ApiKey.class);
                verifyApiKey = ak.value();
            }

            if (verifyApiKey) {
                String apiKey = null;
                // Try query params
                apiKey = requestContext.getUriInfo().getQueryParameters().getFirst(API_KEY_PARAM);
                if (apiKey == null) {
                    apiKey = requestContext.getHeaders().getFirst(API_KEY_PARAM);
                }
                if (apiKey == null) {
                    throw new WebApplicationException("Not authorized", Response.Status.UNAUTHORIZED);
                }
            }
        }
    }

    public ApiKeyVerifier getApiKeyVerifier() {
        return mApiKeyVerifier;
    }

    public void setApiKeyVerifier(ApiKeyVerifier apiKeyVerifier) {
        mApiKeyVerifier = apiKeyVerifier;
    }
}

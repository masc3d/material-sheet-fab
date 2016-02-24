package org.deku.leoz.node.rest.swagger;

import io.swagger.config.Scanner;
import io.swagger.models.Swagger;

/**
 * Context of swagger used in @link SwaggerListingResource
 * Created by masc on 20.05.15.
 */
public interface SwaggerContext {
    Swagger getSwagger();
    Scanner getScanner();
}

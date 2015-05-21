package org.deku.leo2.node.rest.swagger;

import com.wordnik.swagger.config.Scanner;
import com.wordnik.swagger.models.Swagger;

/**
 * Context of swagger used in @link SwaggerListingResource
 * Created by masc on 20.05.15.
 */
public interface SwaggerContext {
    Swagger getSwagger();
    Scanner getScanner();
}

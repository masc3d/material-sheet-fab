package org.deku.leo2.central.rest.swagger;

import com.wordnik.swagger.config.Scanner;
import com.wordnik.swagger.models.Swagger;
import sx.LazyInstance;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Context of swagger used in @link SwaggerListingResource
 * Created by masc on 20.05.15.
 */
public interface SwaggerContext {
    Swagger getSwagger();
    Scanner getScanner();
}

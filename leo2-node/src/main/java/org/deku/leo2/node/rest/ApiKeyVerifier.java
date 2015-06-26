package org.deku.leo2.node.rest;

/**
 * Created by masc on 25.06.15.
 */
public interface ApiKeyVerifier {
    /**
     * Verify api key
      * @param apiKey Api key to verify
     * @return Validity
     */
    boolean verify(String apiKey);
}

package org.deku.leoz.bridge;

/**
 * Created by masc on 27.09.14.
 */
public class MediaType {
    /**
     * JSON UTF-8 media/mime type.
     * Required for compatibility with WCF clients/hosts
     */
    public static final String APPLICATION_JSON_UTF8 = javax.ws.rs.core.MediaType.APPLICATION_JSON + ";charset=utf-8";
}

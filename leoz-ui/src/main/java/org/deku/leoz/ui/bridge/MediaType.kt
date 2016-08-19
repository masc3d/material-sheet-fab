package org.deku.leoz.ui.bridge

/**
 * Created by masc on 27.09.14.
 */
object MediaType {
    /**
     * JSON UTF-8 media/mime type.
     * Required for compatibility with WCF clients/hosts
     */
    const val APPLICATION_JSON_UTF8 = javax.ws.rs.core.MediaType.APPLICATION_JSON + ";charset=utf-8"
}

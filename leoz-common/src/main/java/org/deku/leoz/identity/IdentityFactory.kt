package org.deku.leoz.identity

/**
 * Identity factory interface
 * Created by masc
 */
abstract class IdentityFactory(
        val name: String
) {
    abstract fun create(): Identity
}
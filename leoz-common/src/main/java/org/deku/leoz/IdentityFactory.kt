package org.deku.leoz

/**
 * Identity factory interface
 * Created by masc
 */
abstract class IdentityFactory(
        val name: String
) {
    abstract fun create(): Identity
}
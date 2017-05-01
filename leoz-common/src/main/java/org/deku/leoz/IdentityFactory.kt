package org.deku.leoz

/**
 * Created by masc on 01.05.17.
 */
/**
 * Identity factory interface
 * Created by masc
 */
abstract class IdentityFactory(
        val name: String
) {
    abstract fun create(): Identity
}
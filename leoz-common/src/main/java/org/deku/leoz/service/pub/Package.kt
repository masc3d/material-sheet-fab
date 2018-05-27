package org.deku.leoz.service.pub

/**
 * Package marker. Used for
 * Created by masc on 02.10.15.
 */
class Package {
    companion object {
        @JvmStatic val name = Package::class.java.`package`.name
    }
}
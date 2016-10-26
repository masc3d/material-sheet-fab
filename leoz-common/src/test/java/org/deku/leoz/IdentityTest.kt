package org.deku.leoz

import org.deku.leoz.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.BundleType
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by masc on 27.06.15.
 */
class IdentityTest {
    private var log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testIdentity() {
        val ident = Identity.create(BundleType.LEOZ_NODE.value, SystemInformation.create())
        log.info(ident.toString())
    }
}

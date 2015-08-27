package org.deku.leoz.build

import org.junit.Test
import sx.platform.PlatformId

/**
 * Created by masc on 15.08.15.
 */
class DeploymentTest {

    @Test
    fun testPlatformArch() {
        println(PlatformId.current())
    }

    @Test
    fun testParsePlatformArch() {
        println(PlatformId.parse("osx64"))
    }
}
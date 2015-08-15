package org.deku.leoz.build

import org.junit.Test

/**
 * Created by masc on 15.08.15.
 */
class DeploymentTest {

    @Test
    fun testPlatformArch() {
        println(PlatformArch.current())
    }

    @Test
    fun testParsePlatformArch() {
        println(PlatformArch.parse("osx64"))
    }
}
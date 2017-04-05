package sx.platform

import org.junit.Test

/**
 * Created by masc on 15.08.15.
 */
class PlatformTest {

    @Test
    fun testPlatformId() {
        println(PlatformId.current())
    }

    @Test
    fun testParsePlatformId() {
        println(PlatformId.parse("osx64"))
    }
}